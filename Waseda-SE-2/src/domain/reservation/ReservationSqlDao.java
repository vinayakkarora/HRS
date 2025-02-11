/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package domain.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.DateUtil;
/**
 * DB SQL implementation of Reservation Data Object interface<br>
 * 
 */
public class ReservationSqlDao implements ReservationDao {

	private static final String ID = "sa";

	private static final String PASSWORD = "";

	private static final String DRIVER_NAME = "org.hsqldb.jdbcDriver";

	private static final String URL = "jdbc:hsqldb:hsql://localhost;shutdown=true";

	private static final String TABLE_NAME = "RESERVATION";

	@Override
    public Reservation getReservation(String reservationNumber) throws ReservationException {
        String sql = "SELECT reservationnumber, stayingdate, status FROM " + TABLE_NAME 
                   + " WHERE reservationnumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservationNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setReservationNumber(reservationNumber);
                    reservation.setStatus(rs.getString("status"));
                    reservation.setStayingDate(DateUtil.convertToDate(rs.getString("stayingdate")));
                    return reservation;
                }
                return null;
            }
        } catch (SQLException e) {
            throw handleSqlException(e, "getReservation");
        }
    }

    @Override
    public void updateReservation(Reservation reservation) throws ReservationException {
        String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE reservationnumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservation.getStatus());
            pstmt.setString(2, reservation.getReservationNumber());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw handleSqlException(e, "updateReservation");
        }
    }

    @Override
    public void createReservation(Reservation reservation) throws ReservationException {
        String sql = "INSERT INTO " + TABLE_NAME 
                   + " (reservationnumber, stayingdate, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservation.getReservationNumber());
            pstmt.setString(2, DateUtil.convertToString(reservation.getStayingDate()));
            pstmt.setString(3, reservation.getStatus());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw handleSqlException(e, "createReservation");
        }
    }

    @Override
    public int deleteReservation(String reservationNumber) throws ReservationException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE reservationnumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, reservationNumber);
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw handleSqlException(e, "deleteReservation");
        }
    }

    private ReservationException handleSqlException(SQLException e, String operation) {
        ReservationException ex = new ReservationException(
            ReservationException.CODE_DB_EXEC_QUERY_ERROR, e);
        ex.getDetailMessages().add("Operation: " + operation);
        ex.getDetailMessages().add("SQL State: " + e.getSQLState());
        ex.getDetailMessages().add("Error Code: " + e.getErrorCode());
        return ex;
    }

	private Connection getConnection() throws ReservationException {
		Connection connection = null;
		try {
			Class.forName(DRIVER_NAME);
			connection = DriverManager.getConnection(URL, ID, PASSWORD);
		}
		catch (Exception e) {
			throw new ReservationException(ReservationException.CODE_DB_CONNECT_ERROR, e);
		}
		return connection;
	}

	private void close(ResultSet resultSet, Statement statement, Connection connection)
			throws ReservationException {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
		catch (SQLException e) {
			throw new ReservationException(ReservationException.CODE_DB_CLOSE_ERROR, e);
		}
	}
}
