public class Machine
{
    private int _id;
    private String _name;
    private boolean _active;

    Machine(){}

    Machine(int id, String name, boolean active)
    {
        this._id = id;
        this._name = name;
        this._active = active;
    }

    public void set_id(int id)
    {
        this._id = id;
    }
    public void set_name(String name)
    {
        this._name = name;
    }
    public void set_active(boolean active)
    {
        this._active = active;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_name()
    {
        return _name;
    }
    public boolean is_active()
    {
        return _active;
    }

    @Override
    public String toString()
    {
        return _name;
    }
}
