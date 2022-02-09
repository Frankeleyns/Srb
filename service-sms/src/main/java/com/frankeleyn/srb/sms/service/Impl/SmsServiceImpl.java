package com.frankeleyn.srb.sms.service.Impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.frankeleyn.common.util.RandomUtils;
import com.frankeleyn.srb.sms.service.SmsService;
import com.frankeleyn.srb.sms.utils.SmsProperties;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frankeleyn
 * @date 2022/2/9 14:39
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void send(String mobile) {
        // 1. 生成4位随机验证码
        String fourBitRandom = RandomUtils.getFourBitRandom();
        // 2. 存入 redis
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, fourBitRandom);
        // 3. 通过阿里云发送短信
        aliSend(mobile, fourBitRandom);
    }

    public void aliSend(String mobile, String verCode) {
        // 配置类配置信息，公共参数，每次请求都相同的参数
        DefaultProfile profile = DefaultProfile.getProfile(SmsProperties.REGION_Id,SmsProperties.KEY_ID, SmsProperties.KEY_SECRET);

        // 发送短信, 携带验证码
        IAcsClient iAcsClient = new DefaultAcsClient(profile);

        // 创建请求参数，每次请求不同的参数
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setSysMethod(MethodType.POST);
        // Domain 阿里云短信网关(必加，官方文档没提示，不加就会报错)
        commonRequest.setSysDomain("dysmsapi.aliyuncs.com");
        // Version 版本(必加，官方文档没提示，不加就会报错)
        commonRequest.setSysVersion("2017-05-25");
        commonRequest.setSysAction("SendSms");
        commonRequest.putQueryParameter("PhoneNumbers", mobile);
        commonRequest.putQueryParameter("SignName", SmsProperties.SIGN_NAME);
        commonRequest.putQueryParameter("TemplateCode", SmsProperties.TEMPLATE_CODE);

        /* 将 {"code", "验证码"} 转为 json
         * 也可以直接：
         * commonRequest.putQueryParameter("TemplateParam", "{\"code\" : \" "+ fourBitRandom + " \"}");
         * */
        Map<String,Object> map = new HashMap<>();
        map.put("code", verCode);
        Gson gson = new Gson();
        String code = gson.toJson(map);
        commonRequest.putQueryParameter("TemplateParam", code);

        // 发送请求
        try {
            CommonResponse commonResponse = iAcsClient.getCommonResponse(commonRequest);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
