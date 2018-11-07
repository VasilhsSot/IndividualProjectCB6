package mailservice;

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
import static mailservice.LogInScreen.logInScreen;


public class Mailbox {
//variables=====================================================================
    public static List<User>list1=new ArrayList<>();
    public static Database db=new Database();
    public static User u;
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
            User b= new User(rs.getInt("id"),rs.getString("user_name"),rs.getString("user_password"),rs.getString("role"));
            l1.add(b);
            System.out.println(b);
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
                l2.add("Date:"+rs.getString("Date")+", Sender: "+rs.getString("sender")+", Receiver: "+username+", \nMessage: "+rs.getString("message"));
            }
        }catch (SQLException e){
            System.out.println("Problem with your query. ");
        }
        return l2;        
    }
    
    public static void viewMyMessages() throws SQLException{
        List<String>list=new ArrayList<>();
        list=createMessageList(u.getUser_name());
        for (String s: list){
            System.out.println("\n\n"+s);
        }
        System.out.println("\n\n");
    }
    
    public static void viewMessages(String us) throws SQLException{
        List<String>list=new ArrayList<>();
        boolean check=true;
        for (User u:list1){
            if (u.getUser_name().equals(us)){check=false; break;}
        }
        if (check){System.out.println("The username you provided doesn't exist in the mailbox. ");}
        else {list=createMessageList(us);
               for (String s: list){
               System.out.println("\n\n"+s);
               }
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
                w.write(date+" "+u.getUser_name()+" to "+username+" \nmessage: "+mes+"\n\n\n");
                w.close();
            }catch(FileNotFoundException e){System.out.println("Error exporting message log file.");}
        }else System.out.println("Can't find the user "+username+".\n\n");
    }    
    
    public static void editMessages(String username){
        
    }
    
    public static void deleteMessages (String u){
    
    }
    
    public static void createNew() throws SQLException{
        boolean tycheck=true, user_ex=false, bool=true;
        String ty="normal",username="";
        while(bool){
            user_ex=false;
            System.out.println("Type the username of the new user. ");
            username=sc.nextLine();
            for (User user: list1){
                if (user.getUser_name().equals(username)) {
                    user_ex=true;
                    System.out.println("This username already exists. Please choose a different one. ");                    
                }
            }
            if (!user_ex) bool=false;
        }
        if (!user_ex){
            System.out.println("Type the password of user \""+username+"\".. ");
            String pas=sc.nextLine();
            while(tycheck){
                System.out.println("What user type is "+username+" gonna be? (normal, medium, super)");
                ty=sc.nextLine();
                if (ty.equals("normal") || ty.equals("medium") ||ty.equals("super")) {
                    tycheck=false;
                }else System.out.println("Wrong input. User's role can only be \"normal\", \"medium\" or \"super\".. ");
            }
            String query1="insert into users (id,user_name,role) values (null,'"+username+"','"+ty+"');";
            db.executeStatement(query1);
            String query2="select id from users where user_name='"+username+"';";
            db.stm=db.connection.createStatement();
            ResultSet rs=db.stm.executeQuery(query2);
            int nid=-1;
            while (rs.next()){
                nid=rs.getInt("id");
            }
            String query3="insert into passwords (id, user_password) values ("+nid+",'"+pas+"');";
            db.executeStatement(query3);
            System.out.println("User "+username+" created successfully!! ");
            list1=createUserList();
        }
    }
    
    public static void changeUserType(String username) throws SQLException{
        boolean is=false, indeed=true;
        int uid=-1;
        for (User us: list1){
            if (us.getUser_name().equals(username)){
                is=true; 
                uid=us.getId(); 
                break;}
        }
        if (is){
            while(indeed){
                System.out.println("What type of user you want "+username+" to be? (normal, medium, super)");
                String ntype=sc.nextLine();
                if (ntype.equals("normal") || ntype.equals("medium") || ntype.equals("super")){
                    indeed=false;
                    String query="update users set role='"+ntype+"' where id="+uid+";";
                    db.executeStatement(query);
                    list1=createUserList();
                }else System.out.println("Wrong input. User's role can only be normal, medium or super.. ");
                    }
        }else System.out.println("Can't find the user "+username+".\n\n");
    }
    
    public static boolean systemLogIn(){
        boolean bool1=true;
        String c;
        while(bool1){
//        b.inheritIO().start().waitFor(); //cls
            System.out.println("1. Log in to mailbox. \n2. Exit");
            c=sc.nextLine();
            if (c.equals("1")){
                u=logInScreen(u,list1);
                if(u.getUser_name()!=null){bool1=false; break;}
            }else if (c.equals("2"))break;
            else System.out.println("Not a valid input. Please select 1 or 2. ");
            
        }
        return bool1;
    }
    
    public static boolean showMenu() throws SQLException{
        String ch;
        boolean bool2=true;
        if (u.getUser_name().equals("admin")) {
            System.out.println ("\n\nWell well well.. the admin is here.. Welcome!");
        }
        else {
            System.out.println("\n\nWelcome to your mailbox mr."+u.getUser_name()+" !");
        }
        while (bool2){
            System.out.println("\n\n\nWhat action do you want to do? \n"); 
            System.out.println("1. Exit. \n2. View your messages. \n3. Send a message. \n4. View messages of another user. ");
            if (u.getRole().equals("medium") || u.getRole().equals("super")) {System.out.println("5. Edit someone's message(s). ");}
            if (u.getRole().equals("super")){System.out.println("6. Delete someone's message(s). ");}
            if (u.getUser_name().equals("admin"))System.out.println("7. Create new user. \n8. Change a user's type. (normal, medium, super)");
            ch=sc.nextLine();
            bool2=chooseCh(ch);
        }
        return bool2;       
    }
    
    public static boolean chooseCh(String ch) throws SQLException{
        boolean bool3=true;
        switch (ch){
            case "1" :  bool3=false;
                        break;
            case "2" :  viewMyMessages();
                        break;
            case "3" :  System.out.println("Send message to? ");
                        String k=sc.nextLine();
                        sendMessage(k);
                        break;
                        
            case "4" :  System.out.println("Enter the username of the user you want to view the inbox.. ");
                        String l=sc.nextLine();
                        viewMessages(l);                        
                        break;
                        
            case "5" :  if (u.getRole().equals("super") || u.getRole().equals("medium")){
                        System.out.println("Enter the username of the user you want to edit messages from his/her inbox.. ");
                        String m=sc.nextLine();
                        editMessages(m);
                        break;} 
                        else {System.out.println("Invalid input. Please select 1-4.. \n"); break;}
                        
            case "6" :  if (u.getRole().equals("super")){
                        System.out.println("Enter the username of the user you want to delete messages from his/her inbox.. ");
                        String n=sc.nextLine();
                        deleteMessages(n);
                        break;} 
                        else if (u.getRole().equals("medium")){System.out.println("Invalid input. Please select 1-5.. \n"); break;}
                        else {System.out.println("Invalid input. Please select 1-4.. \n"); break;}
            
            case "7" :  if (u.getUser_name().equals("admin")) {createNew(); break;}
                        else {
                            if (u.getRole().equals("normal"))System.out.println("Invalid input. Please select 1-4.. \n");
                            if (u.getRole().equals("medium"))System.out.println("Invalid input. Please select 1-5.. \n");
                            if (u.getRole().equals("super"))System.out.println("Invalid input. Please select 1-6.. \n");
                        break;}
            
            case "8" :  if (u.getUser_name().equals("admin")) {
                        System.out.println("Please enter the username you want to change the type.. ");
                        String o=sc.nextLine();
                        changeUserType(o); break;
                        }
                        else {
                            if (u.getRole().equals("normal"))System.out.println("Invalid input. Please select 1-4.. \n");
                            if (u.getRole().equals("medium"))System.out.println("Invalid input. Please select 1-5.. \n");
                            if (u.getRole().equals("super"))System.out.println("Invalid input. Please select 1-6.. \n");
                        break;}
                        
            default :   if (u.getRole().equals("normal"))System.out.println("Invalid input. Please select 1-4.. \n");
                        if (u.getRole().equals("medium"))System.out.println("Invalid input. Please select 1-5.. \n");
                        if (u.getRole().equals("super") && !u.getUser_name().equals("admin"))System.out.println("Invalid input. Please select 1-6.. \n");
                        if (u.getUser_name().equals("admin"))System.out.println("Invalid input. Please select 1-8.. \n");
                        break;
        }
        return bool3;
    }
    
//main==========================================================================
    public static void main (String args[]) throws SQLException, IOException, InterruptedException{        
        db.connect(URL,USR,PSD);
        list1=createUserList(); 
//        b.inheritIO().start().waitFor(); //cls
        boolean bool=systemLogIn();
        if (bool) return;
//        b.inheritIO().start().waitFor(); //cls
        boolean bool1=true;
        while (bool1){
        bool1=showMenu();
        }
        db.connection.close();
        sc.close();
    }
}//~class
