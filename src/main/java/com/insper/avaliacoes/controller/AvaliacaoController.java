package com.insper.avaliacoes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.insper.avaliacoes.dto.AvaliacaoRequest;
import com.insper.avaliacoes.dto.AvaliacaoResponse;
import com.insper.avaliacoes.service.AvaliacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse cadastrar(@Valid @RequestBody AvaliacaoRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<AvaliacaoResponse> listar() {
        return service.listar();
    }
}
