package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.VehicleTypeDto;
import java.util.List;

public interface VehicleTypeService {
    List<VehicleTypeDto> getAll();
    List<VehicleTypeDto> getActive();
    VehicleTypeDto getById(Integer id);
    VehicleTypeDto create(VehicleTypeDto dto);
    VehicleTypeDto update(Integer id, VehicleTypeDto dto);
    VehicleTypeDto toggleStatus(Integer id);

}
