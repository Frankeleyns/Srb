package com.frankeleyn.srb.core.service;

import com.frankeleyn.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface DictService extends IService<Dict> {

    boolean importData(MultipartFile file);
}
