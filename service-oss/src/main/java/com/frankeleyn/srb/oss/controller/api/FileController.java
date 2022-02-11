package com.frankeleyn.srb.oss.controller.api;

import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Frankeleyn
 * @date 2022/2/11 10:08
 */
@Api("文件接口")
@CrossOrigin
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(@ApiParam("待上传的文件") MultipartFile file, @ApiParam("模块，表示上传的文件属于哪个项目") String module) {
        String uploadUrl = fileService.upload(file, module);
        return R.ok("fileUrl", uploadUrl);
    }

    @ApiOperation("文件删除")
    @DeleteMapping("/remove")
    public R remove(@ApiParam("待删除文件的 url") String url) {
        fileService.removeFile(url);
        return R.ok();
    }
}
