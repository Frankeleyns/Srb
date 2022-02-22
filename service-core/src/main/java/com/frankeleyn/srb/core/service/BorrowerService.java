package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.vo.BorrowerApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface BorrowerService extends IService<Borrower> {

    /**
     * 根据用户 id 获取额度审核状态
     * @param userId
     * @return
     */
    Integer getBorrowerStatus(Long userId);

    /**
     * 保存借款额度申请
     * @param borrowerVO
     */
    void saveBorrower(BorrowerVO borrowerVO, Long userId);

    /**
     * 分页查询借款人
     * @param pageParam
     * @param keyword
     * @return
     */
    Page<Borrower> getList(Page<Borrower> pageParam, String keyword);

    /**
     * 查询单个借款人的审核信息
     * @param id
     * @return
     */
    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    /**
     * 审核
     * @param approvalVO
     */
    void approval(BorrowerApprovalVO approvalVO);

}
