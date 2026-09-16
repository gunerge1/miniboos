package com.miniboos.controller;

import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.req.BizReq;
import com.miniboos.entity.Company;
import com.miniboos.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/companies")
    @RequireRole({"HR"})
    public Result<Void> submit(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                               @Valid @RequestBody BizReq.Company req) {
        companyService.submit(user.userId(), req);
        return Result.ok();
    }

    @GetMapping("/my/company")
    @RequireRole({"HR"})
    public Result<Company> my(@RequestAttribute("currentUser") JwtUtil.CurrentUser user) {
        return Result.ok(companyService.myCompany(user.userId()));
    }
}
