public class LedAccounting extends MaterialAccounting
{
    private int _ledId;
    private String _remark;

    public void set_ledId(int ledId)
    {
        this._ledId = ledId;
    }
    public void set_remark(String remark)
    {
        this._remark = remark;
    }

    public int get_ledId()
    {
        return _ledId;
    }
    public String get_remark()
    {
        return _remark;
    }
}
