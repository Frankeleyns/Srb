package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.TransTypeEnum;
import com.frankeleyn.srb.core.hfb.FormHelper;
import com.frankeleyn.srb.core.hfb.HfbConst;
import com.frankeleyn.srb.core.hfb.RequestHelper;
import com.frankeleyn.srb.core.mapper.LendItemMapper;
import com.frankeleyn.srb.core.mapper.LendMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.pojo.bo.TransFlowBO;
import com.frankeleyn.srb.core.pojo.entity.Lend;
import com.frankeleyn.srb.core.pojo.entity.LendItem;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.vo.InvestVO;
import com.frankeleyn.srb.core.service.LendItemService;
import com.frankeleyn.srb.core.service.LendService;
import com.frankeleyn.srb.core.service.TransFlowService;
import com.frankeleyn.srb.core.service.UserAccountService;
import com.frankeleyn.srb.core.util.LendNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private TransFlowService transFlowService;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Autowired
    private LendService lendService;

    @Resource
    private LendMapper lendMapper;


    @Override
    public void notified(Map<String, Object> paramMap) {
        // 获得参数
        String agentBillNo = (String)paramMap.get("agentBillNo");// 获取投资编号
        String bindCode = (String)paramMap.get("voteBindCode");// 获取用户的绑定协议号
        String voteAmt = (String)paramMap.get("voteAmt");

        // 1 更新投资人信息 (余额和冻结金额)
        UserInfo investors = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("bind_code", bindCode));
        userAccountService.updateAccount(investors.getId(), new BigDecimal(voteAmt).negate(), new BigDecimal(voteAmt));

        // 2 更新投标状态
        LendItem lendItem = baseMapper.selectOne(new QueryWrapper<LendItem>().eq("lend_item_no", agentBillNo));
        lendItem.setStatus(1);
        baseMapper.updateById(lendItem);

        // 3 更新标的 (已投金额和投资人数)
        Lend lend = lendMapper.selectById(lendItem.getLendId());
        lend.setInvestNum(lend.getInvestNum() + 1);
        lend.setInvestAmount(lend.getInvestAmount().add(new BigDecimal(voteAmt)));
        lendMapper.updateById(lend);

        // 4 新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(voteAmt),
                TransTypeEnum.INVEST_LOCK,
                "投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);
    }

    @Override
    public String commitInvest(InvestVO investVO) {
        Long lendId = investVO.getLendId();
        Lend lend = lendMapper.selectById(lendId);

        // 获取投资人和借款人信息
        UserInfo investors = userInfoMapper.selectById(investVO.getInvestUserId());
        UserInfo borrower = userInfoMapper.selectById(lend.getUserId());

        // 保存投标信息
        LendItem lendItem = new LendItem();
        lendItem.setLendId(lendId);
        // 投资单号
        String lendItemNo = LendNoUtils.getLendItemNo();
        lendItem.setLendItemNo(lendItemNo);
        lendItem.setLendId(lendId);// 对应的标的 id
        lendItem.setLendYearRate(lend.getLendYearRate());// 年化
        lendItem.setLendStartDate(lend.getLendStartDate());// 开始时间
        lendItem.setLendEndDate(lend.getLendEndDate());// 结束时间
        lendItem.setStatus(0);// 状态，默认为 0
        lendItem.setInvestUserId(investVO.getInvestUserId());// 投资人 id
        lendItem.setInvestName(investors.getName());// 投资人姓名
        lendItem.setInvestTime(LocalDateTime.now());// 投资时间
        lendItem.setInvestAmount(new BigDecimal(investVO.getInvestAmount()));// 投资金额
        lendItem.setCreateTime(LocalDateTime.now());// 创建时间
        lendItem.setRealAmount(new BigDecimal("0"));// 实际收益
        // 投资人预期收益
        BigDecimal expectAmount = lendService.getInterestCount(lendItem.getInvestAmount(),
                lendItem.getLendYearRate(),
                lend.getPeriod(),
                lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount);
        baseMapper.insert(lendItem);

        // 生成汇付宝表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("voteBindCode", investors.getBindCode());// 投资人 bindCode
        paramMap.put("benefitBindCode",borrower.getBindCode());// 借款人 bindCode
        paramMap.put("agentProjectCode", lend.getLendNo());// 项目标号
        paramMap.put("agentProjectName", lend.getTitle());

        //在资金托管平台上的投资订单的唯一编号，要和lendItemNo保持一致。
        paramMap.put("agentBillNo", lendItemNo);// 订单编号
        paramMap.put("voteAmt", investVO.getInvestAmount());
        paramMap.put("votePrizeAmt", "0");
        paramMap.put("voteFeeAmt", "0");
        paramMap.put("projectAmt", lend.getAmount()); // 标的总金额
        paramMap.put("note", "");
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL); // 检查常量是否正确
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        String formStr = FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
        return formStr;
    }

}
