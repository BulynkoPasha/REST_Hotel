package org.example.propertyview.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HotelControllerIT extends AbstractIntegrationTest {
    @Test
    void createHotel_returns201AndShortForm() throws Exception {
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "name": "DoubleTree by Hilton Minsk",
                          "brand": "Hilton",
                          "address": {"houseNumber": "9", "street": "Pobediteley Avenue",
                                      "city": "Minsk", "country": "Belarus", "postCode": "220004"},
                          "contacts": {"phone": "+375 17 309-80-00", "email": "doubletreeminsk.info@hilton.com"},
                          "arrivalTime": {"checkIn": "14:00"}
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
                .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"))
                .andExpect(jsonPath("$.phone").value("+375 17 309-80-00"));
    }

    @Test
    void createHotel_withBlankName_returns400() throws Exception {
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "name": "",
                          "brand": "Hilton",
                          "address": {"houseNumber": "9", "street": "S", "city": "C",
                                      "country": "X", "postCode": "1"},
                          "contacts": {"phone": "1", "email": "a@b.com"},
                          "arrivalTime": {"checkIn": "14:00"}
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("name")));
    }

    @Test
    void createHotel_withWrongTimeFormat_returns400() throws Exception {
        mockMvc.perform(post("/hotels").contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "name": "H", "brand": "B",
                          "address": {"houseNumber": "1", "street": "S", "city": "C",
                                      "country": "X", "postCode": "1"},
                          "contacts": {"phone": "1", "email": "a@b.com"},
                          "arrivalTime": {"checkIn": "2pm"}
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returnsAllHotels() throws Exception {
        createHotel("H1", "Hilton", "Minsk", "Belarus");
        createHotel("H2", "Marriott", "Moscow", "Russia");

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getById_returnsDetails() throws Exception {
        long id = createHotel("H1", "Hilton", "Minsk", "Belarus");
        addAmenities(id, "Free WiFi");

        mockMvc.perform(get("/hotels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Hilton"))
                .andExpect(jsonPath("$.address.city").value("Minsk"))
                .andExpect(jsonPath("$.contacts.email").value("hotel@example.com"))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
                .andExpect(jsonPath("$.arrivalTime.checkOut").value("12:00"))
                .andExpect(jsonPath("$.amenities", contains("Free WiFi")));
    }

    @Test
    void getById_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/hotels/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/hotels/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAmenities_doesNotCreateDuplicates() throws Exception {
        long id = createHotel("H1", "Hilton", "Minsk", "Belarus");

        addAmenities(id, "Free WiFi", "Free parking");
        addAmenities(id, "Free parking", "Free WiFi", "Concierge");

        mockMvc.perform(get("/hotels/{id}", id))
                .andExpect(jsonPath("$.amenities", hasSize(3)))
                .andExpect(jsonPath("$.amenities",
                        containsInAnyOrder("Free WiFi", "Free parking", "Concierge")));

        assertThat(amenityRepository.count()).isEqualTo(3);
    }

    @Test
    void addAmenities_unknownHotel_returns404() throws Exception {
        mockMvc.perform(post("/hotels/{id}/amenities", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"Free WiFi\"]"))
                .andExpect(status().isNotFound());
    }
}
