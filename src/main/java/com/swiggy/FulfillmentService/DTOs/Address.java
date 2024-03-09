package com.swiggy.FulfillmentService.DTOs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {

    @NotEmpty(message = "Street must not be empty")
    @Column(nullable = false)
    private String street;

    @NotEmpty(message = "City must not be empty")
    @Column(nullable = false)
    private String city;

    @NotEmpty(message = "State must not be empty")
    @Column(nullable = false)
    private String state;

    @Pattern(regexp = "\\d{6}", message = "Zip code must be of 6 digits")
    @NotEmpty(message = "Zip code must not be empty")
    @Column(nullable = false)
    private String zipcode;
}
