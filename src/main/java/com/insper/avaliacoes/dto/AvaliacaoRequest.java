package com.insper.avaliacoes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotBlank @Email String emailAvaliador,
        @NotBlank @Email String emailAvaliado,
        @NotBlank @Size(max = 1000) String comentarios,
        @NotNull @Min(0) @Max(10) Integer nota) {
}
