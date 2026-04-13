package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.entity.Address;
import com.example.ecommerce.security.UserPrincipal;
import com.example.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<Address>> getUserAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Address> addresses = addressService.getUserAddresses(userPrincipal.getId());
        return ApiResponse.success(addresses);
    }

    @GetMapping("/default")
    public ApiResponse<Address> getDefaultAddress(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Address address = addressService.getDefaultAddress(userPrincipal.getId());
        return ApiResponse.success(address);
    }

    @PostMapping
    public ApiResponse<Address> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Address address) {
        Address newAddress = addressService.addAddress(userPrincipal.getId(), address);
        return ApiResponse.success(newAddress);
    }

    @PutMapping("/{id}")
    public ApiResponse<Address> updateAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody Address address) {
        Address updatedAddress = addressService.updateAddress(id, userPrincipal.getId(), address);
        return ApiResponse.success(updatedAddress);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = addressService.deleteAddress(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }

    @PostMapping("/{id}/default")
    public ApiResponse<Boolean> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean result = addressService.setDefaultAddress(id, userPrincipal.getId());
        return ApiResponse.success(result);
    }
}
