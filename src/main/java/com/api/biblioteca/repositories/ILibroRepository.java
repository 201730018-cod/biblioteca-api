package com.api.biblioteca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.biblioteca.models.LibroModel;

@Repository
public interface ILibroRepository extends JpaRepository<LibroModel, Long> {

    boolean existsByIsbn(String isbn);

    List<LibroModel> findByAutorContainingIgnoreCase(String autor);

    List<LibroModel> findByTituloContainingIgnoreCase(String titulo);

    List<LibroModel> findByEjemplaresDisponiblesGreaterThan(int cantidad);
}