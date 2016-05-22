/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.foxdenstudio.sponge.foxcore.common.network;

import com.google.gson.JsonObject;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by d4rkfly3r on 5/22/2016.
 * Project: SpongeForge
 */
public final class APIConnector implements Supplier<JsonObject> {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + count.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<>(128);

    public static final Executor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
    private final JsonObject contents;
    private Consumer<? super JsonObject> successCallback;
    private Function<Throwable, ? extends Void> errorCallback;

    public APIConnector(JsonObject contents) {
        this.contents = contents;
    }

    public void execute() {
        CompletableFuture.supplyAsync(this, threadPoolExecutor).thenAccept(successCallback).exceptionally(errorCallback);
    }

    public JsonObject getContents() {
        return contents;
    }

    public Consumer<? super JsonObject> getSuccessCallback() {
        return successCallback;
    }

    public void setSuccessCallback(Consumer<? super JsonObject> successCallback) {
        this.successCallback = successCallback;
    }

    public Function<Throwable, ? extends Void> getErrorCallback() {
        return errorCallback;
    }

    public void setErrorCallback(Function<Throwable, ? extends Void> errorCallback) {
        this.errorCallback = errorCallback;
    }

    @Override
    public JsonObject get() {

        return null;
    }
}