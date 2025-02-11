package domain.reservation;

import java.util.Date;

public interface ReservationManager {
    String createReservation(Date stayingDate) throws ReservationException;
    Date consumeReservation(String reservationNumber) throws ReservationException;
    Date getReservationDate(String reservationNumber) throws ReservationException;
    void deleteReservation(String reservationNumber) throws ReservationException;
}