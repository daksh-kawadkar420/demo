package myhospitalapplication.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import myhospitalapplication.dbutil.MyHospitalDBConnection;
import myhospitalapplication.pojo.MyEmpPojo;

public class MyEmployeeDao {
    
    private static PreparedStatement ps, ps1, ps2, ps3;
    private static Statement st, st1, st2, st3, st4, st5;
    
    static {
        
        try {
            
            st = MyHospitalDBConnection.openConnection().createStatement();
            st1 = MyHospitalDBConnection.openConnection().createStatement();
            st2 = MyHospitalDBConnection.openConnection().createStatement();
            st3 = MyHospitalDBConnection.openConnection().createStatement();
            st4 = MyHospitalDBConnection.openConnection().createStatement();
            st5 = MyHospitalDBConnection.openConnection().createStatement();
                    
            ps = MyHospitalDBConnection.openConnection().prepareStatement("INSERT INTO EMPLOYEES VALUES (?,?,?,?)");
            ps1 = MyHospitalDBConnection.openConnection().prepareStatement("SELECT * FROM EMPLOYEES WHERE EMPID = ?");
            ps2 = MyHospitalDBConnection.openConnection().prepareStatement("DELETE FROM EMPLOYEES WHERE EMPID = ?");
            ps3 = MyHospitalDBConnection.openConnection().prepareStatement("UPDATE EMPLOYEES SET ENAME = ?, ROLE = ?, SAL = ? WHERE EMPID = ?");
            
        } catch(SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static String getNewEmpID() throws SQLException {
        
        ResultSet rs = st.executeQuery("SELECT max(EMPID) FROM EMPLOYEES");
        if(rs.next()) {
            String emp = rs.getString(1);
            int no = Integer.parseInt(emp.substring(1));
            return "E" + (no + 1);
        }
        else {
            return "E101";
        }
    }
    
    /*-------------------------------------------------------------------------*/
    
    
    public static boolean addEmployee(MyEmpPojo ep) throws SQLException {
        ps.setString(1, ep.getEmpID());
        ps.setString(2, ep.getEmpName());
        ps.setString(3, ep.getRole());
        ps.setDouble(4, ep.getSal());
        
        return (ps.executeUpdate() != 0);
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static HashMap<String, String> getEmpDetailsByID() {
        
        HashMap<String, String> employeeList = new HashMap<>();
        
        try {
            ResultSet rs = st5.executeQuery("Select EMPID, ENAME from employees");
            while(rs.next()) {
                employeeList.put(rs.getString(1), rs.getString(2));
            }
            System.out.println("coming list from database :" + employeeList);
            return employeeList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static boolean deleteEmpByID(String EMPID) throws SQLException {
        ps2.setString(1, EMPID);
        
        return (ps2.executeUpdate() != 0);
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static ArrayList<MyEmpPojo> getAllEmployee() throws SQLException {
        
        ResultSet rs = st1.executeQuery("SELECT * FROM EMPLOYEES");
        ArrayList<MyEmpPojo> employeeList = new ArrayList<MyEmpPojo>();
        while(rs.next()) {
            MyEmpPojo ep = new MyEmpPojo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getDouble(4));
            employeeList.add(ep);
        }
        return employeeList;
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static HashMap<String, MyEmpPojo> getEmpListByID() throws SQLException {
        ResultSet rs = st4.executeQuery("SELECT EMPID, ENAME, SAL FROM EMPLOYEES");
        HashMap<String, MyEmpPojo> empListByID = new HashMap<>();
        
        while(rs.next()) {
            String id = rs.getString(1);
            String name = rs.getString(2);
            Double sal = rs.getDouble(3);
            MyEmpPojo mep = new MyEmpPojo();
            mep.setEmpName(name);
            mep.setSal(sal);
            empListByID.put(id, mep);
        }
        
        return empListByID;
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static boolean updateEmployee(MyEmpPojo mep) throws SQLException {
        
        ps3.setString(1, mep.getEmpName());
        ps3.setString(2, mep.getRole());
        ps3.setDouble(3, mep.getSal());
        ps3.setString(4, mep.getEmpID());
        
        return (ps3.executeUpdate() != 0);
    }
    
    
    /*-------------------------------------------------------------------------*/
    
    
    public static HashMap<String, String> getAllUnRegisteredReceptionist() throws SQLException {
        
        ResultSet rs = st3.executeQuery("select EMPID,ENAME from EMPLOYEES where ROLE='Receptionist' and EMPID not in (select EMPID from USERS where USERTYPE='Receptionist')");
        HashMap<String, String> receptionist = new HashMap();
        
        while(rs.next()) {
            String id = rs.getString(1);
            String name = rs.getString(2);
            receptionist.put(id, name);
        }
        
        return receptionist;
    }
    
    
    /*-------------------------------------------------------------------------*/
}
