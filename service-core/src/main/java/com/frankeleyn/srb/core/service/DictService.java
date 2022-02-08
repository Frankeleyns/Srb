package com.frankeleyn.srb.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.pojo.entity.Dict;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    List<ExcelDictDTO> listDictData();
}
