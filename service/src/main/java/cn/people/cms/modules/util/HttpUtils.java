package cn.people.cms.modules.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author:xieyanglei
 * @date: 2018/6/7 14:10
 * @description:
 */
public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static final String ContentEncoding = "UTF-8";
    private static final int SocketTimeout = 5000;

    /**
     httpClient的get请求方式
     * @return
     * @throws Exception
     */
    public static String doGet(String url)   {
        logger.info("==================================doGet=={}",url);
        /*
         * 使用 GetMethod 来访问一个 URL 对应的网页,实现步骤:
         * 1:生成一个 HttpClinet 对象并设置相应的参数。
         * 2:生成一个 GetMethod 对象并设置响应的参数。
         * 3:用 HttpClinet 生成的对象来执行 GetMethod 生成的Get方法。
         * 4:处理响应状态码。
         * 5:若响应正常，处理 HTTP 响应内容。
         * 6:释放连接。
         */
        /* 1 生成 HttpClinet 对象并设置参数 */
        HttpClient httpClient = new HttpClient();
        // 设置 Http 连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(SocketTimeout);
        /* 2 生成 GetMethod 对象并设置参数 */
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时为 5 秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, SocketTimeout);
        // 设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        /* 3 执行 HTTP GET 请求 */
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            logger.info("==================================doGet=={}",statusCode);
            /* 4 判断访问的状态码 */
            if (statusCode != HttpStatus.SC_OK&&statusCode != HttpStatus.SC_CREATED&&statusCode != HttpStatus.SC_NO_CONTENT) {
                logger.error("请求出错: "+ getMethod.getStatusLine());
                return response;
            }
            /* 5 处理 HTTP 响应内容 */
            // HTTP响应头部信息，这里简单打印
            /*Header[] headers = getMethod.getResponseHeaders();
            for (Header h : headers){
                logger.info(h.getName() + "------------ " + h.getValue());
            }*/
            // 读取 HTTP 响应内容，这里简单打印网页内容
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组
            response = new String(responseBody, ContentEncoding);
            logger.info("----------response:" + response);
            // 读取为 InputStream，在网页内容数据量大时候推荐使用
            // InputStream response = getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            logger.error("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            logger.error("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * HttpClient PUT请求
     * @author huang
     * @date 2013-4-10
     * @return
     */
    public static String doPut(String uri,String jsonObj){
        logger.info("==================================doPut=={},{}",uri,jsonObj);

        String resStr = "";
        HttpClient htpClient = new HttpClient();
        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader( "Content-Type","application/json" );
        putMethod.getParams().setParameter( HttpMethodParams.HTTP_CONTENT_CHARSET, ContentEncoding );
        putMethod.setRequestBody( jsonObj );
        try{
            int statusCode = htpClient.executeMethod( putMethod );
            logger.info("==================================doPut=={}",statusCode);
            if (statusCode != HttpStatus.SC_OK&&statusCode != HttpStatus.SC_CREATED&&statusCode != HttpStatus.SC_NO_CONTENT) {
                logger.error("Method failed: "+putMethod.getStatusLine() );
                return resStr;
            }
            byte[] responseBody = putMethod.getResponseBody();
            resStr = new String(responseBody,ContentEncoding);
            logger.info("----------response:" + resStr);
        }catch(Exception e){
            logger.error(" failed: " + e.getMessage());
            e.printStackTrace();
        }finally{
            putMethod.releaseConnection();
        }
        return resStr;
    }

    /**
     * post请求
     * @param url
     * @param jsonObj
     * @return
     */
    public static String doPost(String url,String jsonObj){
        logger.info("==================================doPost=={},{}",url,jsonObj);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        String response = "";
        try {
            StringEntity stringEntity = new StringEntity( jsonObj,"UTF-8");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(stringEntity);
            HttpResponse res = client.execute(post);
            int statusCode=res.getStatusLine().getStatusCode();
            logger.info("==================================doPost=={}",statusCode);
            if (statusCode != HttpStatus.SC_OK&&statusCode != HttpStatus.SC_CREATED&&statusCode != HttpStatus.SC_NO_CONTENT) {
                logger.error("Method failed: "+res.getStatusLine() );
                return response;
            }
            response = EntityUtils.toString(res.getEntity());// 返回json格式：
            logger.info("----------response:" + response);
        } catch (Exception e) {
            logger.error(" failed: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public static String doDelete(String uri)  {
        logger.info("==================================doDelete=={}",uri);

        String data= "";
        HttpClient httpClient= new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, ContentEncoding);
        DeleteMethod method = null;
        try{
            method= new DeleteMethod();
            method.setURI(new URI(uri,false));
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, SocketTimeout);
            int statusCode = httpClient.executeMethod(method);
            logger.info("==================================doDelete=={}",statusCode);
            if (statusCode != HttpStatus.SC_OK&&statusCode != HttpStatus.SC_CREATED&&statusCode != HttpStatus.SC_NO_CONTENT) {
                logger.error("Method failed: " + method.getStatusLine());
                return data;
            }
            data= new String(method.getResponseBody(),ContentEncoding);
            logger.info("----------response:" + data);
        }catch(HttpException e){
            e.printStackTrace();
            logger.error("Please check your provided http address!");
        }catch(IOException e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally{
            if(method!=null)
                method.releaseConnection();
        }
        return data;
    }

}
