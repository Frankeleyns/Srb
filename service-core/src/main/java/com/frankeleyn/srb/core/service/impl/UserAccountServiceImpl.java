package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.TransTypeEnum;
import com.frankeleyn.srb.core.hfb.FormHelper;
import com.frankeleyn.srb.core.hfb.HfbConst;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.mapper.UserAccountMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.bo.TransFlowBO;
import com.frankeleyn.srb.core.pojo.entity.UserAccount;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.service.TransFlowService;
import com.frankeleyn.srb.core.service.UserAccountService;
import com.frankeleyn.srb.core.util.LendNoUtils;
import com.frankeleyn.srb.dto.SmsDTO;
import com.frankeleyn.srb.rabbitUtil.constant.MQConst;
import com.frankeleyn.srb.rabbitUtil.service.MqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Autowired
    private MqService mqService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Autowired
    private TransFlowService transFlowService;


    @Override
    public BigDecimal getAccount(Long userId) {
        UserAccount userAccount = baseMapper.selectOne(new QueryWrapper<UserAccount>().eq("user_id", userId));
        return userAccount.getAmount();
    }

    @Override
    public void updateAccount(Long userId, BigDecimal amount, BigDecimal freezeAmount) {
        UpdateWrapper<UserAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(updateWrapper);
        updateWrapper.set("amount", userAccount.getAmount().add(amount));
        updateWrapper.set("freeze_amount", userAccount.getFreezeAmount().add(freezeAmount));
        baseMapper.update(userAccount, updateWrapper);
    }


    @Override
    public void notified(Map<String, Object> notifiedMap) {
        String agentBillNo = (String) notifiedMap.get("agentBillNo"); // 订单号
        String bindCode = (String) notifiedMap.get("bindCode"); // 充值人绑定协议号
        String chargeAmt = (String) notifiedMap.get("chargeAmt"); // 充值金额

        // 更新账户信息
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("bind_code", bindCode));
        updateAccount(userInfo.getId(), new BigDecimal(chargeAmt), new BigDecimal("0"));

        // 新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO(agentBillNo, bindCode, new BigDecimal(chargeAmt), TransTypeEnum.RECHARGE, "充值到账");
        transFlowService.saveTransFlow(transFlowBO);

        // 通过 MQ 调用短信系统，发送充值成功通知
        SmsDTO smsDTO = new SmsDTO(userInfo.getMobile(), "充值成功");
        mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);
    }


    @Override
    public String recharge(BigDecimal chargeAmt, Long userId) {

        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentBillNo", LendNoUtils.getChargeNo()); // 充值单号
        params.put("bindCode", bindCode);
        params.put("chargeAmt", chargeAmt);
        params.put("feeAmt", new BigDecimal("0"));
        params.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL); // 检查常量是否正确
        params.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(params);
        params.put("sign", sign);

        // 调用汇付宝接口生成充值表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, params);
        return formStr;
    }

}
