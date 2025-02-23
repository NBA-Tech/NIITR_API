package com.niitr_api.niitr_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public CompletableFuture<List<Map<String, Object>>> getAllHouses() {
        return CompletableFuture.supplyAsync(()->{
            List<Map<String, Object>> houseDetails=niitrHouseService.getAllHouses();
            return houseDetails;    
        });

    }
    @PostMapping("/filter_rooms")
    public CompletableFuture<List<Map<String, Object>>> filterRooms(@RequestBody Map<String, Object> filter) {
       return CompletableFuture.supplyAsync(() -> {
        List<Map<String, Object>> roomDetails = niitrHouseService.filterRoomsWithFilter(filter);

        return roomDetails;
});

        
    }
}
 

    