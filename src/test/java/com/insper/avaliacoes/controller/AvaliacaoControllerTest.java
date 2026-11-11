package com.insper.avaliacoes.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class AvaliacaoControllerTest {

    private static final String BODY_VALIDO = """
            {"emailAvaliador":"a@x.com","emailAvaliado":"b@x.com","comentarios":"Muito bom","nota":8}
            """;

    @Autowired
    private MockMvc mvc;

    // Evita qualquer chamada de rede ao Auth0 durante os testes.
    @MockBean
    private JwtDecoder jwtDecoder;

    private RequestPostProcessor admin() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private RequestPostProcessor user() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Test
    void getSemTokenRetorna401() throws Exception {
        mvc.perform(get("/avaliacoes")).andExpect(status().isUnauthorized());
    }

    @Test
    void getComUserRetorna200() throws Exception {
        mvc.perform(get("/avaliacoes").with(user())).andExpect(status().isOk());
    }

    @Test
    void getComAdminRetorna200() throws Exception {
        mvc.perform(get("/avaliacoes").with(admin())).andExpect(status().isOk());
    }

    @Test
    void getSemPapelRetorna403() throws Exception {
        mvc.perform(get("/avaliacoes").with(jwt())).andExpect(status().isForbidden());
    }

    @Test
    void postComUserRetorna403() throws Exception {
        mvc.perform(post("/avaliacoes").with(user())
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void postSemTokenRetorna401() throws Exception {
        mvc.perform(post("/avaliacoes")
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postComAdminCriaEPassaAAparecerNaListagem() throws Exception {
        mvc.perform(post("/avaliacoes").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nota").value(8))
                .andExpect(jsonPath("$.dataAvaliacao").isNotEmpty());

        mvc.perform(get("/avaliacoes").with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailAvaliado").value("b@x.com"));
    }

    @Test
    void postComNotaForaDoIntervaloRetorna400() throws Exception {
        String body = """
                {"emailAvaliador":"a@x.com","emailAvaliado":"b@x.com","comentarios":"x","nota":11}
                """;
        mvc.perform(post("/avaliacoes").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSemNotaRetorna400() throws Exception {
        String body = """
                {"emailAvaliador":"a@x.com","emailAvaliado":"b@x.com","comentarios":"x"}
                """;
        mvc.perform(post("/avaliacoes").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postComEmailInvalidoRetorna400() throws Exception {
        String body = """
                {"emailAvaliador":"nao-e-email","emailAvaliado":"b@x.com","comentarios":"x","nota":5}
                """;
        mvc.perform(post("/avaliacoes").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
