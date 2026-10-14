package com.api.biblioteca.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.biblioteca.models.LibroModel;
import com.api.biblioteca.repositories.ILibroRepository;

@Service
public class LibroService {

    private final ILibroRepository libroRepository;

    public LibroService(ILibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<LibroModel> obtenerLibros() {
        return libroRepository.findAll();
    }

    public LibroModel obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un libro con id: " + id));
    }

    public List<LibroModel> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    public List<LibroModel> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<LibroModel> obtenerDisponibles() {
        return libroRepository.findByEjemplaresDisponiblesGreaterThan(0);
    }

    public LibroModel guardarLibro(LibroModel libro) {
        // Regla: el ISBN no se puede repetir
        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un libro con el ISBN: " + libro.getIsbn());
        }
        // Al dar de alta, todos los ejemplares estan disponibles
        libro.setEjemplaresDisponibles(libro.getNoEjemplares());
        return libroRepository.save(libro);
    }

    public LibroModel actualizarLibro(Long id, LibroModel datos) {
        LibroModel libro = obtenerPorId(id);

        // Si cambia el ISBN, se revisa que el nuevo no choque con otro libro
        if (!libro.getIsbn().equals(datos.getIsbn())
                && libroRepository.existsByIsbn(datos.getIsbn())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un libro con el ISBN: " + datos.getIsbn());
        }

        int prestados = libro.getNoEjemplares() - libro.getEjemplaresDisponibles();
        if (datos.getNoEjemplares() < prestados) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede dejar el total en " + datos.getNoEjemplares()
                            + " porque hay " + prestados + " ejemplares prestados");
        }

        libro.setIsbn(datos.getIsbn());
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setEditorial(datos.getEditorial());
        libro.setAnioPublicacion(datos.getAnioPublicacion());
        libro.setNoEjemplares(datos.getNoEjemplares());
        libro.setEjemplaresDisponibles(datos.getNoEjemplares() - prestados);

        return libroRepository.save(libro);
    }

    public void eliminarLibro(Long id) {
        LibroModel libro = obtenerPorId(id);
        // Regla: no se borra un libro que anda prestado
        if (libro.getEjemplaresDisponibles() < libro.getNoEjemplares()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el libro porque tiene ejemplares prestados");
        }
        libroRepository.delete(libro);
    }
}