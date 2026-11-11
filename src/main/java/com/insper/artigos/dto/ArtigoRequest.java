package com.insper.artigos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArtigoRequest(
        @NotBlank @Size(max = 255) String titulo,
        @NotBlank @Size(max = 2000) String descricao,
        @NotBlank @Pattern(regexp = "BAIXA|MEDIA|ALTA") String prioridade,
        @NotBlank @Email String emailCriador) {
}
