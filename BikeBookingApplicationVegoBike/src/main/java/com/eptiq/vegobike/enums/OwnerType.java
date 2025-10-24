package com.eptiq.vegobike.enums;

public enum OwnerType {
    FIRST(1, "1st Owner"),
    SECOND(2, "2nd Owner"),
    THIRD(3, "3rd Owner"),
    FOURTH(4, "4th Owner"),
    FIFTH(5, "5th Owner");

    private final int value;
    private final String displayName;

    OwnerType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OwnerType fromValue(int value) {
        for (OwnerType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid owner type value: " + value);
    }
}
