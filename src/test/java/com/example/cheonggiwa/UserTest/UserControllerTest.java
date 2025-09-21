package com.example.cheonggiwa.UserTest;

import com.example.cheonggiwa.config.TestSecurityConfig;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.example.cheonggiwa.CheonggiwaApplication.class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class) // TestSecurityConfig를 명시적으로 Import
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // 테스트 전 DB 초기화
    }

    @Test
    void testCreateUser() throws Exception {
        User user = User.builder()
                .username("testuser")
                .password("1234")
                .build();

        mockMvc.perform(post("/api/user")   // POST 요청 보내기
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))) // user 객체 → JSON 변환
                .andExpect(status().isCreated()) // 응답 코드 201 CREATED 기대
                .andExpect(jsonPath("$.username").value("testuser")); // 응답 JSON에서 username 확인
    }

    @Test
    void testGetUser() throws Exception {
        User saved = userRepository.save(User.builder()
                .username("getuser")
                .password("pass")
                .build());

        mockMvc.perform(get("/api/user/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("getuser"));
    }

    @Test
    void testUpdateUser() throws Exception {
        User saved = userRepository.save(User.builder()
                .username("oldname")
                .password("123")
                .build());

        User update = User.builder()
                .username("newname")
                .password("456")
                .build();

        mockMvc.perform(put("/api/user/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newname"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User saved = userRepository.save(User.builder()
                .username("todelete")
                .password("123")
                .build());

        mockMvc.perform(delete("/api/user/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }
}
