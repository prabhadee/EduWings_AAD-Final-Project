package lk.ijse.gdse72.backend.service.Impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lk.ijse.gdse72.backend.entity.BatchMonth;
import lk.ijse.gdse72.backend.entity.Payment;
import lk.ijse.gdse72.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicePDFService {

    private final PaymentRepository paymentRepository;

    public byte[] generateInvoicePDF(String paymentReference) {
        try {
            Payment payment = paymentRepository.findByReferenceNumber(paymentReference)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Define fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // Header
            Paragraph title = new Paragraph("EduWings Institute", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Payment Invoice", headerFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20f);
            document.add(subtitle);

            // Invoice Details Table
            PdfPTable invoiceTable = new PdfPTable(2);
            invoiceTable.setWidthPercentage(100);
            invoiceTable.setSpacingAfter(20f);

            // Invoice details
            addTableRow(invoiceTable, "Invoice No:", payment.getReferenceNumber(), labelFont, valueFont);
            addTableRow(invoiceTable, "Date:",
                    payment.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    labelFont, valueFont);
            addTableRow(invoiceTable, "Student Name:", payment.getUser().getUsername(), labelFont, valueFont);
            addTableRow(invoiceTable, "Email:", payment.getUser().getEmail(), labelFont, valueFont);
            addTableRow(invoiceTable, "Status:", payment.getStatus().toString(), labelFont, valueFont);

            document.add(invoiceTable);

            // Payment Details Header
            Paragraph paymentDetailsHeader = new Paragraph("Payment Details", headerFont);
            paymentDetailsHeader.setSpacingBefore(10f);
            paymentDetailsHeader.setSpacingAfter(10f);
            document.add(paymentDetailsHeader);

            // Payment Details Table
            PdfPTable paymentTable = new PdfPTable(3);
            paymentTable.setWidthPercentage(100);
            paymentTable.setWidths(new float[]{3, 2, 1});

            // Headers
            addHeaderCell(paymentTable, "Description", labelFont);
            addHeaderCell(paymentTable, "Months", labelFont);
            addHeaderCell(paymentTable, "Amount (LKR)", labelFont);

            // Months enrolled
            StringBuilder monthsStr = new StringBuilder();
            for (BatchMonth month : payment.getMonths()) {
                if (monthsStr.length() > 0) monthsStr.append(", ");
                monthsStr.append(month.getMonthName());
            }

            // Payment row
            addValueCell(paymentTable, payment.getDescription() != null ? payment.getDescription() : "Course Enrollment", valueFont);
            addValueCell(paymentTable, monthsStr.toString(), valueFont);
            addValueCell(paymentTable, String.format("%.2f", payment.getAmount()), valueFont);

            // Total row
            addValueCell(paymentTable, "", labelFont);
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total:", labelFont));
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setPaddingTop(10f);
            paymentTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f", payment.getAmount()), labelFont));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setPaddingTop(10f);
            paymentTable.addCell(totalValueCell);

            document.add(paymentTable);

            // Footer
            Paragraph footer1 = new Paragraph("Thank you for choosing EduWings Institute!", valueFont);
            footer1.setAlignment(Element.ALIGN_CENTER);
            footer1.setSpacingBefore(30f);
            document.add(footer1);

            Paragraph footer2 = new Paragraph("For support, contact: support@eduwings.com",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
            footer2.setAlignment(Element.ALIGN_CENTER);
            footer2.setSpacingBefore(10f);
            document.add(footer2);

            document.close();

            log.info("Invoice PDF generated for payment: {}", paymentReference);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating invoice PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setPadding(8f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setPadding(8f);
        table.addCell(valueCell);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addValueCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }
}