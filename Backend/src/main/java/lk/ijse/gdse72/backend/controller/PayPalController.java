//package lk.ijse.gdse72.backend.controller;
//
//import lk.ijse.gdse72.backend.dto.PayPalPaymentDTO;
//import lk.ijse.gdse72.backend.service.PayPalService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/paypal")
//@RequiredArgsConstructor
//@CrossOrigin("*")
//public class PayPalController {
//
//    private final PayPalService payPalService;
//
//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder(@RequestBody PayPalPaymentDTO paymentDTO) {
//        try {
//            String approvalUrl = payPalService.createPayment(
//                    paymentDTO.getAmount(),
//                    paymentDTO.getCurrency(),
//                    paymentDTO.getDescription()
//            );
//            return ResponseEntity.ok().body("{\"approvalUrl\": \"" + approvalUrl + "\"}");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error creating PayPal order: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/capture-order")
//    public ResponseEntity<?> captureOrder(@RequestParam String orderId) {
//        try {
//            String captureData = payPalService.capturePayment(orderId);
//            return ResponseEntity.ok().body(captureData);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error capturing PayPal order: " + e.getMessage());
//        }
//    }
//}