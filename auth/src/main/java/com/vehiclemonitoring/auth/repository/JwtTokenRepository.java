package com.vehiclemonitoring.auth.repository;

import com.vehiclemonitoring.auth.model.JwtToken;
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
 * Repositorio JPA para la entidad JwtToken
 */
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, UUID> {

    /**
     * Busca un token por su JTI (JWT ID)
     * @param jti identificador único del token
     * @return Optional con el token si existe
     */
    Optional<JwtToken> findByJti(String jti);

    /**
     * Verifica si existe un token con el JTI dado
     * @param jti identificador único del token
     * @return true si existe, false en caso contrario
     */
    boolean existsByJti(String jti);

    /**
     * Busca todos los tokens de un usuario
     * @param user usuario propietario de los tokens
     * @return Lista de tokens del usuario
     */
    List<JwtToken> findByUser(User user);

    /**
     * Busca todos los tokens de un usuario por ID
     * @param userId ID del usuario
     * @return Lista de tokens del usuario
     */
    List<JwtToken> findByUserId(UUID userId);

    /**
     * Busca todos los tokens de un cliente
     * @param client cliente propietario de los tokens
     * @return Lista de tokens del cliente
     */
    List<JwtToken> findByClient(Client client);

    /**
     * Busca todos los tokens de un cliente por ID
     * @param clientId ID del cliente
     * @return Lista de tokens del cliente
     */
    List<JwtToken> findByClientId(UUID clientId);

    /**
     * Busca tokens por tipo
     * @param tokenType tipo de token (e.g., "access", "refresh")
     * @return Lista de tokens del tipo especificado
     */
    List<JwtToken> findByTokenType(String tokenType);

    /**
     * Busca tokens activos de un usuario (no expirados ni revocados)
     * @param userId ID del usuario
     * @return Lista de tokens activos
     */
    @Query("SELECT t FROM JwtToken t WHERE t.user.id = :userId AND " +
           "t.revokedAt IS NULL AND " +
           "(t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP)")
    List<JwtToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    /**
     * Busca tokens activos de un usuario por tipo
     * @param userId ID del usuario
     * @param tokenType tipo de token
     * @return Lista de tokens activos del tipo especificado
     */
    @Query("SELECT t FROM JwtToken t WHERE t.user.id = :userId AND t.tokenType = :tokenType AND " +
           "t.revokedAt IS NULL AND " +
           "(t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP)")
    List<JwtToken> findActiveTokensByUserIdAndType(@Param("userId") UUID userId, @Param("tokenType") String tokenType);

    /**
     * Busca tokens revocados de un usuario
     * @param userId ID del usuario
     * @return Lista de tokens revocados
     */
    List<JwtToken> findByUserIdAndRevokedAtIsNotNull(UUID userId);

    /**
     * Busca tokens expirados
     * @return Lista de tokens expirados
     */
    @Query("SELECT t FROM JwtToken t WHERE t.expiresAt IS NOT NULL AND t.expiresAt <= CURRENT_TIMESTAMP")
    List<JwtToken> findExpiredTokens();

    /**
     * Busca tokens expirados de un cliente específico
     * @param clientId ID del cliente
     * @return Lista de tokens expirados del cliente
     */
    @Query("SELECT t FROM JwtToken t WHERE t.client.id = :clientId AND " +
           "t.expiresAt IS NOT NULL AND t.expiresAt <= CURRENT_TIMESTAMP")
    List<JwtToken> findExpiredTokensByClientId(@Param("clientId") UUID clientId);

    /**
     * Busca tokens que expiran antes de una fecha específica
     * @param before fecha límite
     * @return Lista de tokens que expiran antes de la fecha
     */
    @Query("SELECT t FROM JwtToken t WHERE t.expiresAt IS NOT NULL AND t.expiresAt <= :before")
    List<JwtToken> findTokensExpiringBefore(@Param("before") OffsetDateTime before);

    /**
     * Busca tokens emitidos después de una fecha específica
     * @param since fecha desde
     * @param clientId ID del cliente
     * @return Lista de tokens emitidos después de la fecha
     */
    @Query("SELECT t FROM JwtToken t WHERE t.client.id = :clientId AND t.issuedAt >= :since")
    List<JwtToken> findTokensIssuedAfterAndClientId(@Param("since") OffsetDateTime since, @Param("clientId") UUID clientId);

    /**
     * Busca tokens que fueron reemplazados por otro token
     * @param clientId ID del cliente
     * @return Lista de tokens reemplazados
     */
    List<JwtToken> findByClientIdAndReplacedByJtiIsNotNull(UUID clientId);

    /**
     * Busca el token que reemplazó a otro token específico
     * @param replacedJti JTI del token que fue reemplazado
     * @return Optional con el token de reemplazo si existe
     */
    Optional<JwtToken> findByReplacedByJti(String replacedJti);

    /**
     * Cuenta el número de tokens activos de un usuario
     * @param userId ID del usuario
     * @return número de tokens activos
     */
    @Query("SELECT COUNT(t) FROM JwtToken t WHERE t.user.id = :userId AND " +
           "t.revokedAt IS NULL AND " +
           "(t.expiresAt IS NULL OR t.expiresAt > CURRENT_TIMESTAMP)")
    long countActiveTokensByUserId(@Param("userId") UUID userId);

    /**
     * Cuenta el número total de tokens de un cliente
     * @param clientId ID del cliente
     * @return número total de tokens
     */
    long countByClientId(UUID clientId);

    /**
     * Elimina tokens expirados anteriores a una fecha específica
     * @param before fecha límite para eliminar tokens expirados
     * @return número de tokens eliminados
     */
    @Query("DELETE FROM JwtToken t WHERE t.expiresAt IS NOT NULL AND t.expiresAt <= :before")
    int deleteExpiredTokensBefore(@Param("before") OffsetDateTime before);

    /**
     * Elimina tokens revocados anteriores a una fecha específica
     * @param before fecha límite para eliminar tokens revocados
     * @return número de tokens eliminados
     */
    @Query("DELETE FROM JwtToken t WHERE t.revokedAt IS NOT NULL AND t.revokedAt <= :before")
    int deleteRevokedTokensBefore(@Param("before") OffsetDateTime before);

    /**
     * Busca tokens de un usuario ordenados por fecha de emisión descendente
     * @param userId ID del usuario
     * @return Lista de tokens ordenados por fecha de emisión
     */
    List<JwtToken> findByUserIdOrderByIssuedAtDesc(UUID userId);
}