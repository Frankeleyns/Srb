package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.pojo.vo.InvestVO;
import com.frankeleyn.srb.core.service.LendItemService;
import com.frankeleyn.srb.core.service.TransFlowService;
import com.frankeleyn.srb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/api/core/lendItem")
public class ApiLendItemController {

    @Autowired
    private LendItemService lendItemService;

    @Autowired
    private TransFlowService transFlowService;

    @PostMapping("/notify")
    public String notified(HttpServletRequest request) {
        Map<String, Object> parameterMap = RequestHelper.switchMap(request.getParameterMap());

        // 幂等性校验，如果交易存在就抛出异常
        String agentBillNo = (String) parameterMap.get("agentBillNo");
        boolean transFlowExit = transFlowService.isSaveTransFlow(agentBillNo);
        Assert.isTrue(!transFlowExit, ResponseEnum.ERROR); // 交易已存在

        lendItemService.notified(parameterMap);
        return "success";
    }

    @PostMapping("/auth/commitInvest")
    public R commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);
        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.LOGIN_AUTH_ERROR);

        investVO.setInvestUserId(userId);

        String formStr = lendItemService.commitInvest(investVO);
        return R.ok("formStr", formStr);
    }
}

