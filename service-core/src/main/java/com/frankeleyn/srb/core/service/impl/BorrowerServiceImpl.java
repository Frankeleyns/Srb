package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.BorrowerStatusEnum;
import com.frankeleyn.srb.core.mapper.BorrowerAttachMapper;
import com.frankeleyn.srb.core.mapper.BorrowerMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.vo.BorrowerVO;
import com.frankeleyn.srb.core.service.BorrowerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional
    public void saveBorrower(BorrowerVO borrowerVO, Long userId) {

        // 查询 userInfo 信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 1. 保存 borrowers
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        // 2. 获得 borrow 主键
        Long borrowerId = borrower.getId();

        // 3. 根据 borrowerId 保存附件到 BorrowerAttach
        borrowerVO.getBorrowerAttachList().forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrowerId);
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 4. 修改 user_info 的 borrower_auth_status
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public Integer getBorrowerStatus(Long userId) {
        Borrower borrower = baseMapper.selectOne(new QueryWrapper<Borrower>().eq("user_id", userId));

        // 用户未提交过额度申请
        if(Objects.isNull(borrower)) {
            return 0;
        }

        return borrower.getStatus();
    }

}
