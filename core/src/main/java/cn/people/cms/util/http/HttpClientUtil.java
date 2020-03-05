package cn.people.cms.util.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 *
 */
public final class HttpClientUtil {
    private static Log logger = LogFactory.getLog(HttpClientUtil.class);
    private static volatile HttpClient httpClient = null;

    /**
     * Constructor
     */
    private HttpClientUtil() {
    }

    /**
     * 2014-07-31 add by ld
     * Download Stream
     * @param url
     * @return
     */
    public static InputStream downloadStream(String url)
    {
    	try 
    	{
    		 HttpGet httpGet = new HttpGet(url);
             HttpResponse response = getHttpClient().execute(httpGet);
             return responseToStream(response);
		}
    	catch (Exception e) 
    	{
			// TODO: handle exception
    		logger.error("Send Get request to url faild, url: " + url, e);
		}
    	return null;
    }
    
    /**
     * Download data
     * 
     * @param url url
     * @return bytes data
     */
    public static byte[] downloadData(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToByte(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send get to URL.
     * 
     * @param url url
     * @return result content
     */
    public static String sendGet(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToString(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send get to URL.
     * 
     * @param url url
     * @param charset charset
     * @return result content
     */
    public static String sendGet(String url, String charset) {
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToString(response, charset);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send get to URL.
     * 
     * @param url url
     * @param args arguments
     * @return result content
     */
    public static HttpResponse sendGetRes(String url, Map<String, Object> args) {
        if (args != null && !args.isEmpty()) {
            String argsForString = "";
            for (String key : args.keySet()) {
                argsForString += key + "=" + args.get(key) + "&";
            }
            url = url + "?" + argsForString.substring(0, argsForString.length() - 1);
        }
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return response;
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }
    
    /**
     * Send get to URL.
     * 
     * @param url url
     * @param args arguments
     * @return result content
     */
    public static String sendGet(String url, Map<String, Object> args) {
        if (args != null && !args.isEmpty()) {
            String argsForString = "";
            for (String key : args.keySet()) {
                argsForString += key + "=" + args.get(key) + "&";
            }
            url = url + "?" + argsForString.substring(0, argsForString.length() - 1);
        }
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = getHttpClient().execute(httpGet);
            return responseToString(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }
    
    public static String sendGetWithHeader(String url, Map<String, Object> headers, Map<String, Object> args) {
    	
    	
        if (args != null && !args.isEmpty()) {
            String argsForString = "";
            for (String key : args.keySet()) {
                argsForString += key + "=" + args.get(key) + "&";
            }
            url = url + "?" + argsForString.substring(0, argsForString.length() - 1);
        }
        try {
        	HttpClient client = getHttpClient();
        	
            HttpGet httpGet = new HttpGet(url);
            if (headers != null && !headers.isEmpty()) {
                for (String key : headers.keySet()) {
                	httpGet.addHeader(key, (String)headers.get(key));
                }
            }
            HttpResponse response = client.execute(httpGet);
            return responseToString(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * 带有用户名和密码的GET
     * 
     * @param url 访问的地址
     * @param userName 校验用户名
     * @param password 校验密码
     * @return String
     */
    public static String sendGetWithAuthor(String url, String userName, String password) {
        HttpGet httpGet = new HttpGet(url);
        try {
            DefaultHttpClient defaultHttpClient = (DefaultHttpClient) getHttpClient();
            defaultHttpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
            HttpResponse response = defaultHttpClient.execute(httpGet);
            return responseToString(response);
        } catch (Exception e) {
            logger.error("Send Get request to url faild, url: " + url, e);
        }
        return null;
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap) throws Exception {
        return sendPost(url, parameterMap, null, "UTF-8");
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @param encoding encoding
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap, String encoding) throws Exception {
        return sendPost(url, parameterMap, null, encoding);
    }

    /**
     * Send post to URL with parameters by given encoding.
     * 
     * @param url url
     * @param parameterMap parameterMap
     * @param headerMap headerMap
     * @param encoding encoding
     * @return result content
     * @throws Exception Exception
     */
    public static String sendPost(String url, Map<String, String> parameterMap, Map<String, String> headerMap,
            String encoding) throws Exception {
        StringEntity entity = null;

        if (parameterMap != null && !parameterMap.isEmpty()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : parameterMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            try {
                // entity = new UrlEncodedFormEntity(params, encoding);
                entity = new StringEntity(URLEncodedUtils.format(params, encoding));
                entity.setContentType(URLEncodedUtils.CONTENT_TYPE);
            } catch (UnsupportedEncodingException e) {
                logger.error("Encode the parameter failed!", e);
            }
        }

        return sendPostWithEntity(url, entity, headerMap);
    }

    /**
     * Send post to URL with parameters by given encoding.
     * @param url
     * @param parameterMap
     * @param headerMap
     * @param encoding
     * @return
     * @throws Exception
     */
    public static HttpResponse sendPostRes(String url, Map<String, String> parameterMap, Map<String, String> headerMap,
            String encoding) throws Exception {
        StringEntity entity = null;

        if (parameterMap != null && !parameterMap.isEmpty()) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (Entry<String, String> entry : parameterMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            try {
                // entity = new UrlEncodedFormEntity(params, encoding);
                entity = new StringEntity(URLEncodedUtils.format(params, encoding));
                entity.setContentType(URLEncodedUtils.CONTENT_TYPE);
            } catch (UnsupportedEncodingException e) {
                logger.error("Encode the parameter failed!", e);
            }
        }

        return sendPostWithEntityRes(url, entity, headerMap);
    }
    
    
    private static HttpResponse sendPostWithEntityRes(String url, HttpEntity entity, Map<String, String> headerMap)
    		 throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (entity != null) {
            httpPost.setEntity(entity);
        }
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        HttpResponse response = getHttpClient().execute(httpPost);
        return response;
    }
    
    private static String sendPostWithEntity(String url, HttpEntity entity, Map<String, String> headerMap)
            throws Exception {
        HttpResponse response = sendPostWithEntityRes(url, entity, headerMap);
        return responseToString(response);
    }

    private static String responseToString(HttpResponse response, String charset) throws Exception {
        HttpEntity entity = getHttpEntity(response);
        if (entity == null) {
            return null;
        }
        return EntityUtils.toString(entity, charset);
    }

    public static String responseToString(HttpResponse response) throws Exception {
        return responseToString(response, "UTF-8");
    }

    private static byte[] responseToByte(HttpResponse response) throws Exception {
        HttpEntity entity = getHttpEntity(response);
        if (entity == null) {
            return null;
        }
        return EntityUtils.toByteArray(entity);
    }
    
    /**
     * 
     * 2014-07-31 add by ld
     * @return
     * @throws Exception
     */
    public static InputStream responseToStream(HttpResponse response) throws Exception
    {
    	HttpEntity entity = getHttpEntity(response);
        if (entity == null) {
            return null;
        }
        return entity.getContent();
    }
    
    private static HttpEntity getHttpEntity(HttpResponse response) throws Exception {
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            logger.error(response);
            EntityUtils.consume(response.getEntity());
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(response);
            }
            return response.getEntity();
        }
    }

    /**
     * When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate deallocation
     * of all system resources
     */
    // public static void shutdown() {
    // if (httpClient != null) {
    // httpClient.getConnectionManager().shutdown();
    // httpClient = null;
    // }
    // }

    /**
     * Create an HttpClient with the ThreadSafeClientConnManager.
     * 
     * @return
     */
    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
            connectionManager.setDefaultMaxPerRoute(100); // 每个host最多连接数
            connectionManager.setMaxTotal(2000); // 总共连接数
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(connectionManager);
            HttpConnectionParams.setConnectionTimeout(defaultHttpClient.getParams(), 15000); // 超时时间(毫秒)
            // 增加gzip支持
            defaultHttpClient.addRequestInterceptor(new AcceptEncodingRequestInterceptor());
            defaultHttpClient.addResponseInterceptor(new ContentEncodingResponseInterceptor());
            httpClient = defaultHttpClient;
            httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            httpClient.getParams().setParameter(ClientPNames.MAX_REDIRECTS, 1000);
        }
        return httpClient;
    }

    private static class AcceptEncodingRequestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }
        }
    }

    private static class ContentEncodingResponseInterceptor implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) throws HttpException {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if ("gzip".equalsIgnoreCase(codecs[i].getName())) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        }
    }

    private static class GzipDecompressingEntity extends HttpEntityWrapper {
        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent() throws IOException {
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

}
