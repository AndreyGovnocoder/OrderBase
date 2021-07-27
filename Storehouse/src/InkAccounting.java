import java.time.LocalDateTime;

public class InkAccounting
{
    private int _id;
    private int _ink;
    private int _quantity;
    private String _procedure;
    private LocalDateTime _dateTime;
    private LocalDateTime _dateTimeOpen;
    private LocalDateTime _dateTimeClose;

    public void set_id(int id)
    {
        this._id = id;
    }
    public void set_ink(int ink)
    {
        this._ink = ink;
    }
    public void set_quantity(int quantity)
    {
        this._quantity = quantity;
    }
    public void set_procedure(String procedure)
    {
        this._procedure = procedure;
    }
    public void set_dateTime(LocalDateTime dateTime)
    {
        this._dateTime = dateTime;
    }
    public void set_dateTimeOpen(LocalDateTime _dateTimeOpen)
    {
        this._dateTimeOpen = _dateTimeOpen;
    }
    public void set_dateTimeClose(LocalDateTime _dateTimeClose)
    {
        this._dateTimeClose = _dateTimeClose;
    }

    public int get_id()
    {
        return _id;
    }
    public int get_ink()
    {
        return _ink;
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
    public LocalDateTime get_dateTimeOpen()
    {
        return _dateTimeOpen;
    }
    public LocalDateTime get_dateTimeClose()
    {
        return _dateTimeClose;
    }
}
