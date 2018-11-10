package mailservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {

//variables=====================================================================
    private int id;
    private String user_name;
    private String password;
    private String role;
    private String timeIn;
    private String timeOut;
//constructors==================================================================
    public User(){
        this.role="simple";
    }
    public User (int id){
        this.id=id;
        this.role="simple";
    }
    public User(int id, String user_name, String password){
        this.id=id;
        this.user_name=user_name;
        this.password=password;
        this.role="simple";
    }
    
    public User(int id, String user_name, String password, String role){
        this.id=id;
        this.user_name=user_name;
        this.password=password;
        this.role=role;
    }

//setters & getters=============================================================
    public void setId(int id) {
        this.id = id;
    }
    
    public void setTimeIn(String timeIn){
        this.timeIn=timeIn;
    }
    
    public String getTimeIn(){
        return timeIn;
    }
    
    public void setTimeOut(String timeOut){
        this.timeOut=timeOut;
    }
    
    public String getTimeOut(){
        return timeOut;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public int getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString(){
        return this.getUser_name()+this.getRole();
    }
    
//methods=======================================================================
        public static double diffTime(String timeNow, String ustime){
        Date d1=null;
        Date d2=null;
        double diff=0.0;
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try{
            d1=format.parse(ustime);
            d2=format.parse(timeNow);
        }catch (ParseException e){
            e.printStackTrace();
        }
        diff=d2.getTime()-d1.getTime();
        return diff/(60*1000);
    }
}//~class
