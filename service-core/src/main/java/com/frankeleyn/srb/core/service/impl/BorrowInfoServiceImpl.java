package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.BorrowInfoStatusEnum;
import com.frankeleyn.srb.core.mapper.BorrowInfoMapper;
import com.frankeleyn.srb.core.mapper.BorrowerMapper;
import com.frankeleyn.srb.core.mapper.IntegralGradeMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.entity.IntegralGrade;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.service.BorrowInfoService;
import com.frankeleyn.srb.core.service.BorrowerService;
import com.frankeleyn.srb.core.service.DictService;
import com.frankeleyn.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Autowired
    private LendService lendService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Autowired
    private BorrowerService borrowerService;

    @Autowired
    private DictService dictService;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void approval(BorrowInfoApprovalVO approvalVO) {
        // 更新借款信息状态
        Long borrowerInfoId = approvalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowerInfoId);
        borrowInfo.setStatus(approvalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        // 调用标的接口
        System.out.println("调用标的接口，生成标的");
        lendService.createLend(approvalVO, borrowInfo);
    }

    @Override
    public Map<String, Object> show(Long id) {
        // 查询借款信息
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        // 装配数据
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);

        // 查询借款人信息
        Borrower borrower = borrowerMapper.selectOne(new QueryWrapper<Borrower>().eq("user_id", borrowInfo.getUserId()));
        BorrowerDetailVO borrowerDetail = borrowerService.getBorrowerDetailVOById(borrower.getId());

        // 封装结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("borrower", borrowerDetail);
        resultMap.put("borrowInfo", borrowInfo);
        return resultMap;
    }

    @Override
    public List<BorrowInfo> getList() {

        List<BorrowInfo> borrowInfoList = baseMapper.selectList(null);
        borrowInfoList.forEach(borrowInfo -> {
            // 查询还款方式，资金用户信息
            UserInfo userInfo = userInfoMapper.selectById(borrowInfo.getUserId());
            String returnMethod = dictService.getNameByDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());

            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
            borrowInfo.setName(userInfo.getName());
            borrowInfo.setMobile(userInfo.getMobile());
        });

        return borrowInfoList;
    }

    @Override
    public Integer getBorrowInfoStatus(Long userId) {
        BorrowInfo borrowInfo = baseMapper.selectOne(new QueryWrapper<BorrowInfo>().eq("user_id", userId));

        if (Objects.isNull(borrowInfo)) {
            return 0;
        }

        return borrowInfo.getStatus();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        // 获取用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 装配 borrowInfo 信息
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setUserId(userId);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);

    }

    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        // 通过 userId 获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer integral = userInfo.getIntegral();

        // 根据用户积分获取用户额度
        QueryWrapper<IntegralGrade> integralQueryWrapper = new QueryWrapper<>();
        integralQueryWrapper.le("integral_start", integral)
                .gt("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralQueryWrapper);

        return integralGrade.getBorrowAmount();
    }

}
