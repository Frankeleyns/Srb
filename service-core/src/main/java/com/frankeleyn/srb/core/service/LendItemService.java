package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.entity.LendItem;
import com.frankeleyn.srb.core.pojo.vo.InvestVO;

import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface LendItemService extends IService<LendItem> {

    /**
     * 提交投资
     * @param investVO
     * @return
     */
    String commitInvest(InvestVO investVO);

    /**
     * 回调
     * @param paramMap
     */
    void notified(Map<String, Object> paramMap);
}
