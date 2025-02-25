package com.niitr_api.niitr_api.Services;

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
            (rs, rowNum) -> Map.of("user_id", rs.getLong("user_id"))
        );
        if(result.isEmpty()){
            String insertQuery = "INSERT INTO NIITR_USERS (email, password, name, phone_number) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(insertQuery, userDetails.get("email"), userDetails.get("password"), userDetails.get("name"), userDetails.get("phone_number"));
            return true;
        }
        return false;
    }


    }