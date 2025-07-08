package com.codeit.weatherwear.domain.clothes.repository;

import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothRepository extends JpaRepository<Cloth, UUID>, ClothRepositoryCustom {

  @EntityGraph(attributePaths = {"clothesWithAttributes", "clothesWithAttributes.attribute"})
  @Query("SELECT c FROM Cloth c WHERE c.id = :clothId")
  Optional<Cloth> findByIdWithAttributes(UUID clothId);

  @EntityGraph(attributePaths = {"clothesWithAttributes", "clothesWithAttributes.attribute"})
  @Query("SELECT c FROM Cloth c WHERE c.id IN :clothIds")
  List<Cloth> findAllByIdWithAttributes(List<UUID> clothIds);

  @EntityGraph(attributePaths = {"clothesWithAttributes", "clothesWithAttributes.attribute"})
  @Query("SELECT c FROM Cloth c WHERE c.user.id = :userId")
  List<Cloth> findAllWithAttributesByUserId(UUID userId);

}
