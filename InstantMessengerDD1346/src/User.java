import java.util.ArrayList;

public class User {

    private String name;
    private boolean isSelf;
    private static ArrayList<User> users = new ArrayList<>();

    public User (String s){
        this.name = s;
        users.add(this);
    }

    public String returnName() {
	return name;
    }

    public boolean returnIsSelf() {
	return isSelf;
    }

    public void setName(String str) {
        name = str;
    }

    public static User stringToUser(String user) throws NullPointerException{
        for (User u: users){
            if (u.toString().equals(user))
            {
                return u;
            }
        }
        throw new NullPointerException();
    }


}
