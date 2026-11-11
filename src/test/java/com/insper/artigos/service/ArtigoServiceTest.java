package com.insper.artigos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.insper.artigos.dto.ArtigoRequest;
import com.insper.artigos.dto.ArtigoResponse;
import com.insper.artigos.model.Artigo;
import com.insper.artigos.repository.ArtigoRepository;

class ArtigoServiceTest {

    private final ArtigoRepository repository = mock(ArtigoRepository.class);
    private final ArtigoService service = new ArtigoService(repository);

    @Test
    void cadastrarSalvaTodosOsCamposEDevolveResposta() {
        when(repository.save(any(Artigo.class))).thenAnswer(inv -> {
            Artigo a = inv.getArgument(0);
            a.setId(7L);
            return a;
        });

        ArtigoResponse response = service.cadastrar(
                new ArtigoRequest("Docker", "Como subir containers", "ALTA", "a@x.com"));

        ArgumentCaptor<Artigo> captor = ArgumentCaptor.forClass(Artigo.class);
        verify(repository).save(captor.capture());
        assertEquals("Docker", captor.getValue().getTitulo());
        assertEquals("Como subir containers", captor.getValue().getDescricao());
        assertEquals("ALTA", captor.getValue().getPrioridade());
        assertEquals("a@x.com", captor.getValue().getEmailCriador());
        assertEquals(7L, response.id());
        assertEquals("Docker", response.titulo());
        assertEquals("Como subir containers", response.descricao());
        assertEquals("ALTA", response.prioridade());
        assertEquals("a@x.com", response.emailCriador());
    }

    @Test
    void listarConverteTodosOsArtigos() {
        Artigo a = new Artigo();
        a.setId(1L);
        a.setTitulo("t");
        a.setDescricao("d");
        a.setPrioridade("BAIXA");
        a.setEmailCriador("a@x.com");
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of(a));

        List<ArtigoResponse> lista = service.listar();

        assertEquals(1, lista.size());
        assertEquals("BAIXA", lista.get(0).prioridade());
    }

    @Test
    void listarSemDadosDevolveListaVazia() {
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of());
        assertTrue(service.listar().isEmpty());
    }

    @Test
    void removerExistenteApagaPeloId() {
        when(repository.existsById(3L)).thenReturn(true);

        service.remover(3L);

        verify(repository).deleteById(3L);
    }

    @Test
    void removerInexistenteLancaNotFoundENaoApaga() {
        when(repository.existsById(9L)).thenReturn(false);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> service.remover(9L));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        verify(repository, never()).deleteById(any());
    }
}
