package com.project1.slangdictionary.util;

import java.util.Arrays;
import java.util.List;

public class TokenUtil {
    public static List<String> splitToTokens(String str) {
        if (str == null || str.isEmpty()) return List.of();
        return Arrays.stream(str.toLowerCase().split("[^a-z0-9']+")).filter(s -> !s.isEmpty()).toList();
    }
}
