package com.frankeleyn.srb.core.controller.api;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.pojo.vo.UserBindVO;
import com.frankeleyn.srb.core.service.UserBindService;
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
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/api/core/userBind")
public class ApiUserBindController {

        @Autowired
        private UserBindService userBindService;

        @PostMapping("/notify")
        public String notified(HttpServletRequest request) {
            Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
            // 验签
            boolean signEquals = RequestHelper.isSignEquals(paramMap);
            Assert.isTrue(signEquals, ResponseEnum.WEIXIN_CALLBACK_PARAM_ERROR);

            userBindService.notified(paramMap);
            return "success";
        }

        @PostMapping("/auth/bind")
        public R userBind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {
            String token = request.getHeader("token");
            Assert.notNull(token, ResponseEnum.LOGIN_AUTH_ERROR);
            Long userId = JwtUtils.getUserId(token);

            // 调用汇付宝 api 接口，生成汇付宝表单
            String formStr = userBindService.bind(userBindVO, userId);
            return R.ok("formStr", formStr);
        }
}

