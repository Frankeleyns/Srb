package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.service.TransFlowService;
import com.frankeleyn.srb.core.service.UserAccountService;
import com.frankeleyn.srb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/api/core/userAccount")
public class ApiUserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private TransFlowService transFlowService;

    @GetMapping("/auth/getAccount")
    public R getAccount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);
        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.LOGIN_AUTH_ERROR);

        BigDecimal account = userAccountService.getAccount(userId);
        return R.ok("account", account);
    }

    @PostMapping("/notify")
    public String notified(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> notifiedMap = RequestHelper.switchMap(parameterMap);

        // 幂等性校验，如果交易存在就抛出异常
        String agentBillNo = (String) notifiedMap.get("agentBillNo");
        boolean transFlowExit = transFlowService.isSaveTransFlow(agentBillNo);
        Assert.isTrue(!transFlowExit, ResponseEnum.ERROR); // 交易已存在

        userAccountService.notified(notifiedMap);

        return "success";
    }

    @PostMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(@PathVariable("chargeAmt") BigDecimal chargeAmt, HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);
        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.LOGIN_AUTH_ERROR);

        String formStr = userAccountService.recharge(chargeAmt, userId);
        return R.ok("formStr", formStr);
    }

}

