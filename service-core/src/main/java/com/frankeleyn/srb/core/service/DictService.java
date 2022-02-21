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

    // 导入数据
    boolean importData(MultipartFile file);

    // 将 Dict 转为 ExcelDictDTO
    List<ExcelDictDTO> listDictData();

    // 根据父 id 查询子节点数据列表
    List<Dict> listByParentId(Long parentId);

    /**
     * 根据 dictCode 查找 dict
     * @param dictCode
     * @return
     */
    List<Dict> findByDictCode(String dictCode);
}
