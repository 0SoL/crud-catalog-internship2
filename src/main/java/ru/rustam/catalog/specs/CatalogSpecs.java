package ru.rustam.catalog.specs;

import org.springframework.data.jpa.domain.Specification;
import ru.rustam.catalog.entity.CatalogEntity;

import java.math.BigDecimal;

public final class CatalogSpecs {
    public static Specification<CatalogEntity> nameContains(String name){
        return ((root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        });
    }

    public static Specification<CatalogEntity> descriptionContains(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + name.toLowerCase() + "%");
        });
    }

    public static Specification<CatalogEntity> priceBetween(BigDecimal min, BigDecimal max) {
        return ((root, query, criteriaBuilder) ->  {
           if (min == null && max == null) return criteriaBuilder.conjunction();
           return criteriaBuilder.between(root.get("price"), min, max);
        });
    }

    public static Specification<CatalogEntity> photoContains(Boolean photo) {
        return ((root, query, criteriaBuilder) -> {
            if (photo == null) return criteriaBuilder.conjunction();
            if (photo) {
                return criteriaBuilder.isNotEmpty(root.get("images"));
            } else {
                return criteriaBuilder.isEmpty(root.get("images"));
            }
        });
    }

}
