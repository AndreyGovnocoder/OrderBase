public class PowerModuleAccounting extends MaterialAccounting
{
    private int _powModuleId;
    private String _remark;

    public void set_powModuleId(int powModuleId)
    {
        this._powModuleId = powModuleId;
    }
    public void set_remark(String remark)
    {
        this._remark = remark;
    }

    public int get_powModuleId()
    {
        return _powModuleId;
    }
    public String get_remark()
    {
        return _remark;
    }
}
