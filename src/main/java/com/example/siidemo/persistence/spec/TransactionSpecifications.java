package com.example.siidemo.persistence.spec;

import com.example.siidemo.persistence.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class TransactionSpecifications {

    public static Specification<Transaction> hasForeignId(Integer foreignId) {
        return (root, query, cb) ->
                foreignId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("foreignId"), foreignId);
    }

    public static Specification<Transaction> timestampAfter(OffsetDateTime from) {
        return (root, query, cb) ->
                from == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get("timestamp"), from);
    }

    public static Specification<Transaction> timestampBefore(OffsetDateTime until) {
        return (root, query, cb) ->
                until == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get("timestamp"), until);
    }

    public static Specification<Transaction> hasType(String type) {
        return (root, query, cb) ->
                (type == null || type.isBlank())
                        ? cb.conjunction()
                        : cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> hasActor(String actor) {
        return (root, query, cb) ->
                (actor == null || actor.isBlank())
                        ? cb.conjunction()
                        : cb.equal(root.get("actor"), actor);
    }
}
