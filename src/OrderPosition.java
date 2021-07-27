public class OrderPosition
{
    private int _id;
    private int _idOrder;
    private String _description;
    private String _quantity = "Не указано";
    private String _issue = "Не указано";
    private boolean _toPrint = false;
    private int _number;

    OrderPosition(int id, int idOrder, String description, String quantity, String issue)
    {
        this._id = id;
        this._idOrder = idOrder;
        this._description = description;
        this._quantity = quantity;
        this._issue = issue;
    }

    OrderPosition(String description, String quantity, String issue)
    {
        this._description = description;
        this._quantity = quantity;
        this._issue = issue;
    }

    OrderPosition(String description, String quantity)
    {
        this._description = description;
        this._quantity = quantity;
    }

    OrderPosition(){}

    public void set_id(int _id) {this._id = _id;}
    public void set_idOrder(int _idOrder){this._idOrder = _idOrder;}
    public void set_description(String _description){this._description = _description;}
    public void set_quantity(String _quantity) {this._quantity = _quantity;}
    public void set_issue(String _issue) {this._issue = _issue;}
    public void set_number(int _number) {this._number = _number;}
    public void set_toPrint(boolean _toPrint) {this._toPrint = _toPrint;}

    public int get_id() {return _id;}
    public int get_idOrder() {return _idOrder;}
    public String get_description() {return _description;}
    public String get_quantity() {return _quantity;}
    public String get_issue() {return _issue;}
    public int get_number() {return _number;}
    public boolean get_toPrint(){return _toPrint;}
}
