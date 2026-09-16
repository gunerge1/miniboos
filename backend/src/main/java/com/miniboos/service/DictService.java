package com.miniboos.service;

import com.miniboos.common.BizException;
import com.miniboos.entity.DictItem;
import com.miniboos.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DictService {

    /** 字典类型白名单——新增类型需改代码（字典类型本身是"系统词汇"） */
    private static final Set<String> TYPES = Set.of("category", "industry", "city", "education");

    private final DictItemMapper dictItemMapper;

    public List<DictItem> enabledByType(String type) {
        checkType(type);
        return dictItemMapper.listEnabledByType(type);
    }

    /** 校验code在指定字典中存在且启用（业务表写入前的软引用校验） */
    public void checkCodeUsable(String type, String code) {
        if (code == null || code.isBlank()) return;
        checkType(type);
        boolean exists = dictItemMapper.listEnabledByType(type).stream()
                .anyMatch(i -> i.getCode().equals(code));
        if (!exists) {
            throw BizException.badRequest("字典值不合法：" + type + "/" + code);
        }
    }

    public List<DictItem> adminList(String type) {
        return dictItemMapper.listAll(type);
    }

    public void adminAdd(DictItem item) {
        checkType(item.getDictType());
        try {
            dictItemMapper.insert(item);
        } catch (DuplicateKeyException e) {
            throw BizException.conflict("该字典类型下code已存在：" + item.getCode());
        }
    }

    public void adminUpdate(DictItem item) {
        if (dictItemMapper.findById(item.getId()) == null) {
            throw BizException.notFound("字典项不存在");
        }
        dictItemMapper.update(item);
    }

    public void adminDelete(Long id) {
        // 删除策略：存量业务数据里的code将无label翻译（前端兜底显示原文），推荐用"停用"代替删除
        dictItemMapper.deleteById(id);
    }

    private void checkType(String type) {
        if (type == null || !TYPES.contains(type)) {
            throw BizException.badRequest("字典类型不合法：" + type);
        }
    }
}
