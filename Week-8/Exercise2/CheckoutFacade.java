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
    boolean isAvailable() { return true; }
}

class Email {
    void send(String to, String subject, String body) {
        System.out.println("Email to " + to + " | " + subject + " | " + body);
    }
}

class TaxCalculator {
    double calculate(double price, String state) {
        double rate = "CA".equalsIgnoreCase(state) ? 0.08 : 0.0;
        return price * rate;
    }
}

class Logger {
    void log(String userId, boolean success) {
        String status = success ? "SUCCESS" : "FAILURE";
        System.out.println("[LOG] " + System.currentTimeMillis() + " | " + userId + " | " + status);
    }
}

class OrderResult {
    private final boolean success;
    private final String trackingNumber;
    private final String message;

    public OrderResult(boolean success, String trackingNumber, String message) {
        this.success = success;
        this.trackingNumber = trackingNumber;
        this.message = message;
    }

    public boolean isSuccess()         { return success; }
    public String getTrackingNumber()  { return trackingNumber; }
    public String getMessage()         { return message; }

    @Override
    public String toString() {
        return "OrderResult{success=" + success + ", tracking=" + trackingNumber + ", message=" + message + "}";
    }
}

public class CheckoutFacade {
    private Inventory inventory;
    private Payment payment;
    private Shipping shipping;
    private Email email;
    private TaxCalculator taxCalculator;
    private Logger logger;

    public CheckoutFacade() {
        this.inventory = new Inventory();
        this.payment = new Payment();
        this.shipping = new Shipping();
        this.email = new Email();
        this.taxCalculator = new TaxCalculator();
        this.logger = new Logger();
    }

    public OrderResult checkout(String userId, String productId, double price, String address, String state) {
        if (!inventory.checkStock(productId)) {
            logger.log(userId, false);
            return new OrderResult(false, null, "Out of stock");
        }

        double tax = taxCalculator.calculate(price, state);
        double total = price + tax;

        if (!payment.charge(userId, total)) {
            logger.log(userId, false);
            return new OrderResult(false, null, "Payment failed");
        }

        inventory.reserve(productId);

        if (!shipping.isAvailable()) {
            payment.refund(userId, total);
            inventory.release(productId);
            logger.log(userId, false);
            return new OrderResult(false, null, "Shipping unavailable, order rolled back");
        }

        String label = shipping.createLabel(address);
        shipping.schedulePickup(label);
        email.send(userId, "Order Confirmed", "Your order is on the way! Total: $" + total + " Tracking: " + label);
        logger.log(userId, true);

        return new OrderResult(true, label, "Order placed successfully");
    }
}

class Main {
    public static void main(String[] args) {
        CheckoutFacade facade = new CheckoutFacade();

        OrderResult result1 = facade.checkout("alice@example.com", "LAPTOP", 999.99, "456 Oak Ave", "TX");
        System.out.println(result1);

        OrderResult result2 = facade.checkout("bob@example.com", "PHONE", 699.00, "123 Main St", "CA");
        System.out.println(result2);
    }
}
