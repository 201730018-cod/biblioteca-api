package com.api.biblioteca.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.biblioteca.models.EstadoPrestamo;
import com.api.biblioteca.models.PrestamoModel;

@Repository
public interface IPrestamoRepository extends JpaRepository<PrestamoModel, Long> {

    List<PrestamoModel> findByEstado(EstadoPrestamo estado);

    List<PrestamoModel> findByPersonaId(Long idPersona);

    List<PrestamoModel> findByLibroId(Long idLibro);

    // Prestamos que ya vencieron y siguen sin devolverse (reporte de morosos)
    List<PrestamoModel> findByFechaDevolucionIsNullAndFechaLimiteBefore(LocalDate fecha);

    long countByPersonaIdAndEstado(Long idPersona, EstadoPrestamo estado);
}