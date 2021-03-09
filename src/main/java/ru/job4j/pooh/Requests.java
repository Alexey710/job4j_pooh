package ru.job4j.pooh;

public enum Requests {
    POST("POST"), GET("GET");

    private final String value;

    Requests(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
