package com.miniboos.controller;

import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.ResumeDetailVO;
import com.miniboos.dto.req.BizReq;
import com.miniboos.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my/resume")
@RequiredArgsConstructor
@RequireRole({"CANDIDATE"})
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    public Result<ResumeDetailVO> my(@RequestAttribute("currentUser") JwtUtil.CurrentUser user) {
        return Result.ok(resumeService.my(user.userId()));
    }

    @PutMapping
    public Result<Void> save(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                             @Valid @RequestBody BizReq.Resume req) {
        resumeService.save(user.userId(), req);
        return Result.ok();
    }

    @PostMapping("/experiences")
    public Result<Void> addExp(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                               @Valid @RequestBody BizReq.Experience req) {
        resumeService.addExperience(user.userId(), req);
        return Result.ok();
    }

    @PutMapping("/experiences/{id}")
    public Result<Void> updateExp(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                  @PathVariable Long id,
                                  @Valid @RequestBody BizReq.Experience req) {
        resumeService.updateExperience(user.userId(), id, req);
        return Result.ok();
    }

    @DeleteMapping("/experiences/{id}")
    public Result<Void> deleteExp(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                  @PathVariable Long id) {
        resumeService.deleteExperience(user.userId(), id);
        return Result.ok();
    }
}
