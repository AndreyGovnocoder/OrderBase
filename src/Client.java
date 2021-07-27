public class Client
{
    private int _id;
    private String _name;
    private String _mail;
    private String _phone;
    private String _contactPerson;
    private boolean _active;

    Client(int id, String name, String phone, String mail, String contactPerson, boolean active)
    {
        this._id = id;
        this._name = name;
        this._mail = mail;
        this._phone = phone;
        this._contactPerson = contactPerson;
        this._active = active;
    }

    Client(String name,  String phone, String mail, String contactPerson)
    {
        this._name = name;
        this._mail = mail;
        this._phone = phone;
        this._contactPerson = contactPerson;
    }

    Client(int id, String name, String phone, String mail)
    {
        this._id = id;
        this._name = name;
        this._mail = mail;
        this._phone = phone;
    }

    Client(String name,  String phone, String mail)
    {
        this._name = name;
        this._mail = mail;
        this._phone = phone;
    }

    Client(int id, String name )
    {
        this._name = name;
        this._id = id;
    }

    Client(String name ){
        this._name = name;
    }

    Client(){}

    public void set_id(int _id) {this._id = _id;}
    public void set_name(String _name) {this._name = _name;}
    public void set_mail(String _mail) {this._mail = _mail;}
    public void set_phone(String _phone){this._phone = _phone;}
    public void set_contactPerson(String _contactPerson) {this._contactPerson = _contactPerson;}
    public void set_active(boolean _active)
    {
        this._active = _active;
    }

    public int get_id() {return _id;}
    public String get_name() {return _name;}
    public String get_mail() {return _mail;}
    public String get_phone(){return _phone;}
    public String get_contactPerson() {return _contactPerson;}
    public boolean is_active()
    {
        return _active;
    }

    @Override
    public String toString() {
        return this._name;
    }
}
