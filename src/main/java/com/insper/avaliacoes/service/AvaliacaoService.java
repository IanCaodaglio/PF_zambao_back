package com.insper.avaliacoes.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.insper.avaliacoes.dto.AvaliacaoRequest;
import com.insper.avaliacoes.dto.AvaliacaoResponse;
import com.insper.avaliacoes.model.Avaliacao;
import com.insper.avaliacoes.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repository;
    private final Clock clock;

    public AvaliacaoService(AvaliacaoRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public AvaliacaoResponse cadastrar(AvaliacaoRequest request) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setEmailAvaliador(request.emailAvaliador());
        avaliacao.setEmailAvaliado(request.emailAvaliado());
        avaliacao.setComentarios(request.comentarios());
        avaliacao.setNota(request.nota());
        avaliacao.setDataAvaliacao(LocalDate.now(clock));
        return AvaliacaoResponse.from(repository.save(avaliacao));
    }

    public List<AvaliacaoResponse> listar() {
        return repository.findAllByOrderByIdDesc().stream()
                .map(AvaliacaoResponse::from)
                .toList();
    }
}
