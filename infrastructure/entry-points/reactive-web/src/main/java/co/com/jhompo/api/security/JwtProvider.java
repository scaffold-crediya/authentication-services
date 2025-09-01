package co.com.jhompo.api.security;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import co.com.jhompo.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;


@Component
public class JwtProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime; // en milisegundos (ej: 3600000 = 1 hora)

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }


    private final RoleRepository roleRepository; // Inyectar repository

    // Constructor
    public JwtProvider(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Generar token
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // Consultar el nombre del rol desde la BD
        String roleName = roleRepository.findById(user.getRoleId())
                .map(Role::getName)
                .block(); // Solo para JWT generation

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", roleName.toUpperCase())
                .claim("roleId", user.getRoleId()) // 🔥 Usar el rol real del usuario
                .claim("userId", user.getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    // Obtener el email del token
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class); // 👈 obtén como String
        if (role == null) {
            return List.of();
        }
        return List.of(role); // lo metemos en lista
    }
}
