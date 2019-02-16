package com.epam.jbehave.utils;

public enum JIRAResult {

    PASSED("Passed"),
    FAILED("Failed"),
    BLOCKED("Blocked"),
    UNTESTED("Untested"),
    OUT_OF_SCOPE("Out of scope");

    private String status;

    JIRAResult(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}