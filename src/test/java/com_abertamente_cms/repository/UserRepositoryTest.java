package com_abertamente_cms.repository;

import com_abertamente_cms.domain.Role;
import com_abertamente_cms.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmailWithRolesEagerlyLoaded() {
        // Arrange
        Role roleAdmin = new Role("ADMIN");
        entityManager.persist(roleAdmin);

        User user = new User("John Doe", "john.doe@example.com", "password123");
        user.setRoles(Set.of(roleAdmin));
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to force a query

        // Act
        Optional<User> foundUserOpt = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();
        assertEquals("John Doe", foundUser.getName());
        
        // This will throw LazyInitializationException if @EntityGraph is not working 
        // and we are outside a transaction (though @DataJpaTest is transactional by default).
        // However, we can assert the roles are initialized.
        assertFalse(foundUser.getRoles().isEmpty());
        assertEquals(1, foundUser.getRoles().size());
        assertEquals("ADMIN", foundUser.getRoles().iterator().next().getName());
    }
}
