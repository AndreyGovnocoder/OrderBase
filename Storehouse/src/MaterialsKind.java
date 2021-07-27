public class MaterialsKind extends MaterialsValue
{
    private boolean _manufacturer = false;
    private boolean _width = false;
    private boolean _height = false;
    private boolean _color = false;
    private boolean _property = false;
    private boolean _thickness = false;
    private boolean _attribute = false;
    private boolean _quantity = false;
    private boolean _active;
    private String _columns;

    public void set_manufacturer(boolean manufacturer) {this._manufacturer = manufacturer;}
    public void set_width(boolean width) {this._width = width;}
    public void set_height(boolean height) {this._height = height;}
    public void set_color(boolean color){this._color = color;}
    public void set_property(boolean property) {this._property = property;}
    public void set_thickness(boolean thickness) {this._thickness = thickness;}
    public void set_attribute(boolean attribute) {this._attribute = attribute;}
    public void set_quantity(boolean quantity) {this._quantity = quantity;}
    public void set_columns(String columns)
    {
        this._columns = columns;
    }
    public void set_active(boolean active)
    {
        this._active = active;
    }

    public boolean get_manufacturer() {return _manufacturer;}
    public boolean get_width() {return _width;}
    public boolean get_height() {return _height;}
    public boolean get_color(){return _color;}
    public boolean get_property() {return _property;}
    public boolean get_thickness(){return _thickness;}
    public boolean get_attribute() {return _attribute;}
    public boolean get_quantity() {return _quantity;}
    public String get_columns()
    {
        return _columns;
    }
    public boolean is_active()
    {
        return _active;
    }
}
