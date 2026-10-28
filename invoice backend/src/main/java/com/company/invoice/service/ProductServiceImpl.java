package com.company.invoice.service;

import com.company.invoice.dto.ProductRequest;
import com.company.invoice.dto.ProductResponse;
import com.company.invoice.entity.Product;
import com.company.invoice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(String q, Pageable pageable) {
        String query = (q == null) ? "" : q.trim();
        return productRepository.search(query, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = find(id);
        apply(product, request);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = find(id);
        product.setActive(false);
        productRepository.save(product);
    }

    private void apply(Product p, ProductRequest r) {
        p.setName(r.name());
        p.setDescription(r.description());
        p.setHsnSac(r.hsnSac());
        p.setUnit(r.unit());
        p.setUnitPrice(r.unitPrice());
        p.setTaxRate(r.taxRate() == null ? BigDecimal.ZERO : r.taxRate());
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getHsnSac(), p.getUnit(), p.getUnitPrice(), p.getTaxRate(), p.isActive());
    }

    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }
}
