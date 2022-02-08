package com.frankeleyn.srb.core;

import com.frankeleyn.srb.core.mapper.DictMapper;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.service.DictService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frankeleyn
 * @date 2022/2/8 10:39
 */
@SpringBootTest
public class DictMapperTest {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictService dictService;

    @Test
    public void testInsert() {
        List<ExcelDictDTO> list = new ArrayList<>();;
        list.add(new ExcelDictDTO(1L, 0L, "ROOT", 1, "ROOT"));
        dictMapper.insertBatch(list);
    }

    @Test
    public void testDictService() {
        dictService.listDictData().forEach(System.out::println);
    }
}
