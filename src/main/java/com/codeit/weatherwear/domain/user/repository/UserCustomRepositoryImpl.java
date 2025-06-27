package com.codeit.weatherwear.domain.user.repository;

import com.codeit.weatherwear.domain.user.entity.QUser;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.global.request.SortDirection;
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
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<User> searchUsers(String cursor, UUID idAfter, int limit,
        String sortBy, SortDirection sortDirection, String emailLike, Role roleEqual,
        Boolean locked) {

        QUser user = QUser.user;

        // 정렬 방향
        Order direction =
            (sortDirection.equals(SortDirection.ASCENDING)) ? Order.ASC : Order.DESC;
        OrderSpecifier<?> orderSpecifier;

        switch (sortBy) {
            case "email":
                orderSpecifier = new OrderSpecifier<>(direction, user.email);
                break;
            case "createdAt":
                orderSpecifier = new OrderSpecifier<>(direction, user.createdAt);
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        }

        // 필터링 (이메일, 권한, 잠금상태)
        BooleanBuilder where = new BooleanBuilder();
        where.and((emailLike == null || emailLike.isBlank()) ? null :
            user.email.containsIgnoreCase(emailLike));
        where.and((roleEqual == null) ? null : user.role.eq(roleEqual));
        where.and((locked == null) ? null : user.locked.eq(locked));

        // 정렬 기준과 커서에 따른 필터링
        if (cursor != null && !cursor.isBlank()) {
            switch (sortBy) {
                case "email":
                    where.and((direction.equals(Order.ASC))
                        ? user.email.gt(cursor)
                        : user.email.lt(cursor));
                    break;
                case "createdAt":
                    Instant cursorInstant = Instant.parse(cursor);
                    where.and((direction.equals(Order.ASC))
                        ? user.createdAt.gt(cursorInstant)
                        .or(user.createdAt.eq(cursorInstant).and(user.id.gt(idAfter)))
                        : user.createdAt.lt(cursorInstant)
                            .or(user.createdAt.eq(cursorInstant).and(user.id.lt(idAfter))));
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
            }
        }

        // limit + 1개 조회
        JPAQuery<User> query = queryFactory.selectFrom(user);
        query.where(where);
        query.orderBy(orderSpecifier);
        query.limit(limit + 1);
        List<User> users = query.fetch();

        boolean hasNext = users.size() > limit;
        if (hasNext) {
            users.remove(users.size() - 1);
        }

        return new SliceImpl<>(users, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public Long getTotalCount(String emailLike, Role roleEqual, Boolean locked) {
        QUser user = QUser.user;
        // 필터링 (이메일, 권한, 잠금상태)
        BooleanBuilder where = new BooleanBuilder();
        where.and((emailLike == null || emailLike.isBlank()) ? null :
            user.email.containsIgnoreCase(emailLike));
        where.and((roleEqual == null) ? null : user.role.eq(roleEqual));
        where.and((locked == null) ? null : user.locked.eq(locked));

        return queryFactory.select(user.count())
            .from(user)
            .where(where)
            .fetchOne();
    }
}
