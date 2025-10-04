package com.eptiq.vegobike.mappers;

import com.eptiq.vegobike.dtos.InvoiceDto;
import com.eptiq.vegobike.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface InvoiceMapper {


    InvoiceDto toDTO(Invoice invoice);

}


