public class Receipt
{
    private int _id;
    private int _orderId;
    private String _positions;
    private String _manager;
    private String _date;
    private String _client;

    Receipt(){}
    Receipt(int id, int orderId, String positions, String manager, String date, String client)
    {
        _id = id;
        _orderId = orderId;
        _positions = positions;
        _manager = manager;
        _date = date;
        _client = client;
    }


    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_orderId(int _orderId)
    {
        this._orderId = _orderId;
    }
    public void set_positions(String _positions)
    {
        this._positions = _positions;
    }
    public void set_manager(String _manager)
    {
        this._manager = _manager;
    }
    public void set_date(String _date)
    {
        this._date = _date;
    }
    public void set_client(String _client)
    {
        this._client = _client;
    }

    public int get_id()
    {
        return _id;
    }
    public int get_orderId()
    {
        return _orderId;
    }
    public String get_positions()
    {
        return _positions;
    }
    public String get_manager()
    {
        return _manager;
    }
    public String get_date()
    {
        return _date;
    }
    public String get_client()
    {
        return _client;
    }
}
