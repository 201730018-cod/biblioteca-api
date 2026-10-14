package com.api.biblioteca.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.models.EstadoPrestamo;
import com.api.biblioteca.models.PrestamoModel;
import com.api.biblioteca.services.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // GET /api/prestamos
    // GET /api/prestamos?estado=PRESTADO
    // GET /api/prestamos?persona=1
    @GetMapping
    public List<PrestamoModel> obtenerPrestamos(
            @RequestParam(required = false) EstadoPrestamo estado,
            @RequestParam(required = false) Long persona) {

        if (estado != null) {
            return prestamoService.buscarPorEstado(estado);
        }
        if (persona != null) {
            return prestamoService.buscarPorPersona(persona);
        }
        return prestamoService.obtenerPrestamos();
    }

    // GET /api/prestamos/vencidos  -> reporte de morosidad
    @GetMapping("/vencidos")
    public List<PrestamoModel> obtenerVencidos() {
        return prestamoService.obtenerVencidos();
    }

    // GET /api/prestamos/1
    @GetMapping("/{id}")
    public PrestamoModel obtenerPorId(@PathVariable Long id) {
        return prestamoService.obtenerPorId(id);
    }

    // POST /api/prestamos
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoModel registrarPrestamo(@RequestBody PrestamoModel prestamo) {
        return prestamoService.registrarPrestamo(prestamo);
    }

    // PATCH /api/prestamos/1/devolver
    @PatchMapping("/{id}/devolver")
    public PrestamoModel devolverPrestamo(@PathVariable Long id) {
        return prestamoService.devolverPrestamo(id);
    }

    // DELETE /api/prestamos/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPrestamo(@PathVariable Long id) {
        prestamoService.eliminarPrestamo(id);
    }
}