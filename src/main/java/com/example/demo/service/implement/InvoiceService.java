package com.example.demo.service.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Invoice;
// import com.example.demo.entity.Rent;
import com.example.demo.exception.NotFoundException;
import com.example.demo.middleware.JwtService;
import com.example.demo.model.BuildingModel;
import com.example.demo.model.DormModel;
import com.example.demo.model.InvoiceDetail;
import com.example.demo.model.InvoiceModel;
import com.example.demo.model.JwtToken;
import com.example.demo.model.RentModel;
import com.example.demo.model.Role;
import com.example.demo.model.RoomModel;
import com.example.demo.model.TenantModel;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.service.IInvoiceService;
import com.example.demo.service.IRentService;
import com.example.demo.util.converter.InvoiceConverter;
import com.example.demo.util.validator.InvoiceValidator;
import com.example.demo.util.validator.RoleValidation;
import com.example.demo.webClient.IDormService;
import com.example.demo.webClient.IRoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {
    private final InvoiceRepository invoiceRepository;
    
    private final IRentService rentService;
    private final IDormService dormService;
    private final IRoomService roomService;
    private final JwtService jwtService;


    @Override
    public InvoiceModel createInvoice(InvoiceModel invoiceModel, JwtToken token){ 
        Role role = jwtService.extractRole(token.getToken());
        RoleValidation.allowRoles(role, Role.ADMIN);

        InvoiceValidator.validateInvoice(invoiceModel);

        RentModel rentModel = rentService.getRentById(invoiceModel.getRent().getRentId(), token);
        DormModel dormModel = dormService.getDormInfo(token);
        TenantModel tenantModel = rentModel.getTenant();
        RoomModel roomModel = roomService.getRoom(rentModel.getRoom().getRoomID(), token);
        
        BuildingModel buildingModel = dormService.getBuilding(roomModel.getBuildingID(), token);
        
        double electPerUnit = buildingModel.getElecPrice();
        double waterPerUnit = buildingModel.getWaterPrice();
        String promptPay = dormModel.getTelephone();
        int basePrice = roomModel.getRoomPrice();

        InvoiceDetail invoiceDetail = new InvoiceDetail(invoiceModel.getWaterUnit(), 
                                                        waterPerUnit, 
                                                        invoiceModel.getElectUnit(), 
                                                        electPerUnit, 
                                                        basePrice,
                                                        promptPay);

        
        

        Invoice invoice = InvoiceConverter.toEntity(invoiceModel, rentModel);
        Invoice invoiceEntity = invoiceRepository.save(invoice);

        return InvoiceConverter.toModel(invoiceEntity, invoiceDetail, dormModel, rentModel, tenantModel, roomModel);
    }
    
    @Override    
    public InvoiceModel getInvoiceById(String invoice_id, JwtToken token) {
        Role role = jwtService.extractRole(token.getToken());
        RoleValidation.allowRoles(role, Role.ADMIN, Role.TENANT);

        UUID uuid = UUID.fromString(invoice_id);
        Invoice invoice = invoiceRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Invoice not found"));
        
        RentModel rentModel = rentService.getRentById(invoice.getRent().getRent_id().toString(), token);
        DormModel dormModel = dormService.getDormInfo(token);
        TenantModel tenantModel = rentModel.getTenant();
        RoomModel roomModel = roomService.getRoom(rentModel.getRoom().getRoomID(), token);
        
        BuildingModel buildingModel = dormService.getBuilding(roomModel.getBuildingID(), token);
        double electPerUnit = buildingModel.getElecPrice();
        double waterPerUnit = buildingModel.getWaterPrice();
        String promptPay = dormModel.getTelephone();
        int basePrice = roomModel.getRoomPrice();
        
        InvoiceDetail invoiceDetail = new InvoiceDetail(invoice.getWaterUnit(), 
                                                    waterPerUnit, 
                                                    invoice.getElectricUnit(), 
                                                    electPerUnit, 
                                                    basePrice,
                                                    promptPay);
        
        return InvoiceConverter.toModel(invoice, invoiceDetail, dormModel, rentModel, tenantModel, roomModel);
    }

    @Override
    public Iterable<InvoiceModel> getInvoiceByRentId(String rent_id, JwtToken token) {
        Role role = jwtService.extractRole(token.getToken());
        RoleValidation.allowRoles(role, Role.ADMIN, Role.TENANT);

        Iterable<Invoice> invoices = invoiceRepository.findByRentId(UUID.fromString(rent_id));
        List<InvoiceModel> invoiceModels = new ArrayList<>();
        for (Invoice invoice : invoices) {
            invoiceModels.add(getInvoiceById(invoice.getId().toString(), token));
        }
        return invoiceModels;
    }

    @Override
    public Iterable<InvoiceModel> getAllInvoices(JwtToken token) {
        Iterable<Invoice> invoices = invoiceRepository.findAllInvoices();
        List<InvoiceModel> invoiceModels = new ArrayList<>();
        for (Invoice invoice : invoices) {
            invoiceModels.add(getInvoiceById(invoice.getId().toString(), token));
        }
        return invoiceModels;
    }

    @Override
    public InvoiceModel updateInvoiceById(String invoice_id, InvoiceModel invoiceModel, JwtToken token) {
        Role role = jwtService.extractRole(token.getToken());
        RoleValidation.allowRoles(role, Role.ADMIN);

        UUID uuid = UUID.fromString(invoice_id);
        Invoice invoice = invoiceRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Invoice not found"));

        // validate
        InvoiceValidator.validateInvoice(invoiceModel);

        invoiceModel.setCreatedAt(invoice.getCreatedAt());
        InvoiceModel new_invoiceModel = createInvoice(invoiceModel, token);
        return new_invoiceModel;
    }


    @Override
    public void deleteInvoiceById(String invoice_id, JwtToken token) {
        Role role = jwtService.extractRole(token.getToken());
        RoleValidation.allowRoles(role, Role.ADMIN);

        UUID uuid = UUID.fromString(invoice_id);
        Invoice iv = invoiceRepository.findById(uuid).orElseThrow(() -> new NotFoundException("Invoice not found"));
        iv.setDeletedAt(LocalDateTime.now());
        invoiceRepository.save(iv);
    }

}
