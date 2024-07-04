package dev.ederfmatos.concorrencia;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompletableFutureFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        List<CompletableFuture<Long>> futures = items.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> HttpClient.call(item)))
                .toList();
        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }
}
