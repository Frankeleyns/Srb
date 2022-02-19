package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.pojo.entity.IntegralGrade;
import com.frankeleyn.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/admin/core/integralGrade")
@Api(tags = "积分等级管理 Api")
public class IntegralGradeController {

    @Autowired
    private IntegralGradeService integralGradeService;

    @ApiOperation("获取积分等级列表")
    @GetMapping("/list")
    public R findAll() {
        List<IntegralGrade> integralGradeList = integralGradeService.list();
        return R.ok("integralGradeList", integralGradeList);
    }

    @ApiOperation(value = "根据 id 删除积分等级", notes = "逻辑删除")
    @DeleteMapping("/remove/{id}")
    public R removeById(@ApiParam("积分等级 id") @PathVariable Long id) {
        boolean remove = integralGradeService.removeById(id);
        if (remove)
            return R.ok("删除积分等级成功");
        else
            return R.error("删除积分等级失败");
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(@ApiParam("积分等级对象") @RequestBody IntegralGrade integralGrade) {

        BigDecimal borrowAmount = integralGrade.getBorrowAmount();
        // 借款额度不能为空
        Assert.notNull(borrowAmount, ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        // 借款额读不嫩能为0
        Assert.isTrue(0!=borrowAmount.intValue(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        boolean save = integralGradeService.save(integralGrade);
        if (save)
            return R.ok("新增积分等级成功");
        else
            return R.error("新增积分等级失败");
    }

    @ApiOperation("根据 id 查询积分等级")
    @GetMapping("/get/{id}")
    public R findById(@ApiParam("积分等级 id") @PathVariable Long id) {
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (Objects.nonNull(integralGrade)) 
            return R.ok("integralGrade", integralGrade);
        else 
            return R.error("未查询到积分等级");
    }
    
    @ApiOperation("修改积分等级")
    @PutMapping("/update")
    public R updateById(@ApiParam("积分等级对象") @RequestBody IntegralGrade integralGrade) {
        boolean update = integralGradeService.updateById(integralGrade);
        if (update)
            return R.ok("修改积分等级成功");
        else
            return R.error("修改积分等级失败");
    }

}

