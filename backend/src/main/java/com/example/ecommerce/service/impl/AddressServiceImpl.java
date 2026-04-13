package com.example.ecommerce.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ecommerce.entity.Address;
import com.example.ecommerce.mapper.AddressMapper;
import com.example.ecommerce.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Override
    public Address getDefaultAddress(Long userId) {
        return addressMapper.findDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public Address addAddress(Long userId, Address address) {
        address.setUserId(userId);
        address.setIsDefault(0);

        long count = count(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId));
        if (count == 0) {
            address.setIsDefault(1);
        }

        save(address);
        return address;
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, Long userId, Address address) {
        Address existing = getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        existing.setReceiverName(address.getReceiverName());
        existing.setReceiverPhone(address.getReceiverPhone());
        existing.setProvince(address.getProvince());
        existing.setCity(address.getCity());
        existing.setDistrict(address.getDistrict());
        existing.setDetailAddress(address.getDetailAddress());
        existing.setZipCode(address.getZipCode());

        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public boolean deleteAddress(Long id, Long userId) {
        Address address = getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long id, Long userId) {
        Address address = getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在");
        }

        addressMapper.clearDefault(userId);
        address.setIsDefault(1);
        return updateById(address);
    }
}
