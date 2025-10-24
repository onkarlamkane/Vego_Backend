package com.eptiq.vegobike.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.model.BikeSale;
import com.eptiq.vegobike.model.BikeSellerDetail;

@Mapper(componentModel = "spring")
public interface BikeSaleMapper {

    // Entity + SellerDetail → DTO
    @Mapping(source = "bikeSale.id", target = "id")
    @Mapping(source = "bikeSale.status", target = "listingStatus")
    @Mapping(source = "sellerDetail.name", target = "name")
    @Mapping(source = "sellerDetail.email", target = "email")
    @Mapping(source = "sellerDetail.contactNumber", target = "contactNumber")
    @Mapping(source = "sellerDetail.alternateContactNumber", target = "alternateContactNumber")
    @Mapping(source = "sellerDetail.city", target = "city")
    @Mapping(source = "sellerDetail.pincode", target = "pincode")
    @Mapping(source = "sellerDetail.address", target = "address")
    @Mapping(source = "bikeSale.isPuc", target = "isPuc", qualifiedByName = "mapIntToBoolean")
    @Mapping(source = "bikeSale.isInsurance", target = "isInsurance", qualifiedByName = "mapIntToBoolean")
    @Mapping(source = "bikeSale.isDocument", target = "isDocument", qualifiedByName = "mapIntToBoolean")
    @Mapping(source = "bikeSale.isRepairRequired", target = "isRepairRequired", qualifiedByName = "mapIntToBoolean")
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "sellId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BikeSaleDTO toDTO(BikeSale bikeSale, BikeSellerDetail sellerDetail);

    // DTO → Entity
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "dto.listingStatus", target = "status")
    @Mapping(source = "dto.isPuc", target = "isPuc", qualifiedByName = "mapBooleanToInt")
    @Mapping(source = "dto.isInsurance", target = "isInsurance", qualifiedByName = "mapBooleanToInt")
    @Mapping(source = "dto.isDocument", target = "isDocument", qualifiedByName = "mapBooleanToInt")
    @Mapping(source = "dto.isRepairRequired", target = "isRepairRequired", qualifiedByName = "mapBooleanToInt")
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "sellId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BikeSale toEntity(BikeSaleDTO dto);

    // Overload for service calls without seller detail
    default BikeSaleDTO toDTO(BikeSale bikeSale) {
        return toDTO(bikeSale, null);
    }

    // Custom converters
    @Named("mapIntToBoolean")
    default Boolean mapIntToBoolean(Integer value) {
        return value != null && value == 1;
    }

    @Named("mapBooleanToInt")
    default Integer mapBooleanToInt(Boolean value) {
        return (value != null && value) ? 1 : 0;
    }
}
