public class Material
{
    private int _id;
    private int _kind;
    private int _manufacturer;
    private int _width;
    private int _height;
    private int _color;
    private int _property;
    private float _thickness;
    private int _attribute;
    private int _quantity;
    private int _price;
    private int _sellPrice;
    private int _colorNumber;
    private boolean _active;
    private boolean _absence;

    public void set_id(int id) {this._id = id;}
    public void set_kind(int kind) {this._kind = kind;}
    public void set_manufacturer(int manufacturer) {this._manufacturer = manufacturer;}
    public void set_width(int width) {this._width = width;}
    public void set_height(int height) {this._height = height;}
    public void set_color(int color){this._color = color;}
    public void set_property(int property) {this._property = property;}
    public void set_thickness(float thickness) {this._thickness = thickness;}
    public void set_attribute(int attribute) {this._attribute = attribute;}
    public void set_quantity(int quantity) {this._quantity = quantity;}
    public void set_price(int price)
    {
        this._price = price;
    }
    public void set_sellPrice(int _sellPrice)
    {
        this._sellPrice = _sellPrice;
    }
    public void set_colorNumber(int _colorNumber)
    {
        this._colorNumber = _colorNumber;
    }
    public void set_active(boolean active)
    {
        this._active = active;
    }
    public void set_absence(boolean absence)
    {
        this._absence = absence;
    }

    public int get_id() {return _id;}
    public int get_kind() {return _kind;}
    public int get_manufacturer() {return _manufacturer;}
    public int get_width() {return _width;}
    public int get_height() {return _height;}
    public int get_color() {return _color;}
    public int get_property() {return _property;}
    public float get_thickness(){return _thickness;}
    public int get_attribute() {return _attribute;}
    public int get_quantity() {return _quantity;}
    public int get_price()
    {
        return _price;
    }
    public int get_sellPrice()
    {
        return _sellPrice;
    }
    public int get_colorNumber()
    {
        return _colorNumber;
    }
    public boolean is_active()
    {
        return _active;
    }
    public boolean is_absence()
    {
        return _absence;
    }

   /*
    @Override
    public String toString()
    {
        return getString();
    }
    */

    @Override
    public String toString()
    {
        MaterialsKind kind = Finder.getMaterialKind(get_kind());
        final StringBuffer sb = new StringBuffer();
        if (kind != null && kind.get_id() != 0)
        {
            sb.append(kind.get_name()).append("\n");

            if(kind.get_manufacturer() && Finder.getManufacturer(_manufacturer) != null)
                sb.append(Finder.getManufacturer(_manufacturer)).append("; ");
            if(kind.get_width())
                sb.append("Ширина: ").append(get_width()).append("; ");
            if(kind.get_height())
                sb.append("Высота/метраж: ").append(get_height()).append("; ");
            if(kind.get_thickness())
                sb.append("Толщина: ").append(get_thickness()).append("; ");
            if(kind.get_color() && Finder.getColor(get_color()) != null)
                sb.append(Finder.getColor(get_color())).append("; ");
            if(kind.get_attribute() && Finder.getAttribute(get_attribute()) != null)
                sb.append("Атрибут: ").append(Finder.getAttribute(get_attribute())).append("; ");
            if(kind.get_property() && Finder.getProperty(get_property()) != null)
                sb.append("Свойство: ").append(Finder.getProperty(get_property())).append("; ");
        }
        return sb.toString();
    }

    private String getString()
    {
        String string = "Материал. ";
        MaterialsKind kind = Finder.getMaterialKind(get_kind());

        if (kind != null && kind.get_id() != 0)
        {
            if(kind.get_manufacturer() && Finder.getManufacturer(_manufacturer) != null)
                string += " Производитель: " + Finder.getManufacturer(_manufacturer) + "\n";
            if(kind.get_width())
                string += " Ширина: " + get_width() + " -- ";
            if(kind.get_height())
                string += " Высота/метраж" + get_height() + " -- ";
            if(kind.get_thickness())
                string += " Толщина: " + get_thickness() + " -- ";
            if(kind.get_color() && Finder.getColor(get_color()) != null)
                string += " Цвет: " + Finder.getColor(get_color()) + " -- ";
            if(kind.get_attribute() && Finder.getAttribute(get_attribute()) != null)
                string += " Атрибут: " + Finder.getAttribute(get_attribute()) + " -- ";
            if(kind.get_property() && Finder.getProperty(get_property()) != null)
                string += " Свойство: " + Finder.getProperty(get_property()) + " -- ";
        }
        return string;
    }
}
