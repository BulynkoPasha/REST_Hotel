package org.example.propertyview.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank String houseNumber,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String country,
        @NotBlank String postCode) {
}
