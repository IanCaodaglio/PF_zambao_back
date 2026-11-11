package com.insper.avaliacoes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insper.avaliacoes.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findAllByOrderByIdDesc();
}
