package mailService;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Mailbox {
//variables=====================================================================
    public static List<User>list1=new ArrayList<>();
    public static Database db=new Database();
    public static User u;
    public static Scanner sc=new Scanner(System.in);
    public static final String URL= "jdbc:mysql://127.0.0.1:3306/mailservice";
    public static final String USR="admin";
    public static final String PSD="admin";

//methods=======================================================================
    public static List<User> createUserList() throws SQLException{
        List<User> l1=new ArrayList<>();
        String q="select u.id,u.user_name,u.role,p.user_password from users as u, passwords as p where u.id=p.id;";
        try{
        db.stm=db.connection.createStatement();
        ResultSet rs=db.stm.executeQuery(q);
        while(rs.next()){
            User b= new User(rs.getInt("id"),rs.getString("user_name"),rs.getString("role"),rs.getString("user_password"));
            l1.add(b);
            }
        } catch (SQLException e){
            System.out.println("Problem with your query. ");
        }
     return l1;
    }
    
    public static void logInScreen(){
        String u,p;
        System.out.println("Enter your username: ");
        u=sc.nextLine();
        System.out.println("Enter your password: ");
        p=PasswordField.readPassword("Enter your password: ");
        logIn(u,p);
    }
    
    public static void logIn(String user_name, String password){
        boolean bool=true;
        for (User us: list1){
            if(user_name.equals(us.getUser_name())){
                bool=false;
                u= new User(us.getId(),us.getUser_name(),us.getPassword(),us.getRole());
                if (u.getUser_name().equals("admin")) {System.out.println ("Well well.. the admin is here.. \nWelcome!");}
                else {System.out.println("Welcome to your mailbox mr."+u.getUser_name()+" !");}
            }
        }
        if (bool) System.out.println("There's no user with the credentials you provided. ");
    }
    
    private static void pressAnyKeyToContinue(){ 
        System.out.println("\nPress Enter key to continue...\n");
        try{
            System.in.read();
        }  
        catch(IOException e) {}  
    }
//main==========================================================================
    public static void main (String args[]) throws SQLException, IOException, InterruptedException{
        ProcessBuilder b=new ProcessBuilder("cmd", "/c", "cls");
        db.connect(URL,USR,PSD);
        list1=createUserList();        
        b.inheritIO().start().waitFor();
        logInScreen();
        db.connection.close();
        sc.close();
    }
}//~class
