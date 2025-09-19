//// UserContentController.java
//package lk.ijse.gdse72.backend.controller;
//
//import lk.ijse.gdse72.backend.dto.UserPurchasedContentDTO;
//import lk.ijse.gdse72.backend.service.UserContentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/user-content")
//public class UserContentController {
//
//    @Autowired
//    private UserContentService userContentService;
//
//    @GetMapping("/purchased")
//    public ResponseEntity<UserPurchasedContentDTO> getPurchasedContent(@RequestHeader("userId") Long userId) {
//        try {
//            UserPurchasedContentDTO purchasedContent = userContentService.getPurchasedContentByUserId(userId);
//            return ResponseEntity.ok(purchasedContent);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//}