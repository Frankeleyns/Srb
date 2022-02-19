package com.frankeleyn.srb.core.controller.api;

import com.alibaba.excel.util.StringUtils;
import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.common.util.RegexValidateUtils;
import com.frankeleyn.srb.core.pojo.vo.LoginVO;
import com.frankeleyn.srb.core.pojo.vo.RegisterVO;
import com.frankeleyn.srb.core.pojo.vo.UserInfoVO;
import com.frankeleyn.srb.core.service.UserInfoService;
import com.frankeleyn.srb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frankeleyn
 * @date 2022/2/12 16:54
 */
@RestController
@RequestMapping("/api/core/userInfo")
public class ApiUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable("mobile") String mobile) {
        return userInfoService.checkMobile(mobile);
    }

    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);

        boolean b = JwtUtils.checkToken(token);
        Assert.isTrue(b, ResponseEnum.WEIXIN_FETCH_ACCESSTOKEN_ERROR);

        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        String mobile = loginVO.getMobile();
        boolean checnkMobile = RegexValidateUtils.checkCellphone(mobile);

        // 校验参数
        Assert.notNull(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(checnkMobile, ResponseEnum.MOBILE_ERROR);
        Assert.notNull(loginVO.getPassword(), ResponseEnum.PASSWORD_NULL_ERROR);

        // 获取访问 ip
        String ip = "";
        ip = request.getRemoteAddr();

        if(StringUtils.isEmpty(ip) || ip.equals("0:0:0:0:0:0:0:1")) {
            // 需要在 nginx 配置
            ip = request.getHeader("x-forwarded-for");
        }

        System.out.println("用户 ip => " + ip);
        UserInfoVO userInfoVO = userInfoService.login(loginVO,ip);
        return R.ok("userInfoVO", userInfoVO);
    }

    @PostMapping("/register")
    public R register(@RequestBody RegisterVO registerVO) {
        userInfoService.register(registerVO);
        return R.ok("注册成功");
    }
}
