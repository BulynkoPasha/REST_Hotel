package org.example.propertyview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.propertyview.dto.HotelShortDto;
import org.example.propertyview.service.HotelSearchCriteria;
import org.example.propertyview.service.HotelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Hotel searching")
public class SearchController {

    private final HotelService hotelService;

    @GetMapping
    @Operation(summary = "Hotel searching by name, brand, city, country, amenities",
            description = "amenities can be passed as a comma-separated list (?amenities=Free WiFi,Free parking) "
                    + "or as a repeatable parameter (?amenities=Free WiFi&amenities=Free parking)")
    public List<HotelShortDto> search(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String brand,
                                      @RequestParam(required = false) String city,
                                      @RequestParam(required = false) String country,
                                      @RequestParam(required = false) List<String> amenities) {
        return hotelService.search(new HotelSearchCriteria(name, brand, city, country, amenities));
    }

}
