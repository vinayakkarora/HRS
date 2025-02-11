package app.cui.checkout;

import app.AppException;
import app.checkout.service.CheckOutService;

/**
 * UI Layer class (text-based form) responsible for user interaction 
 * during the check-out process.
 */
public class CheckOutRoomForm {

    // Reference to the application layer service
    private final CheckOutService checkOutService = new CheckOutService();

    // User-provided room number
    private String roomNumber;

    /**
     * Delegates the check-out action to the application service.
     */
    public void checkOut() throws AppException {
        checkOutService.executeCheckOut(roomNumber);
    }

    // -------------------------
    // Getters & Setters
    // -------------------------
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
