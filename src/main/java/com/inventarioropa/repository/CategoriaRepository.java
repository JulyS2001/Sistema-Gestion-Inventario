package com.inventarioropa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventarioropa.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}