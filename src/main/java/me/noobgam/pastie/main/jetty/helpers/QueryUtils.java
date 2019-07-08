package me.noobgam.pastie.main.jetty.helpers;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class QueryUtils {
    private QueryUtils() {

    }

    public static Map<String, String> splitToUrlParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(s -> s.split("=", 2))
                .filter(arr -> arr.length > 1 && !arr[1].isBlank())
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1],
                        // discard old value.
                        (old, cur) -> cur
                ));
    }
}
