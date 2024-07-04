package dev.ederfmatos.concorrencia;

import java.util.List;
import java.util.function.Function;

public class ParallelStreamFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        return items.parallelStream().map(HttpClient::call).toList();
    }
}
