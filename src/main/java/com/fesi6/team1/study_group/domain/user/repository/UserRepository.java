package com.fesi6.team1.study_group.domain.user.repository;

import com.fesi6.team1.study_group.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialId(String socialId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
