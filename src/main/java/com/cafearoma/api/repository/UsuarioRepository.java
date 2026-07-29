package com.cafearoma.api.repository;

import com.cafearoma.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por correo electrónico
    Optional<Usuario> findByCorreo(String correo);

    // Verifica si ya existe un usuario registrado con ese correo
    boolean existsByCorreo(String correo);
}