package com.insight_pulse.tech.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PdfExtractor {
    public String extractTextPDF(MultipartFile file) {
        try(PDDocument document = Loader.loadPDF(file.getBytes())) {
            if(!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error when reading PDF files: " + e.getMessage());
        }
        return "";
    }
}
