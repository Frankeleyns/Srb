package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.common.util.MD5;
import com.frankeleyn.srb.core.mapper.UserAccountMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.mapper.UserLoginRecordMapper;
import com.frankeleyn.srb.core.pojo.entity.UserAccount;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.entity.UserLoginRecord;
import com.frankeleyn.srb.core.pojo.query.UserInfoQuery;
import com.frankeleyn.srb.core.pojo.vo.LoginVO;
import com.frankeleyn.srb.core.pojo.vo.RegisterVO;
import com.frankeleyn.srb.core.pojo.vo.UserInfoVO;
import com.frankeleyn.srb.core.service.UserInfoService;
import com.frankeleyn.srb.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountMapper userAccountMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;

    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);

        baseMapper.updateById(userInfo);
    }

    @Override
    public Page<UserInfo> lsitPage(Long currentPage, Long limit, UserInfoQuery userInfoQuery) {

        // 查询条件
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(userInfoQuery.getMobile()),"mobile", userInfoQuery.getMobile())
                    .eq(null != userInfoQuery.getUserType(),"user_type", userInfoQuery.getUserType())
                    .eq(null != userInfoQuery.getStatus(), "status", userInfoQuery.getStatus());

        // 分页查询
        Page<UserInfo> userInfoPage = new Page<>();
        userInfoPage.setSize(limit);
        userInfoPage.setPages(currentPage);
        Page<UserInfo> page = baseMapper.selectPage(userInfoPage, queryWrapper);

        return page;
    }

    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = MD5.encrypt(loginVO.getPassword());

        // 用户是否存在
        UserInfo userInfo = baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("mobile", mobile));
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);

        // 密码是否正确
        Assert.equals(password, userInfo.getPassword(), ResponseEnum.LOGIN_PASSWORD_ERROR);

        // 用户是否被锁定
        Assert.equals(userInfo.getStatus(), 1, ResponseEnum.LOGIN_LOKED_ERROR);

        // 生成 Token，保存 userInfoVO 信息
        UserInfoVO userInfoVO = new UserInfoVO();
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        userInfoVO.setToken(token);
        BeanUtils.copyProperties(userInfo, userInfoVO);

        // 记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        return userInfoVO;
    }

    @Override
    public void register(RegisterVO registerVO) {

        // 校验用户是否已被注册
        Integer mobile = baseMapper.selectCount(new QueryWrapper<UserInfo>().eq("mobile", registerVO.getMobile()));
        Assert.isTrue(mobile == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        // 校验验证码
        String code = registerVO.getCode();
        String codeFromCache = (String) redisTemplate.opsForValue().get("srb:sms:code:" + registerVO.getMobile());
        Assert.notNull(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.equals(code, codeFromCache, ResponseEnum.CODE_ERROR);

        // 保存用户信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(registerVO, userInfo);
        userInfo.setNickName(registerVO.getMobile());
        userInfo.setName(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        baseMapper.insert(userInfo);

        // 保存用户账号信息
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);

    }

}
