package com.niitr_api.niitr_api.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NiitrHouseService {
    private final JdbcTemplate jdbcTemplate;

    public NiitrHouseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllHouses() {
        return jdbcTemplate.queryForList("SELECT house_name,house_id FROM  NIITR_HOUSES");
    }
    

public Map<String, Object> filterRoomsWithFilter(Map<String, Object> filter) {
    StringBuilder query = new StringBuilder("SELECT * FROM NIITR_ROOMS WHERE 1=1");
    StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM NIITR_ROOMS WHERE 1=1");
    
    List<Object> params = new ArrayList<>();
    List<Object> countParams = new ArrayList<>();

    if (filter.containsKey("house_id")) {
        query.append(" AND house_id = ?");
        countQuery.append(" AND house_id = ?");
        params.add(filter.get("house_id"));
        countParams.add(filter.get("house_id"));
    }

    if (filter.containsKey("rooms_available")) {
        query.append(" AND room_available >= ?");
        countQuery.append(" AND room_available >= ?");
        params.add(filter.get("rooms_available"));
        countParams.add(filter.get("rooms_available"));
    }

    int totalRows = jdbcTemplate.queryForObject(countQuery.toString(), countParams.toArray(), Integer.class);

    if (filter.containsKey("page")) {
        int page = (int) filter.get("page");
        int offset = (page - 1) * 5;
        query.append(" LIMIT 5 OFFSET ?");
        params.add(offset);
    }

    List<Map<String, Object>> rooms = jdbcTemplate.queryForList(query.toString(), params.toArray());

    Map<String, Object> result = new HashMap<>();
    result.put("total_rows", totalRows);
    result.put("rooms", rooms);

    return result;
}

    public Boolean checkAvailabilityOfHotel(Map<String, Object> bookingRequest){

        Integer count= jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM NIITR_ROOMS 
                WHERE house_id =?
                AND room_available >=?
                """, Integer.class,bookingRequest.get("house_id"), bookingRequest.get("rooms_available"));

        return count>=1;

    }
    

}
