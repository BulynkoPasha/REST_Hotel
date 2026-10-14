package org.example.propertyview.repository;

import org.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @EntityGraph(attributePaths = "amenities")
    Optional<Hotel> findWithAmenitiesById(Long id);

    @Query("select h.brand as label, count(h) as total from Hotel h group by h.brand")
    List<HistogramRow> countByBrand();

    @Query("select h.address.city as label, count(h) as total from Hotel h group by h.address.city")
    List<HistogramRow> countByCity();

    @Query("select h.address.country as label, count(h) as total from Hotel h group by h.address.country")
    List<HistogramRow> countByCountry();

    @Query("select a.name as label, count(h) as total from Hotel h join h.amenities a group by a.name")
    List<HistogramRow> countByAmenity();
}
