package com.eptiq.vegobike.services;

import com.eptiq.vegobike.dtos.PriceListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PriceListService {

    /**
     * Get all price lists with pagination
     */
    Page<PriceListDTO> getAllPriceLists(Pageable pageable);

    /**
     * Get all active price lists
     */
    List<PriceListDTO> getActivePriceLists();

    /**
     * Get price list by ID
     */
    PriceListDTO getPriceListById(Long id);

    /**
     * Create new price list
     */
    PriceListDTO createPriceList(PriceListDTO dto);

    /**
     * Update existing price list
     */
    PriceListDTO updatePriceList(Long id, PriceListDTO dto);

    /**
     * Delete price list
     */
    void deletePriceList(Long id);

    /**
     * Get price lists by category
     */
    List<PriceListDTO> getPriceListsByCategory(Integer categoryId);

    /**
     * Get price by category and days
     */
    PriceListDTO getPriceByCategoryAndDays(Integer categoryId, Integer days);

    /**
     * Get hourly rates
     */
    List<PriceListDTO> getHourlyRates();

    /**
     * Get daily rates
     */
    List<PriceListDTO> getDailyRates();

    /**
     * Activate/Deactivate price list
     */
    PriceListDTO updatePriceListStatus(Long id, boolean isActive);

    /**
     * Get affordable prices within budget
     */
    List<PriceListDTO> getAffordablePrices(BigDecimal maxPrice);
}
