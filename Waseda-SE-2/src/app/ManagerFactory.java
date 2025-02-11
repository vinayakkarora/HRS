/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app;

import domain.payment.PaymentManager;
import domain.reservation.ReservationManager;
import domain.reservation.ReservationSqlManager;
import domain.room.RoomManager;

public class ManagerFactory {
    private static ManagerFactory instance = new ManagerFactory();
    
    // Use concrete implementations instead of interfaces for instance variables
    private ReservationManager reservationManager = new ReservationSqlManager();
    private RoomManager roomManager = new RoomManager();
    private PaymentManager paymentManager = new PaymentManager();

    private ManagerFactory() {
        // Private constructor for singleton
    }

    public static ManagerFactory getInstance() {
        return instance;
    }

    public ReservationManager getReservationManager() {
        return this.reservationManager;
    }

    public RoomManager getRoomManager() {
        return this.roomManager;
    }

    public PaymentManager getPaymentManager() {
        return this.paymentManager;
    }
}