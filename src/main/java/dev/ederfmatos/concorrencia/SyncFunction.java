package dev.ederfmatos.concorrencia;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SyncFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        List<Long> response = new ArrayList<>();
        for (Long item : items) {
            response.add(HttpClient.call(item));
        }
        return response;
    }
}
