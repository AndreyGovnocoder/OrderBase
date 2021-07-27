public class Ink
{
    private int _id;
    private String _color;
    private int _machine;
    private String _name;
    private float _volume;
    private float _consumption;
    private int _quantity;
    private boolean _active;

    Ink(){}

    Ink(int id, String name, String color, int machine,  float volume, float consumption, int quantity, boolean active)
    {
        this._id = id;
        this._color = color;
        this._machine = machine;
        this._name = name;
        this._volume = volume;
        this._consumption = consumption;
        this._quantity = quantity;
        this._active = active;
    }

    public void set_id(int id)
    {
        this._id = id;
    }
    public void set_color(String color)
    {
        this._color = color;
    }
    public void set_machine(int machine)
    {
        this._machine = machine;
    }
    public void set_name(String name)
    {
        this._name = name;
    }
    public void set_volume(float volume)
    {
        this._volume = volume;
    }
    public void set_consumption(float consumption)
    {
        this._consumption = consumption;
    }
    public void set_quantity(int quantity)
    {
        this._quantity = quantity;
    }
    public void set_active(boolean active)
    {
        this._active = active;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_color()
    {
        return _color;
    }
    public int get_machine()
    {
        return _machine;
    }
    public String get_name()
    {
        return _name;
    }
    public float get_volume()
    {
        return _volume;
    }
    public float get_consumption()
    {
        return _consumption;
    }
    public int get_quantity()
    {
        return _quantity;
    }
    public boolean is_active()
    {
        return _active;
    }

    /*

    @Override
    public String toString()
    {
        return _name + " (" + _color + "), " + _volume + " л" + " - " + _quantity + " шт." ;
    }
     */

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append(_name).append("\n");
        sb.append(_color).append("; ");
        sb.append(Finder.getMachine(_machine)).append("; ");
        sb.append(_volume).append(" л; ");
        sb.append(_quantity).append(" шт; ");
        /*
        sb.append("Название: ").append(_name).append("\n");
        sb.append("Цвет: ").append(_color).append("; ");
        sb.append("Станок: ").append(InksForm.getMachine(_machine)).append("; ");
        sb.append("Объем: ").append(_volume).append(" л; ");
         */
        return sb.toString();
    }
}
