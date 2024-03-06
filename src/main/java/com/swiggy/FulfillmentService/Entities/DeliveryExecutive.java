package com.swiggy.FulfillmentService.Entities;

import com.swiggy.FulfillmentService.DTOs.Location;
import com.swiggy.FulfillmentService.Enums.Availability;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_executives")
public class DeliveryExecutive implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    @NotEmpty(message = "First name must not be empty")
    private String firstName;

    @Column(nullable = false)
    @NotEmpty(message = "Last name must not be empty")
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Username must not be empty")
    private String username;

    @Column(nullable = false)
    @NotEmpty(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern.List({
            @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain at least one digit"),
            @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain at least one lowercase letter"),
            @Pattern(regexp = "(?=.*[A-Z]).+", message = "Password must contain at least one uppercase letter"),
    })
    private String password;

    @Column(nullable = false)
    @NotEmpty(message = "Phone number must not be empty")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits long")
    private String phone;

    @Column(nullable = false)
    private Location location;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Availability availability = Availability.AVAILABLE;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
