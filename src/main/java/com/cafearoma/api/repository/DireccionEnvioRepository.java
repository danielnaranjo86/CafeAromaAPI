package com.cafearoma.api.repository;

import com.cafearoma.api.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Long> {

    List<DireccionEnvio> findByClienteIdClienteAndActivaTrue(Long idCliente);

    Optional<DireccionEnvio> findByIdDireccionAndClienteIdClienteAndActivaTrue(Long idDireccion, Long idCliente);

    Optional<DireccionEnvio> findByClienteIdClienteAndPredeterminadaTrueAndActivaTrue(Long idCliente);

    long countByClienteIdClienteAndActivaTrue(Long idCliente);

    @Modifying
    @Query("UPDATE DireccionEnvio d SET d.predeterminada = false WHERE d.cliente.idCliente = :idCliente")
    void quitarPredeterminada(Long idCliente);
}