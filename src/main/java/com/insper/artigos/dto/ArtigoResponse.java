package com.insper.artigos.dto;

import com.insper.artigos.model.Artigo;

public record ArtigoResponse(
        Long id,
        String titulo,
        String descricao,
        String prioridade,
        String emailCriador) {

    public static ArtigoResponse from(Artigo a) {
        return new ArtigoResponse(
                a.getId(),
                a.getTitulo(),
                a.getDescricao(),
                a.getPrioridade(),
                a.getEmailCriador());
    }
}
