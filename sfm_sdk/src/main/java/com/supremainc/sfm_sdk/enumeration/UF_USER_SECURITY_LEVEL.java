package com.supremainc.sfm_sdk.enumeration;

public enum UF_USER_SECURITY_LEVEL {
    UF_USER_SECURITY_DEFAULT(0),
    UF_USER_SECURITY_1_TO_1000(1),
    UF_USER_SECURITY_3_TO_10000(2),
    UF_USER_SECURITY_1_TO_10000(3),
    UF_USER_SECURITY_3_TO_100000(4),
    UF_USER_SECURITY_1_TO_100000(5),
    UF_USER_SECURITY_3_TO_1000000(6),
    UF_USER_SECURITY_1_TO_1000000(7),
    UF_USER_SECURITY_3_TO_10000000(8),
    UF_USER_SECURITY_1_TO_10000000(9),
    UF_USER_SECURITY_3_TO_100000000(10),
    UF_USER_SECURITY_1_TO_100000000(11),
    ;
    private int value;

    UF_USER_SECURITY_LEVEL(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static UF_USER_SECURITY_LEVEL ToAdminLevel(int i) {
        UF_USER_SECURITY_LEVEL userSecurityLevel = null;

        for (UF_USER_SECURITY_LEVEL iter : UF_USER_SECURITY_LEVEL.values()) {
            if (i == iter.getValue()) {
                userSecurityLevel = iter;
            }
        }
        return userSecurityLevel;
    }
}
