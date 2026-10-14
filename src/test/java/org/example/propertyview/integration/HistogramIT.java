package org.example.propertyview.integration;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HistogramIT extends AbstractIntegrationTest {

    @Test
    void histogramByCity() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Hilton", "Moscow", "Russia");
        createHotel("H3", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk").value(1))
                .andExpect(jsonPath("$.Moscow").value(2));
    }

    @Test
    void histogramByBrandAndCountry() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Hilton", "Moscow", "Russia");
        createHotel("H3", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/histogram/brand"))
                .andExpect(jsonPath("$.Hilton").value(2))
                .andExpect(jsonPath("$.Marriott").value(1));

        mockMvc.perform(get("/histogram/country"))
                .andExpect(jsonPath("$.Belarus").value(1))
                .andExpect(jsonPath("$.Russia").value(2));
    }

    @Test
    void histogramByAmenities() throws Exception {
        long h1 = createHotel("H1", "B", "Minsk", "Belarus");
        long h2 = createHotel("H2", "B", "Moscow", "Russia");
        createHotel("H3", "B", "Mogilev", "Belarus");
        addAmenities(h1, "Free WiFi", "Free parking");
        addAmenities(h2, "Free WiFi");

        mockMvc.perform(get("/histogram/amenities"))
                .andExpect(jsonPath("$['Free WiFi']").value(2))
                .andExpect(jsonPath("$['Free parking']").value(1));
    }

    @Test
    void histogram_unknownParam_returns400() throws Exception {
        mockMvc.perform(get("/histogram/stars"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Unsupported")));
    }

    @Test
    void histogram_onEmptyDatabase_returnsEmptyObject() throws Exception {
        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }
}
