import dev.ederfmatos.concorrencia.RxJavaFunction;

import java.util.List;
import java.util.stream.LongStream;

public static void main() {
    final int quantity = 10;
    List<Long> items = LongStream.range(1, quantity + 1).boxed().toList();

//    int availableProcessors = Runtime.getRuntime().availableProcessors();
//    System.out.println("Available Processors: " + availableProcessors);
//    ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
//    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
//    var strategy = new CompletableFutureFunction();
//    var strategy = new ExecutorServiceFunction(executor);
//    var strategy = new ForkJoinPollFunction();
//    var strategy = new ParallelStreamFunction();
//    var strategy = new StructuredTaskScopeFunction();
//    var strategy = new SyncFunction();
//    var strategy = new BlockingQueueFunction();
//    var strategy = new ReactorFunction();
    var strategy = new RxJavaFunction();

    long startTime = System.currentTimeMillis();
    var response = strategy.apply(items);
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;

    System.out.println(response);
    System.out.println("Tempo decorrido: " + elapsedTime + " ms");
}