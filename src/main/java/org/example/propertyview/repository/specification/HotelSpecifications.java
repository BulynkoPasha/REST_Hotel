package org.example.propertyview.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.example.propertyview.entity.Amenity;
import org.example.propertyview.entity.Hotel;
import org.springframework.data.jpa.domain.Specification;

public final class HotelSpecifications {

    public static Specification<Hotel> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Hotel> brandEquals(String brand) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Hotel> cityEquals(String city) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("address").get("city")), city.toLowerCase());
    }

    public static Specification<Hotel> countryEquals(String country) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("address").get("country")), country.toLowerCase());
    }

    public static Specification<Hotel> hasAmenity(String amenity) {
        return (root, query, cb) -> {
            Subquery<Long> sq = query.subquery(Long.class);
            Root<Hotel> sub = sq.from(Hotel.class);
            Join<Hotel, Amenity> join = sub.join("amenities");
            sq.select(sub.get("id")).where(
                    cb.equal(sub.get("id"), root.get("id")),
                    cb.equal(cb.lower(join.get("name")), amenity.toLowerCase()));
            return cb.exists(sq);
        };
    }
}
