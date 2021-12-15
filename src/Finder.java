import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Finder
{
    static Settings _settings;
    final static int A4 = 1;
    final static int A5 = 2;
    static boolean _isFormatA5 = false;
    static boolean _isFormatA4 = false;
    private static ArrayList<Machine> _allMachinesArrayList;
    private static ArrayList<Ink> _allInksArrayList;
    private static ArrayList<Construction> _constructionsList;
    private static ArrayList<Led> _ledArrayList;
    private static ArrayList<MaterialsValue> _allLedKindsList;
    private static ArrayList<Material> _allMaterialsList;
    private static ArrayList<MaterialsKind> _allMaterialsKinds;
    private static ArrayList<MaterialsValue> _allManufacturers;
    private static ArrayList<MaterialsValue> _allColors;
    private static ArrayList<MaterialsValue> _allProperties;
    private static ArrayList<MaterialsValue> _allAttributes;
    private static ArrayList<Polygraphy> _polygraphyList;
    private static ArrayList<PowerModule> _powerModulesList;
    private static ArrayList<MaterialsValue> _allPowersList;
    private static ArrayList<MaterialsValue> _allBodiesList;
    private static ArrayList<RequestsKind> _requestKindsList;
    private static ArrayList<Order> _allOrders;
    private static ArrayList<Client> _allClients;
    private static ArrayList<Staff> _allStaffs;
    private static ArrayList<Account> _allAccounts;
    private static ArrayList<OrderPosition> _allOrdersPositions;
    private static ArrayList<Receipt> _allReceipts;
    private static ArrayList<Request> _allRequests;
    private static ArrayList<RequestStatus> _requestStatuses;
    private static ArrayList<LedStrip> _allLedStrips;
    private static ArrayList<MaterialsValue> _allQuantityAndTypes;
    private static ArrayList<MaterialsValue> _allDegProtections;

    public static ArrayList<Machine> get_allMachinesArrayList()
    {
        return _allMachinesArrayList;
    }
    public static ArrayList<Ink> get_allInksArrayList()
    {
        return _allInksArrayList;
    }
    public static ArrayList<Construction> get_constructionsList()
    {
        return _constructionsList;
    }
    public static ArrayList<Led> get_ledArrayList()
    {
        return _ledArrayList;
    }
    public static ArrayList<MaterialsValue> get_allLedKindsList()
    {
        return _allLedKindsList;
    }
    public static ArrayList<Material> get_allMaterialsList()
    {
        return _allMaterialsList;
    }
    public static ArrayList<MaterialsKind> get_allMaterialsKinds()
    {
        return _allMaterialsKinds;
    }
    public static ArrayList<MaterialsValue> get_allManufacturers()
    {
        return _allManufacturers;
    }
    public static ArrayList<MaterialsValue> get_allColors()
    {
        return _allColors;
    }
    public static ArrayList<MaterialsValue> get_allProperties()
    {
        return _allProperties;
    }
    public static ArrayList<MaterialsValue> get_allAttributes()
    {
        return _allAttributes;
    }
    public static ArrayList<Polygraphy> get_polygraphyList()
    {
        return _polygraphyList;
    }
    public static ArrayList<PowerModule> get_powerModulesList()
    {
        return _powerModulesList;
    }
    public static ArrayList<RequestsKind> get_requestKindsList()
    {
        return _requestKindsList;
    }
    public static ArrayList<Order> get_allOrders()
    {
        return _allOrders;
    }
    public static ArrayList<Client> get_allClients()
    {
        return _allClients;
    }
    public static ArrayList<Staff> get_allStaffs()
    {
        return _allStaffs;
    }
    public static ArrayList<Account> get_allAccounts()
    {
        return _allAccounts;
    }
    public static ArrayList<OrderPosition> get_allOrdersPositions()
    {
        return _allOrdersPositions;
    }
    public static ArrayList<Receipt> get_allReceipts() { return _allReceipts; }
    public static ArrayList<MaterialsValue> get_allPowersList()
    {
        return _allPowersList;
    }
    public static ArrayList<MaterialsValue> get_allBodiesList()
    {
        return _allBodiesList;
    }
    public static ArrayList<Request> get_allRequests()

    {
        return _allRequests;
    }
    public static ArrayList<LedStrip> get_allLedStrips() {return _allLedStrips;}
    public static ArrayList<MaterialsValue> get_allQuantityAndTypes() {return _allQuantityAndTypes;}
    public static ArrayList<MaterialsValue> get_allDegProtections() {return _allDegProtections;}


    Finder()
    {
        loadDataFromDB();
        loadSettings();
    }

    static void loadDataFromDB()
    {
        _allMachinesArrayList = DataBaseStorehouse.getMachinesList();
        _allInksArrayList = DataBaseStorehouse.getInksList();
        _constructionsList = DataBaseStorehouse.getConstructionsArrayList();
        _allLedKindsList = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.LED_KINDS_TABLE);
        _ledArrayList = DataBaseStorehouse.getLedsArrayList();
        _allMaterialsList = DataBaseStorehouse.getMaterialsList();
        _allMaterialsKinds = DataBaseStorehouse.getMaterialsKindsList();
        _allManufacturers = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.MANUFACTURERS_TABLE);
        _allColors = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.COLORS_TABLE);
        _allProperties = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.PROPERTIES_TABLE);
        _allAttributes = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.ATTRIBUTES_TABLE);
        _polygraphyList = DataBaseStorehouse.getPolygraphyArrayList();
        _powerModulesList = DataBaseStorehouse.getPowerModulesList();
        _requestKindsList = DataBaseStorehouse.getRequestsKindsList();
        _allOrders = DataBase.getOrdersList();
        _allClients = DataBase.getClientsList();
        _allStaffs = DataBase.getStaffsList();
        _allAccounts = DataBase.getAccountsList();
        _allOrdersPositions = DataBase.getOrderPositionsList();
        _allReceipts = DataBase.getReceiptsList();
        _allBodiesList = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.BODIES_TABLE);
        _allPowersList = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.POWERS_TABLE);
        _allRequests = DataBaseStorehouse.getRequestList();
        _requestStatuses = DataBaseStorehouse.getRequestStatusList();
        _allLedStrips = DataBaseStorehouse.getLedStripsArrayList();
        _allQuantityAndTypes = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.LEDSTRIP_TYPES_TABLE);
        _allDegProtections = DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.LEDSTRIP_DEGPROTECT_TABLE);
        _isFormatA4 = DataBase.getPaperFormat(A4);
        _isFormatA5 = DataBase.getPaperFormat(A5);
    }

    private static void loadSettings()
    {
        System.out.println("load settings");
        String path = DataBase.path + "\\src\\settings.ser";
        if (new File(path).isFile())
        {
            try
            {
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new FileInputStream(path));

                    _settings = (Settings) objectInputStream.readObject();

                objectInputStream.close();
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
                _settings = new Settings();
            }

        }
        else
        {
            _settings = new Settings();
            System.out.println("file is NOT exist");
        }
    }

    static LedStrip getLedStrip(int ledStripId)
    {
        for (LedStrip ledStrip : _allLedStrips)
        {
            if (ledStrip.get_id() == ledStripId)
                return ledStrip;
        }

        return null;
    }

    static MaterialsValue getQuantityAndType(int id)
    {
        for (MaterialsValue value : _allQuantityAndTypes)
        {
            if (value.get_id() == id)
                return value;
        }
        return null;
    }

    static MaterialsValue getDegProtection(int id)
    {
        for (MaterialsValue value : _allDegProtections)
        {
            if (value.get_id() == id)
                return value;
        }
        return null;
    }

    static Request getRequest(int requestId)
    {
        for (Request request : _allRequests)
        {
            if (request.get_id() == requestId)
                return request;
        }
        return null;
    }

    static int getRequestIndexOf(final int requestId)
    {
        int index = -1;
        for (Request request : _allRequests)
        {
            if (request.get_id() != requestId)
                continue;

            index = _allRequests.indexOf(request);
        }

        return index;
    }

    static RequestStatus getRequestStatus(int stausId)
    {
        for (RequestStatus status : _requestStatuses)
        {
            if (status.get_id() == stausId)
                return status;
        }
        return null;
    }

    static Machine getMachine(int machineId)
    {
        for(Machine machine : _allMachinesArrayList)
        {
            if (machine.get_id() == machineId)
                return machine;
        }

        return new Machine();
    }

    static Ink getInk(int inkId)
    {
        for(Ink ink : _allInksArrayList)
            if (ink.get_id() == inkId)
                return ink;

        //return new Ink();
        return null;
    }

    static Construction getConstruction(int constructionId)
    {
        for(Construction constr : _constructionsList)
            if (constr.get_id() == constructionId)
                return constr;

        return null;
    }

    static Led getLed(final int ledId)
    {
        for (final Led led : _ledArrayList)
            if (led.get_id() == ledId)
                return led;

        return null;
    }

    static MaterialsValue getLedKind(final int kindId)
    {
        for (final MaterialsValue value : _allLedKindsList)
            if (value.get_id() == kindId)
                return value;

        return null;
    }

    static Material getMaterial(int materialId)
    {
        for(Material m : _allMaterialsList)
            if(m.get_id() == materialId)
                return m;

        return null;
    }

    static MaterialsValue getAttribute(int attributeId)
    {
        for (final MaterialsValue value : _allAttributes)
            if(value.get_id() == attributeId)
                return value;

        return null;
    }

    static MaterialsValue getColor(int colorId)
    {
        for (final MaterialsValue value : _allColors)
            if(value.get_id() == colorId)
                return value;

        return null;
    }

    static MaterialsValue getManufacturer(int manufacturerId)
    {
        for (final MaterialsValue value : _allManufacturers)
            if (value.get_id() == manufacturerId)
                return value;

        return null;
    }

    static MaterialsValue getProperty(int propertyId)
    {
        for (final MaterialsValue value : _allProperties)
            if (value.get_id() == propertyId)
                return value;

        return null;
    }

    static MaterialsKind getMaterialKind(int kindId)
    {
        for (final MaterialsKind value : _allMaterialsKinds)
            if (value.get_id() == kindId)
                return value;

        return null;
    }

    static Polygraphy getPolygraphy(int polygraphyId)
    {
        for(Polygraphy polygraphy : _polygraphyList)
            if (polygraphy.get_id() == polygraphyId)
                return polygraphy;

        return null;
    }

    static PowerModule getPowerModule(final int pModuleId)
    {
        for (PowerModule pModule : _powerModulesList)
            if (pModule.get_id() == pModuleId)
                return pModule;

        return null;
    }

    static MaterialsValue getBody(final int bodyId)
    {
        for (MaterialsValue value : _allBodiesList)
            if (bodyId == value.get_id())
                return value;

        return null;
    }

    static MaterialsValue getPower(final int powerId)
    {
        for (MaterialsValue value : _allPowersList)
            if (powerId == value.get_id())
                return value;

        return null;
    }

    static RequestsKind getRequestKind(final int kindId)
    {
        for (final RequestsKind kind : _requestKindsList)
            if (kind.get_id() == kindId)
                return kind;

        return null;
    }

    static Receipt getReceipt(int idReceipt)
    {
        for (final Receipt receipt : _allReceipts)
            if (receipt.get_id() == idReceipt)
                return receipt;

        return null;
    }

    static Account getAccount(int idAccount)
    {
        Account account = new Account();
        for(Account acc : _allAccounts)
            if(acc.get_id() == idAccount)
                account = acc;

        return account;
    }

    static Order getOrder(int idOrder)
    {
        Order order = new Order();
        for (Order ordr : _allOrders)
            if(ordr.get_id() == idOrder)
                order = ordr;

        return order;
    }

    static Client getClient(int idClient)
    {
        Client client = new Client();
        for(Client clnt : _allClients)
            if(clnt.get_id() == idClient)
                client = clnt;

        return client;
    }

    static Staff getStaff(int idStaff)
    {
        Staff staff = new Staff();
        for(Staff stff : _allStaffs)
            if(stff.get_id() == idStaff)
                staff = stff;

        return staff;
    }

    static ArrayList<OrderPosition> getOrdersPositionsList(int idOrder)
    {
        ArrayList<OrderPosition> orderPositionsList = new ArrayList<>();
        for (OrderPosition position : _allOrdersPositions)
            if (position.get_idOrder() == idOrder)
                orderPositionsList.add(position);

        return orderPositionsList;
    }

    static boolean isFormatA4() {return _isFormatA4;}
    static boolean isFormatA5() {return _isFormatA5;}
    static void setFormat(final int format, final boolean value)
    {
        if (format == A5)
        {
            _isFormatA5 = value;
            _isFormatA4 = !value;
            DataBase.setPaperFormat(A5, value);
            DataBase.setPaperFormat(A4, !value);
        }
        else if (format == A4)
        {
            _isFormatA5 = !value;
            _isFormatA4 = value;
            DataBase.setPaperFormat(A5, !value);
            DataBase.setPaperFormat(A4, value);
        }

    }
}
