package com.example.bookshop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bookshop.model.User;
import com.example.bookshop.repository.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private static final String SQL_SCRIPT_BEFORE_TEST =
            "classpath:database/add-data-for-user-test.sql";
    private static final String SQL_SCRIPT_AFTER_TEST =
            "classpath:database/delete-data-for-user-test.sql";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify findByEmail() method works")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByEmail_ValidEmail_ShouldReturnUser() {
        User expected = createUser();
        User actual = userRepository.findByEmail(expected.getEmail()).get();
        EqualsBuilder.reflectionEquals(actual, expected);
    }

    @Test
    @DisplayName("Verify findByEmail() method doesn't work wih not exist email")
    @Sql(scripts = SQL_SCRIPT_BEFORE_TEST, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = SQL_SCRIPT_AFTER_TEST, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findByEmail_NotExistEmail_ShouldReturnEmptyOptional() {
        Optional<User> actual = userRepository.findByEmail("not-exist@email.com");
        assertThat(actual).isEqualTo(Optional.empty());
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setPassword("12345678");
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }
}
