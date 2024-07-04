package dev.ederfmatos.concorrencia

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalTime
import java.time.format.DateTimeFormatter.ISO_TIME
import kotlin.system.measureTimeMillis
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

suspend fun main() {
    val items = (1L..10L).toList()

    val timeSync = measureTimeMillis {
        val response = sync(items)
        println(response)
    }

    val timeAsync = measureTimeMillis {
        val response = asyncWithCoroutines(items)
        println(response)
    }

    val timeChannels = measureTimeMillis {
        val response = channels(items)
        println(response)
    }

    val timeFlow = measureTimeMillis {
        val response = flow(items)
        println(response)
    }

    val timeFlowMerge = measureTimeMillis {
        val response = flowMerge(items)
        println(response)
    }

    val timeSelect = measureTimeMillis {
        val response = select(items)
        println(response)
    }

    val timeMutex = measureTimeMillis {
        val response = mutex(items)
        println(response)
    }

    val timeSupervisor = measureTimeMillis {
        val response = supervisor(items)
        println(response)
    }

    println("(sync): ${timeSync.toDuration(MILLISECONDS)} ms")
    println("(flow): ${timeFlow.toDuration(MILLISECONDS)} ms")
    println("(async): ${timeAsync.toDuration(MILLISECONDS)} ms")
    println("(channels): ${timeChannels.toDuration(MILLISECONDS)} ms")
    println("(flowMerge): ${timeFlowMerge.toDuration(MILLISECONDS)} ms")
    println("(select): ${timeSelect.toDuration(MILLISECONDS)} ms")
    println("(mutex): ${timeMutex.toDuration(MILLISECONDS)} ms")
    println("(supervisor): ${timeSupervisor.toDuration(MILLISECONDS)} ms")
}

suspend fun sync(items: List<Long>): List<Long> {
    return items.map { call(it) }
}

suspend fun asyncWithCoroutines(items: List<Long>): List<Long> = coroutineScope {
    items.map { item -> async { call(item) } }.awaitAll()
}

suspend fun channels(items: List<Long>): List<Long> = coroutineScope {
    val channel = Channel<Long>()
    items.forEach { item ->
        launch { channel.send(call(item)) }
    }
    List(items.size) { channel.receive() }
}

suspend fun flow(items: List<Long>): List<Long> {
    return items.asFlow()
        .map { call(it) }
        .toList()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun flowMerge(items: List<Long>): List<Long> {
    return items.asFlow()
        .flatMapMerge { kotlinx.coroutines.flow.flow { emit(call(it)) } }
        .toList()
}

suspend fun select(items: List<Long>): List<Long> = coroutineScope {
    val channel = Channel<Long>()
    val jobs = items.map { item ->
        launch { channel.send(call(item)) }
    }
    val results = mutableListOf<Long>()
    repeat(items.size) {
        select<Unit> { channel.onReceive(results::add) }
    }
    jobs.forEach { it.join() }
    results
}

suspend fun mutex(items: List<Long>): List<Long> = coroutineScope {
    val mutex = Mutex()
    val results = mutableListOf<Long>()
    items.map { item ->
        async(Dispatchers.IO) {
            val result = call(item)
            mutex.withLock { results.add(result) }
        }
    }.awaitAll()
    results
}

suspend fun supervisor(items: List<Long>): List<Long> = coroutineScope {
    val supervisor = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + supervisor)
    items.map { item -> scope.async { call(item) } }.awaitAll()
}

suspend fun call(item: Long): Long {
    println("${LocalTime.now().format(ISO_TIME)} - Executando item: $item, Thread:${Thread.currentThread().name}")
    delay(item.toDuration(DurationUnit.SECONDS))
    println("${LocalTime.now().format(ISO_TIME)} - Finalizado item: $item, Thread:${Thread.currentThread().name}")
    return item
}