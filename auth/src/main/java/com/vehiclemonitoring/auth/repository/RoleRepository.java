package com.vehiclemonitoring.auth.repository;

import com.vehiclemonitoring.auth.model.Role;
import com.vehiclemonitoring.auth.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Role
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Busca un rol por su nombre y cliente
     * @param name nombre del rol
     * @param client cliente al que pertenece
     * @return Optional con el rol si existe
     */
    Optional<Role> findByNameAndClient(String name, Client client);

    /**
     * Busca un rol por su nombre y cliente ID
     * @param name nombre del rol
     * @param clientId ID del cliente
     * @return Optional con el rol si existe
     */
    Optional<Role> findByNameAndClientId(String name, UUID clientId);

    /**
     * Verifica si existe un rol con el nombre dado en un cliente específico
     * @param name nombre del rol
     * @param clientId ID del cliente
     * @return true si existe, false en caso contrario
     */
    boolean existsByNameAndClientId(String name, UUID clientId);

    /**
     * Busca todos los roles de un cliente
     * @param client cliente
     * @return Lista de roles del cliente
     */
    List<Role> findByClient(Client client);

    /**
     * Busca todos los roles de un cliente por ID
     * @param clientId ID del cliente
     * @return Lista de roles del cliente
     */
    List<Role> findByClientId(UUID clientId);

    /**
     * Busca roles del sistema (roles predefinidos)
     * @param clientId ID del cliente
     * @return Lista de roles del sistema
     */
    List<Role> findByClientIdAndIsSystemTrue(UUID clientId);

    /**
     * Busca roles personalizados (no del sistema)
     * @param clientId ID del cliente
     * @return Lista de roles personalizados
     */
    List<Role> findByClientIdAndIsSystemFalse(UUID clientId);

    /**
     * Busca roles que contengan el texto en su nombre o descripción
     * @param searchText texto a buscar
     * @param clientId ID del cliente
     * @return Lista de roles que coinciden
     */
    @Query("SELECT r FROM Role r WHERE r.client.id = :clientId AND (" +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Role> findBySearchTextAndClientId(@Param("searchText") String searchText, @Param("clientId") UUID clientId);

    /**
     * Busca todos los roles de un cliente ordenados por nombre
     * @param clientId ID del cliente
     * @return Lista de roles ordenados por nombre
     */
    List<Role> findByClientIdOrderByNameAsc(UUID clientId);

    /**
     * Busca roles por lista de nombres en un cliente específico
     * @param names lista de nombres de roles
     * @param clientId ID del cliente
     * @return Lista de roles encontrados
     */
    List<Role> findByNameInAndClientId(List<String> names, UUID clientId);

    /**
     * Busca roles que tienen un permiso específico
     * @param permissionName nombre del permiso
     * @param clientId ID del cliente
     * @return Lista de roles que tienen el permiso
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName AND r.client.id = :clientId")
    List<Role> findByPermissionNameAndClientId(@Param("permissionName") String permissionName, @Param("clientId") UUID clientId);

    /**
     * Busca roles asignados a un usuario específico
     * @param userId ID del usuario
     * @param clientId ID del cliente
     * @return Lista de roles asignados al usuario
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId AND r.client.id = :clientId")
    List<Role> findByUserIdAndClientId(@Param("userId") UUID userId, @Param("clientId") UUID clientId);

    /**
     * Cuenta el número total de roles de un cliente
     * @param clientId ID del cliente
     * @return número total de roles
     */
    long countByClientId(UUID clientId);

    /**
     * Cuenta el número de roles del sistema de un cliente
     * @param clientId ID del cliente
     * @return número de roles del sistema
     */
    long countByClientIdAndIsSystemTrue(UUID clientId);

    /**
     * Cuenta el número de usuarios que tienen un rol específico
     * @param roleId ID del rol
     * @return número de usuarios con el rol
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") UUID roleId);

    /**
     * Busca roles que no tienen usuarios asignados
     * @param clientId ID del cliente
     * @return Lista de roles sin usuarios
     */
    @Query("SELECT r FROM Role r WHERE r.client.id = :clientId AND r.users IS EMPTY")
    List<Role> findUnassignedRolesByClientId(@Param("clientId") UUID clientId);
}