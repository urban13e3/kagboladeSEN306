class Inventory {
    boolean checkStock(String productId) { return true; }
    void reserve(String productId) { System.out.println("Reserved " + productId); }
}

class Payment {
    boolean charge(String userId, double amount) {
        System.out.println("Charged $" + amount + " to " + userId);
        return true;
    }
}

class Shipping {
    String createLabel(String address) { return "TRK" + System.currentTimeMillis(); }
    void schedulePickup(String label)  { System.out.println("Pickup scheduled: " + label); }
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
        Inventory inv   = new Inventory();
        Payment   pay   = new Payment();
        Shipping  ship  = new Shipping();
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

public class LegacyOrderFacade {
    private LegacyOrderProcessor processor;

    public LegacyOrderFacade() {
        this.processor = new LegacyOrderProcessor();
    }

    public void placeOrder(String customerEmail, String itemCode,
                           double amount, String deliveryAddress) {
        processor.processOrder(customerEmail, itemCode, amount, deliveryAddress);
    }
}

class Main {
    public static void main(String[] args) {
        LegacyOrderFacade facade = new LegacyOrderFacade();
        facade.placeOrder("jane@example.com", "MONITOR", 349.99, "789 Elm Street");
    }
}
