// ══════════════════════════════════════════════════════════════
//  SUBSYSTEM CLASSES  (given — do not modify)
// ══════════════════════════════════════════════════════════════

class Inventory {
    boolean checkStock(String productId) { return true; }
    void reserve(String productId)       { System.out.println("Reserved " + productId); }
    void release(String productId)       { System.out.println("Released " + productId); }
}

class Payment {
    boolean charge(String userId, double amount) {
        System.out.println("Charged $" + amount + " to " + userId);
        return true;
    }
    void refund(String userId, double amount) {
        System.out.println("Refunded $" + amount + " to " + userId);
    }
}

class Shipping {
    String createLabel(String address) {
        return "TRK" + System.currentTimeMillis();
    }
    void schedulePickup(String label) {
        System.out.println("Pickup scheduled for " + label);
    }
    boolean isAvailable() { return true; } // for rollback demo
}

class Email {
    void send(String to, String subject, String body) {
        System.out.println("Email to " + to + " | " + subject + " | " + body);
    }
}

// ══════════════════════════════════════════════════════════════
//  EXTENSION SUBSYSTEMS  (Exercise 2 additions — facade-only change)
// ══════════════════════════════════════════════════════════════

class TaxCalculator {
    /**
     * Returns the tax rate for a given state.
     * CA → 8%,  all others → 0%
     */
    double getTaxRate(String state) {
        return "CA".equalsIgnoreCase(state) ? 0.08 : 0.0;
    }

    double calculateTax(double price, String state) {
        return price * getTaxRate(state);
    }
}

class Logger {
    void log(String userId, boolean success) {
        String status    = success ? "SUCCESS" : "FAILURE";
        long   timestamp = System.currentTimeMillis();
        System.out.println("[LOG] " + timestamp + " | userId=" + userId + " | " + status);
    }
}

// ══════════════════════════════════════════════════════════════
//  ORDER RESULT  (return type for checkout)
// ══════════════════════════════════════════════════════════════

class OrderResult {
    private final boolean success;
    private final String  trackingNumber;
    private final String  message;

    public OrderResult(boolean success, String trackingNumber, String message) {
        this.success        = success;
        this.trackingNumber = trackingNumber;
        this.message        = message;
    }

    // Getters
    public boolean isSuccess()          { return success; }
    public String  getTrackingNumber()  { return trackingNumber; }
    public String  getMessage()         { return message; }

    @Override
    public String toString() {
        return "OrderResult{success=" + success
             + ", trackingNumber='" + trackingNumber + "'"
             + ", message='" + message + "'}";
    }
}

// ══════════════════════════════════════════════════════════════
//  CHECKOUT FACADE  (main exercise + Exercise 2 extension)
// ══════════════════════════════════════════════════════════════

public class CheckoutFacade {

    // Core subsystems
    private final Inventory     inventory;
    private final Payment       payment;
    private final Shipping      shipping;
    private final Email         email;

    // Extension subsystems (Exercise 2 — added only here, client code unchanged)
    private final TaxCalculator taxCalculator;
    private final Logger        logger;

    public CheckoutFacade() {
        this.inventory     = new Inventory();
        this.payment       = new Payment();
        this.shipping      = new Shipping();
        this.email         = new Email();
        this.taxCalculator = new TaxCalculator();
        this.logger        = new Logger();
    }

    /**
     * Places an order by orchestrating all subsystems.
     *
     * Workflow:
     *  1. Check stock             — if fails: return failure (nothing charged yet)
     *  2. Calculate tax           — add to total (Exercise 2 extension)
     *  3. Charge payment          — if fails: return failure (inventory untouched)
     *  4. Reserve inventory       — product held for this order
     *  5. Create shipping label   — if shipping unavailable: rollback → refund + release
     *  6. Schedule pickup
     *  7. Send confirmation email — includes tax-inclusive total (Exercise 2 extension)
     *  8. Log result              — (Exercise 2 extension)
     *
     * @param userId    customer identifier (email used here)
     * @param productId product being purchased
     * @param price     base price before tax
     * @param address   delivery address
     * @param state     customer's state for tax calculation (e.g. "CA")
     * @return OrderResult with success flag, tracking number, and message
     */
    public OrderResult checkout(String userId, String productId,
                                double price, String address, String state) {

        // ── Step 1: Check stock ──────────────────────────────────────────
        if (!inventory.checkStock(productId)) {
            logger.log(userId, false);
            return new OrderResult(false, null, "Out of stock: " + productId);
        }

        // ── Step 2: Calculate tax (Exercise 2) ──────────────────────────
        double tax        = taxCalculator.calculateTax(price, state);
        double totalPrice = price + tax;
        System.out.println("Base price: $" + price + " | Tax (" + state + "): $" + tax
                         + " | Total: $" + totalPrice);

        // ── Step 3: Charge payment ───────────────────────────────────────
        if (!payment.charge(userId, totalPrice)) {
            logger.log(userId, false);
            return new OrderResult(false, null, "Payment failed for user: " + userId);
        }

        // ── Step 4: Reserve inventory ────────────────────────────────────
        inventory.reserve(productId);

        // ── Step 5: Create shipping label (with rollback on failure) ─────
        if (!shipping.isAvailable()) {
            // Rollback: refund payment and release reserved stock
            payment.refund(userId, totalPrice);
            inventory.release(productId);
            logger.log(userId, false);
            return new OrderResult(false, null, "Shipping unavailable — order rolled back.");
        }

        String label = shipping.createLabel(address);

        // ── Step 6: Schedule pickup ──────────────────────────────────────
        shipping.schedulePickup(label);

        // ── Step 7: Send confirmation email (includes total with tax) ────
        email.send(userId, "Order Confirmed",
                   "Your order for " + productId + " is on its way! "
                   + "Total charged: $" + totalPrice + " (incl. tax). "
                   + "Tracking: " + label);

        // ── Step 8: Log success (Exercise 2) ────────────────────────────
        logger.log(userId, true);

        return new OrderResult(true, label, "Order placed successfully!");
    }
}

// ══════════════════════════════════════════════════════════════
//  CLIENT CODE  — notice: client never changed between exercises
// ══════════════════════════════════════════════════════════════

class Main {
    public static void main(String[] args) {

        CheckoutFacade facade = new CheckoutFacade();

        // Normal order (non-CA state → 0% tax)
        System.out.println("=== Order 1: Texas ===");
        OrderResult result1 = facade.checkout(
            "alice@example.com", "LAPTOP", 999.99, "456 Oak Ave", "TX"
        );
        System.out.println(result1);

        System.out.println();

        // CA order → 8% tax applied
        System.out.println("=== Order 2: California ===");
        OrderResult result2 = facade.checkout(
            "bob@example.com", "PHONE", 699.00, "123 Main St", "CA"
        );
        System.out.println(result2);
    }
}
