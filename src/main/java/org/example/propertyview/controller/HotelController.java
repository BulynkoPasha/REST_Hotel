package org.example.propertyview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.propertyview.dto.CreateHotelRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelShortDto;
import org.example.propertyview.service.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Hotels control")
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    @Operation(summary = "List of all hotels (short info)")
    public List<HotelShortDto> getAll() {
        return hotelService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail info about hotel")
    public HotelDetailsDto getById(@PathVariable Long id) {
        return hotelService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "New hotel creation")
    public HotelShortDto create(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.create(request);
    }

    @PostMapping("/{id}/amenities")
    @Operation(summary = "Adding amenities to hotel")
    public HotelDetailsDto addAmenities(@PathVariable Long id,
                                        @RequestBody List<String> amenities) {
        return hotelService.addAmenities(id, amenities);
    }
}
