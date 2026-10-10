package com.estateelite.util;

import com.estateelite.model.Transaction;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfInvoiceGenerator {

    public byte[] generate(Transaction t) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            Paragraph title = new Paragraph("Estate Elite - Invoice / Receipt", titleFont);
            title.setSpacingAfter(20f);
            document.add(title);

            document.add(row("Invoice Number:", t.getInvoiceNumber(), labelFont, normalFont));
            document.add(row("Property:", t.getProperty().getTitle(), labelFont, normalFont));
            document.add(row("Buyer:", t.getBuyer().getFullName(), labelFont, normalFont));
            document.add(row("Amount:", "LKR " + t.getAmount().toString(), labelFont, normalFont));
            document.add(row("Status:", t.getStatus().toString(), labelFont, normalFont));
            document.add(row("Date:", t.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), labelFont, normalFont));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for using Estate Elite.", normalFont));

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate invoice PDF: " + e.getMessage(), e);
        }
    }

    private Paragraph row(String label, String value, Font labelFont, Font normalFont) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, normalFont));
        p.setSpacingAfter(8f);
        return p;
    }
}
