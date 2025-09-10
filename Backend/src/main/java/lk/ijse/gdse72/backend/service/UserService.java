package lk.ijse.gdse72.backend.service;

import lk.ijse.gdse72.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findByUsername(String username);
    Optional<User> findById(Long id);
    User save(User user);
    void delete(Long id);
    List<User> findAll();
}