import java.util.List;

public class Account extends Staff
{
    private String _login;
    private String _password;
    private int _staffId;
    private List<Integer> _informedList;

    Account(int id, String name, String position, String login, String password, List<Integer> informedList, int staffId)
    {
        super.set_id(id);
        super.set_name(name);
        super.set_position(position);
        this._login = login;
        this._password = password;
        this._informedList = informedList;
        this._staffId = staffId;
    }

    Account(String name, String position, String login, String password, List<Integer> informedList, int staffId)
    {
        super.set_name(name);
        super.set_position(position);
        this._login = login;
        this._password = password;
        this._informedList = informedList;
        this._staffId = staffId;
    }

    Account(){}

    public void set_login(String _login){this._login = _login;}
    public void set_password(String _password){this._password = _password;}
    public void set_informedList(List<Integer> informedList)
    {
        this._informedList = informedList;
    }
    public void setInformedVers(int vers)
    {
        _informedList.add(vers);
    }
    public void set_staffId(int staffId)
    {
        this._staffId = staffId;
    }

    public String get_login() {return _login;}
    public String get_password() {return _password;}
    public List<Integer> get_informedList()
    {
        return _informedList;
    }
    public int get_staffId()
    {
        return _staffId;
    }
}
