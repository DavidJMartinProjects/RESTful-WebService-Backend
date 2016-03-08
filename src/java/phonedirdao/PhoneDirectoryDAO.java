package phonedirdao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;

public class PhoneDirectoryDAO {

    private Connection con = null;

    public static void main(String[] args) {
        PhoneDirectoryDAO phoneDirectoryDAO = new PhoneDirectoryDAO();
    }

    public PhoneDirectoryDAO() {
        try {
            System.out.println("Loading db driver");
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            //System.out.println("Db driver loaded");
            con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/SOA_DB",
                    "sean",
                    "sean");
        } catch (ClassNotFoundException ex) {
            System.err.println("\nUnable to load the JDBC driver");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("\nSQLException");
            ex.printStackTrace();
        }
    }

    public ArrayList<PhoneDirectoryEntry> getAllNumbers() {
        ArrayList<PhoneDirectoryEntry> entries = new ArrayList<>();

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT * FROM APP.SOA_PHONES");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PhoneDirectoryEntry pde = new PhoneDirectoryEntry(
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5));
                entries.add(pde);
            }

        } catch (SQLException ex) {
            System.err.println("\nSQLException in getAllNumbers()");
            ex.printStackTrace();
        }
        return entries;
    }

    public PhoneDirectoryEntry getPhoneNumberDetails(String phoneNumber) {
        PhoneDirectoryEntry pde = null;
        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT ID, PHONE_NO, FIRST_NAME,"
                    + "SURNAME, ADDRESS"
                    + " FROM "
                    + "APP.SOA_PHONES WHERE (PHONE_NO = ?)");
            pstmt.setString(1, phoneNumber);

            ResultSet rs = pstmt.executeQuery();

            // move the cursor to the start
            if (!rs.next()) {
                return null;
            }

            // we have at least one record...
            pde = new PhoneDirectoryEntry(
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));

        } catch (SQLException ex) {
            System.err.println("\nSQLException in getPhoneNumberDetails()");
            ex.printStackTrace();
        }

        return pde;
    }

    public void addPhoneNumber(PhoneDirectoryEntry myEntry) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO APP.SOA_PHONES "
                    + "(ID, PHONE_NO, FIRST_NAME, "
                    + "SURNAME, ADDRESS, TIMESTAMP) "
                    + "VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, myEntry.getId());
            ps.setString(2, myEntry.getPhoneNo());
            ps.setString(3, myEntry.getFname());
            ps.setString(4, myEntry.getSurname());
            ps.setString(5, myEntry.getAddress());
            ps.setTimestamp(6, myEntry.nowTimestamp);

            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("\n SQLException in addPhoneNumber");
            ex.printStackTrace();
        }
    }

    public void deleteAllNumbers() {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM APP.SOA_PHONES"
            );
            ps.execute();
        } catch (SQLException ex) {
            System.err.println("\n SQLException in deleteAllNumbers");
            ex.printStackTrace();
        }
    }

    public void updatePhoneDetails(PhoneDirectoryEntry myEntry) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE APP.SOA_PHONES "
                    + "SET ID=?, FIRST_NAME=?, SURNAME=?, "
                    + "ADDRESS=?, TIMESTAMP=? "
                    + "WHERE (PHONE_NO=?)"
            );

            ps.setInt(1, myEntry.getId());
            ps.setString(2, myEntry.getFname());
            ps.setString(3, myEntry.getSurname());
            ps.setString(4, myEntry.getAddress());
            ps.setTimestamp(5, myEntry.getMyTimestamp());
            ps.setString(6, myEntry.getPhoneNo());

            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("\n SQLException in updatePhoneDetails");
            ex.printStackTrace();
        }
    }

    public void deletePhoneNumber(String phoneNumber) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM APP.SOA_PHONES "
                    + "WHERE PHONE_NO=?"
            );

            ps.setString(1, phoneNumber);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("\n SQLException in deletePhoneNumber");
            ex.printStackTrace();
        }
    }

    public static boolean checkPhoneNoExist(String number) {
        PhoneDirectoryDAO phoneDirectoryDAO = new PhoneDirectoryDAO();
        ArrayList<PhoneDirectoryEntry> myEntries = phoneDirectoryDAO.getAllNumbers();

        boolean exists = false;

        for (PhoneDirectoryEntry myEntry : myEntries) {
            if (myEntry.getPhoneNo().equals(number)) {
                exists = true;
            }
        }
        return exists;
    }

    public Timestamp getLastModifiedDate(String number) {
        PhoneDirectoryEntry pde = null;

        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT ID, PHONE_NO, FIRST_NAME,"
                    + "SURNAME, ADDRESS, TIMESTAMP"
                    + " FROM "
                    + "APP.SOA_PHONES WHERE (PHONE_NO = ?)");
            pstmt.setString(1, number);

            ResultSet rs = pstmt.executeQuery();

            // move the cursor to the start
            if (!rs.next()) {
                return null;
            }

            // we have at least one record...
            pde = new PhoneDirectoryEntry(
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getTimestamp(6));
        } catch (SQLException ex) {
            System.err.println("\nSQLException in getPhoneNumberDetails()");
            ex.printStackTrace();
        }
        return pde.getMyTimestamp();
    }
}
