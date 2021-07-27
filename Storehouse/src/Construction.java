public class Construction
{
    private int _id;
    private String _name;
    private String _manufacturer;
    private String _description;
    private int _price;
    private int _quantity;
    private boolean _active;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_name(String _name)
    {
        this._name = _name;
    }
    public void set_manufacturer(String _manufacturer)
    {
        this._manufacturer = _manufacturer;
    }
    public void set_description(String _description)
    {
        this._description = _description;
    }
    public void set_price(int _price)
    {
        this._price = _price;
    }
    public void set_quantity(int _quantity)
    {
        this._quantity = _quantity;
    }
    public void set_active(boolean _active)
    {
        this._active = _active;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_name()
    {
        return _name;
    }
    public String get_manufacturer()
    {
        return _manufacturer;
    }
    public String get_description()
    {
        return _description;
    }
    public int get_price()
    {
        return _price;
    }
    public int get_quantity()
    {
        return _quantity;
    }
    public boolean isActive()
    {
        return _active;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append(_name).append(";");
        if (_description != null)
            sb.append("\n" + _description).append("; ");
        return sb.toString();
    }
}
