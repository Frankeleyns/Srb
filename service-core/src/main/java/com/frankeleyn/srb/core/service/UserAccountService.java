package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     * 充值
     * @param chargeAmt
     * @param userId
     * @return
     */
    String recharge(BigDecimal chargeAmt, Long userId);

    /**
     * 回调
     * @param notifiedMap
     */
    void notified(Map<String, Object> notifiedMap);

    /**
     * 获取账户余额
     * @param userId
     * @return
     */
    BigDecimal getAccount(Long userId);
}
