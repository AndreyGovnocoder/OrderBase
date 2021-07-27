import java.time.LocalDateTime;

public class MaterialAccounting
{
    private int _id;
    private int _material;
    private int _accountId;
    private int _quantity;
    private String _procedure;
    private LocalDateTime _dateTime;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_material(int _material)
    {
        this._material = _material;
    }
    public void set_accountId(int _accountId)
    {
        this._accountId = _accountId;
    }
    public void set_quantity(int _quantity)
    {
        this._quantity = _quantity;
    }
    public void set_procedure(String _procedure)
    {
        this._procedure = _procedure;
    }
    public void set_dateTime(LocalDateTime _dateTime)
    {
        this._dateTime = _dateTime;
    }

    public int get_id()
    {
        return _id;
    }
    public int get_material()
    {
        return _material;
    }
    public int get_accountId()
    {
        return _accountId;
    }
    public int get_quantity()
    {
        return _quantity;
    }
    public String get_procedure()
    {
        return _procedure;
    }
    public LocalDateTime get_dateTime()
    {
        return _dateTime;
    }
}
