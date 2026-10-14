package org.example.propertyview.service;

import lombok.RequiredArgsConstructor;
import org.example.propertyview.dto.CreateHotelRequest;
import org.example.propertyview.dto.HotelDetailsDto;
import org.example.propertyview.dto.HotelShortDto;
import org.example.propertyview.entity.Amenity;
import org.example.propertyview.entity.Hotel;
import org.example.propertyview.exception.HotelNotFoundException;
import org.example.propertyview.exception.InvalidParameterException;
import org.example.propertyview.mapper.HotelMapper;
import org.example.propertyview.repository.AmenityRepository;
import org.example.propertyview.repository.HistogramRow;
import org.example.propertyview.repository.HotelRepository;
import org.example.propertyview.repository.specification.HotelSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper mapper;

    @Transactional(readOnly = true)
    public List<HotelShortDto> getAll() {
        return hotelRepository.findAll().stream().map(mapper::toShortDto).toList();
    }

    @Transactional(readOnly = true)
    public HotelDetailsDto getById(Long id) {
        return hotelRepository.findWithAmenitiesById(id)
                .map(mapper::toDetailsDto)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<HotelShortDto> search(HotelSearchCriteria c) {
        Specification<Hotel> spec = Specification.where(null);

        if (hasText(c.name())) spec = spec.and(HotelSpecifications.nameContains(c.name().trim()));
        if (hasText(c.brand())) spec = spec.and(HotelSpecifications.brandEquals(c.brand().trim()));
        if (hasText(c.city())) spec = spec.and(HotelSpecifications.cityEquals(c.city().trim()));
        if (hasText(c.country())) spec = spec.and(HotelSpecifications.countryEquals(c.country().trim()));
        if (c.amenities() != null) {
            for (String amenity : c.amenities()) {
                if (hasText(amenity)) spec = spec.and(HotelSpecifications.hasAmenity(amenity.trim()));
            }
        }
        return hotelRepository.findAll(spec).stream().map(mapper::toShortDto).toList();
    }

    @Transactional
    public HotelShortDto create(CreateHotelRequest request) {
        Hotel saved = hotelRepository.save(mapper.toEntity(request));
        return mapper.toShortDto(saved);
    }

    @Transactional
    public HotelDetailsDto addAmenities(Long hotelId, List<String> names) {
        Hotel hotel = hotelRepository.findWithAmenitiesById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        Set<String> requested = names.stream()
                .filter(n -> n != null && !n.isBlank())
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Amenity> known = amenityRepository.findByNameIn(requested).stream()
                .collect(Collectors.toMap(Amenity::getName, Function.identity()));

        List<Amenity> toAttach = new ArrayList<>();
        for (String name : requested) {
            Amenity amenity = known.get(name);
            if (amenity == null) {
                amenity = amenityRepository.save(new Amenity(name));
            }
            toAttach.add(amenity);
        }

        hotel.getAmenities().addAll(toAttach);
        return mapper.toDetailsDto(hotel);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> histogram(String param) {
        List<HistogramRow> rows = switch (param.toLowerCase()) {
            case "brand" -> hotelRepository.countByBrand();
            case "city" -> hotelRepository.countByCity();
            case "country" -> hotelRepository.countByCountry();
            case "amenities" -> hotelRepository.countByAmenity();
            default -> throw new InvalidParameterException(
                    "Unsupported histogram parameter: " + param
                            + ". Allowed: brand, city, country, amenities");
        };
        Map<String, Long> result = new LinkedHashMap<>();
        rows.forEach(r -> result.put(r.getLabel(), r.getTotal()));
        return result;
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }


}
