package com.miniboos.controller;

import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.PageVO;
import com.miniboos.dto.StatsVO;
import com.miniboos.dto.req.AdminReq;
import com.miniboos.entity.Company;
import com.miniboos.entity.DictItem;
import com.miniboos.entity.Job;
import com.miniboos.service.AdminService;
import com.miniboos.service.DictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})
public class AdminController {

    private final AdminService adminService;
    private final DictService dictService;

    // ---------- 企业审核 ----------
    @GetMapping("/companies")
    public Result<PageVO<Company>> companies(@RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.ok(adminService.companies(status, page, size));
    }

    @PutMapping("/companies/{id}/audit")
    public Result<Void> auditCompany(@PathVariable Long id, @Valid @RequestBody AdminReq.Audit req) {
        adminService.auditCompany(id, req);
        return Result.ok();
    }

    // ---------- 职位审核 ----------
    @GetMapping("/jobs")
    public Result<PageVO<Job>> jobs(@RequestParam(required = false) String status,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return Result.ok(adminService.jobs(status, page, size));
    }

    @PutMapping("/jobs/{id}/audit")
    public Result<Void> auditJob(@PathVariable Long id, @Valid @RequestBody AdminReq.Audit req) {
        adminService.auditJob(id, req);
        return Result.ok();
    }

    // ---------- 用户管理 ----------
    @GetMapping("/users")
    public Result<?> users(@RequestParam(required = false) String role,
                           @RequestParam(required = false) String kw,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return Result.ok(adminService.users(role, kw, page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @Valid @RequestBody AdminReq.UserStatus req) {
        adminService.updateUserStatus(id, req.getStatus());
        return Result.ok();
    }

    // ---------- 数据看板 ----------
    @GetMapping("/stats")
    public Result<StatsVO> stats() {
        return Result.ok(adminService.stats());
    }

    // ---------- 字典管理（v1.1，CEO评审新增） ----------
    @GetMapping("/dicts")
    public Result<?> dicts(@RequestParam(required = false) String type) {
        return Result.ok(dictService.adminList(type));
    }

    @PostMapping("/dicts")
    public Result<Void> addDict(@Valid @RequestBody AdminReq.Dict req) {
        DictItem item = new DictItem();
        item.setDictType(req.getDictType());
        item.setCode(req.getCode());
        item.setLabel(req.getLabel());
        item.setSort(req.getSort());
        dictService.adminAdd(item);
        return Result.ok();
    }

    @PutMapping("/dicts/{id}")
    public Result<Void> updateDict(@PathVariable Long id, @Valid @RequestBody AdminReq.Dict req) {
        DictItem item = new DictItem();
        item.setId(id);
        item.setLabel(req.getLabel());
        item.setSort(req.getSort());
        item.setStatus(req.getStatus());
        dictService.adminUpdate(item);
        return Result.ok();
    }

    @DeleteMapping("/dicts/{id}")
    public Result<Void> deleteDict(@PathVariable Long id) {
        dictService.adminDelete(id);
        return Result.ok();
    }
}
