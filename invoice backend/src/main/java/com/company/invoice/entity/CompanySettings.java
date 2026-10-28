package com.company.invoice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_settings")
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String email;

    private String phone;

    @Column(name = "invoice_prefix", nullable = false)
    private String invoicePrefix = "INV";

    @Column(name = "next_invoice_no", nullable = false)
    private Long nextInvoiceNo = 1L;

    @Column(name = "default_due_days", nullable = false)
    private Integer defaultDueDays = 30;

    private String address;

    @Column(name = "gst_number")
    private String gstNumber;

    @Column(length = 10)
    private String pan;

    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account", length = 30)
    private String bankAccount;

    @Column(name = "bank_ifsc", length = 11)
    private String bankIfsc;

    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "logo_path", columnDefinition = "TEXT")
    private String logoPath;

    @Column(name = "signature_path", columnDefinition = "TEXT")
    private String signaturePath;

    @Column(name = "default_terms", columnDefinition = "TEXT")
    private String defaultTerms;

    @Column(name = "default_notes", columnDefinition = "TEXT")
    private String defaultNotes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public CompanySettings() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getInvoicePrefix() { return invoicePrefix; }
    public void setInvoicePrefix(String invoicePrefix) { this.invoicePrefix = invoicePrefix; }

    public Long getNextInvoiceNo() { return nextInvoiceNo; }
    public void setNextInvoiceNo(Long nextInvoiceNo) { this.nextInvoiceNo = nextInvoiceNo; }

    public Integer getDefaultDueDays() { return defaultDueDays; }
    public void setDefaultDueDays(Integer defaultDueDays) { this.defaultDueDays = defaultDueDays; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }

    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getSignaturePath() { return signaturePath; }
    public void setSignaturePath(String signaturePath) { this.signaturePath = signaturePath; }

    public String getDefaultTerms() { return defaultTerms; }
    public void setDefaultTerms(String defaultTerms) { this.defaultTerms = defaultTerms; }

    public String getDefaultNotes() { return defaultNotes; }
    public void setDefaultNotes(String defaultNotes) { this.defaultNotes = defaultNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
