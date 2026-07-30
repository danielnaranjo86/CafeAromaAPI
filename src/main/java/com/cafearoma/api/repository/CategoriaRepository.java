package com.cafearoma.api.repository;

import com.cafearoma.api.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivaTrue();

    boolean existsByNombreCategoriaIgnoreCase(String nombreCategoria);
}