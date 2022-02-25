package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface LendService extends IService<Lend> {

    /**
     * 创建标的
     * @param approvalVO
     * @param borrowInfo
     */
    void createLend(BorrowInfoApprovalVO approvalVO, BorrowInfo borrowInfo);

    /**
     * 查询标的
     * @return
     */
    List<Lend> getList();

    /**
     * 查询单个标的信息
     * @param id
     * @return
     */
    Map<String, Object> show(Long id);
}
