package com.example.bookshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.user.UserRegistrationRequestDto;
import com.example.bookshop.dto.user.UserResponseDto;
import com.example.bookshop.exception.RegistrationException;
import com.example.bookshop.mapper.UserMapper;
import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.role.RoleRepository;
import com.example.bookshop.repository.user.UserRepository;
import com.example.bookshop.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@gmail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    @DisplayName("Verify register() method works")
    public void register_UserStillNotExist_ShouldReturnUserDto() throws RegistrationException {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();
        Role role = new Role();
        role.setName(Role.RoleName.USER);
        UserResponseDto expected = createUserResponseDto();
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("@g_sJ'#_$ks%1Nq");
        when(roleRepository.findByName(Role.RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDto(user)).thenReturn(expected);
        UserResponseDto actual = userService.register(requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify register() method throws Exception if user already exist in db")
    public void register_UserAlreadyExistInDb_Exception() throws RegistrationException {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto();
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(requestDto));
        String actual = exception.getMessage();
        String expected = "Registration cannot be completed because a user with the same email"
                + " address already exists";
    }

    private UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
                .setEmail(user.getEmail())
                .setPassword("@g_sJ'#_$ks%1Nq")
                .setRepeatPassword("@g_sJ'#_$ks%1Nq")
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
    }

    private UserResponseDto createUserResponseDto() {
        return new UserResponseDto()
                .setId(1L)
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
    }
}
