package org.example.propertyview.mapper;

import org.example.propertyview.dto.CreateHotelRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelShortDto;
import org.example.propertyview.entity.Address;
import org.example.propertyview.entity.Amenity;
import org.example.propertyview.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    Hotel toEntity(CreateHotelRequest request);

    @Mapping(target = "address", expression = "java(formatAddress(hotel.getAddress()))")
    @Mapping(target = "phone", source = "contacts.phone")
    HotelShortDto toShortDto(Hotel hotel);

    HotelDetailsDto toDetailsDto(Hotel hotel);

    default List<String> amenitiesToNames(Set<Amenity> amenities) {
        return amenities.stream().map(Amenity::getName).sorted().toList();
    }

    default String formatAddress(Address a) {
        return "%s %s, %s, %s, %s".formatted(
                a.getHouseNumber(), a.getStreet(), a.getCity(), a.getPostCode(), a.getCountry());
    }
}
