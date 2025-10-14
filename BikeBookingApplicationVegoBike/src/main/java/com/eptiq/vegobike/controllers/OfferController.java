package com.eptiq.vegobike.controllers;

import com.eptiq.vegobike.dtos.OfferDto;
import com.eptiq.vegobike.services.OfferService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/all")
    public List<OfferDto> getAll() {
        return offerService.getAllOffers();
    }

    @GetMapping("/{id}")
    public OfferDto getById(@PathVariable Integer id) {
        return offerService.getOfferById(id);
    }

    @PostMapping("/create")
    public OfferDto create(@RequestBody OfferDto offerDto) {
        return offerService.createOffer(offerDto);
    }

    @PutMapping("/{id}")
    public OfferDto update(@PathVariable Integer id, @RequestBody OfferDto offerDto) {
        return offerService.updateOffer(id, offerDto);
    }

    @PutMapping("/{id}/toggle-status")
    public void toggleStatus(@PathVariable Integer id) {
        offerService.toggleOfferStatus(id);
    }

    @GetMapping("/active")
    public List<OfferDto> getActiveOffers() {
        return offerService.getActiveOffers();
    }


}
