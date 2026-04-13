package com.example.ecommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private long pages;
    private long current;
    private long size;
    private List<T> records;

    public PageResult(long total, long current, long size, List<T> records) {
        this.total = total;
        this.current = current;
        this.size = size;
        this.records = records;
        this.pages = (total + size - 1) / size;
    }
}
