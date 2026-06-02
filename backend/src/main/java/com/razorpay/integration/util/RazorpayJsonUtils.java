package com.razorpay.integration.util;

import org.json.JSONObject;

public final class RazorpayJsonUtils {

    private RazorpayJsonUtils() {
    }

    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    public static Long getLong(JSONObject json, String key) {
        if (json == null || !json.has(key)) {
            return null;
        }
        return toLong(json.get(key));
    }
}
