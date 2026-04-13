package com.example.ecommerce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ecommerce.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {

    List<Address> getUserAddresses(Long userId);

    Address getDefaultAddress(Long userId);

    Address addAddress(Long userId, Address address);

    Address updateAddress(Long id, Long userId, Address address);

    boolean deleteAddress(Long id, Long userId);

    boolean setDefaultAddress(Long id, Long userId);
}
