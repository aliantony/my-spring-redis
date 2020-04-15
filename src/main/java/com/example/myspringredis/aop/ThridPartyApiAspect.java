package com.example.myspringredis.aop;

import com.alibaba.fastjson.JSONObject;
import com.example.myspringredis.util.ObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Set;

/**
 * 使用Aop来实现Api接口签名验证
 * 1、请求发起时间得在限制范围内
 * 2、请求的用户是否真实存在
 * 3、是否存在重复请求
 * 4、请求参数是否被篡改
 */
@Component
@Aspect
@Slf4j
public class ThridPartyApiAspect {


    @Autowired
    private HttpServletRequest request;


    @Autowired
    private HttpServletResponse response;


    //@Autowired
    //private RedisService redisService;


    //@Autowired
    //private CoreApiKeyService coreApiKeyService;


    /**
     * 表示匹配带有自定义注解的方法
     */
    @Pointcut("@annotation(com.example.myspringredis.anno.ThridPartyApi)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) {
        try {
            // 供应商的id，验证用户的真实性
            String appid = request.getHeader("appid");
            // 请求发起的时间
            String timestamp = request.getHeader("timestamp");
            // 随机数
            String nonce = request.getHeader("nonce");
            // 签名算法生成的签名
            String sign = request.getHeader("sign");
            if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(sign)) {
                return ObjectResponse.fail("请求头参数不能为空");
            }
            // 限制为（含）60秒以内发送的请求
            long time = 60;
            long now = System.currentTimeMillis() / 1000;
            if (now - Long.valueOf(timestamp) > time) {
                return ObjectResponse.fail("请求发起时间超过服务器限制时间");
            }
            // 查询appid是否正确
           /* CoreApiKey apiKey = coreApiKeyService.selectByAppid(appid);
            if (apiKey == null) {
                return ObjectResponse.fail("appid参数错误");
            }*/
            // 验证请求是否重复
            /*if (redisService.hasKeyHashItem("third_party_key", apiKey.getAppid() + nonce)) {
                return ObjectResponse.fail("请不要发送重复的请求");
            } else {
                // 如果nonce没有存在缓存中，则加入，并设置失效时间（秒）
                redisService.setHashItem("third_party_key", apiKey.getAppid() + nonce, nonce, time);
            }*/
            JSONObject signObj = new JSONObject();
            signObj.put("appid", appid);
            signObj.put("timestamp", timestamp);
            signObj.put("nonce", nonce);
            /*String mySign = getSign(signObj, apiKey.getSecret());
            // 验证签名
            if (!mySign.equals(sign)) {
                return ObjectResponse.fail("签名信息错误");
            }*/
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ObjectResponse.fail("解析请求参数异常");
        }
        return null;
    }


    /**
     * 获取签名信息
     *
     * @param data
     * @param secret
     * @return
     */
    private static String getSign(JSONObject data, String secret) {
        // 由于map是无序的，这里主要是对key进行排序（字典序）
        Set<String> keySet = data.keySet();
        String[] keyArr = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArr);
        StringBuilder sbd = new StringBuilder();
       /* for (String k : keyArr) {
            if (StringUtil.isNotEmpty(data.getString(k))) {
                sbd.append(k + "=" + data.getString(k) + "&");
            }
        }*/
        // secret最后拼接
        sbd.append("secret=").append(secret);
        return DigestUtils.md5Hex(sbd.toString());
    }
}