package com.sochina.demo.utils.thread

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy
import java.util.concurrent.TimeUnit

object ThreadPoolUtils {
    private val LOGGER: Logger = LoggerFactory.getLogger(ThreadPoolUtils::class.java)

    /**
     * 等待队列长度
     */
    private const val BLOCKING_QUEUE_LENGTH = 1000

    /**
     * 闲置线程存活时间
     */
    private const val KEEP_ALIVE_TIME = 60

    /**
     * 闲置现场存活时间单位
     */
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    /**
     * 线程池执行器
     */
    private var executor: ThreadPoolExecutor? = null

    @get:Synchronized
    val executorPool: ThreadPoolExecutor?
        /**
         * 获取单例的线程池对象
         */
        get() {
            if (executor == null) {
                // 获取处理器数量
                val cpuNum = Runtime.getRuntime().availableProcessors()
                // 根据cpu数量，计算出合理的线程并发
                val maxPoolSize = cpuNum * 2 + 1
                executor = ThreadPoolExecutor(
                    maxPoolSize - 1, // 核心线程数
                    maxPoolSize, // 最大线程数
                    KEEP_ALIVE_TIME.toLong(), // 活跃时间
                    KEEP_ALIVE_TIME_UNIT, // 活跃时间单位
                    LinkedBlockingDeque(BLOCKING_QUEUE_LENGTH), // 线程队列
                    Executors.defaultThreadFactory(),  // 线程工厂
                    // 队列已满，而且当前线程数已经超过最大线程数时的异常处理策略
                    object : AbortPolicy() {
                        override fun rejectedExecution(r: Runnable, e: ThreadPoolExecutor) {
                            LOGGER.warn(
                                "线程等待队列已满，当前运行线程总数：{}，活动线程数：{}，等待运行任务数：{}",
                                e.poolSize,
                                e.activeCount,
                                e.queue.size
                            )
                        }
                    })
            }
            return executor
        }

    /**
     * 向线程池提交一个任务，返回线程结果
     *
     * @param callable 任务
     * @param <T>      T
     * @return 处理结果
    </T> */
    fun <T> submit(callable: Callable<T>): Future<T> {
        return executorPool!!.submit(callable)
    }

    /**
     * 向线程池提交一个任务
     *
     * @param runnable 任务
     */
    fun execute(runnable: Runnable?) {
        if (runnable == null) {
            throw NullPointerException()
        }
        executorPool!!.execute(runnable)
    }

    val size: Int
        /**
         * 获取当前线程池线程数量
         */
        get() = executorPool!!.poolSize
    val activeCount: Int
        /**
         * 获取当前线程池活动线程数量
         */
        get() = executorPool!!.activeCount

    /**
     * 从线程队列移除任务
     *
     * @param runnable 任务
     */
    fun cancel(runnable: Runnable?): Boolean {
        if (executor != null) {
            return executorPool!!.queue.remove(runnable)
        }
        return false
    }
}