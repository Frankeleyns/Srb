package com.frankeleyn.srb.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frankeleyn.srb.core.mapper.IntegralGradeMapper;
import com.frankeleyn.srb.core.pojo.entity.IntegralGrade;
import com.frankeleyn.srb.core.service.IntegralGradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Frankeleyn
 * @date 2022/1/21 14:37
 */
@SpringBootTest
public class IntegralGradeTest {

    @Autowired
    private IntegralGradeService service;
    
    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Test
    public void testFindAll() {
        service.list().forEach(System.out::println);
    }
    
    @Test
    public void testGe() {
        Integer integralGrade = 240;
        QueryWrapper<IntegralGrade> integralQueryWrapper = new QueryWrapper<>();
        integralQueryWrapper.le("integral_start", integralGrade)
                .gt("integral_end", integralGrade);

        IntegralGrade integral= integralGradeMapper.selectOne(integralQueryWrapper);
        System.out.println(integral.getBorrowAmount());

    }
}
