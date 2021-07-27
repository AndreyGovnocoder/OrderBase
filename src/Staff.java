public class Staff
{
    private int _id;
    private String _name;
    private String _position;
    private boolean _active;

    Staff(int id, String name, String position, boolean active)
    {
        this._id = id;
        this._name = name;
        this._position = position;
        this._active = active;
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
    public void set_active(boolean _active)
    {
        this._active = _active;
    }

    public int get_id() {return _id;}
    public String get_name() {return _name;}
    public String get_position() {return _position;}
    public boolean is_active()
    {
        return _active;
    }

    @Override
    public String toString(){return this._name;}
}
