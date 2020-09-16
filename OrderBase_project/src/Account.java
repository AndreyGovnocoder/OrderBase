public class Account extends Staff
{
    private String _login;
    private String _password;

    Account(int id, String name, String position, String login, String password)
    {
        super.set_id(id);
        super.set_name(name);
        super.set_position(position);
        this._login = login;
        this._password = password;
    }

    Account(String name, String position, String login, String password)
    {
        super.set_name(name);
        super.set_position(position);
        this._login = login;
        this._password = password;
    }

    Account(){}

    public void set_login(String _login){this._login = _login;}
    public void set_password(String _password){this._password = _password;}

    public String get_login() {return _login;}
    public String get_password() {return _password;}
}
