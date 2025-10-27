package com.vehiclemonitoring.auth.repository;

import com.vehiclemonitoring.auth.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Permission
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Busca un permiso por su nombre
     * @param name nombre del permiso
     * @return Optional con el permiso si existe
     */
    Optional<Permission> findByName(String name);

    /**
     * Verifica si existe un permiso con el nombre dado
     * @param name nombre del permiso
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);

    /**
     * Busca permisos que contengan el texto en su nombre o descripción
     * @param searchText texto a buscar
     * @return Lista de permisos que coinciden
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Permission> findBySearchText(@Param("searchText") String searchText);

    /**
     * Busca todos los permisos ordenados por nombre
     * @return Lista de permisos ordenados por nombre
     */
    List<Permission> findAllByOrderByNameAsc();

    /**
     * Busca permisos por lista de nombres
     * @param names lista de nombres de permisos
     * @return Lista de permisos encontrados
     */
    List<Permission> findByNameIn(List<String> names);

    /**
     * Busca permisos asignados a un rol específico
     * @param roleId ID del rol
     * @return Lista de permisos asignados al rol
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") UUID roleId);

    /**
     * Busca permisos asignados a roles de un cliente específico
     * @param clientId ID del cliente
     * @return Lista de permisos asignados a roles del cliente
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r WHERE r.client.id = :clientId")
    List<Permission> findByClientId(@Param("clientId") UUID clientId);

    /**
     * Busca permisos asignados a un usuario específico (a través de sus roles)
     * @param userId ID del usuario
     * @return Lista de permisos del usuario
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    List<Permission> findByUserId(@Param("userId") UUID userId);

    /**
     * Busca permisos asignados a un usuario en un cliente específico
     * @param userId ID del usuario
     * @param clientId ID del cliente
     * @return Lista de permisos del usuario en el cliente
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u " +
           "WHERE u.id = :userId AND r.client.id = :clientId")
    List<Permission> findByUserIdAndClientId(@Param("userId") UUID userId, @Param("clientId") UUID clientId);

    /**
     * Cuenta el número total de permisos
     * @return número total de permisos
     */
    @Query("SELECT COUNT(p) FROM Permission p")
    long countTotalPermissions();

    /**
     * Cuenta el número de roles que tienen un permiso específico
     * @param permissionId ID del permiso
     * @return número de roles con el permiso
     */
    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") UUID permissionId);

    /**
     * Busca permisos que no están asignados a ningún rol
     * @return Lista de permisos no asignados
     */
    @Query("SELECT p FROM Permission p WHERE p.roles IS EMPTY")
    List<Permission> findUnassignedPermissions();

    /**
     * Busca permisos que no están asignados a roles de un cliente específico
     * @param clientId ID del cliente
     * @return Lista de permisos no asignados al cliente
     */
    @Query("SELECT p FROM Permission p WHERE p NOT IN " +
           "(SELECT DISTINCT perm FROM Permission perm JOIN perm.roles r WHERE r.client.id = :clientId)")
    List<Permission> findUnassignedPermissionsByClientId(@Param("clientId") UUID clientId);

    /**
     * Busca permisos que comienzan con un prefijo específico
     * @param prefix prefijo del nombre del permiso
     * @return Lista de permisos que comienzan con el prefijo
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE CONCAT(:prefix, '%')")
    List<Permission> findByNameStartingWith(@Param("prefix") String prefix);

    /**
     * Busca los permisos más utilizados (asignados a más roles)
     * @param limit número máximo de resultados
     * @return Lista de permisos más utilizados
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r GROUP BY p ORDER BY COUNT(r) DESC")
    List<Permission> findMostUsedPermissions(@Param("limit") int limit);
}