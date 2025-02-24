package com.niitr_api.niitr_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.niitr_api.niitr_api.Services.NiitrHouseService;

@RestController
@RequestMapping("/niitr-api")  
public class NiitrApiRouteHandler {
    public final NiitrHouseService niitrHouseService;

    public NiitrApiRouteHandler(NiitrHouseService niitrHouseService) {
        this.niitrHouseService = niitrHouseService;
    }
    @GetMapping("/get_all_houses")
    public CompletableFuture<Map<String, Object>> getAllHouses() {
        return CompletableFuture.supplyAsync(()->{
            try {
                List<Map<String, Object>> houseDetails=niitrHouseService.getAllHouses();

                Map<String, Object> resultData = new HashMap<>();

                resultData.put("status_code", 200);
                if(houseDetails.isEmpty()) {
                    resultData.put("message", "No houses found.");
                    return resultData;
                }
                else{
                    resultData.put("houses", houseDetails);
                    return resultData;
                }
                
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 500);
                    put("message", "An error occurred while fetching houses.");
                }};
            }
            
        });

    }
    @PostMapping("/filter_rooms")
    public CompletableFuture<Map<String, Object>> filterRooms(@RequestBody Map<String, Object> filter) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                List<Map<String, Object>> roomDetails = niitrHouseService.filterRoomsWithFilter(filter);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);
    
                if (roomDetails.isEmpty()) {
                    resultData.put("message", "No rooms found matching the filter criteria.");
                } else {
                    resultData.put("rooms", roomDetails); 
                }
    
                return resultData;

            }
            catch(Exception e){
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while filtering rooms.");
                }};
            }
           
        });
    }

    @PostMapping("/check_hotel_availability")
    public CompletableFuture<Map<String, Object>> checkHotelAvailability(@RequestBody Map<String, Object> bookingRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
            boolean isHotelAvailable = niitrHouseService.checkAvailabilityOfHotel(bookingRequest);
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("status_code", 200);
            resultData.put("is_hotel_available", isHotelAvailable);

            return resultData;

                
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while checking hotel availability.");
                }};
            }

        });
    }
}
 

    