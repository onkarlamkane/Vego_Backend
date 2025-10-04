package com.eptiq.vegobike.repositories;

import com.eptiq.vegobike.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    boolean existsByCityNameIgnoreCase(String cityName);
    boolean existsByCityNameIgnoreCaseAndIdNot(String cityName, Integer id);
    List<City> findByIsActive(Integer isActive);
}