package domain.reservation;

import domain.DaoFactory;
import java.util.Date;

public class ReservationSqlManager implements ReservationManager {
    private ReservationDao reservationDao;
    private static long counter = 0;

    @Override
    public String createReservation(Date stayingDate) throws ReservationException {
        Reservation reservation = new Reservation();
        String reservationNumber = generateReservationNumber();
        reservation.setReservationNumber(reservationNumber);
        reservation.setStayingDate(stayingDate);
        reservation.setStatus(Reservation.RESERVATION_STATUS_CREATE);

        getReservationDao().createReservation(reservation);
        return reservationNumber;
    }

    @Override
    public Date consumeReservation(String reservationNumber) throws ReservationException {
        Reservation reservation = getReservationDao().getReservation(reservationNumber);
        validateReservation(reservation, reservationNumber);
        
        Date stayingDate = reservation.getStayingDate();
        reservation.setStatus(Reservation.RESERVATION_STATUS_CONSUME);
        getReservationDao().updateReservation(reservation);
        return stayingDate;
    }

    @Override
    public Date getReservationDate(String reservationNumber) throws ReservationException {
        Reservation reservation = getReservationDao().getReservation(reservationNumber);
        validateReservation(reservation, reservationNumber);
        return reservation.getStayingDate();
    }

    @Override
    public void deleteReservation(String reservationNumber) throws ReservationException {
        int affectedRows = getReservationDao().deleteReservation(reservationNumber);
        if (affectedRows == 0) {
            throw new ReservationException(ReservationException.CODE_RESERVATION_NOT_FOUND);
        }
    }

    private void validateReservation(Reservation reservation, String reservationNumber) 
        throws ReservationException {
        if (reservation == null) {
            throw new ReservationException(ReservationException.CODE_RESERVATION_NOT_FOUND)
                .addDetail("Reservation Number: " + reservationNumber);
        }
        if (Reservation.RESERVATION_STATUS_CONSUME.equals(reservation.getStatus())) {
            throw new ReservationException(ReservationException.CODE_RESERVATION_ALREADY_CONSUMED)
                .addDetail("Reservation Number: " + reservationNumber);
        }
    }

    private synchronized String generateReservationNumber() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "RES-" + System.currentTimeMillis() + "-" + (counter++);
    }

    private ReservationDao getReservationDao() {
        if (reservationDao == null) {
            reservationDao = DaoFactory.getInstance().getReservationDao();
        }
        return reservationDao;
    }
}