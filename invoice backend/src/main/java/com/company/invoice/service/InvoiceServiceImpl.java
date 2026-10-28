package com.company.invoice.service;

import com.company.invoice.dto.InvoiceItemRequest;
import com.company.invoice.dto.InvoiceItemResponse;
import com.company.invoice.dto.InvoiceRequest;
import com.company.invoice.dto.InvoiceResponse;
import com.company.invoice.entity.CompanySettings;
import com.company.invoice.entity.Customer;
import com.company.invoice.entity.Invoice;
import com.company.invoice.entity.InvoiceItem;
import com.company.invoice.entity.Product;
import com.company.invoice.entity.User;
import com.company.invoice.repository.CustomerRepository;
import com.company.invoice.repository.InvoiceItemRepository;
import com.company.invoice.repository.InvoiceRepository;
import com.company.invoice.repository.ProductRepository;
import com.company.invoice.repository.SettingsRepository;
import com.company.invoice.repository.UserRepository;
import com.company.invoice.util.AmountInWords;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWO_HUNDRED = new BigDecimal("200");

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              InvoiceItemRepository itemRepository,
                              CustomerRepository customerRepository,
                              ProductRepository productRepository,
                              UserRepository userRepository,
                              SettingsRepository settingsRepository) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Invoice invoice = new Invoice();
        CompanySettings settings = fillHeader(invoice, request);
        invoice.setCreatedBy(user);
        invoice.setStatus(Invoice.Status.DRAFT);
        invoice.setInvoiceNumber(nextNumber(invoice.getIssueDate(), settings));
        invoice = invoiceRepository.save(invoice);

        saveItems(invoice, request.items(), settings);
        return toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAll(String q, Pageable pageable) {
        String query = (q == null) ? "" : q.trim();
        return invoiceRepository.search(query, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest request) {
        Invoice invoice = find(id);
        if (invoice.getStatus() != Invoice.Status.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT invoices can be edited");
        }
        CompanySettings settings = fillHeader(invoice, request);
        itemRepository.deleteByInvoiceId(invoice.getId());
        itemRepository.flush();
        saveItems(invoice, request.items(), settings);
        return toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse markAsSent(Long id) {
        Invoice invoice = find(id);
        if (invoice.getStatus() != Invoice.Status.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT invoices can be marked as SENT");
        }
        invoice.setStatus(Invoice.Status.SENT);
        invoice.setSentAt(java.time.LocalDateTime.now());
        invoiceRepository.save(invoice);
        return toResponse(invoice);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Invoice invoice = find(id);
        if (invoice.getStatus() == Invoice.Status.PAID
                || invoice.getStatus() == Invoice.Status.PARTIALLY_PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invoice with payments cannot be cancelled");
        }
        invoice.setStatus(Invoice.Status.CANCELLED);
        invoiceRepository.save(invoice);
    }

    // ---------- helpers ----------

    /**
     * Fills invoice header fields and snapshots the customer's address.
     * Returns the loaded CompanySettings so callers can reuse it.
     */
    private CompanySettings fillHeader(Invoice invoice, InvoiceRequest r) {
        Customer customer = customerRepository.findById(r.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found"));

        CompanySettings settings = settingsRepository.findFirst()
                .orElseGet(() -> {
                    CompanySettings s = new CompanySettings();
                    s.setCompanyName("My Company");
                    return settingsRepository.save(s);
                });

        LocalDate issue = r.issueDate() != null ? r.issueDate() : LocalDate.now();
        LocalDate due = r.dueDate() != null ? r.dueDate()
                : issue.plusDays(settings.getDefaultDueDays() != null ? settings.getDefaultDueDays() : 30);
        if (due.isBefore(issue)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date cannot be before issue date");
        }

        invoice.setCustomer(customer);
        invoice.setIssueDate(issue);
        invoice.setDueDate(due);
        invoice.setNotes(r.notes() != null ? r.notes() : settings.getDefaultNotes());
        invoice.setTerms(r.terms() != null ? r.terms() : settings.getDefaultTerms());

        // Place of supply: use request value, else default to customer's billing state code
        String pos = r.placeOfSupply();
        if (pos == null || pos.isBlank()) {
            pos = customer.getBillingStateCode();
        }
        invoice.setPlaceOfSupply(pos);

        // Snapshot bill-to from customer
        invoice.setBillToName(customer.getName());
        invoice.setBillToGstin(customer.getGstNumber());
        invoice.setBillToStreet(customer.getBillingStreet());
        invoice.setBillToCity(customer.getBillingCity());
        invoice.setBillToState(customer.getBillingState());
        invoice.setBillToStateCode(customer.getBillingStateCode());

        // Snapshot ship-to (fall back to bill-to if no separate shipping address)
        String shipName = customer.getShippingName();
        String shipStreet = customer.getShippingStreet();
        invoice.setShipToName(shipName != null ? shipName : customer.getName());
        invoice.setShipToStreet(shipStreet != null ? shipStreet : customer.getBillingStreet());
        invoice.setShipToCity(customer.getShippingCity() != null ? customer.getShippingCity() : customer.getBillingCity());
        invoice.setShipToState(customer.getShippingState() != null ? customer.getShippingState() : customer.getBillingState());
        invoice.setShipToStateCode(customer.getShippingStateCode() != null ? customer.getShippingStateCode() : customer.getBillingStateCode());

        return settings;
    }

    /**
     * Calculates all line-item amounts server-side using BigDecimal.
     * Determines CGST+SGST (intra-state) vs IGST (inter-state) based on
     * company state_code vs invoice place_of_supply.
     */
    private void saveItems(Invoice invoice, List<InvoiceItemRequest> requests, CompanySettings settings) {
        String companyStateCode = settings.getStateCode();
        String placeOfSupply = invoice.getPlaceOfSupply();
        boolean intraState = companyStateCode != null && !companyStateCode.isBlank()
                && Objects.equals(companyStateCode, placeOfSupply);

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        for (InvoiceItemRequest req : requests) {
            Product product = null;
            if (req.productId() != null) {
                product = productRepository.findById(req.productId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
                if (!product.isActive()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Product is inactive: " + product.getName());
                }
            }

            BigDecimal rate = req.taxRate() != null ? req.taxRate() : BigDecimal.ZERO;
            BigDecimal discPct = req.discountPct() != null ? req.discountPct() : BigDecimal.ZERO;

            // gross = qty × unitPrice  (raw, no rounding yet)
            BigDecimal gross = req.quantity().multiply(req.unitPrice());

            // discount = gross × discPct / 100, rounded to 2dp
            BigDecimal discountAmount = gross.multiply(discPct)
                    .divide(HUNDRED, 2, RoundingMode.HALF_UP);

            // taxableValue = gross - discount, rounded to 2dp
            BigDecimal taxableValue = gross.subtract(discountAmount)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal cgstRate = BigDecimal.ZERO;
            BigDecimal cgstAmount = BigDecimal.ZERO;
            BigDecimal sgstRate = BigDecimal.ZERO;
            BigDecimal sgstAmount = BigDecimal.ZERO;
            BigDecimal igstRate = BigDecimal.ZERO;
            BigDecimal igstAmount = BigDecimal.ZERO;

            if (intraState) {
                // CGST = SGST = taxableValue × rate / 200
                cgstRate = rate.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                sgstRate = cgstRate;
                cgstAmount = taxableValue.multiply(rate).divide(TWO_HUNDRED, 2, RoundingMode.HALF_UP);
                sgstAmount = cgstAmount;
            } else {
                // IGST = taxableValue × rate / 100
                igstRate = rate;
                igstAmount = taxableValue.multiply(rate).divide(HUNDRED, 2, RoundingMode.HALF_UP);
            }

            BigDecimal lineTax = cgstAmount.add(sgstAmount).add(igstAmount);
            BigDecimal lineTotal = taxableValue.add(lineTax);

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setDescription(req.description());
            // Copy HSN/SAC and unit from product if not overridden in request
            item.setHsnSac(req.hsnSac() != null ? req.hsnSac()
                    : (product != null ? product.getHsnSac() : null));
            item.setUnit(req.unit() != null ? req.unit()
                    : (product != null ? product.getUnit() : null));
            item.setQuantity(req.quantity());
            item.setUnitPrice(req.unitPrice());
            item.setDiscountPct(discPct);
            item.setDiscountAmount(discountAmount);
            item.setTaxableValue(taxableValue);
            item.setTaxRate(rate);
            item.setCgstRate(cgstRate);
            item.setCgstAmount(cgstAmount);
            item.setSgstRate(sgstRate);
            item.setSgstAmount(sgstAmount);
            item.setIgstRate(igstRate);
            item.setIgstAmount(igstAmount);
            item.setLineTotal(lineTotal);
            itemRepository.save(item);

            subtotal = subtotal.add(taxableValue);
            taxTotal = taxTotal.add(lineTax);
        }

        // Grand total before rounding
        BigDecimal grandTotal = subtotal.add(taxTotal);

        // Round off: round to nearest rupee, store difference
        BigDecimal roundedTotal = grandTotal.setScale(0, RoundingMode.HALF_UP).setScale(2);
        BigDecimal roundOff = roundedTotal.subtract(grandTotal).setScale(2, RoundingMode.HALF_UP);

        invoice.setSubtotal(subtotal);
        invoice.setTaxTotal(taxTotal);
        invoice.setRoundOff(roundOff);
        invoice.setTotal(roundedTotal);
        invoiceRepository.save(invoice);
    }

    /**
     * Atomically reads and increments next_invoice_no using a pessimistic write lock.
     * Format: PREFIX-YYYY-NNNN (zero-padded to 4 digits, grows beyond 4 if needed).
     */
    private String nextNumber(LocalDate date, CompanySettings settings) {
        CompanySettings locked = settingsRepository.findFirstForUpdate()
                .orElse(settings);
        long seq = locked.getNextInvoiceNo();
        locked.setNextInvoiceNo(seq + 1);
        settingsRepository.save(locked);

        String prefix = locked.getInvoicePrefix() != null ? locked.getInvoicePrefix() : "INV";
        return String.format("%s-%d-%04d", prefix, date.getYear(), seq);
    }

    private Invoice find(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found"));
    }

    private InvoiceResponse toResponse(Invoice i) {
        List<InvoiceItemResponse> items = itemRepository.findByInvoiceId(i.getId()).stream()
                .map(it -> new InvoiceItemResponse(
                        it.getId(),
                        it.getProduct() == null ? null : it.getProduct().getId(),
                        it.getDescription(),
                        it.getHsnSac(),
                        it.getUnit(),
                        it.getQuantity(),
                        it.getUnitPrice(),
                        it.getDiscountPct(),
                        it.getDiscountAmount(),
                        it.getTaxableValue(),
                        it.getTaxRate(),
                        it.getCgstRate(),
                        it.getCgstAmount(),
                        it.getSgstRate(),
                        it.getSgstAmount(),
                        it.getIgstRate(),
                        it.getIgstAmount(),
                        it.getLineTotal()))
                .toList();

        BigDecimal balanceDue = i.getTotal().subtract(i.getAmountPaid());
        String amountInWords = AmountInWords.convert(i.getTotal());

        return new InvoiceResponse(
                i.getId(), i.getInvoiceNumber(),
                i.getCustomer().getId(), i.getCustomer().getName(),
                i.getIssueDate(), i.getDueDate(), i.getStatus().name(),
                i.getSubtotal(), i.getTaxTotal(), i.getTotal(),
                i.getAmountPaid(), balanceDue,
                i.getNotes(), i.getSentAt(),
                items, i.getCreatedAt(),
                // new fields
                i.getPlaceOfSupply(), i.getTerms(), i.getRoundOff(), amountInWords,
                i.getBillToName(), i.getBillToGstin(),
                i.getBillToStreet(), i.getBillToCity(), i.getBillToState(), i.getBillToStateCode(),
                i.getShipToName(), i.getShipToStreet(), i.getShipToCity(),
                i.getShipToState(), i.getShipToStateCode());
    }
}
