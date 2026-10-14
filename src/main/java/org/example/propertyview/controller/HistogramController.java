package org.example.propertyview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.propertyview.service.HotelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histogram")
@Tag(name = "Histogram", description = "Hotel statistic")
public class HistogramController {

    private final HotelService hotelService;

    @GetMapping("/{param}")
    @Operation(summary = "Number of hotels grouped by brand, city, country or amenities")
    public Map<String, Long> histogram(@PathVariable String param) {
        return hotelService.histogram(param);
    }

}
