package com.insper.artigos.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@AutoConfigureMockMvc
class ArtigoControllerTest {

    private static final String BODY_VALIDO = """
            {"titulo":"Docker","descricao":"Como subir containers","prioridade":"ALTA","emailCriador":"a@x.com"}
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

    private long criarArtigo() throws Exception {
        MvcResult result = mvc.perform(post("/artigos").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andReturn();
        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    // ---- GET ----

    @Test
    void getSemTokenRetorna401() throws Exception {
        mvc.perform(get("/artigos")).andExpect(status().isUnauthorized());
    }

    @Test
    void getComUserRetorna200() throws Exception {
        mvc.perform(get("/artigos").with(user())).andExpect(status().isOk());
    }

    @Test
    void getComAdminRetorna200() throws Exception {
        mvc.perform(get("/artigos").with(admin())).andExpect(status().isOk());
    }

    @Test
    void getSemPapelRetorna403() throws Exception {
        mvc.perform(get("/artigos").with(jwt())).andExpect(status().isForbidden());
    }

    // ---- POST ----

    @Test
    void postComUserRetorna403() throws Exception {
        mvc.perform(post("/artigos").with(user())
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void postSemTokenRetorna401() throws Exception {
        mvc.perform(post("/artigos")
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postComAdminCriaEPassaAAparecerNaListagem() throws Exception {
        mvc.perform(post("/artigos").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.prioridade").value("ALTA"));

        mvc.perform(get("/artigos").with(user()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Docker"));
    }

    @Test
    void postComPrioridadeInvalidaRetorna400() throws Exception {
        String body = """
                {"titulo":"t","descricao":"d","prioridade":"URGENTE","emailCriador":"a@x.com"}
                """;
        mvc.perform(post("/artigos").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSemTituloRetorna400() throws Exception {
        String body = """
                {"descricao":"d","prioridade":"BAIXA","emailCriador":"a@x.com"}
                """;
        mvc.perform(post("/artigos").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postComEmailInvalidoRetorna400() throws Exception {
        String body = """
                {"titulo":"t","descricao":"d","prioridade":"MEDIA","emailCriador":"nao-e-email"}
                """;
        mvc.perform(post("/artigos").with(admin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    // ---- DELETE ----

    @Test
    void deleteSemTokenRetorna401() throws Exception {
        mvc.perform(delete("/artigos/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void deleteComUserRetorna403() throws Exception {
        mvc.perform(delete("/artigos/1").with(user())).andExpect(status().isForbidden());
    }

    @Test
    void deleteComAdminRemoveEArtigoSomeDaListagem() throws Exception {
        long id = criarArtigo();

        mvc.perform(delete("/artigos/" + id).with(admin())).andExpect(status().isNoContent());

        mvc.perform(delete("/artigos/" + id).with(admin())).andExpect(status().isNotFound());
    }

    @Test
    void deleteDeIdInexistenteRetorna404() throws Exception {
        mvc.perform(delete("/artigos/999999").with(admin())).andExpect(status().isNotFound());
    }
}
