//package cn.people.cms.config;
//
//import cn.people.api.*;
//import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
//import com.googlecode.jsonrpc4j.ProxyUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.net.URL;
//
///**
// * @author : 张新征
// * Date: 2017/9/25 14:54
// * Description:
// */
//@Configuration
//@Slf4j
//public class RpcConfig {
//    @Value("${theone.rpc.url}")
//    private String rpcUrl;
//
//    @Bean
//    public JsonRpcHttpClient userClient() {
//        URL url = null;
//        try {
//            url = new URL(rpcUrl+"/auth/user");
//        } catch (Exception e) {
//            log.error("rpc用户接口访问错误");
//        }
//        return new JsonRpcHttpClient(url);
//    }
//
//    @Bean
//    public JsonRpcHttpClient officeClient() {
//        URL url = null;
//        try {
//            url = new URL(rpcUrl+"/auth/office");
//        } catch (Exception e) {
//            log.error("rpc机构接口访问错误");
//        }
//        return new JsonRpcHttpClient(url);
//    }
//
//    @Bean
//    public JsonRpcHttpClient menuClient() {
//        URL url = null;
//        try {
//            url = new URL(rpcUrl+"/auth/menu");
//        } catch (Exception e) {
//            log.error("rpc菜单接口访问错误");
//        }
//        return new JsonRpcHttpClient(url);
//    }
//
//    @Bean
//    public JsonRpcHttpClient systemClient() {
//        URL url = null;
//        try {
//            url = new URL(rpcUrl+"/auth/system");
//        } catch (Exception e) {
//            log.error("rpc系统接口访问错误");
//        }
//        return new JsonRpcHttpClient(url);
//    }
//
//    @Bean
//    public JsonRpcHttpClient dictClient() {
//        URL url = null;
//        try {
//            url = new URL(rpcUrl+"/auth/dict");
//        } catch (Exception e) {
//            log.error("rpc系统接口访问错误");
//        }
//        return new JsonRpcHttpClient(url);
//    }
//
//    @Bean
//    public RpcIUserService rpcUser(JsonRpcHttpClient userClient) {
//        return ProxyUtil.createClientProxy(getClass().getClassLoader(), RpcIUserService.class, userClient);
//    }
//
//    @Bean
//    public RpcIOfficeService rpcOffice(JsonRpcHttpClient officeClient) {
//        return ProxyUtil.createClientProxy(getClass().getClassLoader(), RpcIOfficeService.class, officeClient);
//    }
//
//    @Bean
//    public RpcIMenuService rpcMenu(JsonRpcHttpClient menuClient) {
//        return ProxyUtil.createClientProxy(getClass().getClassLoader(), RpcIMenuService.class, menuClient);
//    }
//
//    @Bean
//    public RpcISystemService rpcSystem(JsonRpcHttpClient systemClient) {
//        return ProxyUtil.createClientProxy(getClass().getClassLoader(), RpcISystemService.class, systemClient);
//    }
//
//    @Bean
//    public RpcIDictService rpcDict(JsonRpcHttpClient dictClient) {
//        return ProxyUtil.createClientProxy(getClass().getClassLoader(), RpcIDictService.class, dictClient);
//    }
//}
