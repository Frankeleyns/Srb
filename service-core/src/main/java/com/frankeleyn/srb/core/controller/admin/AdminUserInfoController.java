package com.frankeleyn.srb.core.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.query.UserInfoQuery;
import com.frankeleyn.srb.core.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PutMapping("lock/{id}/{status}")
    public R lock(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        userInfoService.lock(id, status);
        return R.ok();
    }

    @GetMapping("/list/{page}/{limit}")
    public R list(@PathVariable("page") Long currentPage, @PathVariable("limit") Long limit, UserInfoQuery userInfoQuery) {
        IPage<UserInfo> pageModel = userInfoService.lsitPage(currentPage, limit, userInfoQuery);
        return R.ok("pageModel", pageModel);
    }
}

