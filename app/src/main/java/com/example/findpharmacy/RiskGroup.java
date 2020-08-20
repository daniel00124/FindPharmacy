package com.example.findpharmacy;

public enum RiskGroup {

    low("low"),
    medium("medium"),
    high("high");

    private final String mLvl;

    RiskGroup(String riskLevel) {
        this.mLvl = riskLevel;
    }


    public static RiskGroup getEnum(String s) {
        switch (s) {
            case "low":
                return low;

            case "medium":
                return medium;
            case "high":
                return high;
            default:
                return null;
        }
    }

}
