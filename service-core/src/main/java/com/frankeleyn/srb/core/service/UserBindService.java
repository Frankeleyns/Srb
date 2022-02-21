package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.entity.UserBind;
import com.frankeleyn.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface UserBindService extends IService<UserBind> {

    /**
     * 回调接口
     * @param paramMap
     */
    void notified(Map<String, Object> paramMap);

    /**
     * 账户绑定提交数据到托管平台
     * @param userBindVO
     * @param userId
     * @return
     */
    String bind(UserBindVO userBindVO, Long userId);
}
