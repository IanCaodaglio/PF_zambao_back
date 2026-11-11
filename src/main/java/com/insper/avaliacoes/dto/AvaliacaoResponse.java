package com.insper.avaliacoes.dto;

import java.time.LocalDate;

import com.insper.avaliacoes.model.Avaliacao;

public record AvaliacaoResponse(
        Long id,
        String emailAvaliador,
        String emailAvaliado,
        String comentarios,
        int nota,
        LocalDate dataAvaliacao) {

    public static AvaliacaoResponse from(Avaliacao a) {
        return new AvaliacaoResponse(
                a.getId(),
                a.getEmailAvaliador(),
                a.getEmailAvaliado(),
                a.getComentarios(),
                a.getNota(),
                a.getDataAvaliacao());
    }
}
