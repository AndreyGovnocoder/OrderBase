import java.text.DecimalFormat;
import java.time.LocalDate;

public class DollarRate
{
    private double _dollar;
    private LocalDate _date;

    public void set_date(LocalDate date)

    {
        this._date = date;
    }
    public void set_dollar(double dollar)
    {
        this._dollar = dollar;
    }

    public LocalDate get_date()
    {
        return _date;
    }
    public double get_dollar()
    {
        return _dollar;
    }

    @Override
    public String toString()
    {
        return "$" + MainInterface.DF.format(_dollar) + " (от " + MainInterface._formatter.format(_date) + ")";
    }
}
