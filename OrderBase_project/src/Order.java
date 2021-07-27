import java.sql.Date;
import java.time.LocalDateTime;

public class Order
{
    private int _id;
    private Date _date;
    private int _client;
    private String _payment;
    private String _amount;
    private int _manager;
    private int _designer;
    private int _accountCreate;
    private int _accountEdit;
    private int _accountAvailability;
    private String _availability;
    private LocalDateTime _dateTimeCreate;
    private LocalDateTime _dateTimeEdit;
    private LocalDateTime _dateTimeAvailability;
    private String _remark;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_date(Date _date)
    {
        this._date = _date;
    }
    public void set_client(int _client)
    {
        this._client = _client;
    }
    public void set_payment(String _payment)
    {
        this._payment = _payment;
    }
    public void set_amount(String _amount)
    {
        this._amount = _amount;
    }
    public void set_manager(int _manager)
    {
        this._manager = _manager;
    }
    public void set_designer(int _designer)
    {
        this._designer = _designer;
    }
    public void set_accountCreate(int _accountCreate)
    {
        this._accountCreate = _accountCreate;
    }
    public void set_accountEdit(int _accountEdit)
    {
        this._accountEdit = _accountEdit;
    }
    public void set_accountAvailability(int _accountAvailability)
    {
        this._accountAvailability = _accountAvailability;
    }
    public void set_availability(String _availability) {
        this._availability = _availability;
    }
    public void set_dateTimeCreate(LocalDateTime _dateTimeCreate)
    {
        this._dateTimeCreate = _dateTimeCreate;
    }
    public void set_dateTimeEdit(LocalDateTime _dateTimeEdit)
    {
        this._dateTimeEdit = _dateTimeEdit;
    }
    public void set_dateTimeAvailability(LocalDateTime _dateTimeAvailability)
    {
        this._dateTimeAvailability = _dateTimeAvailability;
    }
    public void set_remark(String _remark) {
        this._remark = _remark;
    }

    public int get_id() {
        return _id;
    }
    public Date get_date() {
        return _date;
    }
    public int get_client() {
        return _client;
    }
    public String get_payment() {
        return _payment;
    }
    public String get_amount() {
        return _amount;
    }
    public int get_manager() {
        return _manager;
    }
    public int get_designer() {
        return _designer;
    }
    public int get_accountCreate() {
        return _accountCreate;
    }
    public int get_accountEdit() {
        return _accountEdit;
    }
    public int get_accountAvailability() {
        return _accountAvailability;
    }
    public String get_availability() {
        return _availability;
    }
    public LocalDateTime get_dateTimeCreate() {
        return _dateTimeCreate;
    }
    public LocalDateTime get_dateTimeEdit() {
        return _dateTimeEdit;
    }
    public LocalDateTime get_dateTimeAvailability() {
        return _dateTimeAvailability;
    }
    public String get_remark() {
        return _remark;
    }

}
