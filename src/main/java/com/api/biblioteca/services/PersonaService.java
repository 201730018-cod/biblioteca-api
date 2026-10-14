package com.api.biblioteca.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.biblioteca.models.EstadoPersona;
import com.api.biblioteca.models.EstadoPrestamo;
import com.api.biblioteca.models.PersonaModel;
import com.api.biblioteca.repositories.IPersonaRepository;
import com.api.biblioteca.repositories.IPrestamoRepository;

@Service
public class PersonaService {

    private final IPersonaRepository personaRepository;
    private final IPrestamoRepository prestamoRepository;

    public PersonaService(IPersonaRepository personaRepository,
            IPrestamoRepository prestamoRepository) {
        this.personaRepository = personaRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public List<PersonaModel> obtenerPersonas() {
        return personaRepository.findAll();
    }

    public PersonaModel obtenerPorId(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe una persona con id: " + id));
    }

    public List<PersonaModel> buscarPorEstado(EstadoPersona estado) {
        return personaRepository.findByEstado(estado);
    }

    public PersonaModel guardarPersona(PersonaModel persona) {
        // Regla: el correo no se puede repetir
        if (personaRepository.existsByCorreo(persona.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una persona con el correo: " + persona.getCorreo());
        }
        // La fecha de alta y el estado los pone el sistema, no el cliente
        persona.setFechaAlta(null);
        persona.setEstado(EstadoPersona.ACTIVO);
        return personaRepository.save(persona);
    }

    public PersonaModel actualizarPersona(Long id, PersonaModel datos) {
        PersonaModel persona = obtenerPorId(id);

        if (!persona.getCorreo().equals(datos.getCorreo())
                && personaRepository.existsByCorreo(datos.getCorreo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una persona con el correo: " + datos.getCorreo());
        }

        persona.setNombre(datos.getNombre());
        persona.setApellido(datos.getApellido());
        persona.setFechaNacimiento(datos.getFechaNacimiento());
        persona.setCorreo(datos.getCorreo());
        persona.setTelefono(datos.getTelefono());
        persona.setDireccion(datos.getDireccion());

        // La contrasena solo se cambia si viene con valor
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            persona.setContrasena(datos.getContrasena());
        }
        if (datos.getEstado() != null) {
            persona.setEstado(datos.getEstado());
        }

        return personaRepository.save(persona);
    }

    public PersonaModel cambiarEstado(Long id, EstadoPersona estado) {
        PersonaModel persona = obtenerPorId(id);
        persona.setEstado(estado);
        return personaRepository.save(persona);
    }

    public void eliminarPersona(Long id) {
        PersonaModel persona = obtenerPorId(id);
        // Regla: no se da de baja a alguien que trae libros sin devolver
        long prestamosActivos = prestamoRepository.countByPersonaIdAndEstado(id, EstadoPrestamo.PRESTADO);
        if (prestamosActivos > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la persona tiene " + prestamosActivos + " prestamo(s) sin devolver");
        }
        personaRepository.delete(persona);
    }
}