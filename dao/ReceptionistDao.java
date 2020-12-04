package myhospitalapplication.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import myhospitalapplication.dbutil.MyHospitalDBConnection;
import myhospitalapplication.pojo.MyEmpPojo;
import myhospitalapplication.pojo.UserPojo;

public class ReceptionistDao {
    
    private static PreparedStatement ps, ps1, ps2;
    private static Statement st, st1, st2;
    
    static {
        
        try {
            
            st = MyHospitalDBConnection.openConnection().createStatement();
            st1 = MyHospitalDBConnection.openConnection().createStatement();
            st2 = MyHospitalDBConnection.openConnection().createStatement();
            
            ps = MyHospitalDBConnection.openConnection().prepareStatement("INSERT INTO USERS VALUES(?, ?, ?, ?, ?)");
            ps1 = MyHospitalDBConnection.openConnection().prepareStatement("DELETE FROM USERS WHERE USERID = ?");
            ps2 = MyHospitalDBConnection.openConnection().prepareStatement("UPDATE EMPLOYEES,\n"
                    + "    USERS \n"
                    + "SET \n"
                    + "    EMPLOYEES.ENAME = ?,\n"
                    + "    EMPLOYEES.SAL = ?,\n"
                    + "    USERS.USERNAME = ?\n"
                    + "where \n"
                    + "    EMPLOYEES.EMPID=USERS.EMPID and EMPLOYEES.EMPID=?");
                    
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public static ArrayList<String> getReceptionist() throws SQLException {
        
        ResultSet rs = st.executeQuery("SELECT USERID FROM USERS WHERE USERTYPE='Receptionist'");
        ArrayList<String> receptionistid = new ArrayList<>();
        while(rs.next()) {
            receptionistid.add(rs.getString(1));
        }
        return receptionistid;
    }
    
    public static boolean removeReceptionist(String receptionistId) throws SQLException {
        
        ps1.setString(1, receptionistId);
        return (ps1.executeUpdate() != 0);
        
    }
    
    public static ArrayList<MyEmpPojo> getAllReceptionist() throws SQLException {

        ResultSet rs = st1.executeQuery("select USERS.USERID,EMPLOYEES.ENAME,EMPLOYEES.EMPID,EMPLOYEES.SAL from EMPLOYEES inner Join USERS on EMPLOYEES.EMPID=USERS.EMPID where USERS.USERTYPE='Receptionist'");
        ArrayList<MyEmpPojo> employeesList = new ArrayList<MyEmpPojo>();
        while (rs.next()) {
            MyEmpPojo emp = new MyEmpPojo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
            employeesList.add(emp);
        }
        return employeesList;
    }
    
    public static boolean addUser(UserPojo user) throws SQLException {
        ps.setString(1, user.getUserId());
        ps.setString(2, user.getEmpname());
        ps.setString(3, user.getEmpId());
        ps.setString(4, user.getPassword());
        ps.setString(5, user.getUserType());
        
        return(ps.executeUpdate() != 0);
    }
    
    public static HashMap<String, MyEmpPojo> getReceptionistDetailsById() throws SQLException {
        
        ResultSet rs = st2.executeQuery("SELECT EMPLOYEES.EMPID,EMPLOYEES.ENAME,EMPLOYEES.SAL FROM EMPLOYEES INNER JOIN USERS ON EMPLOYEES.EMPID=USERS.EMPID WHERE ROLE='Receptionist'");
        HashMap<String, MyEmpPojo> receptionistList = new HashMap<>();
        while (rs.next()) {
            MyEmpPojo emp = new MyEmpPojo();
            emp.setEmpName(rs.getString(2));
            emp.setSal(rs.getDouble(3));
            receptionistList.put(rs.getString(1), emp);
        }
        return receptionistList;
    }
    
    public static boolean updateRecept(MyEmpPojo emp) throws SQLException {
        ps2.setString(1, emp.getEmpName());
        ps2.setDouble(2, emp.getSal());
        ps2.setString(3, emp.getEmpName());
        ps2.setString(4, emp.getEmpID());
        return (ps2.executeUpdate() != 0);
    }
}