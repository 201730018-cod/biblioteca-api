package com.api.biblioteca.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.models.LibroModel;
import com.api.biblioteca.services.LibroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // GET /api/libros
    // GET /api/libros?autor=garcia
    // GET /api/libros?titulo=cien
    // GET /api/libros?disponibles=true
    @GetMapping
    public List<LibroModel> obtenerLibros(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Boolean disponibles) {

        if (autor != null) {
            return libroService.buscarPorAutor(autor);
        }
        if (titulo != null) {
            return libroService.buscarPorTitulo(titulo);
        }
        if (Boolean.TRUE.equals(disponibles)) {
            return libroService.obtenerDisponibles();
        }
        return libroService.obtenerLibros();
    }

    // GET /api/libros/1
    @GetMapping("/{id}")
    public LibroModel obtenerPorId(@PathVariable Long id) {
        return libroService.obtenerPorId(id);
    }

    // POST /api/libros
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroModel guardarLibro(@Valid @RequestBody LibroModel libro) {
        return libroService.guardarLibro(libro);
    }

    // PUT /api/libros/1
    @PutMapping("/{id}")
    public LibroModel actualizarLibro(@PathVariable Long id, @Valid @RequestBody LibroModel datos) {
        return libroService.actualizarLibro(id, datos);
    }

    // DELETE /api/libros/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
    }
}