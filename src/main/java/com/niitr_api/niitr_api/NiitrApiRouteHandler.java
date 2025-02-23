package com.niitr_api.niitr_api;

import org.springframework.web.bind.annotation.GetMapping;
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
    public CompletableFuture<List<String>> getAllHouses() {
        return CompletableFuture.supplyAsync(() -> niitrHouseService.getAllHouses().stream().map(row -> (String) row.get("house_name")).toList());

    }
    @GetMapping("/get_rooms_using_house_id")
    public CompletableFuture<List<String>> getRoomsUsingHouseID(@RequestParam int houseId) {
       return CompletableFuture.supplyAsync(() -> {
        List<Map<String, Object>> roomDetails = niitrHouseService.getRoomDetailsUsingHouseId(houseId);

        List<String> roomNames = roomDetails.stream()
            .map(row -> (String) row.get("room_name"))
            .toList();

        return roomNames;
});

        
    }
}
 

    