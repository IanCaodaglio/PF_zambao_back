package com.insper.avaliacoes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.insper.avaliacoes.dto.AvaliacaoRequest;
import com.insper.avaliacoes.dto.AvaliacaoResponse;
import com.insper.avaliacoes.model.Avaliacao;
import com.insper.avaliacoes.repository.AvaliacaoRepository;

class AvaliacaoServiceTest {

    private final AvaliacaoRepository repository = mock(AvaliacaoRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-11T12:00:00Z"), ZoneOffset.UTC);
    private final AvaliacaoService service = new AvaliacaoService(repository, clock);

    @Test
    void cadastrarSalvaComDataDoRelogioEDevolveResposta() {
        when(repository.save(any(Avaliacao.class))).thenAnswer(inv -> {
            Avaliacao a = inv.getArgument(0);
            a.setId(7L);
            return a;
        });

        AvaliacaoResponse response = service.cadastrar(
                new AvaliacaoRequest("a@x.com", "b@x.com", "Ótimo trabalho", 9));

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(repository).save(captor.capture());
        assertEquals(LocalDate.of(2026, 11, 11), captor.getValue().getDataAvaliacao());
        assertEquals(7L, response.id());
        assertEquals("a@x.com", response.emailAvaliador());
        assertEquals("b@x.com", response.emailAvaliado());
        assertEquals("Ótimo trabalho", response.comentarios());
        assertEquals(9, response.nota());
        assertEquals(LocalDate.of(2026, 11, 11), response.dataAvaliacao());
    }

    @Test
    void listarConverteTodasAsAvaliacoes() {
        Avaliacao a = new Avaliacao();
        a.setId(1L);
        a.setEmailAvaliador("a@x.com");
        a.setEmailAvaliado("b@x.com");
        a.setComentarios("ok");
        a.setNota(5);
        a.setDataAvaliacao(LocalDate.of(2026, 1, 2));
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of(a));

        List<AvaliacaoResponse> lista = service.listar();

        assertEquals(1, lista.size());
        assertEquals(5, lista.get(0).nota());
    }

    @Test
    void listarSemDadosDevolveListaVazia() {
        when(repository.findAllByOrderByIdDesc()).thenReturn(List.of());
        assertTrue(service.listar().isEmpty());
    }
}
