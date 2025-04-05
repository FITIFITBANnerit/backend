package com.BANnerIt.server.global.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("탈퇴했거나 존재하지 않는 회원입니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}