package com.insper.artigos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insper.artigos.model.Artigo;

public interface ArtigoRepository extends JpaRepository<Artigo, Long> {

    List<Artigo> findAllByOrderByIdDesc();
}
