package mailService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import static mailService.LogInScreen.logInScreen;


public class Mailbox {
//variables=====================================================================
    public static List<User>list1=new ArrayList<>();
    public static List<String>list2=new ArrayList<>();
    public static Database db=new Database();
    public static User u;
    public static User u2=new User();
    public static Scanner sc=new Scanner(System.in);
    
    public static final String URL= "jdbc:mysql://127.0.0.1:3306/mailservice";
    public static final String USR="admin";
    public static final String PSD="admin";
    public static final ProcessBuilder b=new ProcessBuilder("cmd", "/c", "cls");

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
    
    public static List<String> createMessageList(String username) throws SQLException{
        List<String> l2=new ArrayList<>();
        String q="select date,sender,message from inbox_messages as m left join users on m.user_id=users.id and users.user_name='"+username+"';";
        try{
            db.stm=db.connection.createStatement();
            ResultSet rs=db.stm.executeQuery(q);
            while(rs.next()){
                l2.add("Date:"+rs.getString("date")+", Sender: "+rs.getString("sender")+", Receiver: "+username+", \nMessage: "+rs.getString("message"));
            }
        }catch (SQLException e){
            System.out.println("Problem with your query. ");
        }
        return l2;        
    }
    
    public static void viewMessages() throws SQLException{
        List<String>list=new ArrayList<>();
        list=createMessageList(u.getUser_name());
        for (String s: list){
            System.out.println("\n\n"+s);
        }
    }
    
    public static void viewMessages(String us) throws SQLException{
        List<String>list=new ArrayList<>();
        list=createMessageList(us);
        for (String s: list){
            System.out.println("\n\n"+s);
        }
    }
    
    private static void pressAnyKeyToContinue(){ 
        System.out.println("\nPress Enter key to continue...\n");
        try{
            System.in.read();
        }  
        catch(IOException e) {}  
    }
    
    public static void sendMessage(String username){
        boolean is=false;
        int uid=-1;
        System.out.println("\nType your message (max 250 characters): ");
        String mes=sc.nextLine();
        String date=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        for (User us: list1){
            if (us.getUser_name().equals(username)){
                is=true; 
                uid=us.getId(); 
                break;}
        }
        if (is){
        String q="insert into inbox_messages (id,user_id,sender,date,message) values (null,"+uid+",'"+u.getUser_name()+"','"+date+"','"+mes+"');";
        db.executeStatement(q);
        try{ //write to receiver's file  
            PrintWriter w;
            w=new PrintWriter(new FileOutputStream(new File(username+"'s inbox.txt"),true));
            w.write(date+" "+u.getUser_name()+" to "+username+" \nmessage: "+mes+"\n");
            w.close();
        }catch(FileNotFoundException e){System.out.println("Error exporting message log file.");}
        }else System.out.println("Can't find the user "+username);
    }
    
    
//main==========================================================================
    public static void main (String args[]) throws SQLException, IOException, InterruptedException{        
        db.connect(URL,USR,PSD);
        list1=createUserList();        
//        b.inheritIO().start().waitFor(); //cls
        u=logInScreen(u,list1);
        sendMessage("at4k");
        viewMessages("at4k");
        db.connection.close();
        sc.close();
    }
}//~class
