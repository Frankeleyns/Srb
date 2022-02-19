package com.frankeleyn.srb.sms.client.fallback;

import com.frankeleyn.srb.sms.client.CoreUserInfoClient;
import org.springframework.stereotype.Service;

@Service
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        System.out.println("core 服务 checkMobile 接口调用失败");
        return false;
    }
}