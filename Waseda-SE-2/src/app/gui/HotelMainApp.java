package app.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// JavaFX dialogs
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import app.reservation.ReserveRoomForm;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.AppException;
import util.DateUtil;

import java.util.Optional;
import java.util.Date;

public class HotelMainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Simple vertical layout with spacing
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Create buttons
        Button btnReserve = new Button("Make a Reservation");
        Button btnCheckIn = new Button("Check In");
        Button btnCheckOut = new Button("Check Out");

        // Add them to the layout
        root.getChildren().addAll(btnReserve, btnCheckIn, btnCheckOut);

        // Set up event handlers
        btnReserve.setOnAction(e -> handleReservation());
        btnCheckIn.setOnAction(e -> handleCheckIn());
        btnCheckOut.setOnAction(e -> handleCheckOut());

        // Finalize scene & stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Hotel Reservation System (GUI)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Prompts user for arrival date, calls ReserveRoomForm logic.
     */
    private void handleReservation() {
        // For date input, use a TextInputDialog
        TextInputDialog dialog = new TextInputDialog("2025/01/01");
        dialog.setTitle("Reservation");
        dialog.setHeaderText("Enter arrival date (yyyy/mm/dd):");
        Optional<String> result = dialog.showAndWait();

        // If user clicked OK and provided input
        if (result.isPresent()) {
            String dateStr = result.get();
            // Convert string to Date
            Date stayingDate = DateUtil.convertToDate(dateStr);

            // Basic validation
            if (stayingDate == null) {
                showAlert("Invalid Date", "Please use the format yyyy/mm/dd.");
                return;
            }

            // (Optional) Check if date is in the past
            Date today = new Date();
            if (stayingDate.before(stripTime(today))) {
                showAlert("Invalid Date", "Cannot reserve for a past date!");
                return;
            }

            // Create the form and submit
            ReserveRoomForm form = new ReserveRoomForm();
            form.setStayingDate(stayingDate);

            try {
                String reservationNumber = form.submitReservation();
                showAlert("Reservation Success",
                    "Date: " + DateUtil.convertToString(stayingDate) +
                    "\nReservation #: " + reservationNumber);
            } catch (AppException e) {
                showAlert("Reservation Error", e.getMessage());
            }
        }
    }

    /**
     * Prompts user for reservation number, calls CheckInRoomForm logic.
     */
    private void handleCheckIn() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Check In");
        dialog.setHeaderText("Enter Reservation Number:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String reservationNumber = result.get().trim();
            if (reservationNumber.isEmpty()) {
                showAlert("Invalid Input", "Reservation number cannot be empty.");
                return;
            }

            CheckInRoomForm checkInForm = new CheckInRoomForm();
            checkInForm.setReservationNumber(reservationNumber);

            try {
                String roomNumber = checkInForm.checkIn();
                showAlert("Check In Success", "Assigned to Room #: " + roomNumber);
            } catch (AppException e) {
                showAlert("Check In Error", e.getMessage());
            }
        }
    }

    /**
     * Prompts user for room number, calls CheckOutRoomForm logic.
     */
    private void handleCheckOut() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Check Out");
        dialog.setHeaderText("Enter Room Number:");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String roomNumber = result.get().trim();
            if (roomNumber.isEmpty()) {
                showAlert("Invalid Input", "Room number cannot be empty.");
                return;
            }

            CheckOutRoomForm checkOutForm = new CheckOutRoomForm();
            checkOutForm.setRoomNumber(roomNumber);

            try {
                checkOutForm.checkOut();
                showAlert("Check Out Success", "Room " + roomNumber + " is now checked out.");
            } catch (AppException e) {
                showAlert("Check Out Error", e.getMessage());
            }
        }
    }

    /**
     * Utility to show a simple alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Strip time portion from a Date to compare by day only.
     */
    private Date stripTime(Date date) {
        String dateOnly = DateUtil.convertToString(date); // e.g. "2025/01/26"
        return DateUtil.convertToDate(dateOnly);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
