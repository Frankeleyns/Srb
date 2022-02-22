package com.frankeleyn.srb.core.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frankeleyn.common.result.R;
import com.frankeleyn.srb.core.pojo.entity.Borrower;
import com.frankeleyn.srb.core.pojo.vo.BorrowerApprovalVO;
import com.frankeleyn.srb.core.pojo.vo.BorrowerDetailVO;
import com.frankeleyn.srb.core.service.BorrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author Frankeleyn
 * @since 2022-01-21
 */
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO approvalVO) {
        borrowerService.approval(approvalVO);
        return R.ok("审核成功");
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable("id") Long id) {
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok("borrower", borrowerDetailVO);
    }

    @GetMapping("/list/{page}/{limit}")
    public R list(@PathVariable("page") Long page, @PathVariable("limit") Long limit, String keyword) {
        Page<Borrower> pageParam = new Page<>();
        pageParam.setCurrent(page);
        pageParam.setSize(limit);

        Page<Borrower> pageModel = borrowerService.getList(pageParam, keyword);
        return R.ok("pageModel", pageModel);
    }
}

