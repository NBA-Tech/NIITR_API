package com.niitr_api.niitr_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.niitr_api.niitr_api.Services.NiitrAdminService;
import com.niitr_api.niitr_api.Services.NiitrBookingService;
import com.niitr_api.niitr_api.Services.NiitrHouseService;
import com.niitr_api.niitr_api.Services.NiitrUserService;
import com.niitr_api.niitr_api.Services.PaymentService;
import com.atom.ots.enc.AtomEncryption;
import com.niitr_api.niitr_api.Utils.GlobalValue;
@RestController
@RequestMapping("/niitr-api")  
public class NiitrApiRouteHandler {
    public final NiitrHouseService niitrHouseService;
    public final NiitrUserService niitrUserService; 
    public final AtomEncryption atomEncryption;
    public final PaymentService paymentService;
    public final NiitrBookingService niitrBookingService;
    public final NiitrAdminService niitrAdminService;

    public NiitrApiRouteHandler(NiitrHouseService niitrHouseService, NiitrUserService niitrUserService,PaymentService paymentService,NiitrBookingService niitrBookingService,NiitrAdminService niitrAdminService) {
        this.niitrUserService = niitrUserService; 
        this.niitrHouseService = niitrHouseService;
        this.paymentService = paymentService;
        this.niitrBookingService=niitrBookingService;
        this.niitrAdminService=niitrAdminService;
        this.atomEncryption= new AtomEncryption();
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
    @GetMapping("/get_rooms_and_hotel_details")
    public CompletableFuture<Map<String, Object>> getRoomsAndHotelDetails() {
        return CompletableFuture.supplyAsync(() -> {
            try{
                List<Map<String,Object>> houseList=niitrHouseService.getHouseAndRoomDetails();
                if(houseList.size() >0){
                    Map<String, Object> resultData = new HashMap<>();
                    resultData.put("status_code", 200);
                    resultData.put("houses", houseList);
                    return resultData;
                }
                return new HashMap<String,Object>(){{
                    put("status_code", 404);
                    put("message", "No houses and rooms found.");
                }};
            }
            catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching houses and rooms.");
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

    @PostMapping("/user_login")
    public CompletableFuture<Map<String, Object>> userLogin(@RequestBody Map<String, Object> user) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String,Object> user_data = niitrUserService.userLogin(user);
                Map<String, Object> resultData = new HashMap<>();

                if((Boolean)user_data.get("is_data")){
                    resultData.put("status_code", 200);
                    resultData.put("message", "User logged in successfully.");
                    resultData.put("user_data",user_data);
                }
                else{
                    resultData.put("status_code", 400);
                    resultData.put("message", "User does not exist.");
                }

                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while logging in user.");
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
    @PostMapping("/create_hotel")
    public CompletableFuture <Map<String, Object>> createHotel(@RequestBody Map<String, Object> hotelDetails){
        return CompletableFuture.supplyAsync(() -> {
        try{
            Boolean is_created=this.niitrHouseService.createRoom(hotelDetails);
            return new HashMap<String,Object>(){{
                put("status_code", 200);
            }};

        }
        catch(Exception e){
            return new HashMap<String, Object>(){{
                put("status_code", 503);
                put("message", "An error occurred while creating hotel.");
            }};
        }

    });
}

    @PostMapping("/save_booking")
    public CompletableFuture<Map<String,Object>> save_booking(@RequestBody Map<String,Object> booking_body){
        return CompletableFuture.supplyAsync(()->{
            try {
                Boolean status=this.niitrBookingService.saveBookingDetails(booking_body);
                if(!status){
                    return new HashMap<String,Object>(){{
                        put("status_code", 500);
                        put("message","Oops! we encountered error");
                    }};
                }

                return new HashMap<String,Object>(){{
                    put("status_code", 200);
                    put("message", "Booking Successfull");
                }};
                
            } catch (Exception e) {
                return new HashMap<String,Object>(){{
                    put("status_code", 500);
                    put("message","Oops! we encountered error");
                }};
            }
        });
    } 

    @PostMapping("/get_booking_details")
    public CompletableFuture<Map<String, Object>> getBookingDetails(@RequestBody Map<String, Object> bookingFilters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>>  bookingDetails = this.niitrBookingService.getBookingDetails(bookingFilters);
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);
                resultData.put("booking_details", bookingDetails);
                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching booking details.");
                }};
            }
        });
    }
    @GetMapping("/get_count_stat_for_tables")
    public CompletableFuture<Map<String, Object>> getCountStatForTables(@RequestParam String tableNames) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> tableNamesList = Arrays.asList(tableNames.split(","));
                Map<String,Integer> countStats = this.niitrAdminService.getCountStat(tableNamesList);
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);
                resultData.put("count_stats", countStats);
                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching count stats for tables.");
                }};
            }
        });
        
    }
    @GetMapping("/get_booking_stat")
    public CompletableFuture<Map<String, Object>> getBookingStat() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> bookingStat = this.niitrAdminService.getBookingStat();
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("status_code", 200);
                resultData.put("booking_stat", bookingStat);
                return resultData;
            } catch (Exception e) {
                return new HashMap<String, Object>(){{
                    put("status_code", 503);
                    put("message", "An error occurred while fetching booking stat.");
                }};
            }
        });
        
    }
    // @GetMapping("/get_booking_details")
    // public CompletableFuture<Map<String, Object>> getBookingDetails(@RequestParam int bookingId) {
        
    // }
    @GetMapping("/get_atom_id")
    public void getAtomId() throws Exception {
          Map<String, Object> paymentDetails = new LinkedHashMap<>();
        paymentDetails.put("orderId", 123456);
        paymentDetails.put("amount", 5000);
        paymentDetails.put("userEmail", "user@example.com");
        paymentDetails.put("userMobile", "9876543210");
        paymentDetails.put("bookingId", "BK12345");

        String txnDate = "2022-03-07 20:46:00";

        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> payInstrument = new LinkedHashMap<>();

        payInstrument.put("headDetails", Map.of(
                "version", "OTSv1.1",
                "api", "AUTH",
                "platform", "FLASH"
        ));

        payInstrument.put("merchDetails", Map.of(
                "merchId", 317159,
                "userId", "",
                "password", "Test@123",
                "merchTxnId", paymentDetails.get("orderId"),
                "merchTxnDate", txnDate
        ));

        payInstrument.put("payDetails", Map.of(
                "amount", paymentDetails.get("amount"),
                "product", "NSE",
                "custAccNo", "213232323",
                "txnCurrency", "INR"
        ));

        payInstrument.put("custDetails", Map.of(
                "custEmail", paymentDetails.get("userEmail"),
                "custMobile", paymentDetails.get("userMobile")
        ));

        payInstrument.put("extras", Map.of(
                "udf1", paymentDetails.getOrDefault("bookingId", ""),
                "udf2", "",
                "udf3", "",
                "udf4", "",
                "udf5", ""
        ));

        payload.put("payInstrument", payInstrument);

        ObjectMapper objectMapper = new ObjectMapper();
        // String jsonPayload = objectMapper.writeValueAsString(payload);
        String jsonPayload="""
                {"payInstrument":{"headDetails":{"version":"OTSv1.1","api":"AUTH","platform":"FLASH"},"merchDetails":{"merchId":"317159","userId":"","password":"Test@123","merchTxnDate":"2022-03-07 20:46:00","merchTxnId":"test000123"},"payDetails":{"amount":"100","product":"NSE","custAccNo":"213232323","txnCurrency":"INR"},"custDetails":{"custEmail":"user@atomtech.in","custMobile":"9898989898"},"extras":{"udf1":"","udf2":"","udf3":"","udf4":"","udf5":""}}}
                """;
        System.out.println(jsonPayload);

        String encrypted_payload=this.atomEncryption.encrypt(jsonPayload,GlobalValue.REQ_ENC_KEY);
        
        String atom_id_encrypted=this.paymentService.paymentPostRequest(encrypted_payload);

        System.out.println("Atom ID encrypted: "+ atom_id_encrypted);

        System.out.println("encrypted_payload: "+ encrypted_payload);
        
    }

}
 

    