package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.exception.Assert;
import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import com.frankeleyn.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@CrossOrigin
public class AdminDictController {

    @Autowired
    DictService dictService;

    @ApiOperation("Excel批量导入数据字典")
    @PostMapping("import")
    public R importFIle( @ApiParam(value = "Excel文件", required = true) @RequestParam("file") MultipartFile file) {
        Assert.isTrue(dictService.importData(file), ResponseEnum.UPLOAD_ERROR);
        return R.ok();
    }
}

