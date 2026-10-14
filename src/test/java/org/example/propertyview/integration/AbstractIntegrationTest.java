package org.example.propertyview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.example.propertyview.repository.AmenityRepository;
import org.example.propertyview.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected HotelRepository hotelRepository;

    @Autowired
    protected AmenityRepository amenityRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        hotelRepository.deleteAll();
        amenityRepository.deleteAll();
    }

    protected long createHotel(String name, String brand, String city, String country) throws Exception {
        String body = """
                {
                  "name": "%s",
                  "brand": "%s",
                  "address": {"houseNumber": "9", "street": "Pobediteley Avenue",
                              "city": "%s", "country": "%s", "postCode": "220004"},
                  "contacts": {"phone": "+375 17 309-80-00", "email": "hotel@example.com"},
                  "arrivalTime": {"checkIn": "14:00", "checkOut": "12:00"}
                }
                """.formatted(name, brand, city, country);

        MvcResult result = mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    protected void addAmenities(long hotelId, String... names) throws Exception {
        mockMvc.perform(post("/hotels/{id}/amenities", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(names)))
                .andExpect(status().isOk());
    }
}
