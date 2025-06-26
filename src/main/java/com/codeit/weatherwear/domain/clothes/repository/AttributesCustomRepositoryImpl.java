package com.codeit.weatherwear.domain.clothes.repository;

import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import com.codeit.weatherwear.domain.clothes.entity.QAttributes;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttributesCustomRepositoryImpl implements AttributesCustomRepository {

    private final JPAQueryFactory factory;

    @Override
    public Slice<Attributes> searchAttributes(String cursor, UUID idAfter, int limit, String sortBy,
        AttributesSortDirection sortDirection, String keywordLike) {

        QAttributes attributes = QAttributes.attributes;
        Order direction = (sortDirection.equals(AttributesSortDirection.ASCENDING) ? Order.ASC : Order.DESC);

        OrderSpecifier<?> orderSpecifier = buildOrderSpecifiers(sortBy, direction, attributes);

        BooleanBuilder condition = new BooleanBuilder();

        condition.and((keywordLike==null||keywordLike.isBlank())?null
            : attributes.name.containsIgnoreCase(keywordLike));

        if(cursor != null &&!cursor.isBlank()) {
            condition.and(getCursorCondition(cursor, idAfter, sortBy, keywordLike, condition, direction, attributes));
        }

        JPAQuery<Attributes> query = factory.selectFrom(attributes);
        query.where(condition);
        query.orderBy(orderSpecifier);
        query.limit(limit+1);
        List<Attributes> attributesList = query.fetch();
        boolean hasNext=attributesList.size()>limit;
        if(hasNext) {
            attributesList.remove(attributesList.size()-1);
        }
        return new SliceImpl<>(attributesList, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public Long getTotalCount(String keywordLike) {
        QAttributes attributes = QAttributes.attributes;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and((keywordLike==null||keywordLike.isBlank())?null
            :attributes.name.containsIgnoreCase(keywordLike));
        return factory.select(attributes.count())
            .from(attributes)
            .where(builder)
            .fetchOne();
    }

    private BooleanBuilder getCursorCondition(String cursor, UUID idAfter, String sortBy, String keywordLike,
        BooleanBuilder condition, Order direction, QAttributes attributes) {
        switch (sortBy) {
            case "name":
                condition.and((direction.equals(Order.ASC))
                    ? attributes.name.gt(cursor)
                    .or(attributes.name.eq(cursor).and(attributes.id.gt(idAfter)))
                    : attributes.name.lt(cursor)
                        .or(attributes.name.eq(cursor).and(attributes.id.lt(idAfter))));
                break;
            case "createdAt":
                Instant parse = Instant.parse(cursor);
                condition.and((direction.equals(Order.ASC))
                    ? attributes.createdAt.gt(parse)
                    .or(attributes.createdAt.eq(parse).and(attributes.id.gt(idAfter)))
                    : attributes.createdAt.lt(parse)
                        .or(attributes.createdAt.eq(parse).and(attributes.id.lt(idAfter))));
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 정렬기준입니다.");
        }
        return condition;
    }


    // 정렬 기준 필드 + createdAt을 함께 적용한 Order By 조건을 생성함
    private OrderSpecifier<?> buildOrderSpecifiers(String sortBy, Order direction,
        QAttributes attributes) {
        OrderSpecifier<?> orderSpecifier;

        switch (sortBy) {
            case "createdAt":
                orderSpecifier = new OrderSpecifier<>(direction, attributes.createdAt);
                break;
            case "name":
                orderSpecifier = new OrderSpecifier<>(direction, attributes.name);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        }
        return orderSpecifier;
    }
}
