package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.CityDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CityService {
    List<CityDto> getAllCities();
    List<CityDto> getActiveCities();
    CityDto getCityById(Integer id);
    CityDto createCity(CityDto cityDto, MultipartFile image) throws IOException;
    CityDto updateCity(Integer id, CityDto cityDto, MultipartFile image) throws IOException;
    CityDto toggleCityStatus(Integer id);


}