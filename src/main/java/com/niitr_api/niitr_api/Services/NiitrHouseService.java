package com.niitr_api.niitr_api.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Map<String, Object>> filterRoomsWithFilter(Map<String, Object> filter) {
        StringBuilder query = new StringBuilder("SELECT * FROM NIITR_ROOMS WHERE ");
        List<Object> params = new ArrayList<>();

        if (filter.containsKey("house_id")) {
            query.append("house_id = ?");
            params.add(filter.get("house_id"));
        }

        if (filter.containsKey("rooms_available")) {
            query.append(" AND room_available >= ?");
            params.add(filter.get("rooms_available"));
        }

        return jdbcTemplate.queryForList(query.toString(), params.toArray());
    }

}
