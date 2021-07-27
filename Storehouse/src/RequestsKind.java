public class RequestsKind
{
    private int _id;
    private String _kind;

    public void set_id(int _id)
    {
        this._id = _id;
    }
    public void set_kind(String _kind)
    {
        this._kind = _kind;
    }

    public int get_id()
    {
        return _id;
    }
    public String get_kind()
    {
        return _kind;
    }

    @Override
    public String toString() { return _kind; }
}
