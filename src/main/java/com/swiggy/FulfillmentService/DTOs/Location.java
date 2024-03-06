package com.swiggy.FulfillmentService.DTOs;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    private String street;
    private String city;
    private String state;

    @Pattern(regexp = "\\d{6}", message = "Zip code must be of 6 digits")
    @NotEmpty(message = "Zip code must not be empty")
    private String zipcode;
}
