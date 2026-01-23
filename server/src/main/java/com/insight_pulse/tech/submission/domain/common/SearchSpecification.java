package com.insight_pulse.tech.submission.domain.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.insight_pulse.tech.submission.domain.Submission;

import jakarta.persistence.criteria.Predicate;

public class SearchSpecification {
    
    public static Specification<Submission> belongsToCampaign(String campaignId) {
        return(r,q,cb) -> cb.equal(r.get("campaign").get("id"), campaignId);
    }
    
    public static Specification<Submission> byName(String name) {
        return (root, query, cb) -> {
            if(name == null || name.isEmpty()) {
                return null;
            }
            return cb.like(root.get("fullname"), "%" + name + "%");
        };
    }    

    public static Specification<Submission> byScoreRange(Double minScore, Double maxScore) {
        return (r, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(minScore != null) predicates.add(cb.greaterThanOrEqualTo(r.get("score"), minScore));
            if(maxScore != null) predicates.add(cb.lessThanOrEqualTo(r.get("score"), maxScore));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Submission> byDateTime(LocalDateTime from, LocalDateTime to) {
        return (r, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(from != null) predicates.add(cb.greaterThanOrEqualTo(r.get("submittedAt"), from));
            if(to != null) predicates.add(cb.lessThanOrEqualTo(r.get("submittedAt"), to));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Submission> byStarred(Boolean starred){
        return (r, q, cb) -> starred != null ? cb.equal(r.get("starred"), starred) : null;
    }
}
