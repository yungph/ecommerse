package com.ecommerce.ecommerse.Service;

import com.ecommerce.ecommerse.Models.FAQs;
import com.ecommerce.ecommerse.Repo.FAQsRepo;
import jakarta.persistence.EntityNotFoundException; // Added for consistency
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FAQsService {
    @Autowired
    FAQsRepo faqRepository;

    public List<FAQs> getAllFAQs() {
        return faqRepository.findAll();
    }

    public FAQs getFAQById(long id) { // Changed id to long, and return type to FAQs
        return faqRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("FAQ not found with id " + id));
    }

    public FAQs createFAQ(FAQs faq) { // Changed return type to FAQs
        return faqRepository.save(faq);
    }

    public FAQs updateFAQ(FAQs faq) { // Changed return type to FAQs
        // Ensure ID is not null for update
        if (faq.getId() == null) { // Assuming getId() exists in FAQs model
            throw new IllegalArgumentException("FAQ ID must not be null for update.");
        }
        if (!faqRepository.existsById(faq.getId())) {
            throw new EntityNotFoundException("FAQ not found with id " + faq.getId() + " for update operation.");
        }
        return faqRepository.save(faq); // save can also be used for updates if ID is present
    }

    public void deleteFAQ(long id) { // Changed id to long
        if (!faqRepository.existsById(id)) {
            throw new EntityNotFoundException("FAQ not found with id " + id + " for delete operation.");
        }
        faqRepository.deleteById(id);
    }
}
