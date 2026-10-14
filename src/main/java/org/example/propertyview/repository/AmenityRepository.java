package org.example.propertyview.repository;

import org.example.propertyview.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    List<Amenity> findByNameIn(Collection<String> names);
}
