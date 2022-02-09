package com.frankeleyn.srb.sms.controller.api;

import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.common.util.RegexValidateUtils;
import com.frankeleyn.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Frankeleyn
 * @date 2022/2/9 14:14
 */
@Api("短信接口")
@RestController
@CrossOrigin
@RequestMapping("/api/sms")
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @ApiOperation("获取验证码")
    @GetMapping("/send/{mobile}")
    public R send(@ApiParam("手机号码") @PathVariable("mobile") String mobile) {
        // 校验手机号
        boolean checkCellphone = RegexValidateUtils.checkCellphone(mobile);
        Assert.isTrue(checkCellphone, ResponseEnum.MOBILE_ERROR);
        // 调用业务层的发送短信流程
        smsService.send(mobile);
        return R.ok();
    }

}
