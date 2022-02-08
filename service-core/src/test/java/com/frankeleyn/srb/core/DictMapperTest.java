package com.frankeleyn.srb.core;

import com.frankeleyn.srb.core.mapper.DictMapper;
import com.frankeleyn.srb.core.service.DictService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Frankeleyn
 * @date 2022/2/8 10:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DictMapperTest {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictService dictService;

    @Test
    public void testDictService() {
        dictService.listDictData().forEach(System.out::println);
    }
}
