package com.frankeleyn.srb.core.service.impl;

import com.frankeleyn.srb.core.pojo.entity.UserLoginRecord;
import com.frankeleyn.srb.core.mapper.UserLoginRecordMapper;
import com.frankeleyn.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

}
