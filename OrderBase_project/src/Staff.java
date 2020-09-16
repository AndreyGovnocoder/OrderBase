public class Staff
{
    private int _id;
    private String _name;
    private String _position;

    Staff(int id, String name, String position)
    {
        this._id = id;
        this._name = name;
        this._position = position;
    }

    Staff(String name, String position)
    {
        this._name = name;
        this._position = position;
    }

    Staff (){}

    public void set_id(int _id){this._id = _id;}
    public void set_name(String _name){this._name = _name;}
    public void set_position(String _position){this._position = _position;}
    public int get_id() {return _id;}
    public String get_name() {return _name;}
    public String get_position() {return _position;}

    @Override
    public String toString(){return this._name;}
}
