package com.company.invoice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;

    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total")
    private BigDecimal taxTotal = BigDecimal.ZERO;

    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "round_off")
    private BigDecimal roundOff = BigDecimal.ZERO;

    private String notes;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @Column(name = "place_of_supply", length = 100)
    private String placeOfSupply;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // --- Bill-to snapshot ---
    @Column(name = "bill_to_name", length = 200)
    private String billToName;

    @Column(name = "bill_to_gstin", length = 20)
    private String billToGstin;

    @Column(name = "bill_to_street", columnDefinition = "TEXT")
    private String billToStreet;

    @Column(name = "bill_to_city", length = 100)
    private String billToCity;

    @Column(name = "bill_to_state", length = 100)
    private String billToState;

    @Column(name = "bill_to_state_code", length = 2)
    private String billToStateCode;

    // --- Ship-to snapshot ---
    @Column(name = "ship_to_name", length = 200)
    private String shipToName;

    @Column(name = "ship_to_street", columnDefinition = "TEXT")
    private String shipToStreet;

    @Column(name = "ship_to_city", length = 100)
    private String shipToCity;

    @Column(name = "ship_to_state", length = 100)
    private String shipToState;

    @Column(name = "ship_to_state_code", length = 2)
    private String shipToStateCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Invoice() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxTotal() { return taxTotal; }
    public void setTaxTotal(BigDecimal taxTotal) { this.taxTotal = taxTotal; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getRoundOff() { return roundOff; }
    public void setRoundOff(BigDecimal roundOff) { this.roundOff = roundOff; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }

    public String getPlaceOfSupply() { return placeOfSupply; }
    public void setPlaceOfSupply(String placeOfSupply) { this.placeOfSupply = placeOfSupply; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getBillToName() { return billToName; }
    public void setBillToName(String billToName) { this.billToName = billToName; }

    public String getBillToGstin() { return billToGstin; }
    public void setBillToGstin(String billToGstin) { this.billToGstin = billToGstin; }

    public String getBillToStreet() { return billToStreet; }
    public void setBillToStreet(String billToStreet) { this.billToStreet = billToStreet; }

    public String getBillToCity() { return billToCity; }
    public void setBillToCity(String billToCity) { this.billToCity = billToCity; }

    public String getBillToState() { return billToState; }
    public void setBillToState(String billToState) { this.billToState = billToState; }

    public String getBillToStateCode() { return billToStateCode; }
    public void setBillToStateCode(String billToStateCode) { this.billToStateCode = billToStateCode; }

    public String getShipToName() { return shipToName; }
    public void setShipToName(String shipToName) { this.shipToName = shipToName; }

    public String getShipToStreet() { return shipToStreet; }
    public void setShipToStreet(String shipToStreet) { this.shipToStreet = shipToStreet; }

    public String getShipToCity() { return shipToCity; }
    public void setShipToCity(String shipToCity) { this.shipToCity = shipToCity; }

    public String getShipToState() { return shipToState; }
    public void setShipToState(String shipToState) { this.shipToState = shipToState; }

    public String getShipToStateCode() { return shipToStateCode; }
    public void setShipToStateCode(String shipToStateCode) { this.shipToStateCode = shipToStateCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum Status {
        DRAFT, SENT, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
    }
}
