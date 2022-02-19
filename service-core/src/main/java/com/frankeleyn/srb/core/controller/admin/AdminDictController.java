package com.frankeleyn.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.exception.BusinessException;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.pojo.dto.ExcelDictDTO;
import com.frankeleyn.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@Api(tags = "数据字典管理 Api")
@RestController
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("根据父 id 获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R listByParentId(@ApiParam(value = "父 id", required = true) @PathVariable("parentId") Long parentId) {
        return R.ok("list", dictService.listByParentId(parentId));
    }

    @ApiOperation("Excel 批量导出数据字典")
    @GetMapping("export")
    public void export(HttpServletResponse response) {
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + "mydict.xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR);
        }

    }

    @ApiOperation("Excel 批量导入数据字典")
    @PostMapping("import")
    public R importFIle( @ApiParam(value = "Excel文件", required = true) @RequestParam("file") MultipartFile file) {
        Assert.isTrue(dictService.importData(file), ResponseEnum.UPLOAD_ERROR);
        return R.ok();
    }
}

