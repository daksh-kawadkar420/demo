package myhospitalapplication.dao;

import java.sql.*;
import java.util.HashMap;
import myhospitalapplication.dbutil.MyHospitalDBConnection;
import myhospitalapplication.pojo.Users;

public class UserDao {
    
    private static PreparedStatement ps;
    
    static {
        try {
            ps = MyHospitalDBConnection.openConnection().prepareStatement("SELECT USERNAME FROM USERS WHERE USERID = ? AND BINARY PASSWORD = ? AND USERTYPE =?");
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
            }
    }
    
    public static String validateUser(Users user) throws SQLException {
        ps.setString(1, user.getUserID());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getUserType());
        ResultSet rs = ps.executeQuery();
        String username = null;
        if(rs.next()) {
            username = rs.getString(1);
        }
        return username;
    }
    public static boolean changePassword(String userid, String password) throws SQLException {
        PreparedStatement ps3 = MyHospitalDBConnection.openConnection().prepareStatement("UPDATE USERS SET PASSWORD = ? WHERE USERID = ?");
        ps3.setString(1, password);
        ps3.setString(2, userid);
        return (ps3.executeUpdate() != 0);
    }

    public static HashMap<String, String> getReceptionist() throws SQLException {
        HashMap<String, String> receptionistList = new HashMap<>();
        Statement st2 = MyHospitalDBConnection.openConnection().createStatement();
        ResultSet rs = st2.executeQuery("SELECT USERID, USERNAME FROM USERS WHERE USERTYPE = 'Receptionist'");
        while (rs.next()) {
            receptionistList.put(rs.getString(1), rs.getString(2));
        }
        return receptionistList;
    }

}
