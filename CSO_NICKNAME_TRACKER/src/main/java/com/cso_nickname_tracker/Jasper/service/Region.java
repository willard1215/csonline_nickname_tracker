package com.cso_nickname_tracker.Jasper.service;

public enum Region {
    KR, TW, CN;

    public static Region from(String s) {
        if (s == null) throw new IllegalArgumentException("서버종류 입력은 필수입니다.");
        return Region.valueOf(s.trim().toUpperCase());
    }
}


