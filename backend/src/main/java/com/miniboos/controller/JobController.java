package com.miniboos.controller;

import com.miniboos.common.BizException;
import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.JobVO;
import com.miniboos.dto.PageVO;
import com.miniboos.dto.req.BizReq;
import com.miniboos.entity.Job;
import com.miniboos.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/jobs")
    @RequireRole({"CANDIDATE"})
    public Result<PageVO<JobVO>> list(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String city,
                                      @RequestParam(required = false) String kw) {
        return Result.ok(jobService.listPublic(category, city, kw, page, size, user.userId()));
    }

    @GetMapping("/jobs/{id}")
    @RequireRole({"CANDIDATE"})
    public Result<JobVO> detail(@PathVariable Long id) {
        JobVO vo = jobService.detail(id);
        if (vo == null) throw BizException.notFound("职位不存在或已下架");
        return Result.ok(vo);
    }

    @PostMapping("/jobs")
    @RequireRole({"HR"})
    public Result<Void> publish(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                @Valid @RequestBody BizReq.Job req) {
        jobService.publish(user.userId(), req);
        return Result.ok();
    }

    @GetMapping("/my/jobs")
    @RequireRole({"HR"})
    public Result<?> myJobs(@RequestAttribute("currentUser") JwtUtil.CurrentUser user) {
        return Result.ok(jobService.myJobs(user.userId()));
    }

    @PutMapping("/jobs/{id}/status")
    @RequireRole({"HR"})
    public Result<Void> switchStatus(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                                     @PathVariable Long id, @RequestBody Map<String, String> body) {
        jobService.switchStatus(user.userId(), id, body.get("status"));
        return Result.ok();
    }
}
