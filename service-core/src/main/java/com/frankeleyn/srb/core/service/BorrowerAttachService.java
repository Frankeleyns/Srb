package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    /**
     * 根据 BorrowerId 查询 用户上传附件
     * @param id
     * @return
     */
    List<BorrowerAttachVO> selectBorrowerAttachVOList(Long id);
}
