package com.api.biblioteca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.biblioteca.models.EstadoPersona;
import com.api.biblioteca.models.PersonaModel;

@Repository
public interface IPersonaRepository extends JpaRepository<PersonaModel, Long> {

    boolean existsByCorreo(String correo);

    List<PersonaModel> findByEstado(EstadoPersona estado);
}