package com.example.OOAD.RepositoryLayer;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.OOAD.Users;

import java.util.List;
import java.util.Optional;

/**
 * Repository Pattern (Structural) — UserRepo
 *
 * Intent: Mediate between the domain and data mapping layers using a
 * collection-like interface for accessing domain objects.
 *
 * Spring Data JPA implements this pattern: JpaRepository provides
 * standard CRUD operations, and this interface only declares the
 * domain-specific query methods the application needs.
 * The concrete implementation is generated at runtime by Spring.
 */
@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {
    int countByRole(String role);
    static List<Users> findTop6ByOrderByCreatedAtDesc() {
        throw new UnsupportedOperationException("Unimplemented method 'findTop6ByOrderByCreatedAtDesc'");
    }
    Optional<Users> findByEmail(String email);
     @Query("SELECT u FROM Users u ORDER BY u.user_id DESC")
    List<Users> findTopUsers(PageRequest pageable);
}
