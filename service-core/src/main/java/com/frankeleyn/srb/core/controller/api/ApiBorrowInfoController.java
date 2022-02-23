package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.frankeleyn.srb.core.service.BorrowInfoService;
import com.frankeleyn.srb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/api/core/borrowInfo")
public class ApiBorrowInfoController {

    @Autowired
    private BorrowInfoService borrowInfoService;

    @GetMapping("/auth/getBorrowInfoStatus")
    public R getBorrowInfoStatus(HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);

        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);

        Integer borrowInfoStatus = borrowInfoService.getBorrowInfoStatus(userId);
        return R.ok("borrowInfoStatus",borrowInfoStatus);
    }

    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);

        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);

        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return R.ok().message("提交成功");
    }

    @GetMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);

        Long userId = JwtUtils.getUserId(token);
        Assert.notNull(userId, ResponseEnum.WEIXIN_FETCH_USERINFO_ERROR);

        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok("borrowAmount", borrowAmount);
    }
}

