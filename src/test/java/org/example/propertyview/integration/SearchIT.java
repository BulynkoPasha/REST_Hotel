package org.example.propertyview.integration;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchIT extends AbstractIntegrationTest {

    @Test
    void searchByCity_isCaseInsensitive() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Hilton", "Moscow", "Russia");

        mockMvc.perform(get("/search").param("city", "minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("H1"));
    }

    @Test
    void searchByNameSubstring() throws Exception {
        createHotel("DoubleTree by Hilton", "Hilton", "Minsk", "Belarus");
        createHotel("Other", "Local", "Minsk", "Belarus");

        mockMvc.perform(get("/search").param("name", "doubletree"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("DoubleTree by Hilton"));
    }

    @Test
    void combinedParameters_useAndLogic() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Marriott", "Minsk", "Belarus");

        mockMvc.perform(get("/search").param("city", "Minsk").param("brand", "Hilton"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("H1"));
    }

    @Test
    void amenities_commaSeparated_requireAll() throws Exception {
        long both = createHotel("Both", "B", "Minsk", "Belarus");
        long onlyWifi = createHotel("OnlyWifi", "B", "Minsk", "Belarus");
        addAmenities(both, "Free WiFi", "Free parking");
        addAmenities(onlyWifi, "Free WiFi");

        mockMvc.perform(get("/search").param("amenities", "Free WiFi,Free parking"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Both"));
    }

    @Test
    void amenities_repeatedParameter_requireAll() throws Exception {
        long both = createHotel("Both", "B", "Minsk", "Belarus");
        long onlyWifi = createHotel("OnlyWifi", "B", "Minsk", "Belarus");
        addAmenities(both, "Free WiFi", "Free parking");
        addAmenities(onlyWifi, "Free WiFi");

        mockMvc.perform(get("/search")
                        .param("amenities", "Free WiFi")
                        .param("amenities", "Free parking"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Both"));
    }

    @Test
    void noParameters_returnsAll() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Hilton", "Moscow", "Russia");

        mockMvc.perform(get("/search"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void noMatches_returnsEmptyList() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");

        mockMvc.perform(get("/search").param("city", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
