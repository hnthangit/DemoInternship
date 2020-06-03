package com.shopping.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.shopping.entity.Review;

public class ReviewSpec {
	public static Specification<Review> search(String name, int rating, String content) {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (name != null) {
				predicates.add(builder.like(root.get("productEntity").get("name"), "%" + name + "%"));
			}
			if (content != null) {
				predicates.add(builder.like(root.get("content"), "%" + content + "%"));
			}
			if (rating != 0) {
				predicates.add(builder.equal(root.get("rating"), rating));
			}
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
