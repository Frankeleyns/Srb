package com.frankeleyn.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.frankeleyn.srb.core.mapper.DictMapper;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frankeleyn
 * @date 2022/2/8 9:40
 */
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

    private DictMapper dictMapper;

    private static final long BATCH_COUNT = 10;

    List<ExcelDictDTO> dtoList = new ArrayList<>();

    // 通过构造器传入 dictMapper
    public ExcelDictDTOListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        System.out.println("读取到一条数据 " + data.getName());
        dtoList.add(data);
        // 达到设定的阈值，就去存储数据库，清空 dtoList，防止 OOM
        if (dtoList.size() > BATCH_COUNT) {
            saveData();
            dtoList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        System.out.println("已读完");
    }

    // 保存数据进数据库
    private void saveData() {
        dictMapper.insertBatch(dtoList);
    }
}
