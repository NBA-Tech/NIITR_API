package com.niitr_api.niitr_api.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class NiitrUserService {
    private final JdbcTemplate jdbcTemplate;

    public NiitrUserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean registerUser(Map<String, Object> userDetails){
        String checkQuery = "SELECT * FROM NIITR_USERS WHERE  email = ?";

        List<Map<String, Object>> result = jdbcTemplate.query(checkQuery, new Object[]{userDetails.get("email")}, 
            (rs, rowNum) -> Map.of("id", rs.getLong("id"))
        );
        if(result.isEmpty()){
            String insertQuery = "INSERT INTO NIITR_USERS (email, password, name, phone_number) VALUES (?,?,?,?)";
            jdbcTemplate.update(insertQuery, userDetails.get("email"), userDetails.get("password"), userDetails.get("name"), userDetails.get("phone_number"));
            return true;
        }
        return false;
    }

    public Map<String, Object> userLogin(Map<String, Object> userDetails) {
        String checkQuery = "SELECT * FROM NIITR_USERS WHERE email = ? AND password = ?";

        List<Map<String, Object>> result = jdbcTemplate.query(checkQuery, 
            new Object[]{userDetails.get("email"), userDetails.get("password")}, 
            (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getLong("id"));
                map.put("email", rs.getString("email"));
                map.put("name", rs.getString("name")); // example fields
                return map;
            }
        );

        if (result.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("is_data", false);
            return response;
        }

        Map<String, Object> userData = result.get(0);
        userData.put("is_data", true);
        return userData;
    }

    public  List<Map<String, Object>>  getGuestsDetails(){
        String checkQuery = "SELECT * FROM NIITR_GUESTS";

        List<Map<String, Object>> result = jdbcTemplate.query(checkQuery, 
            (rs, rowNum) -> Map.of("gender", rs.getString("gender"))
        );
        return result;
    }


}