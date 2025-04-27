package com.niitr_api.niitr_api.Services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NiitrBookingService {
    private final JdbcTemplate jdbcTemplate;

    public NiitrBookingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int generateId(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
    
        return min + (int) (Math.random() * (max - min + 1));
    }
    
    public Boolean saveBookingDetails(Map<String, Object> bookingRequest) {
        List<Map<String, String>> guestsList = (List<Map<String, String>>) bookingRequest.remove("guests");

        String insertQueryBooking = "INSERT INTO NIITR_BOOKINGS (booking_id,house_id,total_room_book,price,room_id,start_date,end_date,gst,total_price,booking_timestamp) VALUES (?,?,?,?,?,?,?,?,?,?)";

        String insertQueryGuest = "InSERT INTO NIITR_GUESTS (guest_id,booking_id,name,gender,mobile_number) VALUES (?,?,?,?,?)";

        int bookingId = this.generateId(10);
        try {

            jdbcTemplate.update(insertQueryBooking,
                    bookingId,
                    bookingRequest.get("house_id"),
                    bookingRequest.get("total_room_book"),
                    bookingRequest.get("price"),
                    bookingRequest.get("room_id"),
                    bookingRequest.get("start_date"),
                    bookingRequest.get("end_date"),
                    bookingRequest.get("gst"),
                    bookingRequest.get("total_price"),
                    bookingRequest.get("booking_timestamp"));

            for (Map<String, String> guest : guestsList) {
                jdbcTemplate.update(insertQueryGuest,
                        this.generateId(10),
                        bookingId,
                        guest.get("name"),
                        guest.get("gender"),
                        guest.get("mobile_number"));

            }

            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
