package com.codeit.weatherwear.domain.directmessage;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, UUID> {

}
