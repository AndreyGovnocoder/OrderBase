import java.time.LocalDateTime;
import java.util.Date;

public class Request
{
    private int _id;
    private int _kind;
    private int _valueId;
    private String _description;
    private int _status;
    private LocalDateTime _dateTime;
    private boolean _viewed;

    public void set_id(int id)
    {
        this._id = id;
    }
    public void set_kind(int kind)
    {
        this._kind = kind;
    }
    public void set_valueId(int valueId)
    {
        this._valueId = valueId;
    }
    public void set_description(String description)
    {
        this._description = description;
    }
    public void set_status(int status)
    {
        this._status = status;
    }
    public void set_dateTime(LocalDateTime _dateTime)
    {
        this._dateTime = _dateTime;
    }
    public void set_viewed(boolean viewed) { this._viewed = viewed; }

    public int get_id()
    {
        return _id;
    }
    public int get_kind()
    {
        return _kind;
    }
    public int get_valueId()
    {
        return _valueId;
    }
    public String get_description()
    {
        return _description;
    }
    public int get_status()
    {
        return _status;
    }
    public LocalDateTime get_dateTime()
    {
        return _dateTime;
    }
    public boolean is_viewed() { return _viewed; }
}
