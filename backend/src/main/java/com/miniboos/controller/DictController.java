package com.miniboos.controller;

import com.miniboos.common.Result;
import com.miniboos.entity.DictItem;
import com.miniboos.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公开字典读取（免鉴权，三端下拉框数据源）
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping
    public Result<List<DictItem>> list(@RequestParam String type) {
        return Result.ok(dictService.enabledByType(type));
    }
}
