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
            }
        } catch (SQLException e){
            System.out.println("Problem with your query. ");
        }
     return l1;
    }
    
    public static List<String> createMessageList(String username) throws SQLException{
        List<String> l2=new ArrayList<>();
        String q="select inbox_messages.id,date,sender,message from inbox_messages inner join users on inbox_messages.user_id=users.id and users.user_name='"+username+"';";
        try{
            db.stm=db.connection.createStatement();
            ResultSet rs=db.stm.executeQuery(q);
            while(rs.next()){
                l2.add("\nMessage id:"+rs.getInt("id")+"\nDate:"+rs.getString("Date")+", Sender: "+rs.getString("sender")+", Receiver: "+username+", \nMessage: "+rs.getString("message"));
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
            System.out.println(s);
        }
        System.out.println("\n");
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
    
    public static void sendMessage(String username) throws SQLException{
        String date=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        topAnnouncement(date,username);
        boolean is=false;
        int uid=-1;
        System.out.println("\nType your message (max 250 characters): ");
        String mes=sc.nextLine();
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
    
    public static void topAnnouncement(String date,String username) throws SQLException{        
        String query1="select id from users where user_name='"+username+"';";
        db.stm=db.connection.createStatement();
        ResultSet rs=db.stm.executeQuery(query1);
        int nid=-1;
        while (rs.next()){
            nid=rs.getInt("id");
        }
        String query2="select timeout from last_online where id="+nid+";";
        db.stm=db.connection.createStatement();
        ResultSet rs2=db.stm.executeQuery(query2);
        String timeOut="";
        while (rs2.next()){
            timeOut=rs2.getString("timeout");
        }
        System.out.println("User: \""+username+"\" is offline for: "+User.diffTime(date, timeOut)+" minutes.\n");
    }
    
    public static void editMessages(String username) throws SQLException{
        String date=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        topAnnouncement(date,username);
        System.out.println("Choose the message you would like to edit by choosing the message id. \n");
        viewMessages(username);
        String ed=sc.nextLine();
        System.out.println("Write a new message to replace this one. ");
        String nm=sc.nextLine();
        String query3="update inbox_messages set message='"+nm+"' where id="+ed+";";
        db.executeStatement(query3);        
        try{
            PrintWriter w;
                w=new PrintWriter(new FileOutputStream(new File(username+"'s inbox.txt"),true));
                w.write(date+" \""+u.getUser_name()+"\" edited message with id:"+ed+" to\nmessage: "+nm+"\n\n\n");
        }catch(FileNotFoundException e){System.out.println("Error exporting message log file.");}
    }
    
    public static void deleteMessages (String username) throws SQLException{
        List<String> l=new ArrayList<>();
        l=createMessageList(username);
        String date=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        topAnnouncement(date,username);
        System.out.println("Choose the message you would like to delete by choosing the message id. \n");
        for(String s:l){
            System.out.println(s);
        }
        String ed=sc.nextLine();
        String query3="delete from inbox_messages where id='"+ed+"';";
        db.executeStatement(query3);        
        try{
            PrintWriter w;
                w=new PrintWriter(new FileOutputStream(new File(username+"'s inbox.txt"),true));
                w.write(date+" \""+u.getUser_name()+"\" deleted message with id:"+ed+".\n\n\n");
        }catch(FileNotFoundException e){System.out.println("Error exporting message log file.");}
    }
    
    public static void createNew() throws SQLException{
        String date=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
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
            String query4="insert into last_online (id, timeout) values ("+nid+",'"+date+"');";
            db.executeStatement(query4);
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
    
    public static void clearConsole() throws IOException, InterruptedException{
        b.inheritIO().start().waitFor();
        String timeNow=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        System.out.println("User: \""+u.getUser_name()+"\", online for: "+User.diffTime(timeNow, u.getTimeIn())+" minutes.");
    }
    
    public static boolean systemLogIn(){
        boolean bool1=true;
        String c;
        while(bool1){
//        b.inheritIO().start().waitFor(); //cls
            System.out.println("1. Log in to mailbox. \n2. Exit mailbox.");
            c=sc.nextLine();
            if (c.equals("1")){
                u=logInScreen(u,list1);
                if(u.getUser_name()!=null){bool1=false; break;}
            }else if (c.equals("2"))break;
            else System.out.println("Not a valid input. Please select 1 or 2. ");
            
        }
        return bool1;
    }
    
    public static void logOut(){
        String timeNow=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        String query="update last_online set timeout='"+timeNow+"' where id="+u.getId()+";";
        db.executeStatement(query);
    }
    
    public static boolean showMenu() throws SQLException, IOException, InterruptedException{
//        b.inheritIO().start().waitFor(); //cls
        String ch;
        boolean bool2=true;
        while (bool2){
            System.out.println("\n\n\nWhat action do you want to do? \n"); 
            System.out.println("1. Log out. \n2. View your messages. \n3. Send a message. \n4. View messages of another user. ");
            if (u.getRole().equals("medium") || u.getRole().equals("super")) {System.out.println("5. Edit someone's message(s). ");}
            if (u.getRole().equals("super")){System.out.println("6. Delete someone's message(s). ");}
            if (u.getUser_name().equals("admin"))System.out.println("7. Create new user. \n8. Change a user's type. (normal, medium, super)");
            ch=sc.nextLine();
            bool2=chooseCh(ch);
        }
        return bool2;       
    }
    
    public static boolean chooseCh(String ch) throws SQLException, IOException, InterruptedException{
        boolean bool3=true;
        switch (ch){
            case "1" :  bool3=false;
                        logOut();
                        break;
            case "2" :  viewMyMessages();
                        pressAnyKeyToContinue();
                        break;
            case "3" :  clearConsole();
                        System.out.println("Send message to? ");
                        String k=sc.nextLine();
                        sendMessage(k);
                        break;
                        
            case "4" :  clearConsole();
                        System.out.println("Enter the username of the user you want to view the inbox.. ");
                        String l=sc.nextLine();
                        viewMessages(l);
                        pressAnyKeyToContinue();
                        break;
                        
            case "5" :  if (u.getRole().equals("super") || u.getRole().equals("medium")){
                        clearConsole();
                        System.out.println("Enter the username of the user you want to edit messages from his/her inbox.. ");
                        String m=sc.nextLine();
                        editMessages(m);
                        pressAnyKeyToContinue();
                        break;} 
                        else {System.out.println("Invalid input. Please select 1-4.. \n"); break;}
                        
            case "6" :  if (u.getRole().equals("super")){
                        clearConsole();
                        System.out.println("Enter the username of the user you want to delete messages from his/her inbox.. ");
                        String n=sc.nextLine();
                        deleteMessages(n);
                        pressAnyKeyToContinue();
                        break;} 
                        else if (u.getRole().equals("medium")){System.out.println("Invalid input. Please select 1-5.. \n"); break;}
                        else {System.out.println("Invalid input. Please select 1-4.. \n"); break;}
            
            case "7" :  if (u.getUser_name().equals("admin")) {clearConsole(); createNew(); pressAnyKeyToContinue(); break;}
                        else {
                            if (u.getRole().equals("normal"))System.out.println("Invalid input. Please select 1-4.. \n");
                            if (u.getRole().equals("medium"))System.out.println("Invalid input. Please select 1-5.. \n");
                            if (u.getRole().equals("super"))System.out.println("Invalid input. Please select 1-6.. \n");
                        break;}
            
            case "8" :  if (u.getUser_name().equals("admin")) {
                        clearConsole();
                        System.out.println("Please enter the username you want to change the type.. ");
                        String o=sc.nextLine();
                        changeUserType(o); 
                        pressAnyKeyToContinue();
                        break;
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
        b.inheritIO().start().waitFor(); //cls
        boolean bool=systemLogIn();
        if (bool) return;
        clearConsole();
        boolean bool1=true;
        while (bool1){
        bool1=showMenu();
        clearConsole();
        }
        db.connection.close();
        sc.close();
    }
}//~class
