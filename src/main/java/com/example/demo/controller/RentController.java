package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.JwtToken;
import com.example.demo.model.RentModel;
import com.example.demo.model.response.ResponseBody;
import com.example.demo.service.IRentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RentController {
    private final IRentService rentService;

    @GetMapping("/rent")
    public  ResponseBody<Iterable<RentModel>> getAllRents(@RequestHeader("Authorization") String token) {
        token = token.substring(7); 
        JwtToken jwtToken = JwtToken.builder().token(token).build();

        Iterable<RentModel> rents = rentService.getAllRents(jwtToken);
        return new ResponseBody<>(HttpStatus.OK.value(),"Rents retrieved successfully",rents);
    }
    
    @GetMapping("/rent/{rent_id}")
    public ResponseBody<RentModel> getRentById(@RequestHeader("Authorization") String token, @PathVariable String rent_id) {
        token = token.substring(7); 
        JwtToken jwtToken = JwtToken.builder().token(token).build();
        
        RentModel rent = rentService.getRentById(rent_id, jwtToken);
        return new ResponseBody<>(HttpStatus.OK.value(),"Rent retrieved successfully",rent);
    }

    // @GetMapping("/rent/deleted")
    // public ResponseBody<Iterable<RentResponse>> getDeletedRents() {
    //     Iterable<RentResponse> rents = rentService.getDeletedRents();
    //     return new ResponseBody<>(HttpStatus.OK,"Deleted rents retrieved successfully",rents);
    // }
    
    @PostMapping("/rent")
    public ResponseBody<RentModel> createRent(@RequestHeader("Authorization") String token, 
                                            @RequestBody RentModel rentRequest,
                                            @RequestParam("image") MultipartFile file) {
        token = token.substring(7); 
        JwtToken jwtToken = JwtToken.builder().token(token).build();

        RentModel rent = rentService.saveRent(rentRequest, file, jwtToken);
        return new ResponseBody<>(HttpStatus.CREATED.value(),"Rent created successfully",rent);
    }

    @PutMapping("/rent/{rent_id}")
    public ResponseBody<RentModel> updateRent(@RequestHeader("Authorization") String token, 
                                                @PathVariable String rent_id, 
                                                @RequestBody RentModel rentRequest,
                                                @RequestParam("image") MultipartFile file) {
        token = token.substring(7); 
        JwtToken jwtToken = JwtToken.builder().token(token).build();

        RentModel rent = rentService.updateRent(rent_id, rentRequest, file,  jwtToken);
        return new ResponseBody<>(HttpStatus.OK.value(),"Rent updated successfully",rent);
    }

    // @PutMapping("/rent/early-cancel/{rent_id}")
    // public ResponseBody<RentResponse> earlyCancelRent(@PathVariable String rent_id, @RequestBody CancelRentRequest cancelRentRequest) {
    //     RentResponse rent = rentService.cancelRent(rent_id, cancelRentRequest);
    //     return new ResponseBody<>(HttpStatus.OK,"Rent cancelled successfully",rent);
    // }

    @DeleteMapping("/rent/{rent_id}")
    public ResponseBody<String> deleteRent(@RequestHeader("Authorization") String token, @PathVariable String rent_id) {
        token = token.substring(7); 
        JwtToken jwtToken = JwtToken.builder().token(token).build();

        rentService.deleteRent(rent_id, jwtToken);
        return new ResponseBody<>(HttpStatus.OK.value(),"Rent deleted successfully","Rent deleted successfully");
    }


}
