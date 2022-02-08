package com.frankeleyn.srb.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.pojo.entity.Dict;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
public interface DictMapper extends BaseMapper<Dict> {
    // 批量插入
    void insertBatch(List<ExcelDictDTO> list);
}
