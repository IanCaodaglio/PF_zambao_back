package com.insper.avaliacoes.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emailAvaliador;

    @Column(nullable = false)
    private String emailAvaliado;

    @Column(nullable = false, length = 1000)
    private String comentarios;

    @Column(nullable = false)
    private int nota;

    @Column(nullable = false)
    private LocalDate dataAvaliacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAvaliador() {
        return emailAvaliador;
    }

    public void setEmailAvaliador(String emailAvaliador) {
        this.emailAvaliador = emailAvaliador;
    }

    public String getEmailAvaliado() {
        return emailAvaliado;
    }

    public void setEmailAvaliado(String emailAvaliado) {
        this.emailAvaliado = emailAvaliado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
}
