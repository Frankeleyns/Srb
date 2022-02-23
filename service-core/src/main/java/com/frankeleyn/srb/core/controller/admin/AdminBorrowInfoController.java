package com.frankeleyn.srb.core.controller.admin;


import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.BorrowInfo;
import com.frankeleyn.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.frankeleyn.srb.core.service.BorrowInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Autowired
    private BorrowInfoService borrowInfoService;

    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO approvalVO) {
        borrowInfoService.approval(approvalVO);
        return R.ok("审核成功");
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable("id") Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.show(id);
        return R.ok("borrowInfoDetail", borrowInfoDetail);
    }

    @GetMapping("/list")
    public R getList() {
        List<BorrowInfo> borrowInfoList = borrowInfoService.getList();
        return R.ok("list", borrowInfoList);
    }
}

