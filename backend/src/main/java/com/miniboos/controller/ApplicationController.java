package com.miniboos.controller;

import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.ResumeDetailVO;
import com.miniboos.dto.req.AdminReq;
import com.miniboos.dto.req.BizReq;
import com.miniboos.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/applications")
    @RequireRole({"CANDIDATE"})
    public Result<Void> apply(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                              @Valid @RequestBody BizReq.Apply req) {
        applicationService.apply(user.userId(), req.getJobId());
        return Result.ok();
    }

    @GetMapping("/my/applications")
    @RequireRole({"CANDIDATE"})
    public Result<?> my(@RequestAttribute("currentUser") JwtUtil.CurrentUser user) {
        return Result.ok(applicationService.my(user.userId()));
    }

    @GetMapping("/my/jobs/{id}/applications")
    @RequireRole({"HR"})
    public Result<?> candidates(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                @PathVariable Long id) {
        return Result.ok(applicationService.candidatesOfJob(user.userId(), id));
    }

    @PutMapping("/applications/{id}/status")
    @RequireRole({"HR"})
    public Result<Void> updateStatus(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                     @PathVariable Long id,
                                     @Valid @RequestBody AdminReq.AppStatus req) {
        applicationService.updateStatus(user.userId(), id, req.getStatus());
        return Result.ok();
    }

    /** HR查看投递牛人的完整简历（A类缺口补齐，2026-09-16） */
    @GetMapping("/applications/{id}/resume")
    @RequireRole({"HR"})
    public Result<ResumeDetailVO> resume(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                         @PathVariable Long id) {
        return Result.ok(applicationService.resumeOfApplication(user.userId(), id));
    }
}
