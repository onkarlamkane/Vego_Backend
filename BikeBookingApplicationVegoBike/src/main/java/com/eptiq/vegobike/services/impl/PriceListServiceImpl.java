package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.dtos.PriceListDTO;
import com.eptiq.vegobike.exceptions.ResourceNotFoundException;
import com.eptiq.vegobike.exceptions.DuplicateResourceException;
import com.eptiq.vegobike.mappers.PriceListMapper;
import com.eptiq.vegobike.model.PriceList;
import com.eptiq.vegobike.repositories.PriceListRepository;
import com.eptiq.vegobike.services.PriceListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PriceListServiceImpl implements PriceListService {

    private final PriceListRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<PriceListDTO> getAllPriceLists(Pageable pageable) {
        log.info("PRICE_LIST_GET_ALL - Fetching price lists: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<PriceList> pricePage = repository.findAll(pageable);
            Page<PriceListDTO> result = pricePage.map(PriceListMapper::toDto);

            log.info("PRICE_LIST_GET_ALL_SUCCESS - Found {} price lists, total pages: {}",
                    result.getTotalElements(), result.getTotalPages());

            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_ALL_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch price lists: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListDTO> getActivePriceLists() {
        log.info("PRICE_LIST_GET_ACTIVE - Fetching active price lists");

        try {
            List<PriceList> activePrices = repository.findByIsActive(1);
            List<PriceListDTO> result = activePrices.stream()
                    .map(PriceListMapper::toDto)
                    .collect(Collectors.toList());

            log.info("PRICE_LIST_GET_ACTIVE_SUCCESS - Found {} active price lists", result.size());
            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_ACTIVE_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch active price lists: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PriceListDTO getPriceListById(Long id) {
        log.info("PRICE_LIST_GET - Fetching price list with ID: {}", id);

        try {
            PriceList priceList = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PRICE_LIST_GET_FAILED - Price list not found: ID={}", id);
                        return new ResourceNotFoundException("Price list not found with id: " + id);
                    });

            PriceListDTO result = PriceListMapper.toDto(priceList);
            log.info("PRICE_LIST_GET_SUCCESS - Found price list: ID={}", id);

            return result;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_GET_FAILED - Error fetching price list ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to get price list: " + e.getMessage(), e);
        }
    }

    @Override
    public PriceListDTO createPriceList(PriceListDTO dto) {
        log.info("PRICE_LIST_CREATE - Creating price list: Category={}, Days={}",
                dto.getCategoryId(), dto.getDays());

        try {
            // Check for duplicates
            validateNoDuplicatePricing(dto.getCategoryId(), dto.getDays(), null);

            PriceList entity = PriceListMapper.toEntity(dto);
            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            PriceList saved = repository.save(entity);

            log.info("PRICE_LIST_CREATE_SUCCESS - Created price list with ID: {}", saved.getId());
            return PriceListMapper.toDto(saved);

        } catch (DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_CREATE_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create price list: " + e.getMessage(), e);
        }
    }

    @Override
    public PriceListDTO updatePriceList(Long id, PriceListDTO dto) {
        log.info("PRICE_LIST_UPDATE - Updating price list with ID: {}", id);

        try {
            PriceList existing = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PRICE_LIST_UPDATE_FAILED - Price list not found: ID={}", id);
                        return new ResourceNotFoundException("Price list not found with id: " + id);
                    });

            // Check for duplicates (excluding current record)
            if (dto.getCategoryId() != null && dto.getDays() != null) {
                validateNoDuplicatePricing(dto.getCategoryId(), dto.getDays(), id);
            }

            PriceListMapper.updateEntityFromDto(dto, existing);
            existing.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            PriceList updated = repository.save(existing);

            log.info("PRICE_LIST_UPDATE_SUCCESS - Updated price list: ID={}", updated.getId());
            return PriceListMapper.toDto(updated);

        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_UPDATE_FAILED - Error updating price list ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update price list: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletePriceList(Long id) {
        log.info("PRICE_LIST_DELETE - Deleting price list with ID: {}", id);

        try {
            if (!repository.existsById(id)) {
                log.warn("PRICE_LIST_DELETE_FAILED - Price list not found: ID={}", id);
                throw new ResourceNotFoundException("Price list not found with id: " + id);
            }

            repository.deleteById(id);

            log.info("PRICE_LIST_DELETE_SUCCESS - Deleted price list with ID: {}", id);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_DELETE_FAILED - Error deleting price list ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete price list: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListDTO> getPriceListsByCategory(Integer categoryId) {
        log.info("PRICE_LIST_GET_BY_CATEGORY - Fetching prices for category: {}", categoryId);

        try {
            List<PriceList> prices = repository.findByCategoryIdAndIsActive(categoryId, 1);
            List<PriceListDTO> result = prices.stream()
                    .map(PriceListMapper::toDto)
                    .collect(Collectors.toList());

            log.info("PRICE_LIST_GET_BY_CATEGORY_SUCCESS - Found {} prices for category: {}",
                    result.size(), categoryId);

            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_BY_CATEGORY_FAILED - Error for category {}: {}",
                    categoryId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch prices by category: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PriceListDTO getPriceByCategoryAndDays(Integer categoryId, Integer days) {
        log.info("PRICE_LIST_GET_BY_CATEGORY_DAYS - Fetching price: Category={}, Days={}",
                categoryId, days);

        try {
            PriceList priceList = repository.findActivePriceByCategoryAndDays(categoryId, days)
                    .orElseThrow(() -> {
                        log.warn("PRICE_LIST_GET_BY_CATEGORY_DAYS_FAILED - No price found: Category={}, Days={}",
                                categoryId, days);
                        return new ResourceNotFoundException(
                                "No active price found for category " + categoryId + " and " + days + " days");
                    });

            PriceListDTO result = PriceListMapper.toDto(priceList);
            log.info("PRICE_LIST_GET_BY_CATEGORY_DAYS_SUCCESS - Found price: ID={}", priceList.getId());

            return result;

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_GET_BY_CATEGORY_DAYS_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get price by category and days: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListDTO> getHourlyRates() {
        log.info("PRICE_LIST_GET_HOURLY - Fetching hourly rates");

        try {
            List<PriceList> hourlyRates = repository.findActiveHourlyRates();
            List<PriceListDTO> result = hourlyRates.stream()
                    .map(PriceListMapper::toDto)
                    .collect(Collectors.toList());

            log.info("PRICE_LIST_GET_HOURLY_SUCCESS - Found {} hourly rates", result.size());
            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_HOURLY_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch hourly rates: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListDTO> getDailyRates() {
        log.info("PRICE_LIST_GET_DAILY - Fetching daily rates");

        try {
            List<PriceList> dailyRates = repository.findActiveDailyRates();
            List<PriceListDTO> result = dailyRates.stream()
                    .map(PriceListMapper::toDto)
                    .collect(Collectors.toList());

            log.info("PRICE_LIST_GET_DAILY_SUCCESS - Found {} daily rates", result.size());
            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_DAILY_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch daily rates: " + e.getMessage(), e);
        }
    }

    @Override
    public PriceListDTO updatePriceListStatus(Long id, boolean isActive) {
        log.info("PRICE_LIST_STATUS_UPDATE - Updating status for ID: {} to: {}", id, isActive);

        try {
            PriceList existing = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("PRICE_LIST_STATUS_UPDATE_FAILED - Price list not found: ID={}", id);
                        return new ResourceNotFoundException("Price list not found with id: " + id);
                    });

            existing.setIsActive(isActive ? 1 : 0);
            existing.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            PriceList updated = repository.save(existing);

            log.info("PRICE_LIST_STATUS_UPDATE_SUCCESS - Updated status for ID: {} to: {}", id, isActive);
            return PriceListMapper.toDto(updated);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("PRICE_LIST_STATUS_UPDATE_FAILED - Error updating status for ID {}: {}",
                    id, e.getMessage(), e);
            throw new RuntimeException("Failed to update price list status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListDTO> getAffordablePrices(BigDecimal maxPrice) {
        log.info("PRICE_LIST_GET_AFFORDABLE - Fetching prices under: {}", maxPrice);

        try {
            List<PriceList> affordablePrices = repository.findAffordablePrices(maxPrice);
            List<PriceListDTO> result = affordablePrices.stream()
                    .map(PriceListMapper::toDto)
                    .collect(Collectors.toList());

            log.info("PRICE_LIST_GET_AFFORDABLE_SUCCESS - Found {} affordable prices", result.size());
            return result;

        } catch (Exception e) {
            log.error("PRICE_LIST_GET_AFFORDABLE_FAILED - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch affordable prices: " + e.getMessage(), e);
        }
    }

    // ✅ Private helper methods
    private void validateNoDuplicatePricing(Integer categoryId, Integer days, Long excludeId) {
        long duplicateCount = repository.countDuplicatePricing(categoryId, days, excludeId);
        if (duplicateCount > 0) {
            String daysText = days == 0 ? "hourly" : days + " day" + (days > 1 ? "s" : "");
            throw new DuplicateResourceException(
                    "A price already exists for category " + categoryId + " and " + daysText);
        }
    }
}
