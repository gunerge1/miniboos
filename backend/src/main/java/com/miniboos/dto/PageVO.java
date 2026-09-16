package com.miniboos.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageVO<T> {
    private List<T> list;
    private long total;
    private int page;
    private int size;

    public static <T> PageVO<T> of(List<T> list, long total, int page, int size) {
        PageVO<T> p = new PageVO<>();
        p.list = list;
        p.total = total;
        p.page = page;
        p.size = size;
        return p;
    }
}
