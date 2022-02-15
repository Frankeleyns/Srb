package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frankeleyn.srb.core.pojo.entity.UserLoginRecord;
import com.frankeleyn.srb.core.mapper.UserLoginRecordMapper;
import com.frankeleyn.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<UserLoginRecord> getUserLoginRecordTop50(Integer id) {

        QueryWrapper<UserLoginRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id).last("limit 50");
        queryWrapper.orderByDesc("create_time");

        List<UserLoginRecord> recordList = baseMapper.selectList(queryWrapper);

        return recordList;
    }
}
