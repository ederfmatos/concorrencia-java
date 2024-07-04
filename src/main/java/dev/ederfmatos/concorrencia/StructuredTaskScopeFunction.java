package dev.ederfmatos.concorrencia;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Function;

public class StructuredTaskScopeFunction implements Function<List<Long>, List<Long>> {

    @Override
    public List<Long> apply(List<Long> items) {
        return run(items, HttpClient::call);
    }

    private <T, V> List<V> run(List<T> items, Function<T, V> mapper) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var futures = items.stream()
                    .map(item -> scope.fork(() -> mapper.apply(item)))
                    .toList();

            scope.join().throwIfFailed();

            return futures.stream()
                    .map(StructuredTaskScope.Subtask::get)
                    .toList();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
