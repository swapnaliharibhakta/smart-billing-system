package com.company.invoice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String description;

    @Column(name = "hsn_sac", length = 8)
    private String hsnSac;

    @Column(length = 20)
    private String unit;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_pct", nullable = false)
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "taxable_value", nullable = false)
    private BigDecimal taxableValue = BigDecimal.ZERO;

    @Column(name = "tax_rate", nullable = false)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name = "cgst_rate", nullable = false)
    private BigDecimal cgstRate = BigDecimal.ZERO;

    @Column(name = "cgst_amount", nullable = false)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_rate", nullable = false)
    private BigDecimal sgstRate = BigDecimal.ZERO;

    @Column(name = "sgst_amount", nullable = false)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_rate", nullable = false)
    private BigDecimal igstRate = BigDecimal.ZERO;

    @Column(name = "igst_amount", nullable = false)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false)
    private BigDecimal lineTotal;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public InvoiceItem() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHsnSac() { return hsnSac; }
    public void setHsnSac(String hsnSac) { this.hsnSac = hsnSac; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getDiscountPct() { return discountPct; }
    public void setDiscountPct(BigDecimal discountPct) { this.discountPct = discountPct; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxableValue() { return taxableValue; }
    public void setTaxableValue(BigDecimal taxableValue) { this.taxableValue = taxableValue; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getCgstRate() { return cgstRate; }
    public void setCgstRate(BigDecimal cgstRate) { this.cgstRate = cgstRate; }

    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }

    public BigDecimal getSgstRate() { return sgstRate; }
    public void setSgstRate(BigDecimal sgstRate) { this.sgstRate = sgstRate; }

    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }

    public BigDecimal getIgstRate() { return igstRate; }
    public void setIgstRate(BigDecimal igstRate) { this.igstRate = igstRate; }

    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
