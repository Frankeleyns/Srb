package com.frankeleyn.srb.core;

import com.frankeleyn.srb.core.service.IntegralGradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Frankeleyn
 * @date 2022/1/21 14:37
 */
@SpringBootTest
public class IntegralGradeTest {

    @Autowired
    private IntegralGradeService service;

    @Test
    public void testFindAll() {
        service.list().forEach(System.out::println);
    }
}
