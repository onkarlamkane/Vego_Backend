package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.LateChargeRequestDTO;
import com.eptiq.vegobike.dtos.LateChargeResponseDTO;

import java.util.List;

public interface LateChargeService {
    LateChargeResponseDTO createLateCharge(LateChargeRequestDTO request);
    LateChargeResponseDTO updateLateCharge(Integer id, LateChargeRequestDTO request);
    LateChargeResponseDTO getLateChargeById(Integer id);
    List<LateChargeResponseDTO> getAllLateCharges();
    void deleteLateCharge(Integer id);
    LateChargeResponseDTO changeStatus(Integer id, Integer isActive);
    List<LateChargeResponseDTO> getAllActiveLateCharges();


}
