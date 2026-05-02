class RoomService {
    boolean isAvailable(String roomType) {
        System.out.println("Checking availability for: " + roomType);
        return true;
    }
    void book(String roomType, String guest) {
        System.out.println("Room " + roomType + " booked for " + guest);
    }
}

class PaymentService {
    boolean charge(String guest, double price) {
        System.out.println("Charging " + guest + " $" + price);
        return true;
    }
}

class LoyaltyPoints {
    void addPoints(String guest, int points) {
        System.out.println("Added " + points + " loyalty points for " + guest);
    }
}

class EmailService {
    void sendConfirmation(String guest, String roomType) {
        System.out.println("Email sent to " + guest + " for room: " + roomType);
    }
}

public class BookingFacade {
    private RoomService rooms;
    private PaymentService payment;
    private LoyaltyPoints loyalty;
    private EmailService email;

    public BookingFacade() {
        this.rooms = new RoomService();
        this.payment = new PaymentService();
        this.loyalty = new LoyaltyPoints();
        this.email = new EmailService();
    }

    public boolean bookRoom(String guest, String roomType, double price) {
        if (!rooms.isAvailable(roomType)) {
            System.out.println("Room not available");
            return false;
        }
        if (!payment.charge(guest, price)) {
            System.out.println("Payment declined");
            return false;
        }
        rooms.book(roomType, guest);
        loyalty.addPoints(guest, (int) price);
        email.sendConfirmation(guest, roomType);
        System.out.println("Booking confirmed");
        return true;
    }
}

class Main {
    public static void main(String[] args) {
        BookingFacade booking = new BookingFacade();
        booking.bookRoom("john@example.com", "DELUXE", 250.00);
    }
}
