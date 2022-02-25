package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.mapper.TransFlowMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.bo.TransFlowBO;
import com.frankeleyn.srb.core.pojo.entity.TransFlow;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.service.TransFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("bind_code", transFlowBO.getBindCode()));

        TransFlow transFlow = new TransFlow();
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        transFlow.setTransNo(transFlowBO.getAgentBillNo()); // 流水号
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());
        baseMapper.insert(transFlow);
    }

    @Override
    public boolean isSaveTransFlow(String agentBillNo) {
        TransFlow transFlow = baseMapper.selectOne(new QueryWrapper<TransFlow>().eq("trans_no", agentBillNo));
        if (Objects.nonNull(transFlow)) {
            return true;
        }

        return false;
    }
}
