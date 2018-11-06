package mailService;

import java.util.List;
import java.util.Scanner;

public class LogInScreen {
//variables=====================================================================
    public static Scanner sc=new Scanner(System.in);

//methods=======================================================================
    public static User logInScreen(User us, List<User> list){
        String u,p;
        System.out.println("Enter your username: ");
        u=sc.nextLine();
        System.out.println("Enter your password: ");
        p=sc.nextLine();
//        p=PasswordField.readPassword("Enter your password: ");
        us=logIn(u,p,list);
        return us;
    }
    
    public static User logIn(String user_name, String password, List<User> list){
        boolean bool=true;
        User u=new User();
        for (User us: list){
            if(user_name.equals(us.getUser_name())){
                bool=false;
                u= new User(us.getId(),us.getUser_name(),us.getPassword(),us.getRole());
                if (u.getUser_name().equals("admin")) {System.out.println ("\n\nWell well well.. the admin is here.. Welcome!");}
                else {System.out.println("\n\nWelcome to your mailbox mr."+u.getUser_name()+" !\n\n");}
            }
        }
        if (bool) System.out.println("There's no user with the credentials you provided. ");
        return u;
    }


}//~class
