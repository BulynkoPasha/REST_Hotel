package org.example.propertyview.unit;

import org.example.propertyview.dto.AddressDto;
import org.example.propertyview.dto.ArrivalTimeDto;
import org.example.propertyview.dto.ContactsDto;
import org.example.propertyview.dto.CreateHotelRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelShortDto;
import org.example.propertyview.entity.Address;
import org.example.propertyview.entity.Amenity;
import org.example.propertyview.entity.ArrivalTime;
import org.example.propertyview.entity.Contacts;
import org.example.propertyview.entity.Hotel;
import org.example.propertyview.mapper.HotelMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;


class HotelMapperTest {

    private final HotelMapper mapper = Mappers.getMapper(HotelMapper.class);

    private Hotel sampleHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("DoubleTree by Hilton Minsk");
        hotel.setBrand("Hilton");
        hotel.setAddress(new Address("9", "Pobediteley Avenue", "Minsk", "Belarus", "220004"));
        hotel.setContacts(new Contacts("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"));
        hotel.setArrivalTime(new ArrivalTime(LocalTime.of(14, 0), null));
        return hotel;
    }

    @Test
    void toShortDto_formatsAddressAndTakesPhone() {
        HotelShortDto dto = mapper.toShortDto(sampleHotel());

        assertThat(dto.address()).isEqualTo("9 Pobediteley Avenue, Minsk, 220004, Belarus");
        assertThat(dto.phone()).isEqualTo("+375 17 309-80-00");
    }

    @Test
    void toDetailsDto_sortsAmenitiesAlphabetically() {
        Hotel hotel = sampleHotel();
        hotel.getAmenities().add(new Amenity("Free WiFi"));
        hotel.getAmenities().add(new Amenity("Concierge"));

        HotelDetailsDto dto = mapper.toDetailsDto(hotel);

        assertThat(dto.amenities()).containsExactly("Concierge", "Free WiFi");
        assertThat(dto.address().city()).isEqualTo("Minsk");
        assertThat(dto.arrivalTime().checkOut()).isNull();
    }

    @Test
    void toEntity_ignoresIdAndAmenities() {
        CreateHotelRequest request = new CreateHotelRequest("H", null, "B",
                new AddressDto("1", "S", "C", "X", "1"),
                new ContactsDto("1", "a@b.com"),
                new ArrivalTimeDto(LocalTime.of(14, 0), null));

        Hotel hotel = mapper.toEntity(request);

        assertThat(hotel.getId()).isNull();
        assertThat(hotel.getAmenities()).isEmpty();
        assertThat(hotel.getAddress().getCity()).isEqualTo("C");
    }
}
