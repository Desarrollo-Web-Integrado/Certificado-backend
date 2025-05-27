package com.UTP.Certificado.service;

import com.UTP.Certificado.model.Usuario;
import com.UTP.Certificado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario si el correo no está en uso.
     */
    public Usuario registrarUsuario(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(usuario.getCorreo());

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        // Encriptar la clave antes de guardar
        String claveEncriptada = passwordEncoder.encode(usuario.getClave());
        usuario.setClave(claveEncriptada);

        return usuarioRepository.save(usuario);
    }

    /**
     * Devuelve un usuario por correo. Lanza excepción si no se encuentra.
     */
    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }
}