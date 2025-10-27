package com.vehiclemonitoring.auth.repository;

import com.vehiclemonitoring.auth.model.User;
import com.vehiclemonitoring.auth.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad User
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca un usuario por su email
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por email y cliente
     * @param email email del usuario
     * @param client cliente al que pertenece
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmailAndClient(String email, Client client);

    /**
     * Busca un usuario por email y cliente ID
     * @param email email del usuario
     * @param clientId ID del cliente
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmailAndClientId(String email, UUID clientId);

    /**
     * Verifica si existe un usuario con el email dado
     * @param email email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado en un cliente específico
     * @param email email del usuario
     * @param clientId ID del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmailAndClientId(String email, UUID clientId);

    /**
     * Busca todos los usuarios de un cliente
     * @param client cliente
     * @return Lista de usuarios del cliente
     */
    List<User> findByClient(Client client);

    /**
     * Busca todos los usuarios de un cliente por ID
     * @param clientId ID del cliente
     * @return Lista de usuarios del cliente
     */
    List<User> findByClientId(UUID clientId);

    /**
     * Busca usuarios activos de un cliente
     * @param clientId ID del cliente
     * @return Lista de usuarios activos
     */
    List<User> findByClientIdAndIsActiveTrue(UUID clientId);

    /**
     * Busca todos los usuarios activos
     * @return Lista de usuarios activos
     */
    List<User> findByIsActiveTrue();

    /**
     * Busca usuarios con email verificado
     * @param clientId ID del cliente
     * @return Lista de usuarios con email verificado
     */
    List<User> findByClientIdAndIsEmailVerifiedTrue(UUID clientId);

    /**
     * Busca usuarios por rol
     * @param roleName nombre del rol
     * @param clientId ID del cliente
     * @return Lista de usuarios con el rol especificado
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.client.id = :clientId")
    List<User> findByRoleNameAndClientId(@Param("roleName") String roleName, @Param("clientId") UUID clientId);

    /**
     * Busca usuarios que contengan el texto en su email o nombre completo
     * @param searchText texto a buscar
     * @param clientId ID del cliente
     * @return Lista de usuarios que coinciden
     */
    @Query("SELECT u FROM User u WHERE u.client.id = :clientId AND (" +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<User> findBySearchTextAndClientId(@Param("searchText") String searchText, @Param("clientId") UUID clientId);

    /**
     * Busca usuarios que iniciaron sesión después de una fecha específica
     * @param since fecha desde
     * @param clientId ID del cliente
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM User u WHERE u.client.id = :clientId AND u.lastLogin >= :since")
    List<User> findByLastLoginAfterAndClientId(@Param("since") OffsetDateTime since, @Param("clientId") UUID clientId);

    /**
     * Busca usuarios que nunca han iniciado sesión
     * @param clientId ID del cliente
     * @return Lista de usuarios que nunca han iniciado sesión
     */
    List<User> findByClientIdAndLastLoginIsNull(UUID clientId);

    /**
     * Cuenta el número de usuarios activos de un cliente
     * @param clientId ID del cliente
     * @return número de usuarios activos
     */
    long countByClientIdAndIsActiveTrue(UUID clientId);

    /**
     * Cuenta el número total de usuarios de un cliente
     * @param clientId ID del cliente
     * @return número total de usuarios
     */
    long countByClientId(UUID clientId);

    /**
     * Busca usuarios con permisos específicos
     * @param permissionName nombre del permiso
     * @param clientId ID del cliente
     * @return Lista de usuarios con el permiso
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r JOIN r.permissions p " +
           "WHERE p.name = :permissionName AND u.client.id = :clientId")
    List<User> findByPermissionNameAndClientId(@Param("permissionName") String permissionName, @Param("clientId") UUID clientId);

    /**
     * Busca todos los usuarios de un cliente ordenados por fecha de creación
     * @param clientId ID del cliente
     * @return Lista de usuarios ordenados por fecha de creación descendente
     */
    List<User> findByClientIdOrderByCreatedAtDesc(UUID clientId);
}