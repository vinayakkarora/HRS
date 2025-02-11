/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import app.AppException;
import app.cancellation.CancelReservationForm;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomForm;
import util.DateUtil;

public class CUI2 {

    private static final String LS = System.lineSeparator();
    private BufferedReader reader;

    public static void main(String[] args) throws Exception {
        CUI2 cui = new CUI2();
        cui.run();
    }

    public CUI2() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

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
                    case 4:
                        doCancelReservation();
                        break;
                    case 9:
                        running = false;
                        printBox("System has ended. Goodbye!");
                        break;
                    default:
                        System.out.println(LS + "!!! Invalid selection. Please choose a valid option.");
                        break;
                }
            } catch (IOException e) {
                printError("Input/Output error: " + e.getMessage());
            } catch (AppException e) {
                printError("Application Error: " + e.getMessage());
                if (!e.getDetailMessages().isEmpty()) {
                    printError("Details:");
                    e.getDetailMessages().forEach(msg -> printError("- " + msg));
                }
            } catch (Exception e) {
                printError("Unexpected error: " + e.getMessage());
            }
        }
        closeReader();
    }

    private void printMainMenu() {
        System.out.println(LS + "==========================================");
        System.out.println("          HOTEL RESERVATION SYSTEM          ");
        System.out.println("==========================================");
        System.out.println("1. Make a Reservation");
        System.out.println("2. Check-In");
        System.out.println("3. Check-Out & Payment");
        System.out.println("4. Cancel Reservation");
        System.out.println("9. Exit");
        System.out.println("------------------------------------------");
        System.out.print("Enter your choice -> ");
    }

    private void doReserveRoom() throws IOException, AppException {
        printHeader("New Reservation");
        Date stayingDate = readDate("Enter arrival date (yyyy/mm/dd):");
        if (stayingDate == null) return;

        ReserveRoomForm form = new ReserveRoomForm();
        form.setStayingDate(stayingDate);
        String reservationNumber = form.submitReservation();

        printBox("Reservation Successful!",
                "Arrival Date: " + DateUtil.convertToString(stayingDate),
                "Reservation No: " + reservationNumber);
    }

    private void doCheckIn() throws IOException, AppException {
        printHeader("Check-In");
        String reservationNumber = readInput("Enter reservation number:");
        if (isBlank(reservationNumber)) {
            printError("Reservation number cannot be empty.");
            return;
        }

        CheckInRoomForm form = new CheckInRoomForm();
        form.setReservationNumber(reservationNumber);
        String roomNumber = form.checkIn();

        printBox("Check-In Successful!", "Room No: " + roomNumber);
    }

    private void doCheckOut() throws IOException, AppException {
        printHeader("Check-Out");
        String roomNumber = readInput("Enter room number:");
        if (isBlank(roomNumber)) {
            printError("Room number cannot be empty.");
            return;
        }

        CheckOutRoomForm form = new CheckOutRoomForm();
        form.setRoomNumber(roomNumber);
        int amount = form.checkOut();

        printBox("Check-Out Successful!", 
                 "Room No: " + roomNumber,
                 "Total Amount Paid: " + amount + " yen");
    }

    private void doCancelReservation() throws IOException, AppException {
        printHeader("Cancel Reservation");
        String reservationNumber = readInput("Enter reservation number:");
        if (isBlank(reservationNumber)) {
            printError("Reservation number cannot be empty.");
            return;
        }

        CancelReservationForm form = new CancelReservationForm();
        form.setReservationNumber(reservationNumber);
        form.cancel();

        printBox("Reservation Cancelled Successfully!", 
                 "Reservation No: " + reservationNumber);
    }

    // Helper methods
    private Date readDate(String prompt) throws IOException {
        System.out.println(prompt);
        System.out.print("-> ");
        String dateStr = reader.readLine();
        Date date = DateUtil.convertToDate(dateStr);

        if (date == null) {
            printError("Invalid date format. Use yyyy/mm/dd.");
            return null;
        }

        Date today = new Date();
        if (date.before(stripTime(today))) {
            printError("Cannot reserve past dates. Enter a future date.");
            return null;
        }
        return date;
    }

    private String readInput(String prompt) throws IOException {
        System.out.println(prompt);
        System.out.print("-> ");
        return reader.readLine();
    }

    private void printHeader(String title) {
        System.out.println(LS + "══════════════════════════════════════");
        System.out.println(" " + title.toUpperCase());
        System.out.println("══════════════════════════════════════");
    }

    private void printBox(String... lines) {
        System.out.println(LS + "┌──────────────────────────────────┐");
        for (String line : lines) {
            System.out.println("│  " + String.format("%-30s", line) + "│");
        }
        System.out.println("└──────────────────────────────────┘");
    }

    private void printError(String message) {
        System.out.println(LS + "!!!  ERROR: " + message);
    }

    private int parseIntOrDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void closeReader() {
        try {
            if (reader != null) reader.close();
        } catch (IOException e) {
            System.err.println("Error closing reader: " + e.getMessage());
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private Date stripTime(Date date) {
        if (date == null) return null;
        return DateUtil.convertToDate(DateUtil.convertToString(date));
    }
}