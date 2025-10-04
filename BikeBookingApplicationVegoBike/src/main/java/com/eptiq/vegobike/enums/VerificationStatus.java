package com.eptiq.vegobike.enums;

import lombok.Getter;

@Getter
public enum VerificationStatus {
    PENDING(0, "Pending"),
    VERIFIED(1, "Verified"),
    REJECTED(2, "Rejected");

    private final int code;
    private final String description;

    VerificationStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static VerificationStatus fromCode(Integer code) {
        if (code == null) return PENDING;

        for (VerificationStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
