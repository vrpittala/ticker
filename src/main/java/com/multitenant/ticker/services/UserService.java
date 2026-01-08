package com.multitenant.ticker.services;

import com.multitenant.ticker.entity.UserEntity;
import com.multitenant.ticker.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private  final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean userExistsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public UserEntity resolveUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElse(null);
    }

    public Boolean userExistsByUsernameAndTenantId(String username, UUID tenantId) {
        return this.userRepository.findByUsernameAndTenantId(username, tenantId).isPresent();
    }

    public UserEntity resolveUserByUsernameAndTenantId(String username, UUID tenantId) {
        return this.userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElse(null);
    }

    public void saveUser(UserEntity user) {
        this.userRepository.save(user);
    }
}
