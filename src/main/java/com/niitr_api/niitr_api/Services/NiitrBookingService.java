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

        String insertQueryBooking = "INSERT INTO NIITR_BOOKINGS (booking_id,house_id,total_room_book,price,room_id,start_date,end_date,gst,total_price,booking_timestamp,booking_status,user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

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
                    bookingRequest.get("booking_timestamp"),
                    bookingRequest.get("booking_status"),
                    bookingRequest.get("user_id"));

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

    public  List<Map<String, Object>> getBookingDetails(Map<String, Object> bookingFilters) {

        StringBuilder sqlQuery=new StringBuilder("SELECT booking.*");

        if(bookingFilters.containsKey("table_filter")){
            Map<String,Object> tableFilter=(Map<String,Object>)bookingFilters.get("table_filter");
            if(tableFilter.containsKey("column_name")){
                sqlQuery.append(", "+tableFilter.get("column_name"));
            }
            sqlQuery.append(" FROM NIITR_BOOKINGS booking");

            if(tableFilter.containsKey("table_name")){
                List<Map<String, Object>> tableNames = (List<Map<String, Object>>) tableFilter.get("table_name");

                for (Map<String, Object> table : tableNames) {
                    sqlQuery.append(" INNER JOIN ")
                            .append(table.get("table_name"))
                            .append(" ON ")
                            .append(table.get("table_name"))
                            .append(".")
                            .append(table.get("pk_column"))
                            .append(" = booking.")
                            .append(table.get("fk_column"));
                }
            }
        }

        if(bookingFilters.containsKey("filters")){
            Map<String,Object> filters=(Map<String,Object>)bookingFilters.get("filters");
            sqlQuery.append(" WHERE 1=1");
            for(String key:filters.keySet()){
                sqlQuery.append(" AND "+key+"=?");
            }
        }
        List<Object> queryParams = new ArrayList<>();

        if(bookingFilters.containsKey("filters")){
            Map<String,Object> filters=(Map<String,Object>)bookingFilters.get("filters");
            for(String key:filters.keySet()){
                queryParams.add(filters.get(key));
            }
        }
        List<Map<String, Object>> resultList=jdbcTemplate.queryForList(sqlQuery.toString(),queryParams.toArray());
        return resultList;

    }

    public Boolean updateBookingStatus(int bookingId, String status,int roomId) {
        String updateBookingQuery = "UPDATE NIITR_BOOKINGS SET booking_status = ? WHERE booking_id = ?";

        String updateRoomsQuery="UPDATE NIITR_ROOMS SET room_available=room_available-1 WHERE room_id=?";
        try {
            if(status.equals("approved")){
                jdbcTemplate.update(updateRoomsQuery,roomId);
            }
            jdbcTemplate.update(updateBookingQuery, status, bookingId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
