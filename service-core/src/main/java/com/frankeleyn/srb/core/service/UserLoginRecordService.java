package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.entity.UserLoginRecord;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    List<UserLoginRecord> getUserLoginRecordTop50(Integer userId);

}
