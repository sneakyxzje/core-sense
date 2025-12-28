package com.insight_pulse.tech.utils;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class PageableUtils {
    public static Pageable convertToNativePageable(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream().map(order -> {
            if ("submittedAt".equals(order.getProperty())) {
                return new Sort.Order(order.getDirection(), "submitted_at");
            }
            if ("createdAt".equals(order.getProperty())) {
                return new Sort.Order(order.getDirection(), "created_at");
            }
            if ("score".equals(order.getProperty())) {
                return new Sort.Order(order.getDirection(), "score"); 
            }
            return order;
        }).toList();
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }
}
