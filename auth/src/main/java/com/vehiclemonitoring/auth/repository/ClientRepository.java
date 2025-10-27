package com.vehiclemonitoring.auth.repository;

import com.vehiclemonitoring.auth.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Client
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Busca un cliente por su slug único
     * @param slug slug del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Client> findBySlug(String slug);

    /**
     * Busca un cliente por su nombre
     * @param name nombre del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Client> findByName(String name);

    /**
     * Verifica si existe un cliente con el slug dado
     * @param slug slug del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsBySlug(String slug);

    /**
     * Verifica si existe un cliente con el nombre dado
     * @param name nombre del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Busca todos los clientes activos
     * @return Lista de clientes activos
     */
    List<Client> findByIsActiveTrue();

    /**
     * Busca todos los clientes inactivos
     * @return Lista de clientes inactivos
     */
    List<Client> findByIsActiveFalse();

    /**
     * Busca clientes que contengan el texto en su nombre o slug
     * @param searchText texto a buscar
     * @return Lista de clientes que coinciden
     */
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.slug) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Client> findBySearchText(@Param("searchText") String searchText);

    /**
     * Busca clientes activos que contengan el texto en su nombre o slug
     * @param searchText texto a buscar
     * @return Lista de clientes activos que coinciden
     */
    @Query("SELECT c FROM Client c WHERE c.isActive = true AND (" +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.slug) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Client> findActiveBySearchText(@Param("searchText") String searchText);

    /**
     * Cuenta el número de clientes activos
     * @return número de clientes activos
     */
    long countByIsActiveTrue();

    /**
     * Busca todos los clientes ordenados por nombre
     * @return Lista de clientes ordenados por nombre
     */
    List<Client> findAllByOrderByNameAsc();

    /**
     * Busca clientes activos ordenados por nombre
     * @return Lista de clientes activos ordenados por nombre
     */
    List<Client> findByIsActiveTrueOrderByNameAsc();
}