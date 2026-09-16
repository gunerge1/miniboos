package com.miniboos.controller;

import com.miniboos.common.JwtUtil;
import com.miniboos.common.RequireRole;
import com.miniboos.common.Result;
import com.miniboos.dto.req.BizReq;
import com.miniboos.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会话留言（留言板模式：会话双方=牛人+该职位企业HR）
 */
@RestController
@RequestMapping("/api/applications/{id}/messages")
@RequiredArgsConstructor
@RequireRole({"CANDIDATE", "HR"})
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public Result<?> list(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                          @PathVariable Long id) {
        return Result.ok(messageService.list(id, user.userId()));
    }

    @PostMapping
    public Result<Void> send(@RequestAttribute("currentUser") JwtUtil.CurrentUser user,
                             @PathVariable Long id,
                             @Valid @RequestBody BizReq.Message req) {
        messageService.send(id, user.userId(), req.getContent());
        return Result.ok();
    }
}
