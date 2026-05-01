// ══════════════════════════════════════════════════════════════
//  LEGACY CLASS  (DO NOT MODIFY — given as-is)
// ══════════════════════════════════════════════════════════════

class Inventory {
    boolean checkStock(String productId) { return true; }
    void reserve(String productId)       { System.out.println("Reserved " + productId); }
}

class Payment {
    boolean charge(String userId, double amount) {
        System.out.println("Charged $" + amount + " to " + userId);
        return true;
    }
}

class Shipping {
    String createLabel(String address)   { return "TRK" + System.currentTimeMillis(); }
    void schedulePickup(String label)    { System.out.println("Pickup scheduled: " + label); }
}

class Email {
    void send(String to, String subject, String body) {
        System.out.println("Email to " + to);
    }
}

// DO NOT MODIFY THIS CLASS
class LegacyOrderProcessor {
    public void processOrder(String customerEmail, String itemCode,
                             double amount, String deliveryAddress) {
        // Hardcoded dependencies inside
        Inventory inv  = new Inventory();
        Payment   pay  = new Payment();
        Shipping  ship = new Shipping();
        Email     email = new Email();

        if (!inv.checkStock(itemCode)) {
            System.out.println("Out of stock");
            return;
        }
        if (!pay.charge(customerEmail, amount)) {
            System.out.println("Payment fail");
            return;
        }
        inv.reserve(itemCode);
        String label = ship.createLabel(deliveryAddress);
        ship.schedulePickup(label);
        email.send(customerEmail, "Order", "Shipped");
        System.out.println("Order complete");
    }
}

// ══════════════════════════════════════════════════════════════
//  YOUR SOLUTION: LegacyOrderFacade
//  Uses COMPOSITION to wrap LegacyOrderProcessor.
//  The legacy class is NOT touched — only new code is added.
// ══════════════════════════════════════════════════════════════

/**
 * LegacyOrderFacade wraps the messy LegacyOrderProcessor behind a
 * clean, intention-revealing interface.
 *
 * WHY COMPOSITION (not inheritance)?
 *  - We hold a private instance of LegacyOrderProcessor.
 *  - We delegate to it without exposing its messy API.
 *  - The legacy class is unchanged; we only add this new class.
 *
 * This is the "Facade over legacy code" pattern — extremely common
 * in real-world software when you cannot refactor existing code
 * (e.g., third-party library, shared system, frozen codebase).
 */
public class LegacyOrderFacade {

    // Composition: we OWN an instance of the legacy processor
    private final LegacyOrderProcessor processor;

    public LegacyOrderFacade() {
        this.processor = new LegacyOrderProcessor();
    }

    /**
     * Clean, descriptive method the client calls.
     * Delegates all work to the legacy class internally.
     *
     * @param customerEmail  customer's email address
     * @param itemCode       product/item identifier
     * @param amount         order total
     * @param deliveryAddress shipping destination
     */
    public void placeOrder(String customerEmail, String itemCode,
                           double amount, String deliveryAddress) {
        // All the messy orchestration is hidden inside LegacyOrderProcessor.
        // The client only needs to call this one clean method.
        processor.processOrder(customerEmail, itemCode, amount, deliveryAddress);
    }
}

// ══════════════════════════════════════════════════════════════
//  CLIENT — clean and simple
// ══════════════════════════════════════════════════════════════

class Main {
    public static void main(String[] args) {

        // Client uses the facade — knows nothing about LegacyOrderProcessor
        LegacyOrderFacade facade = new LegacyOrderFacade();
        facade.placeOrder("jane@example.com", "MONITOR", 349.99, "789 Elm Street");
    }
}
