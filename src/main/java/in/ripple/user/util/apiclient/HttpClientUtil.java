package in.ripple.user.util.apiclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;

@Component
public class HttpClientUtil {


    private HttpClientUtil() {
    }

    public static class ByteResponse {


        private int statusCode;


        private byte[] content;


        public ByteResponse(int statusCode, byte[] content) {
            super();
            this.statusCode = statusCode;
            if (content != null) {
                this.content = Arrays.copyOf(content, content.length);
            }
        }

        public int getStatusCode() {
            return statusCode;
        }

        public byte[] getContent() {
            return content;
        }
    }


    public static class StringResponse {

        private int statusCode;

        private String content;


        public StringResponse(int statusCode) {
            this(statusCode, null);
        }


        public StringResponse(int statusCode, String content) {
            super();
            this.statusCode = statusCode;
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }
        public String getContent() {
            return content;
        }
    }


    public static StringResponse post4(String uri, Map<String, String> headers, String data) throws IOException {
        CloseableHttpClient  client = HttpClientBuilder.create().build();
        enableHttpsTunnelIfRequired(uri, client);

        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(uri);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (!"content-length".equalsIgnoreCase(header.getKey()))
                    post.setHeader(header.getKey(), header.getValue());
            }

            post.setEntity(new StringEntity(data));

            httpResponse = client.execute(post);
            return new StringResponse(httpResponse.getStatusLine().getStatusCode(),
                    EntityUtils.toString(httpResponse.getEntity()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (client != null && client.getClass() != null) {
                client.close();
            }
        }
    }

    public static void enableHttpsTunnelIfRequired(String url, HttpClient client) {

        System.out.println("URL is "+url);
        // enable https tunneling only for https urls
        if (!url.toLowerCase().startsWith("https"))
            return;

        X509TrustManager xtm = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return;
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        TrustManager mytm[] = { xtm };
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                return true;
            }
        };
    }



}
