package mailService;

import java.security.Key;


public class User {

//variables=====================================================================
    private int id;
    private String user_name;
    private String password;
    private String role;

//constructors==================================================================
    public User(){
        this.role="simple";
    }
    
    public User(String user_name, String password){
        this.user_name=user_name;
        this.password=password;
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

//methods=======================================================================


//main==========================================================================

}//~class
