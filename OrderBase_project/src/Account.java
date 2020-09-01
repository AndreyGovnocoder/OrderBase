public class Account {
    private long id;
    private String login;
    private String userName;
    private String password;

    Account (long id, String login, String userName, String password){
        this.id = id;
        this.login = login;
        this.userName = userName;
        this.password = password;
    }

    Account (String login, String userName, String password){
        this.login = login;
        this.userName = userName;
        this.password = password;
    }

    Account (){}

    public void setId(long id) {
        this.id = id;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public long getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }

}
