package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    /**
     * 根据用户 id 获取借款金额
     * @param userId
     * @return
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * 提交借款申请
     * @param borrowInfo
     * @param userId
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 根据 userId 查询借款状态
     * @param userId
     * @return
     */
    Integer getBorrowInfoStatus(Long userId);

    /**
     * 查询所有借款信息
     * @return
     */
    List<BorrowInfo> getList();

    /**
     * 查询借款信息
     * @param id
     * @return
     */
    Map<String, Object> show(Long id);

    /**
     * 借款审核
     * @param approvalVO
     */
    void approval(BorrowInfoApprovalVO approvalVO);
}
