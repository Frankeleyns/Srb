package com.frankeleyn.srb.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * @author Frankeleyn
 * @date 2022/1/22 10:40
 */
@SpringBootTest
public class AssertTest {

    @Test
    public void testNotAssert() {
        Object o = null;

        if (o == null) {
            throw new IllegalArgumentException("对象不能为空");
        }
    }

    @Test
    public void testAssert() {
        Object o = null;
        Assert.notNull(o, "对象不能为空");
    }
}
