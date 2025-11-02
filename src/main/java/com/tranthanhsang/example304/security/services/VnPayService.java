package com.tranthanhsang.example304.security.services;

import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnPayService {

    private final String vnp_TmnCode = "YOUR_SANDBOX_CODE"; // Mã terminal sandbox
    private final String vnp_HashSecret = "YOUR_SANDBOX_SECRET"; // Key sandbox
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_ReturnUrl = "http://localhost:8080/api/bills/vnpay-return"; // Backend callback

    public String createPayment(Long orderId, Long amount) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Đã nhân 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn #" + orderId);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Thêm thời gian hết hạn (15 phút)
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(expire.getTime()));

        // 1. Tạo chuỗi dữ liệu THÔ (KHÔNG ENCODE) để tính Hash
        String rawData = generateRawData(vnp_Params);
        String vnp_SecureHash = HmacSHA512(vnp_HashSecret, rawData);

        // 2. Tạo chuỗi Query String (ĐÃ URL-ENCODE)
        String queryString = generateQueryString(vnp_Params);

        // 3. Trả về URL hoàn chỉnh
        return vnp_Url + "?" + queryString + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    /**
     * Phương thức xác thực vnp_SecureHash từ callback VNPay
     * 
     * @param fields Map chứa tất cả các tham số từ VNPay
     * @return true nếu Hash hợp lệ, ngược lại false
     */
    public boolean validateHash(Map<String, String> fields) {
        String secureHashFromUrl = fields.get("vnp_SecureHash");
        if (secureHashFromUrl == null) {
            return false;
        }

        // Loại bỏ vnp_SecureHash và vnp_SecureHashType để tạo chuỗi Hash
        Map<String, String> data = new HashMap<>(fields);
        data.remove("vnp_SecureHash");
        data.remove("vnp_SecureHashType"); // (Nếu có)

        // Tạo chuỗi THÔ để so sánh
        String rawData = generateRawData(data);
        String calculatedHash = HmacSHA512(vnp_HashSecret, rawData);

        return calculatedHash.equalsIgnoreCase(secureHashFromUrl);
    }

    // -----------------------------------------------------------
    // PHƯƠNG THỨC HỖ TRỢ
    // -----------------------------------------------------------

    /**
     * Tạo chuỗi dữ liệu THÔ (raw data) theo thứ tự alphabet để tính Hash.
     * KHÔNG URL-ENCODE giá trị.
     */
    private String generateRawData(Map<String, String> vnp_Params) {
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder rawData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                if (rawData.length() > 0)
                    rawData.append('&');
                // Nối tham số thô: key=value
                rawData.append(fieldName).append('=').append(value);
            }
        }
        return rawData.toString();
    }

    /**
     * Tạo chuỗi Query String (ĐÃ URL-ENCODE) để dùng trong URL gửi đi.
     */
    private String generateQueryString(Map<String, String> vnp_Params) {
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();

        try {
            for (String fieldName : fieldNames) {
                String value = vnp_Params.get(fieldName);
                if (value != null && !value.isEmpty()) {
                    if (query.length() > 0)
                        query.append('&');

                    // Nối tham số đã URL-ENCODE: key=encoded_value
                    query.append(fieldName).append('=')
                            .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo query string: " + e.getMessage());
        }
        return query.toString();
    }

    private String HmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            return bytesToHex(result);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo HmacSHA512: " + e.getMessage(), e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}