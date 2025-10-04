package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.services.MSG91OTPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MSG91OTPServiceImpl implements MSG91OTPService {

    @Value("${msg91.authkey:default-authkey}")
    private String authKey;

    @Value("${msg91.sendOtpTemplateId:default-template-id}")
    private String templateId;

    private final RestTemplate restTemplate = new RestTemplate();

    public MSG91OTPServiceImpl() {
        log.info("MSG91_SERVICE_INIT - MSG91 OTP Service initialized");
    }

    @Override
    public void sendOTP(String phoneNumber) {
        String correlationId = generateCorrelationId();
        long startTime = System.currentTimeMillis();

        log.info("MSG91_OTP_SEND_START - CorrelationId: {}, Phone: {}, Timestamp: {}",
                correlationId, maskPhoneNumber(phoneNumber), LocalDateTime.now());

        String url = buildSendOTPUrl(phoneNumber);

        try {
            log.debug("MSG91_OTP_SEND_REQUEST - CorrelationId: {}, Phone: {}, URL: {}, TemplateId: {}",
                    correlationId, maskPhoneNumber(phoneNumber), maskUrl(url), templateId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "VegoBike-App/1.0");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            log.debug("MSG91_OTP_SEND_API_CALL - CorrelationId: {}, Making API call to MSG91", correlationId);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            long processingTime = System.currentTimeMillis() - startTime;

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("MSG91_OTP_SEND_SUCCESS - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, ResponseLength: {} chars",
                        correlationId, maskPhoneNumber(phoneNumber), response.getStatusCode().value(),
                        processingTime, response.getBody() != null ? response.getBody().length() : 0);

                if (log.isDebugEnabled() && response.getBody() != null) {
                    log.debug("MSG91_OTP_SEND_RESPONSE_BODY - CorrelationId: {}, Response: {}",
                            correlationId, truncateResponse(response.getBody()));
                }
            } else {
                log.error("MSG91_OTP_SEND_FAILED - CorrelationId: {}, Phone: {}, StatusCode: {}, StatusText: {}, ProcessingTime: {}ms, Response: {}",
                        correlationId, maskPhoneNumber(phoneNumber), response.getStatusCode().value(),
                        response.getStatusCode().toString(), processingTime,
                        truncateResponse(response.getBody()));
                throw new RuntimeException("Failed to send OTP via MSG91: HTTP " + response.getStatusCode().value());
            }

        } catch (HttpClientErrorException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_SEND_CLIENT_ERROR - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, Error: {}, ResponseBody: {}",
                    correlationId, maskPhoneNumber(phoneNumber), e.getStatusCode().value(),
                    processingTime, e.getMessage(), truncateResponse(e.getResponseBodyAsString()));
            throw new RuntimeException("MSG91 client error (HTTP " + e.getStatusCode().value() + "): " + e.getMessage(), e);

        } catch (HttpServerErrorException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_SEND_SERVER_ERROR - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, Error: {}, ResponseBody: {}",
                    correlationId, maskPhoneNumber(phoneNumber), e.getStatusCode().value(),
                    processingTime, e.getMessage(), truncateResponse(e.getResponseBodyAsString()));
            throw new RuntimeException("MSG91 server error (HTTP " + e.getStatusCode().value() + "): " + e.getMessage(), e);

        } catch (ResourceAccessException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_SEND_NETWORK_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage());
            throw new RuntimeException("MSG91 network error: Check internet connectivity and MSG91 service availability", e);

        } catch (RestClientException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_SEND_REST_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage(), e);
            throw new RuntimeException("MSG91 REST client error: " + e.getMessage(), e);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_SEND_UNEXPECTED_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending OTP via MSG91", e);
        }
    }

    @Override
    public boolean verifyOTP(String phoneNumber, String otp) {
        String correlationId = generateCorrelationId();
        long startTime = System.currentTimeMillis();

        log.info("MSG91_OTP_VERIFY_START - CorrelationId: {}, Phone: {}, Timestamp: {}",
                correlationId, maskPhoneNumber(phoneNumber), LocalDateTime.now());

        String url = buildVerifyOTPUrl(phoneNumber, otp);

        try {
            log.debug("MSG91_OTP_VERIFY_REQUEST - CorrelationId: {}, Phone: {}, URL: {}",
                    correlationId, maskPhoneNumber(phoneNumber), maskUrl(url));

            HttpHeaders headers = new HttpHeaders();
            headers.set("authkey", authKey);
            headers.set("User-Agent", "VegoBike-App/1.0");

            HttpEntity<?> entity = new HttpEntity<>(headers);

            log.debug("MSG91_OTP_VERIFY_API_CALL - CorrelationId: {}, Making verification API call to MSG91", correlationId);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            long processingTime = System.currentTimeMillis() - startTime;

            Map<String, Object> body = response.getBody();
            boolean isVerified = false;

            if (response.getStatusCode().is2xxSuccessful() && body != null) {
                Object typeValue = body.get("type");
                isVerified = typeValue != null && "success".equalsIgnoreCase(typeValue.toString());

                log.debug("MSG91_OTP_VERIFY_RESPONSE_PARSED - CorrelationId: {}, Phone: {}, ResponseType: {}, IsVerified: {}",
                        correlationId, maskPhoneNumber(phoneNumber), typeValue, isVerified);

                if (isVerified) {
                    log.info("MSG91_OTP_VERIFY_SUCCESS - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, VerificationResult: VALID",
                            correlationId, maskPhoneNumber(phoneNumber), response.getStatusCode().value(), processingTime);
                } else {
                    log.warn("MSG91_OTP_VERIFY_FAILED - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, VerificationResult: INVALID, ResponseBody: {}",
                            correlationId, maskPhoneNumber(phoneNumber), response.getStatusCode().value(),
                            processingTime, truncateResponse(body.toString()));
                }

            } else {
                log.error("MSG91_OTP_VERIFY_ERROR_RESPONSE - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, ResponseBody: {}",
                        correlationId, maskPhoneNumber(phoneNumber), response.getStatusCode().value(),
                        processingTime, body != null ? truncateResponse(body.toString()) : "null");
            }

            return isVerified;

        } catch (HttpClientErrorException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_VERIFY_CLIENT_ERROR - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, Error: {}, ResponseBody: {}",
                    correlationId, maskPhoneNumber(phoneNumber), e.getStatusCode().value(),
                    processingTime, e.getMessage(), truncateResponse(e.getResponseBodyAsString()));
            return false;

        } catch (HttpServerErrorException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_VERIFY_SERVER_ERROR - CorrelationId: {}, Phone: {}, StatusCode: {}, ProcessingTime: {}ms, Error: {}, ResponseBody: {}",
                    correlationId, maskPhoneNumber(phoneNumber), e.getStatusCode().value(),
                    processingTime, e.getMessage(), truncateResponse(e.getResponseBodyAsString()));
            return false;

        } catch (ResourceAccessException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_VERIFY_NETWORK_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage());
            return false;

        } catch (RestClientException e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_VERIFY_REST_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage(), e);
            return false;

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("MSG91_OTP_VERIFY_UNEXPECTED_ERROR - CorrelationId: {}, Phone: {}, ProcessingTime: {}ms, Error: {}",
                    correlationId, maskPhoneNumber(phoneNumber), processingTime, e.getMessage(), e);
            return false;
        }
    }

    // ✅ All private helper methods properly implemented
    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return phoneNumber.substring(0, 2) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    private String buildSendOTPUrl(String phoneNumber) {
        return "https://control.msg91.com/api/v5/otp?template_id=" + templateId +
                "&mobile=" + phoneNumber + "&authkey=" + authKey;
    }

    private String buildVerifyOTPUrl(String phoneNumber, String otp) {
        return "https://control.msg91.com/api/v5/otp/verify?otp=" + otp +
                "&mobile=" + phoneNumber;
    }

    private String maskAuthKey(String authKey) {
        if (authKey == null || authKey.length() < 8) {
            return "****";
        }
        return authKey.substring(0, 4) + "****" + authKey.substring(authKey.length() - 4);
    }

    private String maskUrl(String url) {
        if (url == null) {
            return "****";
        }
        // Replace authkey parameter value with masked version
        return url.replaceAll("(authkey=)[^&]*", "$1" + maskAuthKey(authKey))
                .replaceAll("(otp=)[^&]*", "$1****"); // Also mask OTP if present
    }

    private String truncateResponse(String response) {
        if (response == null) {
            return "null";
        }
        if (response.length() <= 500) {
            return response;
        }
        return response.substring(0, 500) + "... [TRUNCATED]";
    }
}
