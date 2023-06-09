//package com.uit.flowerstore.services;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.uit.flowerstore.domain.UserOrder;
//import com.uit.flowerstore.repository.UserOrderRepository;
//
//@Service
//public class UserOrderService {
//
//    private final UserOrderRepository userOrderRepository;
//
//    @Autowired
//    public UserOrderService(UserOrderRepository userOrderRepository) {
//        this.userOrderRepository = userOrderRepository;
//    }
//
//    public List<UserOrder> findAll() {
//        return userOrderRepository.findAll();
//    }
//
//    public UserOrder findById(long id) {
//        return userOrderRepository.findById(id).orElse(null);
//    }
//
//    public UserOrder save(UserOrder userOrder) {
//        return userOrderRepository.save(userOrder);
//    }
//
//    public void deleteById(long id) {
//        userOrderRepository.deleteById(id);
//    }
//
//    public void deleteAll() {
//        userOrderRepository.deleteAll();
//    }
//}

package com.uit.flowerstore.services;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uit.flowerstore.domain.User;
import com.uit.flowerstore.domain.UserOrder;
import com.uit.flowerstore.repository.UserOrderRepository;
import com.uit.flowerstore.repository.UserRepository;
import com.uit.flowerstore.security.services.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserOrderService {

    private final UserOrderRepository userOrderRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserOrderService(UserOrderRepository userOrderRepository, UserRepository userRepository) {
        this.userOrderRepository = userOrderRepository;
        this.userRepository = userRepository;
    }

    public List<UserOrder> getUserOrders(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            return userOrderRepository.findAllByUser(user);
        }
        return Collections.emptyList();
    }
    public UserOrder createOrder(UserOrder userOrder, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            userOrder.setUser(user);
            return userOrderRepository.save(userOrder);
        }
        throw new EntityNotFoundException("User not found");
    }
    public UserOrder getOrderById(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            return userOrderRepository.findByIdAndUser(id, user).orElse(null);
        }
        throw new EntityNotFoundException("Không tìm thấy người dùng");
    }

    public void deleteOrder(Long id, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            userOrderRepository.deleteByIdAndUser(id, user);
        }
    }

    public UserOrder updateOrder(Long id, UserOrder updatedOrder, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            UserOrder existingOrder = userOrderRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id: " + id));
            existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
            return userOrderRepository.save(existingOrder);
        }
        return null;
    }

    public List<UserOrder> getAllOrders() {
        return userOrderRepository.findAll();
    }

    public void deleteOrder(Long id) {
        userOrderRepository.deleteById(id);
    }

    public UserOrder updateOrder(Long id, UserOrder updatedOrder) {
        UserOrder existingOrder = userOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng với id: " + id));

        existingOrder.setOrderStatus(updatedOrder.getOrderStatus());
        return userOrderRepository.save(existingOrder);
    }
}
