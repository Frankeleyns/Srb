package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.UserLoginRecord;
import com.frankeleyn.srb.core.service.UserLoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Autowired
    private UserLoginRecordService userLoginRecordService;

    @GetMapping("/getUserLoginRecordTop50/{id}")
    public R getUserLoginRecordTop50(@PathVariable("id") Integer userId) {
        List<UserLoginRecord> recordList = userLoginRecordService.getUserLoginRecordTop50(userId);
        return R.ok("list", recordList);
    }
}

