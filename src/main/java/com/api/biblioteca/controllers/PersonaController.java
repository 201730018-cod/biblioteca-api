package com.api.biblioteca.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.models.EstadoPersona;
import com.api.biblioteca.models.PersonaModel;
import com.api.biblioteca.services.PersonaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // GET /api/personas
    // GET /api/personas?estado=ACTIVO
    @GetMapping
    public List<PersonaModel> obtenerPersonas(@RequestParam(required = false) EstadoPersona estado) {
        if (estado != null) {
            return personaService.buscarPorEstado(estado);
        }
        return personaService.obtenerPersonas();
    }

    // GET /api/personas/1
    @GetMapping("/{id}")
    public PersonaModel obtenerPorId(@PathVariable Long id) {
        return personaService.obtenerPorId(id);
    }

    // POST /api/personas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaModel guardarPersona(@Valid @RequestBody PersonaModel persona) {
        return personaService.guardarPersona(persona);
    }

    // PUT /api/personas/1
    @PutMapping("/{id}")
    public PersonaModel actualizarPersona(@PathVariable Long id, @Valid @RequestBody PersonaModel datos) {
        return personaService.actualizarPersona(id, datos);
    }

    // PATCH /api/personas/1/estado?valor=SUSPENDIDO
    @PatchMapping("/{id}/estado")
    public PersonaModel cambiarEstado(@PathVariable Long id, @RequestParam EstadoPersona valor) {
        return personaService.cambiarEstado(id, valor);
    }

    // DELETE /api/personas/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPersona(@PathVariable Long id) {
        personaService.eliminarPersona(id);
    }
}