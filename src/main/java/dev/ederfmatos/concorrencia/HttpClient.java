package dev.ederfmatos.concorrencia;

import java.time.LocalTime;

import static java.lang.Thread.currentThread;
import static java.time.format.DateTimeFormatter.ISO_TIME;

public class HttpClient {
    public static Long call(Long item) {
        try {
            System.out.printf("%s - Executando item: %d, Thread:%s%n", LocalTime.now().format(ISO_TIME), item, currentThread().getName());
            Thread.sleep(1000 * item);
            System.out.printf("%s - Finalizado item: %d, Thread:%s%n", LocalTime.now().format(ISO_TIME), item, currentThread().getName());
            return item;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
