package dev.ederfmatos.concorrencia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ExecutorServiceFunction implements Function<List<Long>, List<Long>> {
    private final ExecutorService executor;

    public ExecutorServiceFunction(ExecutorService executorService) {
        this.executor = executorService;
    }

    @Override
    public List<Long> apply(List<Long> items) {
        List<Future<Long>> futures = new ArrayList<>();
        for (Long item : items) {
            Future<Long> future = executor.submit(() -> HttpClient.call(item));
            futures.add(future);
        }
        List<Long> response = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                response.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
        return response;
    }
}
