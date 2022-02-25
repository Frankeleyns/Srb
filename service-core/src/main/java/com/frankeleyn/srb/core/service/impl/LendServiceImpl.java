package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.LendStatusEnum;
import com.frankeleyn.srb.core.mapper.BorrowerMapper;
import com.frankeleyn.srb.core.mapper.LendMapper;
import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.service.BorrowerService;
import com.frankeleyn.srb.core.service.DictService;
import com.frankeleyn.srb.core.service.LendService;
import com.frankeleyn.srb.core.util.LendNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Autowired
    private BorrowerService borrowerService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private DictService dictService;

    @Override
    public Map<String, Object> show(Long id) {
        Map<String, Object> resultMap = new HashMap<>();

        // 查询 lend
        Lend lend = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByDictCodeAndValue("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod", returnMethod);
        lend.getParam().put("status", status);

        // 查询 borrower
        Long userId = lend.getUserId();
        Borrower borrower = borrowerMapper.selectOne(new QueryWrapper<Borrower>().eq("user_id", userId));
        BorrowerDetailVO borrowerDetail = borrowerService.getBorrowerDetailVOById(borrower.getId());

        resultMap.put("borrower", borrowerDetail);
        resultMap.put("lend", lend);
        return resultMap;
    }

    @Override
    public List<Lend> getList() {
        List<Lend> lendList = baseMapper.selectList(null);

        lendList.forEach(lend -> {
            String returnMethod = dictService.getNameByDictCodeAndValue("returnMethod", lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            lend.getParam().put("returnMethod", returnMethod);
            lend.getParam().put("status", status);
        });

        return lendList;
    }

    @Override
    public void createLend(BorrowInfoApprovalVO approvalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();

        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus()); // 标的状态
        lend.setRealAmount(new BigDecimal(0));  // 标的实际募资
        lend.setInvestAmount(new BigDecimal(0)); // 投资金额
        lend.setInvestNum(0);   // 投资人数
        lend.setReturnMethod(borrowInfo.getReturnMethod()); // 还款方式
        lend.setTitle(approvalVO.getTitle()); // 标的标题
        lend.setLendInfo(approvalVO.getLendInfo()); // 标的信息
        lend.setUserId(borrowInfo.getUserId());
        lend.setPublishDate(LocalDateTime.now()); // 标的发布时间
        LocalDate startDate = LocalDate.parse(approvalVO.getLendStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lend.setLendStartDate(startDate); // 标的起息时间
        LocalDate endDate = startDate.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(endDate); // 标的结束日期 = 起息日期 + 借款期数
        lend.setPeriod(borrowInfo.getPeriod()); // 标的的期数
        lend.setLowestAmount(new BigDecimal(100)); // 最低投资金额
        lend.setLendYearRate(borrowInfo.getBorrowYearRate()); // 投资年利率
        lend.setServiceRate(approvalVO.getServiceRate().divide(new BigDecimal(100))); // 服务费率
        lend.setAmount(borrowInfo.getAmount()); // 标的总额
        lend.setLendNo(LendNoUtils.getLendNo()); // 标的单号
        lend.setCreateTime(LocalDateTime.now());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);
        lend.setBorrowInfoId(borrowInfo.getId());

        // 平台收益 = 月服务费率 * 标的总额 * 期数
        BigDecimal serviceMonthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_HALF_DOWN);
        BigDecimal expectAmount = lend.getAmount().multiply(serviceMonthRate).multiply(new BigDecimal(borrowInfo.getPeriod()));
        lend.setExpectAmount(expectAmount);

        baseMapper.insert(lend);
    }

}
