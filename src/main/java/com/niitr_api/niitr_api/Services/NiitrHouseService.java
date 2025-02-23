package com.niitr_api.niitr_api.Services;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NiitrHouseService {
    private final JdbcTemplate jdbcTemplate;

    public NiitrHouseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllHouses() {
        return jdbcTemplate.queryForList("SELECT house_name FROM  NIITR_HOUSES");
    }

    public List<Map<String, Object>> getRoomDetailsUsingHouseId(int houseId) {
        return jdbcTemplate.queryForList("""
            SELECT * FROM NIITR_ROOMS
            WHERE house_id = ?
                """, houseId);
    }
    
}
