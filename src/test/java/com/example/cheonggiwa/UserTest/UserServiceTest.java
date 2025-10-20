package com.example.cheonggiwa.UserTest;

import com.example.cheonggiwa.dto.UserDTO;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.repository.UserRepository;
import com.example.cheonggiwa.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserTest() {
        User user = User.builder().username("mockuser").password("123").build();
        when(userRepository.save(user)).thenReturn(user);

        UserDTO dto = userService.createUser(user);

        assertEquals("mockuser", dto.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserTest() {
        User user = User.builder().id(1L).username("mockuser").password("123").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUser(1L);

        assertEquals("mockuser", dto.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }
}
