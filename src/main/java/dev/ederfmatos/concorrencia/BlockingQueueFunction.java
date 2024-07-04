package dev.ederfmatos.concorrencia;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockingQueueFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        try (ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>())) {
            List<Future<Long>> futures = items.stream()
                    .map(item -> executor.submit(() -> HttpClient.call(item)))
                    .toList();

            List<Long> results = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            executor.shutdown();
            return results;
        }
    }
}