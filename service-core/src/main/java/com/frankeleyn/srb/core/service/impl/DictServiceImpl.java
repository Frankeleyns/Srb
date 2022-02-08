package com.frankeleyn.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.frankeleyn.srb.core.listener.ExcelDictDTOListener;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.pojo.entity.Dict;
import com.frankeleyn.srb.core.mapper.DictMapper;
import com.frankeleyn.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public boolean importData(MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(), ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet("数据字典").doRead();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
