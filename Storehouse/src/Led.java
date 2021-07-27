public class Led
{
    private int _id;
    private String _name;
    private int _kind;
    private int _luminousFlux;
    private float _power;
    private String _color;
    private int _quantity;
    private boolean _active;
    private double _price;

    public void set_id(int id)
    {
        this._id = id;
    }
    public void set_name(String name)
    {
        this._name = name;
    }
    public void set_kind(int kind)
    {
        this._kind = kind;
    }
    public void set_luminousFlux(int luminousFlux)
    {
        this._luminousFlux = luminousFlux;
    }
    public void set_power(float power)
    {
        this._power = power;
    }
    public void set_color(String color)
    {
        this._color = color;
    }
    public void set_quantity(int quantity)
    {
        this._quantity = quantity;
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
    public int get_kind()
    {
        return _kind;
    }
    public int get_luminousFlux()
    {
        return _luminousFlux;
    }
    public float get_power()
    {
        return _power;
    }
    public String get_color()
    {
        return _color;
    }
    public int get_quantity()
    {
        return _quantity;
    }
    public boolean isActive()
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
        if (Finder.getLedKind(_kind) != null)
            sb.append(Finder.getLedKind(_kind)).append("; ");
        sb.append(_luminousFlux).append("; ");
        sb.append(_power).append("; ");
        sb.append(_color).append("; ");
        /*
        sb.append("Название: ").append(_name).append(";\n");
        if (Finder.getLedKind(_kind) != null)
            sb.append("Вид: ").append(Finder.getLedKind(_kind)).append("; ");
        sb.append("Сила светового потока (лм): ").append(_luminousFlux).append("; ");
        sb.append("Мощность (Вт): ").append(_power).append("; ");
        sb.append("Цвет: ").append(_color).append("; ");
         */
        return sb.toString();
    }
}
