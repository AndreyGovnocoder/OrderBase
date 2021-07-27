public class PowerModule
{
    private int _id;
    private String _name;
    private int _body;
    private int _power;
    private int _quantity;
    private boolean _active;
    private double _price;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_name(String _name)
    {
        this._name = _name;
    }
    public void set_body(int _body)
    {
        this._body = _body;
    }
    public void set_power(int _power)
    {
        this._power = _power;
    }
    public void set_quantity(int _quantity)
    {
        this._quantity = _quantity;
    }
    public void set_active(boolean active)
    {
        this._active = active;
    }
    public void set_price(double price)
    {
        this._price = price;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_name()
    {
        return _name;
    }
    public int get_body()
    {
        return _body;
    }
    public int get_power()
    {
        return _power;
    }
    public int get_quantity()
    {
        return _quantity;
    }
    public boolean is_active()
    {
        return _active;
    }
    public double get_price()
    {
        return _price;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append(_name).append(";\n");
        sb.append(Finder.getBody(_body)).append("; ");
        sb.append(Finder.getPower(_power)).append("; ");
        /*
        sb.append("Название: ").append(_name).append(";\n");
        sb.append("Корпус: ").append(Finder.getBody(_body)).append("; ");
        sb.append("Мощьность: ").append(Finder.getPower(_power)).append("; ");
         */
        return sb.toString();
    }
}
