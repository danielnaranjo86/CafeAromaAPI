package com.cafearoma.api.config;

import com.cafearoma.api.model.Rol;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.RolRepository;
import com.cafearoma.api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseGet(() -> rolRepository.save(new Rol(null, "ADMIN")));

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseGet(() -> rolRepository.save(new Rol(null, "CLIENTE")));

        if (!usuarioRepository.existsByCorreo("admin@cafearoma.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador Café Aroma");
            admin.setCorreo("admin@cafearoma.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin12345"));
            admin.setEstado(true);
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);
        }
    }
}