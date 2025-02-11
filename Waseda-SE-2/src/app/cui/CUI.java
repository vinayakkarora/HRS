package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import app.AppException;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomForm;
import util.DateUtil;

public class CUI {

    private static final String LS = System.lineSeparator();
    private BufferedReader reader;

    public static void main(String[] args) throws Exception {
        CUI cui = new CUI();
        cui.run();
    }

    public CUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Main loop: displays the menu and handles user actions.
     */
    private void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            try {
                String menuInput = reader.readLine();
                int selection = parseIntOrDefault(menuInput, -1);

                switch (selection) {
                    case 1:
                        doReserveRoom();
                        break;
                    case 2:
                        doCheckIn();
                        break;
                    case 3:
                        doCheckOut();
                        break;
                    case 9:
                        running = false;
                        System.out.println("System has ended. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid selection. Please choose an option from the menu.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("Input/Output error: " + e.getMessage());
            } catch (AppException e) {
                System.err.println("An application error occurred:");
                e.getDetailMessages().forEach(System.err::println);
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }

        // Cleanup
        closeReader();
    }

    /**
     * Prints the main menu options.
     */
    private void printMainMenu() {
        System.out.println(LS + "========== Hotel Reservation System ==========");
        System.out.println("1. Make a Reservation");
        System.out.println("2. Check-in");
        System.out.println("3. Check-out");
        System.out.println("9. Exit");
        System.out.print("Select an option > ");
    }

    /**
     * Handle room reservation flow.
     * - Disallows reservations for dates in the past.
     */
    private void doReserveRoom() throws IOException, AppException {
        System.out.println(LS + "[Reservation] Enter arrival date (yyyy/mm/dd):");
        System.out.print("> ");
        String dateStr = reader.readLine();

        Date stayingDate = DateUtil.convertToDate(dateStr);
        if (stayingDate == null) {
            System.out.println("Invalid date format. Please use yyyy/mm/dd.");
            return;
        }

        // Compare user-entered date with the current system date
        Date today = new Date();
        if (stayingDate.before(stripTime(today))) {
            System.out.println("Cannot reserve for a past date! Please enter a valid future date.");
            return;
        }

        ReserveRoomForm reserveForm = new ReserveRoomForm();
        reserveForm.setStayingDate(stayingDate);

        String reservationNumber = reserveForm.submitReservation();
        System.out.println("Reservation completed successfully!");
        System.out.println("Arrival Date : " + DateUtil.convertToString(stayingDate));
        System.out.println("Reservation Number : " + reservationNumber);
    }

    /**
     * Handle room check-in flow.
     */
    private void doCheckIn() throws IOException, AppException {
        System.out.println(LS + "[Check-in] Enter reservation number:");
        System.out.print("> ");
        String reservationNumber = reader.readLine();

        if (isBlank(reservationNumber)) {
            System.out.println("Reservation number cannot be empty.");
            return;
        }

        CheckInRoomForm checkInForm = new CheckInRoomForm();
        checkInForm.setReservationNumber(reservationNumber);

        String roomNumber = checkInForm.checkIn();
        System.out.println("Check-in successful!");
        System.out.println("Room Number : " + roomNumber);
    }

    /**
     * Handle room check-out flow.
     */
    private void doCheckOut() throws IOException, AppException {
        System.out.println(LS + "[Check-out] Enter room number:");
        System.out.print("> ");
        String roomNumber = reader.readLine();

        if (isBlank(roomNumber)) {
            System.out.println("Room number cannot be empty.");
            return;
        }

        CheckOutRoomForm checkoutForm = new CheckOutRoomForm();
        checkoutForm.setRoomNumber(roomNumber);
        checkoutForm.checkOut();

        System.out.println("Check-out completed successfully!");
    }

    /**
     * Attempts to parse a string to an integer. If parsing fails, returns the default value.
     */
    private int parseIntOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Closes the reader resource.
     */
    private void closeReader() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Failed to close input reader: " + e.getMessage());
            }
        }
    }

    /**
     * Check if a string is null or empty after trimming.
     */
    private boolean isBlank(String str) {
        return (str == null || str.trim().isEmpty());
    }

    /**
     * Helper method to strip time (HH:mm:ss) from a Date, so that only the date portion is compared.
     * Ensures the user can't reserve for a date strictly "before" today's date in local time.
     */
    private Date stripTime(Date date) {
        if (date == null) return null;
        // For a simple approach, just parse the date back from its yyyy/MM/dd representation:
        String dateOnly = DateUtil.convertToString(date); // e.g. "2025/01/26"
        return DateUtil.convertToDate(dateOnly);
    }
}
