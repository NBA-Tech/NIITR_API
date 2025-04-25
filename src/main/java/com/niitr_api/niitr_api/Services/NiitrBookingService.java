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


    public Boolean saveBookingDetails(Map<String,Object> bookingRequest){
        List<Map<String,Object>> guestsList=new ArrayList<>();

        

        for(Map<String,String> guest : (List<Map<String, String>>) bookingRequest.get("guests")){

        }
        System.out.println(bookingRequest);
        return true;

    }
}
