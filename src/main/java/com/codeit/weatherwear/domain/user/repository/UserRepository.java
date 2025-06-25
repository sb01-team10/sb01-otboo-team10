package com.codeit.weatherwear.domain.user.repository;

import com.codeit.weatherwear.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserCustomRepository {

    boolean existsByName(String name);

    boolean existsByEmail(String email);
//
//    @Query("""
//            SELECT u FROM User u
//            WHERE (:emailLike IS NULL OR u.email LIKE CONCAT('%', CAST(:emailLike AS string), '%'))
//              AND (:role IS NULL OR u.role = :role)
//              AND (:locked IS NULL OR u.locked = :locked)
//              AND (
//                :cursorEmail IS NULL OR
//                (
//                    (:direction = 'ASC' AND (u.email > :cursorEmail OR (u.email = :cursorEmail AND u.id > :idAfter))) OR
//                    (:direction = 'DESC' AND (u.email < :cursorEmail OR (u.email = :cursorEmail AND u.id < :idAfter)))
//                )
//              )
//            ORDER BY
//              CASE WHEN :direction = 'ASC' THEN u.email END ASC,
//              CASE WHEN :direction = 'DESC' THEN u.email END DESC,
//              u.id
//        """)
//    List<User> searchUsersByEmailCursor(
//        @Param("emailLike") String emailLike,
//        @Param("role") String role,
//        @Param("locked") Boolean locked,
//        @Param("cursorEmail") String cursorEmail,
//        @Param("idAfter") UUID idAfter,
//        @Param("direction") String direction,
//        Pageable pageable
//    );
//
//    @Query("""
//            SELECT u FROM User u
//            WHERE (:emailLike IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:emailLike AS string), '%')))
//              AND (:role IS NULL OR u.role = :role)
//              AND (:locked IS NULL OR u.locked = :locked)
//              AND (
//                :cursorCreatedAt IS NULL OR
//                (
//                    (:direction = 'ASC' AND (u.createdAt > :cursorCreatedAt OR (u.createdAt = :cursorCreatedAt AND u.id > :idAfter))) OR
//                    (:direction = 'DESC' AND (u.createdAt < :cursorCreatedAt OR (u.createdAt = :cursorCreatedAt AND u.id < :idAfter)))
//                )
//              )
//            ORDER BY
//              CASE WHEN :direction = 'ASC' THEN u.createdAt END ASC,
//              CASE WHEN :direction = 'DESC' THEN u.createdAt END DESC,
//              u.id
//        """)
//    List<User> searchUsersByCreatedAtCursor(
//        @Param("emailLike") String emailLike,
//        @Param("role") Role role,
//        @Param("locked") Boolean locked,
//        @Param("cursorCreatedAt") Instant cursorCreatedAt,
//        @Param("idAfter") UUID idAfter,
//        @Param("direction") String direction,
//        Pageable pageable
//    );
//
//    @Query("""
//            SELECT COUNT(u) FROM User u
//            WHERE (:emailLike IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :emailLike, '%')))
//              AND (:role IS NULL OR u.role = :role)
//              AND (:locked IS NULL OR u.locked = :locked)
//        """)
//    long countUsersByFilter(@Param("emailLike") String emailLike,
//        @Param("role") String role,
//        @Param("locked") Boolean locked);


}
