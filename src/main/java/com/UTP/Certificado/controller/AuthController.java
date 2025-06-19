package com.UTP.Certificado.controller;

import com.UTP.Certificado.dto.LoginDTO;
import com.UTP.Certificado.dto.RegisterDTO;
import com.UTP.Certificado.model.Usuario;
import com.UTP.Certificado.repository.UsuarioRepository;
import com.UTP.Certificado.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDTO dto) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setCorreo(dto.getCorreo());
            usuario.setClave(dto.getClave()); // Se encripta en el servicio
            usuario.setRol(dto.getRolEnum()); // Método personalizado del DTO

            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO dto) {
        try {
            Usuario usuario = usuarioService.obtenerPorCorreo(dto.getCorreo());

            if (passwordEncoder.matches(dto.getClave(), usuario.getClave())) {
                return ResponseEntity.ok("Login exitoso.");
            } else {
                return ResponseEntity.status(401).body("Clave incorrecta.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    //Buscar todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }


    //Buscar usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }





    // Buscar usuario por correo (GET) (esto luego se cambiara a una clase especifica para estos casos)
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarPorCorreo(@RequestParam String correo) {
        return usuarioRepository.findByCorreo(correo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
