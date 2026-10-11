package com.bgm.service;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    List<T> items;
    Long total;
    int pageNum;
    int pageSize;
}
