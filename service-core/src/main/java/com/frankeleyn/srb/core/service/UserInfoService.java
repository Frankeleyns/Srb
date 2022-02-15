package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.entity.UserInfo;
import com.frankeleyn.srb.core.pojo.query.UserInfoQuery;
import com.frankeleyn.srb.core.pojo.vo.LoginVO;
import com.frankeleyn.srb.core.pojo.vo.RegisterVO;
import com.frankeleyn.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface UserInfoService extends IService<UserInfo> {
    /**
     * 注册
     * @param registerVO
     */
    void register(RegisterVO registerVO);

    /**
     * 登录
     * @param loginVO
     * @param ip
     */
    UserInfoVO login(LoginVO loginVO, String ip);

    /**
     * 分页查询
     * @param currentPage
     * @param limit
     * @param userInfoQuery
     * @return
     */
    Page<UserInfo> lsitPage(Long currentPage, Long limit, UserInfoQuery userInfoQuery);

    /**
     * 会员锁定
     * @param id
     * @param status
     */
    void lock(Long id, Integer status);
}
