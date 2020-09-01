public class Account {
    private int _id;
    private String userName;
    private String userPositon;
    private String login;
    private String password;

    Account (int id,  String userName, String userPositon, String login, String password){
        this._id = id;
        this.userName = userName;
        this.userPositon = userPositon;
        this.login = login;
        this.password = password;
    }

    Account (String userName, String userPositon, String login, String password){
        this.userName = userName;
        this.userPositon = userPositon;
        this.login = login;
        this.password = password;
    }

    Account (){}

    public void setId(int id){this._id = id;}
    public void setLogin(String login) {
        this.login = login;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUserPositon(String userPositon) {
        this.userPositon = userPositon;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getId() { return _id; }
    public String getLogin() {
        return login;
    }
    public String getUserName() {
        return userName;
    }
    public String getUserPositon() {
        return userPositon;
    }
    public String getPassword() {
        return password;
    }

}
