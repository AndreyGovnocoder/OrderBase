import java.sql.Date;
import java.time.LocalTime;

public class Montage
{
    private int _id;
    private String _object;
    private String _description;
    private String _contactPerson;
    private Date _date;
    private LocalTime _time;
    private boolean _measure;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_object(String _object)
    {
        this._object = _object;
    }
    public void set_description(String _description)
    {
        this._description = _description;
    }
    public void set_contactPerson(String _contactPerson)
    {
        this._contactPerson = _contactPerson;
    }
    public void set_date(Date _date)
    {
        this._date = _date;
    }
    public void set_time(LocalTime _time)
    {
        this._time = _time;
    }
    public void set_measure(boolean _measure)
    {
        this._measure = _measure;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_object()
    {
        return _object;
    }
    public String get_description()
    {
        return _description;
    }
    public String get_contactPerson()
    {
        return _contactPerson;
    }
    public Date get_date()
    {
        return _date;
    }
    public LocalTime get_time()
    {
        return _time;
    }
    public boolean get_measure() { return _measure;}
}
