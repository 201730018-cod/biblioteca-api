package com.api.biblioteca.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.api.biblioteca.models.EstadoPrestamo;
import com.api.biblioteca.models.LibroModel;
import com.api.biblioteca.models.PersonaModel;
import com.api.biblioteca.models.PrestamoModel;
import com.api.biblioteca.repositories.ILibroRepository;
import com.api.biblioteca.repositories.IPersonaRepository;
import com.api.biblioteca.repositories.IPrestamoRepository;

@Service
public class PrestamoService {

    private static final int LIMITE_PRESTAMOS_POR_PERSONA = 3;

    private final IPrestamoRepository prestamoRepository;
    private final IPersonaRepository personaRepository;
    private final ILibroRepository libroRepository;

    public PrestamoService(IPrestamoRepository prestamoRepository,
            IPersonaRepository personaRepository,
            ILibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.personaRepository = personaRepository;
        this.libroRepository = libroRepository;
    }

    public List<PrestamoModel> obtenerPrestamos() {
        return prestamoRepository.findAll();
    }

    public PrestamoModel obtenerPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un prestamo con id: " + id));
    }

    public List<PrestamoModel> buscarPorEstado(EstadoPrestamo estado) {
        return prestamoRepository.findByEstado(estado);
    }

    public List<PrestamoModel> buscarPorPersona(Long idPersona) {
        return prestamoRepository.findByPersonaId(idPersona);
    }

    public List<PrestamoModel> obtenerVencidos() {
        return prestamoRepository.findByFechaDevolucionIsNullAndFechaLimiteBefore(LocalDate.now());
    }

    @Transactional
    public PrestamoModel registrarPrestamo(PrestamoModel prestamo) {

        if (prestamo.getPersona() == null || prestamo.getPersona().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar la persona");
        }
        if (prestamo.getLibro() == null || prestamo.getLibro().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el libro");
        }

        Long idPersona = prestamo.getPersona().getId();
        Long idLibro = prestamo.getLibro().getId();

        PersonaModel persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe una persona con id: " + idPersona));

        LibroModel libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un libro con id: " + idLibro));

        if (!persona.puedePedirPrestado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La persona esta " + persona.getEstado() + " y no puede pedir prestamos");
        }

        if (!libro.hayDisponibles()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay ejemplares disponibles del libro: " + libro.getTitulo());
        }

        long activos = prestamoRepository.countByPersonaIdAndEstado(idPersona, EstadoPrestamo.PRESTADO);
        if (activos >= LIMITE_PRESTAMOS_POR_PERSONA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La persona ya tiene " + activos + " prestamos activos (limite: "
                            + LIMITE_PRESTAMOS_POR_PERSONA + ")");
        }

        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
        libroRepository.save(libro);

        prestamo.setPersona(persona);
        prestamo.setLibro(libro);
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDate.now());
        }
        prestamo.setFechaLimite(prestamo.getFechaPrestamo().plusDays(PrestamoModel.DIAS_DE_PRESTAMO));
        prestamo.setFechaDevolucion(null);
        prestamo.setEstado(EstadoPrestamo.PRESTADO);

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public PrestamoModel devolverPrestamo(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese prestamo ya fue devuelto");
        }

        LibroModel libro = prestamo.getLibro();
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucion(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public void eliminarPrestamo(Long id) {
        PrestamoModel prestamo = obtenerPorId(id);
        if (prestamo.getEstado() == EstadoPrestamo.PRESTADO) {
            LibroModel libro = prestamo.getLibro();
            libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
            libroRepository.save(libro);
        }
        prestamoRepository.delete(prestamo);
    }
}