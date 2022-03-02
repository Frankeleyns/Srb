package com.frankeleyn.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.enums.LendStatusEnum;
import com.frankeleyn.srb.core.enums.ReturnMethodEnum;
import com.frankeleyn.srb.core.enums.TransTypeEnum;
import com.frankeleyn.srb.core.hfb.HfbConst;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.mapper.*;
import com.frankeleyn.srb.core.pojo.bo.TransFlowBO;
import com.frankeleyn.srb.core.pojo.entity.*;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.service.*;
import com.frankeleyn.srb.core.util.*;
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

    @Resource
    private LendItemReturnMapper lendItemReturnMapper;

    @Resource
    private LendReturnMapper lendReturnMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Autowired
    private TransFlowService transFlowService;
    
    @Autowired
    private UserAccountService userAccountService;

    @Resource
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private BorrowerService borrowerService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private DictService dictService;

    // 回款计划
    public Map<String,BigDecimal> returnInvest(Lend lend, LendItem lendItem, LendReturn lendReturn, Integer currentPeriod) {
        Map<String, BigDecimal> itemReturnMap = new HashMap<>();
        LendItemReturn lendItemReturn = new LendItemReturn();

        // 计算当前还款期，lendItem 收到的本金和利息
        Integer r = lend.getReturnMethod();

        Map<Integer, BigDecimal> perMonthPrincipal = new HashMap<>();
        Map<Integer, BigDecimal> perMonthInterest = new HashMap<>();

        if (r == ReturnMethodEnum.ONE.getMethod()) {
            // 等额本息
            perMonthPrincipal = Amount1Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount1Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }

        if (r == ReturnMethodEnum.TWO.getMethod()) {
            // 等额本金
            perMonthPrincipal = Amount2Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount2Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }

        if (r == ReturnMethodEnum.THREE.getMethod()) {
            // 按期付息
            perMonthPrincipal = Amount3Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount3Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }

        if (r == ReturnMethodEnum.FOUR.getMethod()) {
            // 还本付息
            perMonthPrincipal = Amount4Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount4Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }

        lendItemReturn.setLendId(lend.getId());
        lendItemReturn.setLendItemId(lendItem.getId());
        lendItemReturn.setLendReturnId(lendReturn.getId());
        lendItemReturn.setCreateTime(LocalDateTime.now());
        lendItemReturn.setCurrentPeriod(currentPeriod);
        lendItemReturn.setFee(new BigDecimal(0));
        lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
        lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
        lendItemReturn.setReturnMethod(lend.getReturnMethod());
        lendItemReturn.setLendYearRate(lend.getLendYearRate());
        lendItemReturn.setOverdue(false);
        lendItemReturn.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lendItemReturn.setPrincipal(perMonthPrincipal.get(currentPeriod));
        lendItemReturn.setInterest(perMonthInterest.get(currentPeriod));
        lendItemReturn.setTotal(perMonthPrincipal.get(currentPeriod).add(perMonthInterest.get(currentPeriod)));
        lendItemReturnMapper.insert(lendItemReturn);

        itemReturnMap.put("itemReturnPrincipal", perMonthPrincipal.get(currentPeriod));
        itemReturnMap.put("itemReturnInterest", perMonthInterest.get(currentPeriod));
        return itemReturnMap;
    }

    // 还款计划
    public void repaymentPlan(Lend lend, List<LendItem> lendItemList) {
        int period = lend.getPeriod();
        for (int i = 1; i < period + 1; i++) {
            // 根据借贷期数生成还款计划
            LendReturn lendReturn = new LendReturn();
            lendReturn.setCurrentPeriod(i);// 当前期数
            lendReturn.setFee(new BigDecimal("0"));// 手续费
            lendReturn.setLendId(lend.getId());
            lendReturn.setOverdue(false);// 是否逾期
            lendReturn.setLendYearRate(lend.getLendYearRate());// 年华
            lendReturn.setStatus(LendStatusEnum.PAY_RUN.getStatus());
            lendReturn.setAmount(lend.getAmount());// 借款总额
            lendReturn.setBaseAmount(lend.getInvestAmount());// 金额
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setCreateTime(LocalDateTime.now());
            lendReturn.setReturnMethod(lend.getReturnMethod());// 还款方式
            lendReturn.setUserId(lend.getUserId());
            // lendReturn.setLast(null);
            lendReturnMapper.insert(lendReturn);

            BigDecimal returnPrincipal = new BigDecimal("0");
            BigDecimal returnInterest = new BigDecimal("0");

            for(LendItem lendItem : lendItemList){
                System.out.println("  每循环一个投资人，生成一个回款计划");
                // 返回当前还款日期的本金和利息
                Map<String,BigDecimal> itemReturnMap = returnInvest(lend, lendItem, lendReturn, i);

                returnPrincipal = returnPrincipal.add(itemReturnMap.get("itemReturnPrincipal"));
                returnInterest = returnInterest.add(itemReturnMap.get("itemReturnInterest"));
            }

            lendReturn.setTotal(returnPrincipal.add(returnInterest));// 本期还款总额 = 本期还款本金 + 本期还款本息
            lendReturn.setPrincipal(returnPrincipal); // 本期本金
            lendReturn.setInterest(returnInterest); // 本期还款中包含的利息
            lendReturnMapper.updateById(lendReturn); // 更新回款和
        }

    }

    @Override
    public void makeLoan(Long id) {
        Lend lend = baseMapper.selectById(id);
        
        // 1 调用汇付宝接口
        Map<String, Object> hfbParam = new HashMap<>();
        hfbParam.put("agentId", HfbConst.AGENT_ID);// 商户ID
        hfbParam.put("agentProjectCode", lend.getLendNo());// 标的编号
        String agentBillNo = LendNoUtils.getLoanNo();// 放款编号
        hfbParam.put("agentBillNo", agentBillNo);
        // 月服务费率
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_DOWN);
        //平台收益 = 已投金额 * 月服务费率 * 标的期数
        BigDecimal serviceAmount = lend.getInvestAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        hfbParam.put("mchFee", serviceAmount);// 商户手续费(平台收益)
        hfbParam.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(hfbParam);
        hfbParam.put("sign", sign);// 签名

        JSONObject jsonObject = RequestHelper.sendRequest(hfbParam, HfbConst.MAKE_LOAD_URL);
        String resultCode = jsonObject.getString("resultCode");
        Assert.isTrue(resultCode == "0000", ResponseEnum.MAKE_LOAN_ERROR);

        // 2 更新标的状态
        String resultMchFee = jsonObject.getString("mchFee");
        lend.setRealAmount(new BigDecimal(resultMchFee));
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);
        lend.setPaymentTime(LocalDateTime.now());
        lend.setPaymentAdminId(1L);
        baseMapper.updateById(lend);
        
        // 3 根据接口返回结果，更新借款人和投资人的账户信息，生成流水
        // 借款人账户转入金额
        BigDecimal voteAmt = new BigDecimal(jsonObject.getString("voteAmt"));
        Long borrowerUserId = lend.getUserId();
        userAccountService.updateAccount(borrowerUserId, voteAmt, new BigDecimal("0"));
        //新增借款人交易流水
        UserInfo borrower = userInfoMapper.selectById(borrowerUserId);
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                borrower.getBindCode(),
                voteAmt,
                TransTypeEnum.BORROW_BACK,
                "借款放款到账，编号：" + lend.getLendNo());//项目编号
        transFlowService.saveTransFlow(transFlowBO);
        // 更新投资人账户信息, 生成流水
        List<LendItem> investList = lendItemMapper.selectList(new QueryWrapper<LendItem>().eq("lend_id", lend.getId()));
        investList.forEach(lendItem -> {
            // 投资人账户扣钱
            Long investUserId = lendItem.getInvestUserId();
            BigDecimal investAmount = lendItem.getInvestAmount();// 投资金额
            userAccountService.updateAccount(investUserId, new BigDecimal("0"), investAmount.negate());

            // 生成投资人交易流水
            UserInfo investInfo = userInfoMapper.selectById(investUserId);
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getTransNo(),
                    investInfo.getBindCode(),
                    lendItem.getInvestAmount(),
                    TransTypeEnum.INVEST_UNLOCK,
                    "冻结资金转出，出借放款，编号：" + lend.getLendNo());//项目编号
            transFlowService.saveTransFlow(investTransFlowBO);
        });

        // 4 新增还款计划和回款计划
        repaymentPlan(lend, investList);

        System.out.println("放款结束");
    }

    @Override
    public BigDecimal getInterestCount(BigDecimal investAmount, BigDecimal lendYearRate, Integer period, Integer returnMethod) {

        BigDecimal interestCount = new BigDecimal("0");

        int r = returnMethod.intValue();

        if (r == ReturnMethodEnum.ONE.getMethod()) {
            // 等额本息
            interestCount = Amount1Helper.getInterestCount(investAmount, lendYearRate, period);
        }

        if (r == ReturnMethodEnum.TWO.getMethod()) {
            // 等额本金
            interestCount = Amount2Helper.getInterestCount(investAmount, lendYearRate, period);
        }

        if (r == ReturnMethodEnum.THREE.getMethod()) {
            // 按期付息
            interestCount = Amount3Helper.getInterestCount(investAmount, lendYearRate, period);
        }

        if (r == ReturnMethodEnum.FOUR.getMethod()) {
            // 还本付息
            interestCount = Amount4Helper.getInterestCount(investAmount, lendYearRate, period);
        }

        return interestCount;
    }

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
