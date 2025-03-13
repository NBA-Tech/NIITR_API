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
import com.niitr_api.niitr_api.Services.NiitrUserService;
import com.niitr_api.niitr_api.Services.PaymentService;
import com.niitr_api.niitr_api.Utils.AESEncryption;;
@RestController
@RequestMapping("/niitr-api")  
public class NiitrApiRouteHandler {
    public final NiitrHouseService niitrHouseService;
    public final NiitrUserService niitrUserService; 
    public final AESEncryption AESEncryption;
    public final PaymentService paymentService;

    public NiitrApiRouteHandler(NiitrHouseService niitrHouseService, NiitrUserService niitrUserService, AESEncryption AESEncryption,PaymentService paymentService) {
        this.AESEncryption = AESEncryption;  
        this.niitrUserService = niitrUserService; 
        this.niitrHouseService = niitrHouseService;
        this.paymentService = paymentService;
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
    @GetMapping("/get_hote_meta_data")
    public CompletableFuture<Map<String, Object>> getHotelMetaData(@RequestParam int houseId,@RequestParam int rooms_available) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> hotelMetaData = niitrHouseService.getHotelMetaData(houseId,rooms_available);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);

                if (hotelMetaData.isEmpty()) {
                    resultData.put("message", "No hotel found with the given id.");
                } else {
                    resultData.put("hotel_meta_data", hotelMetaData);
                }

                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching hotel metadata.");
                }};
            }
        });
    }
    @PostMapping("/filter_rooms")
    public CompletableFuture<Map<String, Object>> filterRooms(@RequestBody Map<String, Object> filter) {
        return CompletableFuture.supplyAsync(() -> {
            try{
                Map<String, Object> roomDetails = niitrHouseService.filterRoomsWithFilter(filter);

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
                System.out.println(e);
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

    @PostMapping("/user_register")
    public CompletableFuture<Map<String, Object>> userRegister(@RequestBody Map<String, Object> user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean isRegistered= niitrUserService.registerUser(user);
                Map<String, Object> resultData = new HashMap<>();

                if(isRegistered){
                    resultData.put("status_code", 200);
                    resultData.put("message", "User registered successfully.");
                }
                else{
                    resultData.put("status_code", 400);
                    resultData.put("message", "User already exists.");
                }
                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while registering user.");
                }};
            }
        });
    }

    @GetMapping("/get_room_details")
    public CompletableFuture<Map<String, Object>> getRoomDetails(@RequestParam int roomId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> roomDetails = niitrHouseService.getRoomDetails(roomId);

                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);

                if (roomDetails.isEmpty()) {
                    resultData.put("message", "No room found with the given id.");
                } else {
                    resultData.put("room_details", roomDetails);
                }

                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching room details.");
                }};
            }
        });

    }
    @GetMapping("/get_atom_id")
    public void getAtomId() throws Exception {
        Map<String, Object> paymentDetails = Map.of(
            "orderId", 123456,
            "amount", 5000,
            "userEmail", "user@example.com",
            "userMobile", "9876543210",
            "bookingId", "BK12345"
        );

        String txnDate = "2024-03-13T10:15:30Z"; 

        Map<String, Object> payload = Map.of(
            "payInstrument", Map.of(
                "headDetails", Map.of(
                    "version", "OTSv1.1",
                    "api", "AUTH",
                    "platform", "FLASH"
                ),
                "merchDetails", Map.of(
                    "merchId", 317159,
                    "userId", "",
                    "password", "Test@123",
                    "merchTxnId", paymentDetails.get("orderId"),
                    "merchTxnDate", txnDate
                ),
                "payDetails", Map.of(
                    "amount", paymentDetails.get("amount"),
                    "product", "NSE",
                    "custAccNo", "213232323",
                    "txnCurrency", "INR"
                ),
                "custDetails", Map.of(
                    "custEmail", paymentDetails.get("userEmail"),
                    "custMobile", paymentDetails.get("userMobile")
                ),
                "extras", Map.of(
                    "udf1", paymentDetails.getOrDefault("bookingId", ""),
                    "udf2", "",
                    "udf3", "",
                    "udf4", "",
                    "udf5", ""
                ),
                "payModeSpecificData", Map.of(
                    "subChannel", "DC"
                )
            )
        );

        String encrypted_payload=this.AESEncryption.encrypt(payload);
        
        String atom_id_encrypted=this.paymentService.paymentPostRequest(encrypted_payload);

        System.out.println("Atom ID encrypted: "+ atom_id_encrypted);

        System.out.println("encrypted_payload: "+ encrypted_payload);
        
    }

}
 

    