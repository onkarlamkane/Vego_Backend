package com.eptiq.vegobike.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.eptiq.vegobike.dtos.BikeSaleDTO;
import com.eptiq.vegobike.model.BikeSale;
import com.eptiq.vegobike.model.BikeSellerDetail;

@Mapper(componentModel = "spring")
public interface BikeSaleMapper {

    // Entity -> DTO with seller detail
    @Mapping(source = "bikeSale.id", target = "id")
    @Mapping(source = "bikeSale.color", target = "color")
    @Mapping(source = "bikeSale.sellingPrice", target = "sellingPrice")
    @Mapping(source = "bikeSale.status", target = "status")
    @Mapping(source = "bikeSale.bikeCondition", target = "bikeCondition")
    @Mapping(source = "bikeSale.additionalNotes", target = "additionalNotes")
    @Mapping(source = "bikeSale.customerSellingClosingPrice", target = "customerSellingClosingPrice")
    @Mapping(source = "bikeSale.supervisorName", target = "supervisorName")
    @Mapping(source = "bikeSale.isRepairRequired", target = "isRepairRequired")
    @Mapping(source = "bikeSale.pucImage", target = "pucImage")
    @Mapping(source = "bikeSale.documentImage", target = "documentImage")
    @Mapping(source = "bikeSale.isPuc", target = "isPuc", qualifiedByName = "mapIntToBoolean")
    @Mapping(source = "bikeSale.isInsurance", target = "isInsurance", qualifiedByName = "mapIntToBoolean")
    @Mapping(source = "bikeSale.isDocument", target = "isDocument", qualifiedByName = "mapIntToBoolean")

    // Seller details
    @Mapping(source = "sellerDetail.name", target = "nameOfPerson")
    @Mapping(source = "sellerDetail.email", target = "email")
    @Mapping(source = "sellerDetail.contactNumber", target = "contactNumber")
    @Mapping(source = "sellerDetail.alternateContactNumber", target = "alternateContactNumber")
    @Mapping(source = "sellerDetail.city", target = "city")
    @Mapping(source = "sellerDetail.pincode", target = "pincode")
    @Mapping(source = "sellerDetail.address", target = "address")

    // Ignore system fields
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "sellId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BikeSaleDTO toDTO(BikeSale bikeSale, BikeSellerDetail sellerDetail);

    // DTO -> Entity
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "dto.color", target = "color")
    @Mapping(source = "dto.sellingPrice", target = "sellingPrice")
    @Mapping(source = "dto.status", target = "status")
    @Mapping(source = "dto.bikeCondition", target = "bikeCondition")
    @Mapping(source = "dto.additionalNotes", target = "additionalNotes")
    @Mapping(source = "dto.customerSellingClosingPrice", target = "customerSellingClosingPrice")
    @Mapping(source = "dto.supervisorName", target = "supervisorName")
    @Mapping(source = "dto.isRepairRequired", target = "isRepairRequired")
    @Mapping(source = "dto.pucImage", target = "pucImage")
    @Mapping(source = "dto.documentImage", target = "documentImage")
    @Mapping(source = "dto.isPuc", target = "isPuc", qualifiedByName = "mapBooleanToInt")
    @Mapping(source = "dto.isInsurance", target = "isInsurance", qualifiedByName = "mapBooleanToInt")
    @Mapping(source = "dto.isDocument", target = "isDocument", qualifiedByName = "mapBooleanToInt")

    // Ignore system fields here too
    @Mapping(target = "addedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "sellId", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BikeSale toEntity(BikeSaleDTO dto);

    // ✅ Overload for service calls without seller detail
    default BikeSaleDTO toDTO(BikeSale bikeSale) {
        return toDTO(bikeSale, null);
    }

    // Converters
    @Named("mapIntToBoolean")
    default Boolean mapIntToBoolean(int value) {
        return value == 1;
    }

    @Named("mapBooleanToInt")
    default int mapBooleanToInt(Boolean value) {
        return value != null && value ? 1 : 0;
    }
}

