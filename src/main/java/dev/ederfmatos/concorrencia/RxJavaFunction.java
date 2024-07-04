package dev.ederfmatos.concorrencia;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;
import java.util.function.Function;

public class RxJavaFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        return Observable.fromIterable(items)
                .map(HttpClient::call)
                .toList()
                .blockingGet();
    }
}