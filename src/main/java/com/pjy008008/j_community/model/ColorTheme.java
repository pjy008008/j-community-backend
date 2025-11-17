package com.pjy008008.j_community.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ColorTheme {
    BLUE("bg-blue-500"),
    GREEN("bg-green-500"),
    RED("bg-red-500"),
    PURPLE("bg-purple-500"),
    ORANGE("bg-orange-500"),
    YELLOW("bg-yellow-500"),
    INDIGO("bg-indigo-500"),
    PINK("bg-pink-500"),
    ROSE("bg-rose-500"),
    AMBER("bg-amber-500");

    private final String cssClass;

    ColorTheme(String cssClass) {
        this.cssClass = cssClass;
    }

    @JsonCreator
    public static ColorTheme fromString(String value) {
        try {
            return ColorTheme.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(value + " is not a valid ColorTheme");
        }
    }
}