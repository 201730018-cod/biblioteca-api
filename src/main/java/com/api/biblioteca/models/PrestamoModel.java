package com.api.biblioteca.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestamos")
public class PrestamoModel {

    public static final int DIAS_DE_PRESTAMO = 14;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PersonaModel persona;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_libro", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LibroModel libro;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    // Se llena hasta que el libro regresa
    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.PRESTADO;

    @PrePersist
    public void onPrePersist() {
        if (this.fechaPrestamo == null) {
            this.fechaPrestamo = LocalDate.now();
        }
        if (this.fechaLimite == null) {
            this.fechaLimite = this.fechaPrestamo.plusDays(DIAS_DE_PRESTAMO);
        }
        if (this.estado == null) {
            this.estado = EstadoPrestamo.PRESTADO;
        }
    }

    // Un prestamo esta vencido si no se ha devuelto y ya paso la fecha limite
    public boolean estaVencido() {
        return this.fechaDevolucion == null
                && this.fechaLimite != null
                && this.fechaLimite.isBefore(LocalDate.now());
    }

    // Dias de retraso, 0 si va al corriente
    public long getDiasDeRetraso() {
        if (!estaVencido()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.fechaLimite, LocalDate.now());
    }

    public PrestamoModel() {
    }

    public PrestamoModel(PersonaModel persona, LibroModel libro, LocalDate fechaPrestamo) {
        this.persona = persona;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = EstadoPrestamo.PRESTADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonaModel getPersona() {
        return persona;
    }

    public void setPersona(PersonaModel persona) {
        this.persona = persona;
    }

    public LibroModel getLibro() {
        return libro;
    }

    public void setLibro(LibroModel libro) {
        this.libro = libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }
}