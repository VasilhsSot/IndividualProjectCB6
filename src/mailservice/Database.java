package mailservice;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
//variables=====================================================================
    public Connection connection;
    public DriverManager dm;
    public Statement stm;

//constructors==================================================================
    public Database(){}   

//methods=======================================================================
    public Connection connect(String _DB_URL, String _username, String _password) {
        try{
            connection= DriverManager.getConnection(_DB_URL, _username,_password);
            return connection;
        } catch (SQLException e){
            System.out.println("Problem connecting to the database. ");
            return null;
        }
    }
    
    public void executeStatement (String sql) {
        try {
            stm=connection.createStatement();
            stm.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("Could not execute the statement. ");
        }
    }
    
}//~class