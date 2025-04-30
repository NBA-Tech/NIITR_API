package com.niitr_api.niitr_api.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NiitrHouseService {
    private final JdbcTemplate jdbcTemplate;
    private final CloudNiaryService cloudinaryService;

    public NiitrHouseService(JdbcTemplate jdbcTemplate, CloudNiaryService cloudNiaryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.cloudinaryService = cloudNiaryService;
    }

    public List<Map<String, Object>> getAllHouses() {
        return jdbcTemplate.queryForList("SELECT house_name,house_id FROM  NIITR_HOUSES");
    }

    public Map<String, Object> getHotelMetaData(int house_id, int rooms_available) {
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

    public List<Map<String, Object>> getHouseAndRoomDetails() {
        List<Map<String, Object>> houseList = jdbcTemplate.queryForList(
                """
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

        if (filter.containsKey("tags") && filter.get("tags") instanceof List<?>
                && !((List<?>) filter.get("tags")).isEmpty()) {
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

    public Boolean checkAvailabilityOfHotel(Map<String, Object> bookingRequest) {

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM NIITR_ROOMS
                WHERE house_id =?
                AND room_available >=?
                """, Integer.class, bookingRequest.get("house_id"), bookingRequest.get("rooms_available"));

        return count >= 1;

    }

    public Map<String, Object> getRoomDetails(int room_id) {
        return jdbcTemplate.queryForMap("SELECT * FROM NIITR_ROOMS WHERE room_id =?", room_id);
    }

    public Boolean createRoom(Map<String, Object> roomDetails) {
        List<String> base64_image = new ArrayList<String>();
        JSONObject room_tags = new JSONObject();
        if (((String) roomDetails.get("banner_image")).length() > 0) {
            base64_image.add((String) roomDetails.get("banner_image"));
        }
        if (roomDetails.get("room_images") instanceof ArrayList && ((ArrayList) roomDetails.get("room_images")).size() > 0) {
            for (Object image : (ArrayList) roomDetails.get("room_images")) {
                String temp_image = image.toString();
                base64_image.add(temp_image);

            }
        }
        if(roomDetails.get("room_tags") instanceof ArrayList && ((ArrayList) roomDetails.get("room_tags")).size() > 0){

        for (Object tag : (ArrayList) roomDetails.get("room_tags")) {
            String temp_tag = (String) tag.toString();
            room_tags.put(temp_tag.split(":")[0], temp_tag.split(":")[1]);

            }
        }
        String banner_url=null;
        List<String> image_url = new ArrayList<String>();
        if(base64_image.size() != 0){
            image_url = this.cloudinaryService.putCloudinaryImage(base64_image);
            banner_url = image_url.remove(0);
            
        }

        StringBuilder query = new StringBuilder("INSERT INTO NIITR_ROOMS (");
        List<Object> params = new ArrayList<>();
       
        String imageUrlJson = image_url.isEmpty() ? null : new JSONArray(image_url).toString();

        String tagsJson = room_tags.isEmpty() ? null : room_tags.toString();
        Integer roomId = (int)roomDetails.get("room_id");

        if (roomId != -1) {
            query.append("room_id, ");
            params.add(roomId);
        }
    
        query.append("""
            house_id, hotel_name, room_type, bed_type, description, room_available, is_available, 
            tags, price, image_url, banner_url, gst
            ) VALUES (
        """);
    
        if (roomId != -1) query.append("?, ");
    
        query.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
        params.add(roomDetails.get("house_id"));
        params.add(roomDetails.get("hotel_name"));
        params.add(roomDetails.get("room_type"));
        params.add(roomDetails.get("bed_type"));
        params.add(roomDetails.get("description"));
        params.add(roomDetails.get("room_available"));
        params.add(roomDetails.get("is_available"));
        params.add(tagsJson);
        params.add(roomDetails.get("price"));
        params.add(imageUrlJson);
        params.add(banner_url);
        params.add(roomDetails.get("gst"));
    
        if (roomId != -1) {
            query.append("""
                ON DUPLICATE KEY UPDATE 
                    house_id = COALESCE(VALUES(house_id), house_id),
                    hotel_name = COALESCE(VALUES(hotel_name), hotel_name),
                    room_type = COALESCE(VALUES(room_type), room_type),
                    bed_type = COALESCE(VALUES(bed_type), bed_type),
                    description = COALESCE(VALUES(description), description),
                    room_available = COALESCE(VALUES(room_available), room_available),
                    is_available = COALESCE(VALUES(is_available), is_available),
                    tags = COALESCE(VALUES(tags), tags),
                    price = COALESCE(VALUES(price), price),
                    image_url = COALESCE(VALUES(image_url), image_url),
                    banner_url = COALESCE(VALUES(banner_url), banner_url),
                    gst = COALESCE(VALUES(gst), gst)
            """);
        }
        try {
            this.jdbcTemplate.update(query.toString(), params.toArray());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

}

