package com.example.demo.model;

import java.time.LocalDateTime;
import java.time.LocalDate;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceModel {
    private String invoice_id;  
    
    private double waterUnit;  // input
    private double waterPrice;
    private double waterTotal;
    
    private double electUnit; // input
    private double electPrice;
    private double electTotal;
    
    private double roomUnit;
    private double roomPrice;
    private double roomTotal;
    private double total;

    private String qrcode;
    private String monthly;  // input
    private String dueDate; // input
    private String recordDate; // input
    
    private RentModel rent; // input rent id
    private DormModel dorm; // input dorm id
    // private 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


}
