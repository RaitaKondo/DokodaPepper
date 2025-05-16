package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long>{

    Optional<City> findById(City city);
}
