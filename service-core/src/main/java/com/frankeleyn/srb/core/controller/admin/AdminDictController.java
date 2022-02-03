package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.result.R;
import io.swagger.annotations.Api;
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

    @PostMapping("import")
    public R importFIle(@RequestParam("file") MultipartFile file) {

        return R.ok();
    }
}

