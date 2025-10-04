package com.eptiq.vegobike.services;

import com.eptiq.vegobike.model.BookingStatus;
import java.util.List;

public interface BookingStatusService {
    List<BookingStatus> getAllStatuses();
    BookingStatus getStatusById(int id);
}
