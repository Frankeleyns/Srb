package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
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
     * 保存接口额度申请
     * @param borrowerVO
     */
    void saveBorrower(BorrowerVO borrowerVO, Long userId);
}
