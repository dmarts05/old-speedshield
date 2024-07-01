package com.dmarts05.speedshield.repository;

import com.dmarts05.speedshield.model.RefreshTokenEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends ListCrudRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteAllByExpiryDateBefore(Instant limitDate);
}
