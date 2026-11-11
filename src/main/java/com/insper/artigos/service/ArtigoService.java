package com.insper.artigos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.insper.artigos.dto.ArtigoRequest;
import com.insper.artigos.dto.ArtigoResponse;
import com.insper.artigos.model.Artigo;
import com.insper.artigos.repository.ArtigoRepository;

@Service
public class ArtigoService {

    private final ArtigoRepository repository;

    public ArtigoService(ArtigoRepository repository) {
        this.repository = repository;
    }

    public ArtigoResponse cadastrar(ArtigoRequest request) {
        Artigo artigo = new Artigo();
        artigo.setTitulo(request.titulo());
        artigo.setDescricao(request.descricao());
        artigo.setPrioridade(request.prioridade());
        artigo.setEmailCriador(request.emailCriador());
        return ArtigoResponse.from(repository.save(artigo));
    }

    public List<ArtigoResponse> listar() {
        return repository.findAllByOrderByIdDesc().stream()
                .map(ArtigoResponse::from)
                .toList();
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artigo não encontrado");
        }
        repository.deleteById(id);
    }
}
