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

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by d4rkfly3r on 5/22/2016.
 */
public final class APIConnector implements Supplier<Optional<JsonObject>> {

    private static final String API_ENDPOINT = "https://cdn.jfreedman.us/backend";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger(1);

        public Thread newThread(@Nonnull Runnable r) {
            return new Thread(r, "AsyncTask #" + count.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<>(128);

    private static final Executor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, poolWorkQueue, threadFactory);

    private static HostnameVerifier allHostsValid;
    private static SSLContext sslContext;

    // HAck to ignore certificate validation for local testing. Should be removed in final code
    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // Create all-trusting host name verifier
            allHostsValid = (hostname, session) -> true;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private final JsonObject contents;
    private Consumer<Optional<? super JsonObject>> successCallback;
    private Function<Throwable, ? extends Optional<JsonObject>> errorCallback;
    private CompletableFuture<Optional<JsonObject>> futureTask;
    private HttpsURLConnection urlConnection;

    public APIConnector(@Nonnull final JsonObject contents) {
        this.contents = contents;
        try {
            urlConnection = (HttpsURLConnection) new URL(API_ENDPOINT).openConnection();
            urlConnection.setHostnameVerifier(allHostsValid);
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    private static JsonElement convertStreamToJsonObject(@Nonnull InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new JsonParser().parse(sb.toString());
    }

    public void execute() {
        futureTask = CompletableFuture.supplyAsync(this, threadPoolExecutor);
        if (successCallback != null) {
            futureTask.thenAccept(successCallback);
        }
        if (errorCallback != null) {
            futureTask.exceptionally(errorCallback);
        }
    }

    @Nonnull
    public JsonObject getContents() {
        return contents;
    }

    @Nullable
    public final Consumer<Optional<? super JsonObject>> getSuccessCallback() {
        return successCallback;
    }

    public void setSuccessCallback(@Nullable final Consumer<Optional<? super JsonObject>> successCallback) {
        this.successCallback = successCallback;
    }

    @Nullable
    public final Function<Throwable, ? extends Optional<JsonObject>> getErrorCallback() {
        return errorCallback;
    }

    public void setErrorCallback(@Nullable final Function<Throwable, ? extends Optional<JsonObject>> errorCallback) {
        this.errorCallback = errorCallback;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return futureTask.cancel(mayInterruptIfRunning);
    }

    @Override
    public Optional<JsonObject> get() {
        final JsonObject payload = new JsonObject();
        payload.add("payload", contents);

        JsonObject returnData = null;

        try {
            urlConnection.addRequestProperty("payload", payload.toString());
            urlConnection.connect();
            final JsonElement returnPayload = convertStreamToJsonObject(urlConnection.getInputStream());
            if (returnPayload.isJsonObject()) {
                if (returnPayload.getAsJsonObject().has("payload")) {
                    returnData = returnPayload.getAsJsonObject().getAsJsonObject("payload");
                }
            }
        } catch (IOException e) {
            if (errorCallback != null) {
                errorCallback.apply(e);
            } else {
                e.printStackTrace();
            }
        } finally {
            urlConnection.disconnect();
        }

        return Optional.fromNullable(returnData);
    }
}