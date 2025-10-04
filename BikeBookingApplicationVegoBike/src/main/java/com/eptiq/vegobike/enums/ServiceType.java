package com.eptiq.vegobike.enums;
import com.eptiq.vegobike.enums.ServiceType;


public enum ServiceType {
    GENERAL_SERVICE(1),
    ADMIN_SERVICES(2),
    BIKE_REPAIR(3);

    private final int code;

    ServiceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ServiceType fromCode(Integer code) {
        if (code == null) return null;
        for (ServiceType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ServiceType code: " + code);
    }
}
