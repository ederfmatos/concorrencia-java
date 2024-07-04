package dev.ederfmatos.concorrencia;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Function;

public class ReactorFunction implements Function<List<Long>, List<Long>> {
    @Override
    public List<Long> apply(List<Long> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> Flux.just(HttpClient.call(item)))
                .collectList()
                .subscribeOn(Schedulers.boundedElastic())
                .block();
    }
}