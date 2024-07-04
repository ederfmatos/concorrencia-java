package dev.ederfmatos.concorrencia;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.Function;

public class ForkJoinPollFunction extends RecursiveTask<List<Long>> implements Function<List<Long>, List<Long>> {

    private final List<Long> items;

    public ForkJoinPollFunction(List<Long> items) {
        this.items = items;
    }

    @Override
    public List<Long> apply(List<Long> items) {

        try (ForkJoinPool forkJoinPool = new ForkJoinPool()) {
            return forkJoinPool.invoke(new ForkJoinPollFunction(items));
        }
    }

    @Override
    protected List<Long> compute() {
        if (items.size() <= 1) {
            return items.stream().map(HttpClient::call).toList();
        }
        int mid = items.size() / 2;
        ForkJoinPollFunction leftTask = new ForkJoinPollFunction(items.subList(0, mid));
        ForkJoinPollFunction rightTask = new ForkJoinPollFunction(items.subList(mid, items.size()));

        invokeAll(leftTask, rightTask);

        List<Long> rightResult = rightTask.compute();
        List<Long> leftResult = leftTask.join();

        List<Long> result = new ArrayList<>();
        result.addAll(leftResult);
        result.addAll(rightResult);
        return result;
    }
}
