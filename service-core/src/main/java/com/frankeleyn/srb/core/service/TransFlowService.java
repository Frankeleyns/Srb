package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.bo.TransFlowBO;
import com.frankeleyn.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface TransFlowService extends IService<TransFlow> {

    /**
     * 保存流水
     * @param transFlowBO
     */
    void saveTransFlow(TransFlowBO transFlowBO);

    /**
     * 查询是否交易已存在
     * @param agentBillNo
     * @return
     */
    boolean isSaveTransFlow(String agentBillNo);
}
