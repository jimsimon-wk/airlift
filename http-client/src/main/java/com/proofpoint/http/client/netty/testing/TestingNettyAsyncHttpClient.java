package com.proofpoint.http.client.netty.testing;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.proofpoint.http.client.AsyncHttpClient;
import com.proofpoint.http.client.HttpClientConfig;
import com.proofpoint.http.client.HttpRequestFilter;
import com.proofpoint.http.client.Request;
import com.proofpoint.http.client.RequestStats;
import com.proofpoint.http.client.ResponseHandler;
import com.proofpoint.http.client.netty.NettyAsyncHttpClient;
import com.proofpoint.http.client.netty.NettyAsyncHttpClientConfig;
import com.proofpoint.http.client.netty.NettyIoPool;
import com.proofpoint.http.client.netty.NettyIoPoolConfig;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Testing variant of the {@link NettyAsyncHttpClient} which manages the thread pool itself.
 */
public class TestingNettyAsyncHttpClient
        implements AsyncHttpClient
{
    private final NettyIoPool ioPool;
    private final NettyAsyncHttpClient httpClient;

    public static AsyncHttpClient getClientForTesting()
    {
        return new TestingNettyAsyncHttpClient(new HttpClientConfig(), new NettyAsyncHttpClientConfig(), new NettyIoPoolConfig());
    }

    public static AsyncHttpClient getClientForTesting(HttpClientConfig httpClientConfig, NettyAsyncHttpClientConfig asyncHttpClientConfig, NettyIoPoolConfig ioPoolConfig)
    {
        checkNotNull(httpClientConfig, "httpClientConfig is null");
        checkNotNull(asyncHttpClientConfig, "asyncHttpClientConfig is null");
        checkNotNull(ioPoolConfig, "ioPoolConfig is null");

        return new TestingNettyAsyncHttpClient(httpClientConfig, asyncHttpClientConfig, ioPoolConfig);
    }

    private TestingNettyAsyncHttpClient(HttpClientConfig httpClientConfig, NettyAsyncHttpClientConfig asyncHttpClientConfig, NettyIoPoolConfig ioPoolConfig)
    {
        this.ioPool = new NettyIoPool(ioPoolConfig);
        this.httpClient = new NettyAsyncHttpClient("testing", ioPool, httpClientConfig, asyncHttpClientConfig, ImmutableSet.<HttpRequestFilter>of());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void close()
    {
        Closeables.closeQuietly(httpClient);
        Closeables.closeQuietly(ioPool);
    }

    @Override
    public <T, E extends Exception> T execute(Request request, ResponseHandler<T, E> responseHandler)
            throws E
    {
        return httpClient.execute(request, responseHandler);
    }

    @Override
    public RequestStats getStats()
    {
        return httpClient.getStats();
    }

    @Override
    public <T, E extends Exception> AsyncHttpResponseFuture<T, E> executeAsync(Request request, ResponseHandler<T, E> responseHandler)
            throws E
    {
        return httpClient.executeAsync(request, responseHandler);
    }
}