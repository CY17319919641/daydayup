# Kotlin 协程教学笔记与完整 Demo

本文档是一个独立的 Kotlin 协程学习材料，不依赖当前 Android 项目的任何代码。你可以把文中的完整 Demo 文件复制到一个新的 Kotlin/JVM 工程中运行，也可以只把它当作阅读材料。

依赖版本说明：截至 2026-08-13，`kotlinx.coroutines` 官方 GitHub README 和 Releases 显示稳定版本为 `1.11.0`，配套 Kotlin 版本为 `2.2.20`。如果你在已有工程中学习，可以按自己工程的 Kotlin 版本选择兼容的协程版本。

## 1. 协程的基础概念

协程可以理解为“可挂起、可恢复的轻量级并发任务”。它不是线程，但在 JVM/Android 上最终还是会运行在线程之上。

几个核心点：

- 协程很轻量，可以创建大量协程，而不等于创建大量线程。
- `suspend` 函数可以在不阻塞线程的情况下暂停执行，稍后再恢复。
- 挂起不是阻塞。`delay(1000)` 会挂起当前协程，让线程去执行其他任务；`Thread.sleep(1000)` 会阻塞当前线程。
- 协程的并发是协作式的。取消、切换、挂起都依赖协程代码在合适的位置检查取消状态或调用挂起函数。

最小例子：

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000)
        println("World")
    }
    println("Hello")
}
```

输出顺序通常是：

```text
Hello
World
```

`launch` 启动一个子协程，`delay` 挂起子协程，父协程继续执行。

## 2. suspend 函数

`suspend` 是协程最基础的函数标记：

```kotlin
suspend fun fetchUserName(): String {
    delay(500)
    return "Alice"
}
```

`suspend` 函数只能在协程或另一个 `suspend` 函数中调用。它的意义不是“自动开线程”，而是“允许当前函数在某些点挂起并恢复”。

常见误区：

- `suspend` 函数本身不会自动异步。
- `suspend` 函数运行在哪个线程，取决于调用它的协程上下文和调度器。
- 真正启动协程的是协程构建器，例如 `launch`、`async`、`runBlocking`。

## 3. 协程构建器

协程构建器负责创建协程。

### runBlocking

`runBlocking` 会创建一个协程，并阻塞当前线程直到内部协程执行完毕。它适合用于：

- `main` 函数中的演示代码。
- 测试代码。
- 把非协程世界桥接到协程世界。

不建议在 Android 主线程或服务端请求线程中滥用 `runBlocking`。

```kotlin
fun main() = runBlocking {
    delay(100)
    println("done")
}
```

### launch

`launch` 启动一个“不直接返回结果”的协程，返回 `Job`。

```kotlin
val job = launch {
    delay(100)
    println("work done")
}
job.join()
```

适合场景：

- 触发一个任务。
- 不需要直接返回值。
- 通过 `Job` 管理生命周期、取消和等待。

### async

`async` 启动一个“会返回结果”的协程，返回 `Deferred<T>`。通过 `await()` 获取结果。

```kotlin
val deferred = async {
    delay(100)
    42
}
println(deferred.await())
```

适合场景：

- 并发执行多个有返回值的任务。
- 最后汇总结果。

注意：`async` 中的异常通常会在 `await()` 时重新抛出。

### withContext

`withContext` 不一定创建新的并发任务，它更像是“切换协程上下文，然后执行一段代码并返回结果”。

```kotlin
val text = withContext(Dispatchers.IO) {
    // 执行 IO 操作
    "file content"
}
```

适合场景：

- 切换到 IO 线程池执行阻塞 IO。
- 切换到 CPU 线程池执行计算。
- 在 Android 中从后台线程切回 `Dispatchers.Main` 更新 UI。

### coroutineScope

`coroutineScope` 会创建一个新的结构化作用域。它会等待所有子协程完成。如果任意子协程失败，整个作用域失败，并取消其他子协程。

```kotlin
suspend fun loadBoth(): Pair<String, String> = coroutineScope {
    val a = async { loadA() }
    val b = async { loadB() }
    a.await() to b.await()
}
```

### supervisorScope

`supervisorScope` 也是结构化作用域，但子协程之间的失败不会自动互相取消。

```kotlin
supervisorScope {
    val a = async { loadA() }
    val b = async { runCatching { loadB() }.getOrDefault("fallback") }
    println(a.await())
    println(b.await())
}
```

适合场景：

- 多个任务相互独立。
- 某个任务失败不应该影响其他任务。

### GlobalScope

`GlobalScope` 会创建脱离局部生命周期的顶层协程。它容易造成任务泄漏、错误难追踪、取消不可控。除非你明确需要进程级生命周期，否则应避免使用。

## 4. 调度器与线程切换

协程上下文 `CoroutineContext` 由多种元素组成，常见元素包括：

- `Job`：控制生命周期。
- `CoroutineDispatcher`：决定协程在哪些线程上执行。
- `CoroutineName`：调试时给协程命名。
- `CoroutineExceptionHandler`：处理未捕获异常。

常见调度器：

| 调度器 | 典型用途 |
| --- | --- |
| `Dispatchers.Default` | CPU 密集型任务，例如排序、JSON 解析、大量计算 |
| `Dispatchers.IO` | 阻塞 IO，例如文件、数据库、传统网络调用 |
| `Dispatchers.Main` | Android、Swing、JavaFX 等 UI 主线程，需要对应平台依赖 |
| `Dispatchers.Unconfined` | 不限制线程，挂起前后线程可能变化，通常只用于特殊场景或实验 |
| 自定义 Dispatcher | 使用自己的线程池，必须记得关闭 |

线程切换例子：

```kotlin
withContext(Dispatchers.IO) {
    // 当前代码倾向在 IO 线程池执行
}

withContext(Dispatchers.Default) {
    // 当前代码倾向在 Default 线程池执行
}
```

调度器选择原则：

- CPU 密集型任务用 `Default`。
- 阻塞 IO 用 `IO`。
- UI 更新用 `Main`。
- 不要为了“异步”盲目切线程。挂起函数如果本来就是非阻塞的，不一定需要 `Dispatchers.IO`。

## 5. 协程生命周期与取消

协程通过 `Job` 管理生命周期。

常见状态可以粗略理解为：

- New：创建但未启动，例如 `CoroutineStart.LAZY`。
- Active：正在运行或可运行。
- Completing：主体完成，等待子协程完成。
- Cancelling：正在取消。
- Cancelled：已取消。
- Completed：正常完成。

取消是协作式的：

```kotlin
val job = launch {
    while (isActive) {
        delay(100)
        println("working")
    }
}

delay(300)
job.cancelAndJoin()
```

如果协程执行的是纯 CPU 循环，并且不调用挂起函数，就需要主动检查取消状态：

```kotlin
while (isActive) {
    // do some CPU work
    yield()
}
```

常见取消 API：

- `job.cancel()`：请求取消。
- `job.join()`：等待完成。
- `job.cancelAndJoin()`：取消并等待完成。
- `withTimeout(...)`：超时后抛出 `TimeoutCancellationException`。
- `withTimeoutOrNull(...)`：超时后返回 `null`。
- `ensureActive()`：如果已取消，立即抛出 `CancellationException`。
- `NonCancellable`：在 `finally` 中执行不可取消的清理逻辑。

## 6. 异常处理

协程异常处理要区分 `launch` 和 `async`。

`launch`：

- 没有返回值。
- 未捕获异常会向父协程传播。
- 如果是顶层或监督作用域中的 `launch`，可以被 `CoroutineExceptionHandler` 观察到。

`async`：

- 异常会保存在 `Deferred` 中。
- 调用 `await()` 时重新抛出。
- 如果不 `await()`，异常容易被延迟暴露或被忽略。

普通结构化作用域中的异常规则：

- 一个子协程失败，父作用域失败。
- 父作用域会取消其他子协程。
- 父协程会等待所有子协程完成取消和清理后再抛出异常。

`CancellationException` 通常表示正常取消，不应当作为业务错误处理。

`CoroutineExceptionHandler` 不是万能的 `try-catch`。它更像“最后一道未捕获异常观察器”，不能代替在 `await()`、业务边界或子任务内部做明确异常处理。

## 7. 结构化并发与作用域

结构化并发的核心思想：协程必须有清晰的父子关系和生命周期归属。

它带来的好处：

- 父协程会等待子协程完成。
- 父协程取消时，子协程也会被取消。
- 子协程失败时，错误传播路径可预测。
- 不容易出现后台任务泄漏。

推荐写法：

```kotlin
suspend fun loadScreenData(): ScreenData = coroutineScope {
    val user = async { loadUser() }
    val messages = async { loadMessages() }
    ScreenData(user.await(), messages.await())
}
```

不推荐写法：

```kotlin
suspend fun loadScreenData() {
    GlobalScope.launch {
        // 生命周期脱离调用方，不容易取消和追踪
    }
}
```

在 Android 中，常见作用域包括：

- `viewModelScope`：跟随 `ViewModel` 生命周期。
- `lifecycleScope`：跟随 `LifecycleOwner` 生命周期。
- `rememberCoroutineScope`：Compose 中跟随组合生命周期。

本文 Demo 不使用这些 Android 作用域，避免和当前项目耦合。

## 8. Channel

`Channel` 是协程间通信的队列。一个协程发送数据，另一个或多个协程接收数据。

```kotlin
val channel = Channel<Int>()

launch {
    channel.send(1)
    channel.close()
}

launch {
    for (value in channel) {
        println(value)
    }
}
```

Channel 的特点：

- `send` 和 `receive` 都是挂起函数。
- 默认 `Channel()` 是 rendezvous channel，发送方和接收方需要会合。
- `Channel.BUFFERED` 可以缓存一部分元素。
- `Channel.CONFLATED` 只保留最新值。
- `Channel.UNLIMITED` 理论上无限缓冲，要警惕内存增长。
- 发送方完成后应调用 `close()`，否则接收方的 `for (value in channel)` 可能一直等待。

Channel 适合：

- 工作队列。
- 生产者消费者模型。
- 多协程之间传递一次性事件。

如果你只是表达一段异步数据流，通常优先考虑 Flow。

## 9. Flow

`Flow<T>` 是冷的异步数据流。

“冷”的意思是：只有调用终端操作符，例如 `collect`、`toList`、`first`，上游代码才会执行。每次收集都会重新执行上游。

基本例子：

```kotlin
val numbers = flow {
    emit(1)
    emit(2)
    emit(3)
}

numbers
    .map { it * 2 }
    .filter { it > 2 }
    .collect { println(it) }
```

常见 Flow API：

- 创建：`flow {}`、`flowOf(...)`、`asFlow()`。
- 转换：`map`、`filter`、`transform`、`onEach`。
- 终端：`collect`、`toList`、`first`、`single`、`reduce`。
- 上下文：`flowOn(dispatcher)` 改变上游执行上下文。
- 异常：`catch {}` 捕获上游异常。
- 取消旧任务：`collectLatest {}`。
- 背压优化：`buffer()`、`conflate()`。
- 组合：`zip`、`combine`、`flatMapLatest`。

`StateFlow` 和 `SharedFlow` 是热流：

- `StateFlow`：有当前状态值，适合 UI 状态。
- `SharedFlow`：可配置 replay 和 buffer，适合广播事件或共享上游。

冷流和热流的区别：

| 类型 | 是否主动运行 | 是否保存状态 | 典型用途 |
| --- | --- | --- | --- |
| `Flow` | 否，collect 时运行 | 否 | 一次查询、请求、数据转换管道 |
| `StateFlow` | 是，和持有它的作用域相关 | 是，始终有 value | UI 状态 |
| `SharedFlow` | 是，和持有它的作用域相关 | 可配置 replay | 事件广播、共享数据源 |

## 10. 完整 Demo 文件

下面是一个完整、独立、可运行的 Kotlin/JVM Demo 文件。建议文件名：

```text
CoroutinesTeachingDemo.kt
```

最小 Gradle 配置可以使用：

```kotlin
plugins {
    kotlin("jvm") version "2.2.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
}

application {
    mainClass.set("CoroutinesTeachingDemoKt")
}
```

完整代码：

```kotlin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.Executors

fun main() = runBlocking(CoroutineName("demo-main")) {
    section("1. 基础概念：挂起不是阻塞") {
        basicConceptsDemo()
    }

    section("2. 协程构建器：launch / async / coroutineScope") {
        buildersDemo()
    }

    section("3. 调度器与线程切换") {
        dispatchersDemo()
    }

    section("4. 生命周期与取消") {
        cancellationDemo()
    }

    section("5. 异常处理") {
        exceptionDemo()
    }

    section("6. 结构化并发与 supervisorScope") {
        structuredConcurrencyDemo()
    }

    section("7. Channel：生产者消费者") {
        channelDemo()
    }

    section("8. Flow：冷流、操作符、热流") {
        flowDemo()
    }
}

private suspend fun CoroutineScope.section(
    title: String,
    block: suspend CoroutineScope.() -> Unit
) {
    println()
    println("========== $title ==========")
    block()
}

private fun log(message: String) {
    println("[${Thread.currentThread().name}] $message")
}

private suspend fun basicConceptsDemo() = coroutineScope {
    log("parent starts")

    val job = launch(CoroutineName("basic-child")) {
        log("child starts")
        delay(200)
        log("child resumes after delay")
    }

    log("parent continues while child is suspended")
    job.join()
    log("parent waits for child by join")
}

private suspend fun buildersDemo() = coroutineScope {
    val launchJob: Job = launch {
        delay(120)
        log("launch returns Job and is used for fire-and-forget work")
    }

    val answer = async {
        delay(80)
        40 + 2
    }

    val lazyValue = async(start = kotlinx.coroutines.CoroutineStart.LAZY) {
        log("lazy async starts only after start or await")
        delay(60)
        "lazy-result"
    }

    log("lazy async has not started yet")
    lazyValue.start()

    log("async result = ${answer.await()}")
    log("lazy async result = ${lazyValue.await()}")
    launchJob.join()

    val combined = coroutineScope {
        val left = async {
            delay(70)
            "left"
        }
        val right = async {
            delay(50)
            "right"
        }
        "${left.await()} + ${right.await()}"
    }

    log("coroutineScope waits for children and returns: $combined")
}

private suspend fun dispatchersDemo() = coroutineScope {
    val singleThreadDispatcher = Executors
        .newSingleThreadExecutor { runnable -> Thread(runnable, "demo-single-thread") }
        .asCoroutineDispatcher()

    try {
        withContext(Dispatchers.Default + CoroutineName("cpu-work")) {
            log("Dispatchers.Default is suitable for CPU-bound work")
            var sum = 0L
            repeat(300_000) { sum += it }
            log("CPU result = $sum")
        }

        withContext(Dispatchers.IO + CoroutineName("io-work")) {
            log("Dispatchers.IO is suitable for blocking IO")
            delay(80)
            log("pretend file or database read finished")
        }

        withContext(singleThreadDispatcher + CoroutineName("custom-thread")) {
            log("custom dispatcher runs on a named single thread")
        }

        launch(Dispatchers.Unconfined + CoroutineName("unconfined-demo")) {
            log("Unconfined before delay")
            delay(50)
            log("Unconfined after delay, thread may change")
        }.join()
    } finally {
        singleThreadDispatcher.close()
    }
}

private suspend fun cancellationDemo() = coroutineScope {
    val job = launch {
        try {
            repeat(10) { index ->
                ensureActive()
                log("working item $index")
                delay(80)
            }
        } finally {
            log("finally runs after cancellation, isActive=$isActive")
            withContext(NonCancellable) {
                delay(40)
                log("non-cancellable cleanup finished")
            }
        }
    }

    delay(230)
    log("request cancellation")
    job.cancelAndJoin()
    log("job is cancelled and joined")

    val timeoutResult = withTimeoutOrNull(180) {
        repeat(5) { index ->
            delay(70)
            log("timeout block step $index")
        }
        "completed"
    }

    log("withTimeoutOrNull result = $timeoutResult")

    val cpuJob = launch(Dispatchers.Default) {
        var i = 0
        while (isActive) {
            i++
            if (i % 100_000 == 0) {
                yield()
            }
        }
        log("CPU loop sees cancellation")
    }

    delay(50)
    cpuJob.cancelAndJoin()
}

private suspend fun exceptionDemo() {
    try {
        coroutineScope {
            launch {
                delay(80)
                error("launch child failed")
            }

            launch {
                try {
                    repeat(10) { index ->
                        delay(40)
                        log("sibling still working $index")
                    }
                } finally {
                    log("sibling is cancelled because another child failed")
                }
            }
        }
    } catch (e: IllegalStateException) {
        log("parent catches child failure: ${e.message}")
    }

    supervisorScope {
        val deferred = async {
            delay(50)
            error("async failed")
        }

        val result = runCatching {
            deferred.await()
        }.getOrElse { throwable ->
            "fallback after await caught: ${throwable.message}"
        }

        log("async exception result = $result")
    }

    try {
        throw CancellationException("normal cancellation example")
    } catch (e: CancellationException) {
        log("CancellationException usually means normal cancellation: ${e.message}")
    }
}

private suspend fun structuredConcurrencyDemo() {
    try {
        coroutineScope {
            launch {
                try {
                    repeat(10) { index ->
                        delay(50)
                        log("structured child A step $index")
                    }
                } finally {
                    log("structured child A is cancelled with parent scope")
                }
            }

            launch {
                delay(130)
                error("structured child B failed")
            }
        }
    } catch (e: IllegalStateException) {
        log("coroutineScope failed as a whole: ${e.message}")
    }

    val result = supervisorScope {
        val required = async {
            delay(80)
            "required-data"
        }

        val optional = async {
            delay(40)
            error("optional-data failed")
        }

        val optionalResult = runCatching {
            optional.await()
        }.getOrDefault("optional-fallback")

        "${required.await()} + $optionalResult"
    }

    log("supervisorScope lets independent work continue: $result")
}

private suspend fun channelDemo() = coroutineScope {
    val channel = Channel<Int>(capacity = Channel.BUFFERED)

    val producer = launch {
        repeat(6) { value ->
            channel.send(value)
            log("producer sent $value")
        }
        channel.close()
        log("producer closed channel")
    }

    val consumers = List(2) { consumerId ->
        launch {
            for (value in channel) {
                delay(70)
                log("consumer $consumerId received $value")
            }
            log("consumer $consumerId completed")
        }
    }

    producer.join()
    consumers.joinAll()
}

private suspend fun flowDemo() = coroutineScope {
    val coldNumbers = flow {
        log("cold flow starts only when collected")
        for (value in 1..5) {
            delay(40)
            emit(value)
        }
    }

    val evenSquares = coldNumbers
        .map { value -> value * value }
        .filter { value -> value % 2 == 0 }
        .onEach { value -> log("flow operator sees $value") }
        .flowOn(Dispatchers.Default)
        .catch { throwable -> log("flow caught error: ${throwable.message}") }
        .toList()

    log("flow result = $evenSquares")

    flow {
        repeat(4) { value ->
            emit(value)
            delay(50)
        }
    }.collectLatest { value ->
        log("collectLatest starts value=$value")
        delay(90)
        log("collectLatest finishes value=$value")
    }

    val state = MutableStateFlow(0)
    val stateCollector = launch {
        state.take(4).collect { value ->
            log("StateFlow value=$value")
        }
    }

    repeat(3) { index ->
        delay(30)
        state.value = index + 1
    }
    stateCollector.join()

    val shared = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)
    shared.emit("event-before-collector")

    val sharedCollector = launch {
        shared.take(2).collect { value ->
            log("SharedFlow event=$value")
        }
    }

    delay(30)
    shared.emit("event-after-collector")
    sharedCollector.join()
}
```

## 11. Demo 观察重点

运行 Demo 时，重点看这些现象：

- 日志中的线程名会变化，说明调度器决定协程在哪些线程上恢复。
- `delay` 期间父协程可以继续运行，说明挂起不是阻塞。
- `cancelAndJoin` 会触发 `finally`，适合释放资源。
- `withTimeoutOrNull` 超时返回 `null`，不会把超时当普通业务值。
- `coroutineScope` 中一个子协程失败，会取消兄弟协程。
- `supervisorScope` 中独立任务可以自行兜底，不影响其他任务。
- `Channel` 中每个元素只会被某一个消费者处理。
- `Flow` 的上游只在 `collect` 时启动，`collectLatest` 会取消上一次还没处理完的收集逻辑。
- `StateFlow` 总有当前值，`SharedFlow` 可以通过 `replay` 给后来的收集者补发事件。

## 12. 实践建议

写协程代码时可以遵循这些规则：

- 优先写 `suspend` 函数，让调用方决定作用域和调度器。
- 不要在普通业务函数里随意创建 `GlobalScope`。
- 有返回值的并发任务用 `async`，并且总是明确 `await()` 和处理异常。
- 不需要返回值的任务用 `launch`，并通过 `Job` 或父作用域管理生命周期。
- CPU 密集型代码用 `Dispatchers.Default`，阻塞 IO 用 `Dispatchers.IO`。
- 在长时间循环里检查 `isActive`、调用 `yield()` 或 `ensureActive()`。
- 清理资源放在 `finally` 中；清理本身需要挂起时使用 `withContext(NonCancellable)`。
- 对独立子任务使用 `supervisorScope`，对强依赖子任务使用普通 `coroutineScope`。
- 简单异步流优先用 `Flow`；生产者消费者队列用 `Channel`。
- 在 Android 中让协程绑定 `viewModelScope`、`lifecycleScope` 等生命周期作用域。

## 13. 常见问题

### suspend 和 async 有什么区别？

`suspend` 是函数能力，表示函数可以挂起。`async` 是协程构建器，会启动一个协程并返回 `Deferred<T>`。

### launch 和 async 怎么选？

不需要返回值用 `launch`。需要结果用 `async`，并且明确 `await()`。

### withContext 和 async 有什么区别？

`withContext` 通常用于切换上下文并等待结果，它不会让当前作用域继续往下并发执行。`async` 会启动一个并发子任务，后续通过 `await()` 汇合。

### Flow 和 Channel 怎么选？

`Flow` 表达异步数据流和转换管道，通常是声明式的。`Channel` 表达协程之间的通信队列，通常用于生产者消费者。

### 为什么取消有时不生效？

协程取消是协作式的。如果代码长时间执行 CPU 循环，且没有调用挂起函数、`yield()`、`ensureActive()` 或检查 `isActive`，取消就不能及时生效。

### CoroutineExceptionHandler 为什么没捕获异常？

它主要处理根协程或监督作用域中未捕获的 `launch` 异常。普通子协程的异常会先传播给父协程，`async` 的异常会在 `await()` 时抛出。业务代码中仍然需要 `try-catch`、`runCatching` 或明确的失败返回模型。

## 14. 参考资料

- Kotlin 官方文档：Coroutines guide  
  https://kotlinlang.org/docs/coroutines-guide.html
- Kotlin 官方文档：Coroutines basics  
  https://kotlinlang.org/docs/coroutines-basics.html
- Kotlin 官方文档：Coroutines and channels tutorial  
  https://kotlinlang.org/docs/coroutines-and-channels.html
- Kotlinx.coroutines 官方 GitHub 仓库  
  https://github.com/Kotlin/kotlinx.coroutines
- Kotlinx.coroutines 官方 Releases  
  https://github.com/Kotlin/kotlinx.coroutines/releases
