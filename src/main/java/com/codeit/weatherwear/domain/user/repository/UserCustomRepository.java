package com.codeit.weatherwear.domain.user.repository;

import com.codeit.weatherwear.domain.user.dto.request.UserSortDirection;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface UserCustomRepository {

    Slice<User> searchUsers(
        String cursor,
        UUID idAfter,
        int limit,
        String sortBy,
        UserSortDirection sortDirection,
        String emailLike,
        Role roleEqual,
        Boolean locked
    );

    Long getTotalCount(String emailLike, Role roleEqual, Boolean locked);
}
