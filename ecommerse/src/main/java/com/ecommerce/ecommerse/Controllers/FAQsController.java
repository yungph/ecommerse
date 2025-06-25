package com.ecommerce.ecommerse.Controllers;

import com.ecommerce.ecommerse.Models.FAQs;
import com.ecommerce.ecommerse.Service.FAQsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List; // For getAllFAQs

@RestController
@RequestMapping("/faqs") // Added base path
public class FAQsController {

    @Autowired
    private FAQsService faqsService;

    @PostMapping("/admin/create") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQs> createFaq(@RequestBody FAQs faq) {
        FAQs newFaq = faqsService.createFAQ(faq); // Assuming createFAQ returns the created FAQ
        return ResponseEntity.ok(newFaq);
    }

    @PutMapping("/admin/update") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQs> updateFaq(@RequestBody FAQs faq) {
        FAQs updatedFaq = faqsService.updateFAQ(faq); // Assuming updateFAQ returns the updated FAQ
        return ResponseEntity.ok(updatedFaq);
    }

    @GetMapping // Path: /faqs
    public ResponseEntity<List<FAQs>> getAllFaq() {
        return ResponseEntity.ok().body(faqsService.getAllFAQs());
    }

    @GetMapping("/{id}") // Path: /faqs/{id}
    public ResponseEntity<FAQs> getFaqById(@PathVariable long id) { // Changed to long
        FAQs faq = faqsService.getFAQById(id);
        if (faq == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faq);
    }

    @DeleteMapping("/admin/delete/{id}") // Changed path
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaq(@PathVariable long id) { // Changed to long
        faqsService.deleteFAQ(id);
        return ResponseEntity.ok().build();
    }
}
