package com.niitr_api.niitr_api.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
    
    public Map<String,Object> getHotelMetaData(int house_id,int rooms_available){
        return jdbcTemplate.queryForMap("""
            SELECT 
                COUNT(*) AS total_rows,
                MAX(price) AS max_price,
                GROUP_CONCAT(DISTINCT room_type) AS room_type,
                GROUP_CONCAT(DISTINCT JSON_KEYS(tags)) AS room_options
            FROM NIITR_ROOMS
            WHERE house_id = ?
            AND room_available >= ?
        """, house_id, rooms_available);
        

    }
    public List<Map<String,Object>> getHouseAndRoomDetails(){
        List<Map<String,Object>> houseList = jdbcTemplate.queryForList("""
            SELECT * FROM NIITR_ROOMS AS N_ROOM INNER JOIN NIITR_HOUSES AS N_HOUSE ON N_ROOM.house_id = N_HOUSE.house_id
            """);
        return houseList;
    }

    public Map<String, Object> filterRoomsWithFilter(Map<String, Object> filter) {
        StringBuilder query = new StringBuilder("SELECT * FROM NIITR_ROOMS WHERE 1=1");
        List<Object> params = new ArrayList<>();
    
        if (filter.containsKey("house_id")) {
            query.append(" AND house_id = ?");
            params.add(filter.get("house_id"));
        }
    
        if (filter.containsKey("rooms_available")) {
            query.append(" AND room_available >= ?");
            params.add(filter.get("rooms_available"));
        }
    
        if (filter.containsKey("room_type")) {
            query.append(" AND room_type = ?");
            params.add(filter.get("room_type"));
        }
    
        if (filter.containsKey("min_price") && filter.containsKey("max_price")) {
            query.append(" AND price >= ? AND price <= ?");
            params.add(filter.get("min_price"));
            params.add(filter.get("max_price"));
        }
    
        if (filter.containsKey("is_available")) {
            query.append(" AND is_available = ?");
            params.add((boolean) filter.get("is_available") ? 1 : 0);
        }
    
        if (filter.containsKey("sort_value")) {
            if ("high_low".equals(filter.get("sort_value"))) {
                query.append(" ORDER BY price DESC");
            } else if ("low_high".equals(filter.get("sort_value"))) {
                query.append(" ORDER BY price ASC");
            }
        }
    
        if (filter.containsKey("tags") && filter.get("tags") instanceof List<?> && !((List<?>) filter.get("tags")).isEmpty()) {
            List<String> tags = (List<String>) filter.get("tags");
            if (!tags.isEmpty()) {
                query.append(" AND (");
                List<String> conditions = new ArrayList<>();
                for (String tag : tags) {
                    conditions.add("tags LIKE ?");
                    params.add("%" + tag + "%");
                }
                query.append(String.join(" OR ", conditions));
                query.append(")");
            }
        }
        
    
        if (filter.containsKey("page")) {
            int page = (int) filter.get("page");
            int offset = (page - 1) * 10;
            query.append(" LIMIT 10 OFFSET ?");
            params.add(offset);
        }
    
        List<Map<String, Object>> rooms = jdbcTemplate.queryForList(query.toString(), params.toArray());
    
        Map<String, Object> result = new HashMap<>();
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
    
    public Map<String, Object> getRoomDetails(int room_id){
        return jdbcTemplate.queryForMap("SELECT * FROM NIITR_ROOMS WHERE room_id =?", room_id);
    }

}