package com.niitr_api.niitr_api.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
@Service
public class NiitrAdminService {
        private final JdbcTemplate jdbcTemplate;


    public NiitrAdminService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String,Integer> getCountStat(List<String> tables){

        Map<String,Integer> countStats = new HashMap<>();
        
        for(String table : tables){
            String sql = String.format("SELECT COUNT(*) FROM %s", table);
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            countStats.put(table, count);

        }
        return countStats;
    }

    public List<Map<String, Object>> getBookingStat(){
        String sqlQuery = """
            SELECT NIITR_BOOKINGS.*, house_name 
            FROM NIITR_HOUSES 
            INNER JOIN NIITR_BOOKINGS 
            ON NIITR_BOOKINGS.house_id = NIITR_HOUSES.house_id
            """;
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
        return result;
    }
    
}
