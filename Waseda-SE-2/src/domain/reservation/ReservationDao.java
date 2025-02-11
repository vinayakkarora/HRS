/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package domain.reservation;

/**
 * Interface for accessing to Reservation Data Object<br>
 * 
 */
public interface ReservationDao {
	Reservation getReservation(String reservationNumber) throws ReservationException;
    void updateReservation(Reservation reservation) throws ReservationException;
    void createReservation(Reservation reservation) throws ReservationException;
    int deleteReservation(String reservationNumber) throws ReservationException; // Added
}
