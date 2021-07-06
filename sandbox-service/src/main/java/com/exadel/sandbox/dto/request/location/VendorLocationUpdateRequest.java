package com.exadel.sandbox.dto.request.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VendorLocationUpdateRequest {

    @NotNull(message = "Id is mandatory")
    private Long id;

    @Min(-90)
    @Max(90)
    private double latitude;

    @Min(-180)
    @Max(180)
    private double longitude;

    @NotNull(message = "Street is mandatory")
    @Size(min = 4, max = 80, message = "Street must be between 4 and 80 characters")
    private String street;

    @NotNull(message = "Number is mandatory")
    @Size(min = 1, max = 10, message = "Number must be between 1 and 10 characters")
    private String number;

    @NotNull(message = "City id is mandatory")
    private Long cityId;
}
