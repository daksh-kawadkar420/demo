package myhospitalapplication.dao;

import java.sql.*;
import java.util.*;
import myhospitalapplication.dbutil.MyHospitalDBConnection;
import myhospitalapplication.pojo.DoctorListPojo;
import myhospitalapplication.pojo.DoctorPojo;

public class DoctorDao {
    
    private static Statement st, st1, st2, st3, st4, st5;
    private static PreparedStatement ps, ps1, ps2, ps3, ps4;
    
    
    static {
        
        try {
            // Statements
            st  = MyHospitalDBConnection.openConnection().createStatement();
            st1 = MyHospitalDBConnection.openConnection().createStatement();
            st2 = MyHospitalDBConnection.openConnection().createStatement();
            st3 = MyHospitalDBConnection.openConnection().createStatement();
            st4 = MyHospitalDBConnection.openConnection().createStatement();
            st5 = MyHospitalDBConnection.openConnection().createStatement();
            
            // Prepared Statements
            
            ps = MyHospitalDBConnection.openConnection().prepareStatement("INSERT INTO USERS VALUES(?, ?, ?, ?, ?)");
            ps1 = MyHospitalDBConnection.openConnection().prepareStatement("INSERT INTO DOCTORS VALUES(?, ?, ?, ?, ?)");
            ps2 = MyHospitalDBConnection.openConnection().prepareStatement("DELETE FROM USERS WHERE USERID = ?");
            ps3 = MyHospitalDBConnection.openConnection().prepareStatement("UPDATE DOCTORS set ACTIVE = 'N' WHERE USERID = ?");
            ps4 = MyHospitalDBConnection.openConnection().prepareStatement("UPDATE DOCTORS ,EMPLOYEES,USERS \n"
                    + "SET \n"
                    + "    EMPLOYEES.ENAME = ?,\n"
                    + "    EMPLOYEES.SAL = ?,\n"
                    + "    USERS.USERNAME = ?,\n"
                    + "    DOCTORS.QUALIFICATION = ?,\n"
                    + "    DOCTORS.SPECIALIST = ?\n"
                    + "Where\n"
                    + "   USERS.USERID=DOCTORS.USERID and EMPLOYEES.EMPID=USERS.EMPID and EMPLOYEES.EMPID= ?");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
    public static HashMap<String, String> getNonRegisteredDoctors() throws SQLException {
        
        ResultSet rs = st1.executeQuery("SELECT EMPID, ENAME FROM EMPLOYEES WHERE ROLE = 'DOCTOR' AND EMPID not in (SELECT EMPID FROM USERS WHERE USERTYPE = 'Doctor')");
        HashMap<String, String> doctor = new HashMap<>();
        
        while(rs.next()) {
            String id = rs.getString(1);
            String name = rs.getString(2);
            doctor.put(id, name);
        }
        
        return doctor;
        
    }
    
    public static String getNewDoctorId() throws SQLException {
        ResultSet rs = st2.executeQuery("select max(DOCTORID) from DOCTORS");
        rs.next();
        String doctorid = rs.getString(1);
//        System.out.println("doctorid ::" + doctorid);
        if (doctorid == null) {
            return "D101";
        }
        int dno = Integer.parseInt(doctorid.substring(1));
        return "D" + (dno + 1);
    }
    
    public static boolean addDoctor(DoctorPojo dp) throws SQLException {
        ps.setString(1, dp.getUserId());
        ps.setString(2, dp.getEmpname());
        ps.setString(3, dp.getEmpId());
        ps.setString(4, dp.getPassword());
        ps.setString(5, dp.getUserType());
        
        int ans = ps.executeUpdate();
        
        if(ans > 0) {
            ps1.setString(1, dp.getUserId());
            ps1.setString(2, dp.getDoctorId());
            ps1.setString(3, dp.getQualification());
            ps1.setString(4, dp.getSpecialist());
            ps1.setString(5, dp.getIsActive());
            return (ps1.executeUpdate() != 0);
        } else {
            ps2.setString(1, dp.getUserId());
            ps2.executeUpdate();
            return false;
        }
    }
    
    public static ArrayList getDoctorId() throws SQLException {
        
        ResultSet rs = st.executeQuery("SELECT USERID FROM DOCTORS WHERE ACTIVE = 'Y'");
        ArrayList<String> doctorId = new ArrayList<>();
        while(rs.next()) {
            doctorId.add(rs.getString(1));
        }
        return doctorId;
    }
    
    public static boolean removeDoctor(String doctorId) throws SQLException {
        
        ps3.setString(1, doctorId);
        return (ps3.executeUpdate() != 0);
        
    }
    
    public static ArrayList<DoctorListPojo> getAllDoctor() throws SQLException {

        ResultSet rs = st3.executeQuery("SELECT EMPLOYEES.EMPID,EMPLOYEES.ENAME,EMPLOYEES.ROLE,EMPLOYEES.SAL,USERS.USERID,DOCTORS.DOCTORID,DOCTORS.QUALIFICATION,DOCTORS.SPECIALIST FROM\n"
                + " EMPLOYEES INNER JOIN USERS ON EMPLOYEES.EMPID = USERS.EMPID AND EMPLOYEES.ROLE=USERS.USERTYPE INNER JOIN DOCTORS ON USERS.USERID=DOCTORS.USERID WHERE USERS.USERTYPE='Doctor' AND DOCTORS.Active='Y'");
        ArrayList<DoctorListPojo> doctorList = new ArrayList<>();
        while (rs.next()) {
            DoctorListPojo emp = new DoctorListPojo(rs.getString(1), rs.getString(2), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8));
            doctorList.add(emp);
        }
        return doctorList;
    }
    
    public static HashMap<String, DoctorListPojo> getDoctorDetailsById() throws SQLException {
        
        ResultSet rs = st5.executeQuery("select USERS.EMPID,USERS.USERNAME,EMPLOYEES.SAL,DOCTORS.DOCTORID,DOCTORS.QUALIFICATION,DOCTORS.SPECIALIST from USERS INNER JOIN DOCTORS on USERS.USERID=DOCTORS.USERID INNER JOIN EMPLOYEES on USERS.EMPID=EMPLOYEES.EMPID WHERE USERS.USERTYPE='Doctor' and DOCTORS.Active='Y'");
        HashMap<String, DoctorListPojo> doctList = new HashMap<>();
        while(rs.next()) {
            DoctorListPojo doct = new DoctorListPojo();
            
            doct.setEmpName(rs.getString(2));
            doct.setSal(rs.getDouble(3));
            doct.setDoctorid(rs.getString(4));
            doct.setQualification(rs.getString(5));
            doct.setSpecialist(rs.getString(6));

            doctList.put(rs.getString(1), doct);
        }
        return doctList;
    }
    
    public static boolean updateData(DoctorListPojo doct) throws SQLException {
        
        ps4.setString(1, doct.getEmpName());
        ps4.setDouble(2, doct.getSal());
        ps4.setString(3, doct.getEmpName());
        ps4.setString(4, doct.getQualification());
        ps4.setString(5, doct.getSpecialist());
        ps4.setString(6, doct.getEmpId());
        
        return(ps4.executeUpdate() != 0);
    }
    
}
