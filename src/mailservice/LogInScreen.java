package mailservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class LogInScreen {
//variables=====================================================================
    public static Scanner sc=new Scanner(System.in);

//methods=======================================================================
    public static User logInScreen(User us, List<User> list){
        String u,p;
        System.out.println("\nEnter your username: ");
        u=sc.nextLine();
        p=PasswordField.readPassword("Enter your password: ");
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
                }
        }
        if (bool) System.out.println("\nThere's no user with the credentials you provided. \n\n");
        String timeNow=new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
        u.setTimeIn(timeNow);
        return u;
    }


}//~class
