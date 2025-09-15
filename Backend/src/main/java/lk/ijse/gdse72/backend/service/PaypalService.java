//package lk.ijse.gdse72.backend.service;
//
//import com.paypal.core.PayPalEnvironment;
//import com.paypal.core.PayPalHttpClient;
//import com.paypal.orders.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Slf4j
//public class PayPalService {
//
//    private final PayPalEnvironment environment;
//    private final PayPalHttpClient client;
//
//    public PayPalService(
//            @Value("${paypal.client.id}") String clientId,
//            @Value("${paypal.client.secret}") String clientSecret,
//            @Value("${paypal.mode}") String mode) {
//
//        if ("live".equals(mode)) {
//            this.environment = new PayPalEnvironment.Live(clientId, clientSecret);
//        } else {
//            this.environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
//        }
//        this.client = new PayPalHttpClient(environment);
//    }
//
//    public String createPayment(String amount, String currency, String description) throws IOException {
//        OrdersCreateRequest request = new OrdersCreateRequest();
//        request.requestBody(buildOrderRequest(amount, currency, description));
//
//        Order order = client.execute(request).result();
//        return extractApprovalUrl(order);
//    }
//
//    private OrderRequest buildOrderRequest(String amount, String currency, String description) {
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.checkoutPaymentIntent("CAPTURE");
//
//        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
//        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
//                .amountWithBreakdown(new AmountWithBreakdown()
//                        .currencyCode(currency)
//                        .value(amount))
//                .description(description);
//
//        purchaseUnitRequests.add(purchaseUnitRequest);
//        orderRequest.purchaseUnits(purchaseUnitRequests);
//
//        ApplicationContext applicationContext = new ApplicationContext()
//                .returnUrl("http://localhost:3000/payment/success")
//                .cancelUrl("http://localhost:3000/payment/cancel");
//        orderRequest.applicationContext(applicationContext);
//
//        return orderRequest;
//    }
//
//    private String extractApprovalUrl(Order order) {
//        return order.links().stream()
//                .filter(link -> "approve".equals(link.rel()))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("No approval URL found"))
//                .href();
//    }
//
//    public String capturePayment(String orderId) throws IOException {
//        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
//
//        Order order = client.execute(request).result();
//        return order.toString();
//    }
//}