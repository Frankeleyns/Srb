package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.UserBindEnum;
import com.frankeleyn.srb.core.hfb.FormHelper;
import com.frankeleyn.srb.core.hfb.HfbConst;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.mapper.UserBindMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.entity.UserBind;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.vo.UserBindVO;
import com.frankeleyn.srb.core.service.UserBindService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void notified(Map<String, Object> paramMap) {
        // 获取参数
        String bind_code = (String) paramMap.get("bindCode");
        String userId = (String) paramMap.get("agentUserId");

        // 1. 更新用户的绑定信息 Table: user_bind  Field: bind_code|status
        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>().eq("user_id", userId));
        userBind.setBindCode(bind_code);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        // 2. 更新用户的信息 Table: user_info  Field: name|nick_name|id_card|bind_status
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setName(userBind.getName());
        userInfo.setNickName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setBindCode(bind_code);
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public String bind(UserBindVO userBindVO, Long userId) {

        // 1. 将用户账户信息保存到 srb
        UserBind userBind = baseMapper.selectOne(new QueryWrapper<UserBind>().eq("user_id", userId));
        if (Objects.nonNull(userBind)) {
            // 用户之前绑定过
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        } else {
            // 用户之前没绑定过
            userBind = new UserBind();
            userBind.setUserId(userId);
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.insert(userBind);
        }

        // 2. 根据提交的参数和 api 生成汇付宝表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);
        return formStr;
    }
}
