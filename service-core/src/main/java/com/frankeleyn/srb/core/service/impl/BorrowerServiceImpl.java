package com.frankeleyn.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frankeleyn.srb.core.enums.BorrowerStatusEnum;
import com.frankeleyn.srb.core.enums.IntegralEnum;
import com.frankeleyn.srb.core.mapper.BorrowerAttachMapper;
import com.frankeleyn.srb.core.mapper.BorrowerMapper;
import com.frankeleyn.srb.core.mapper.UserInfoMapper;
import com.frankeleyn.srb.core.mapper.UserIntegralMapper;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.entity.UserIntegral;
import com.frankeleyn.srb.core.pojo.vo.BorrowerApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerAttachVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerVO;
import com.frankeleyn.srb.core.service.BorrowerAttachService;
import com.frankeleyn.srb.core.service.BorrowerService;
import com.frankeleyn.srb.core.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
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
    private UserIntegralMapper userIntegralMapper;

    @Autowired
    private DictService dictService;

    @Autowired
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void approval(BorrowerApprovalVO approvalVO) {

        Borrower borrower = baseMapper.selectById(approvalVO.getBorrowerId());
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 保存 user_integral
        Boolean isHouseOk = approvalVO.getIsHouseOk();
        Boolean isCarOk = approvalVO.getIsCarOk();
        Boolean isIdCardOk = approvalVO.getIsIdCardOk();
        Integer infoIntegral = approvalVO.getInfoIntegral();

        // 总积分
        int countIntegral = userInfo.getIntegral();

        // 基本积分
        countIntegral += infoIntegral;
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(infoIntegral);
        userIntegral.setContent("基本积分");
        userIntegralMapper.insert(userIntegral);

        // 房子积分
        if(isHouseOk) {
            countIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
            UserIntegral userIntegralHouse = new UserIntegral();
            userIntegralHouse.setUserId(userId);
            userIntegralHouse.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegralHouse.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegralHouse);
        }

        // 车子积分
        if(isCarOk) {
            countIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
            UserIntegral userIntegralCar = new UserIntegral();
            userIntegralCar.setUserId(userId);
            userIntegralCar.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegralCar.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegralCar);
        }

        // 身份证积分
        if(isIdCardOk) {
            countIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
            UserIntegral userIntegralIdCard = new UserIntegral();
            userIntegralIdCard.setUserId(userId);
            userIntegralIdCard.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegralIdCard.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegralIdCard);
        }

        // 修改 user_info
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_OK.getStatus());
        userInfo.setIntegral(countIntegral);
        userInfo.setBorrowAuthStatus(approvalVO.getStatus());
        userInfoMapper.updateById(userInfo);

        // 修改 审核状态
        borrower.setStatus(approvalVO.getStatus());
        baseMapper.updateById(borrower);
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        BorrowerDetailVO detailVO = new BorrowerDetailVO();

        // 填充借款人信息
        Borrower borrower = baseMapper.selectById(id);
        BeanUtils.copyProperties(borrower, detailVO);
        detailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus())); // 状态
        detailVO.setSex(borrower.getSex() == 1? "男" : "女"); // 性别
        detailVO.setMarry(borrower.getMarry() ? "已婚" : "未婚");

        // 填充字典信息
        String industry = dictService.getNameByDictCodeAndValue("industry", borrower.getIndustry());
        String income = dictService.getNameByDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByDictCodeAndValue("returnSource", borrower.getReturnSource());
        String relation = dictService.getNameByDictCodeAndValue("relation", borrower.getContactsRelation());
        String education = dictService.getNameByDictCodeAndValue("education", borrower.getEducation());

        detailVO.setEducation(education);
        detailVO.setIndustry(industry);
        detailVO.setIncome(income);
        detailVO.setReturnSource(returnSource);
        detailVO.setContactsRelation(relation);

        // 查询附件列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        detailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return detailVO;
    }

    @Override
    public Page<Borrower> getList(Page<Borrower> pageParam, String keyword) {

        if (StringUtils.isEmpty(keyword)) {
            return baseMapper.selectPage(pageParam, null);
        }

        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword)
                .orderByDesc("id");

        return baseMapper.selectPage(pageParam, queryWrapper);
    }

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
