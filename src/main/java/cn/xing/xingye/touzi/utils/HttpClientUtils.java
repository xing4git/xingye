package cn.xing.xingye.touzi.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by jimmey on 15/8/26.
 */
public class HttpClientUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static HttpClientBuilder clientBuilder = null;
    private static RequestConfig requestConfig = null;

    public static int CONNECT_TIMEOUT = 3000;
    public static int SOCKET_TIMEOUT = 3000;
    public static int MAX_CONNECTION = 3000;
    public static int MAX_HOST_CONNECTION = 10;
    public static String CONTENT_TYPE = "Content-Type";
    public static String DEFAULT_CHARSET = "utf8";
    public static Header REFER_HEADER;
    public static String ZONE = "hz-";

    static {
        String bizName = System.getProperty("project.name", "default");
        REFER_HEADER = new BasicHeader("X-Refer-Service", ZONE + bizName);
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .build();

        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTION);
        connectionManager.setDefaultMaxPerRoute(MAX_HOST_CONNECTION);
        clientBuilder = HttpClients.custom();
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                boolean r = !(exception instanceof SocketTimeoutException) && super.retryRequest(exception, executionCount, context);
                logger.error("RetryRequest {} {}", r, exception.getMessage());
                return r;
            }
        });
    }

    public static void setTimeout(int timeout) {
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
    }

    public static PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public static CloseableHttpClient getHttpClient() {
        return clientBuilder.build();
    }

    public static String get(String uri, String charset) throws Exception {
        return new String(get(uri), charset);
    }

    public static byte[] get(String uri) throws Exception {
        return get(uri, 0);
    }

    public static byte[] get(String uri, int timeout) throws Exception {
        return get(uri, null, timeout);
    }

    public static byte[] get(String uri, Map<String, String> headers) throws Exception {
        return get(uri, headers, 0);
    }

    public static byte[] get(String uri, Map<String, String> headers, int timeout) throws Exception {
        HttpGet get = new HttpGet(uri);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                get.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(get, timeout);
    }

    public static String post(String uri, String body, String charset) throws Exception {
        return new String(post(uri, body), charset);
    }

    public static byte[] post(String uri, String body) throws Exception {
        return post(uri, body, 0);
    }

    public static byte[] post(String uri, String body, int timeout) throws Exception {
        return post(uri, body, null, timeout);
    }


    public static byte[] post(String uri, String body, Map<String, String> headers, int timeout) throws Exception {
        HttpPost post = new HttpPost(uri);
        AbstractHttpEntity entity = null;
        if (body != null) {
            entity = new StringEntity(body);
            post.setEntity(entity);
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entity != null && entry.getKey().equals(CONTENT_TYPE)) {
                    entity.setContentType(entry.getValue());
                } else {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        return execute(post, timeout);
    }

    public static byte[] execute(final HttpRequestBase request, int timeout) throws Exception {
        InputStream in = null;
        try {
            RequestConfig config = requestConfig;
            if (timeout > 0) {
                config = RequestConfig.custom()
                        .setSocketTimeout(timeout)
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        .setConnectionRequestTimeout(timeout)
                        .build();
            }
            request.setConfig(config);
            CloseableHttpResponse response = execute(request);
            HttpEntity entity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            in = entity.getContent();
            if (code == HttpStatus.SC_OK) {
                if (isGzip(entity)) {
                    in = new GZIPInputStream(in);
                }
                return IOUtils.toByteArray(in);
            } else {
                logger.error("Request {} Code {}", request.getURI(), code);
                throw new Exception("Code " + code);
            }
        } catch (Exception e) {
            String param = "";
            if (request instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase r = (HttpEntityEnclosingRequestBase) request;
                HttpEntity entity = r.getEntity();
                if (entity != null) {
                    param = IOUtils.toString(entity.getContent());
                }
            }
            logger.error("HttpFailed uri={} param={} msg={}", request.getURI(), param, e.getMessage());
            throw e;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static CloseableHttpResponse execute(final HttpRequestBase request) throws Exception {
        request.setHeader(REFER_HEADER);
        return getHttpClient().execute(request);
    }

    private static boolean isGzip(HttpEntity entity) {
        Header encode = entity.getContentEncoding();
        return null != encode && StringUtils.isNotEmpty(encode.getValue())
                && encode.getValue().contains("gzip");
    }
}
