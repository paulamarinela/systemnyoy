package rq.tech.services;

import org.springframework.stereotype.Service;
import rq.tech.models.Repair;

import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    public String generateReceiptContent(Repair repair) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("      RQ TECH REPAIR SHOP      \n");
        sb.append("===============================\n");
        sb.append("Repair ID: ").append(repair.getId()).append("\n");
        sb.append("UUID: ").append(repair.getUuid()).append("\n");
        sb.append("Date: ").append(repair.getDateReceived().format(formatter)).append("\n");
        sb.append("-------------------------------\n");
        sb.append("Customer: ").append(repair.getCustomerName()).append("\n");
        sb.append("Device: ").append(repair.getDevice()).append("\n");
        if (repair.getSerialNumber() != null) {
            sb.append("SN: ").append(repair.getSerialNumber()).append("\n");
        }
        sb.append("Issue: ").append(repair.getIssue()).append("\n");
        sb.append("-------------------------------\n");
        sb.append("Total Bill: P ").append(String.format("%.2f", repair.getBill())).append("\n");
        sb.append("Payment Status: ").append(repair.getPaymentStatus()).append("\n");
        sb.append("-------------------------------\n");
        sb.append("   Thank you for choosing us!   \n");
        sb.append("===============================\n");

        return sb.toString();
    }
}
