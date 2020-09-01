import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;
    private Date date;
    private Date dateCreate;
    private Date dateEdit;
    private Date dateAvailability;
    private Client client;
    private int id_client;
    private String payment;
    private String amount;
    private String manager;
    private String designer;
    private String loginCreate;
    private String loginEdit;
    private String loginAvailability;
    private ArrayList<OrdersPosition> positions;
    private String availability;
    private LocalTime timeCreate;
    private LocalTime timeEdit;
    private LocalTime timeAvailability;
    private String remark;

    Order( Date date, Client client, int id_client, String payment,
          String amount, String manager, String designer, String loginCreate, List<OrdersPosition> positions, String availability){
        this.date = date;
        this.client = client;
        this.id_client = id_client;
        this.payment = payment;
        this.amount = amount;
        this.manager = manager;
        this.designer = designer;
        this.loginCreate = loginCreate;
        this.positions = new ArrayList<>(positions);
        this.availability = availability;
    }

    Order( Date date, Date dateEdit,Client client, String payment,
           String amount, String manager, String designer, String loginCreate, String loginEdit,
           List<OrdersPosition> positions, String availability){
        this.date = date;
        this.dateEdit = dateEdit;
        this.client = client;
        this.id_client = 0;
        this.payment = payment;
        this.amount = amount;
        this.manager = manager;
        this.designer = designer;
        this.loginCreate = loginCreate;
        this.loginEdit = loginEdit;
        this.positions = new ArrayList<>(positions);
        this.availability = availability;
    }

    Order(long id, Date date, Date dateCreate, Date dateEdit, Date dateAvailability,
          Client client, int id_client, String payment, String amount, String manager, String designer,
          String loginCreate, String loginEdit, String loginAvailability, List<OrdersPosition> positions,
          String availability){
        this.id = id;
        this.date = date;
        this.dateCreate = dateCreate;
        this.dateEdit = dateEdit;
        this.dateAvailability = dateAvailability;
        this.client = client;
        this.id_client = id_client;
        this.payment = payment;
        this.amount = amount;
        this.manager = manager;
        this.designer = designer;
        this.loginCreate = loginCreate;
        this.loginEdit = loginEdit;
        this.loginAvailability = loginAvailability;
        this.positions = new ArrayList<>(positions);
        this.availability = availability;
    }

    Order(){}

    public void setId(long id) {
        this.id = id;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public void setId_client(int id_client) {
        this.id_client = id_client;
    }
    public void setPayment(String payment) {
        this.payment = payment;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setManager(String manager) {
        this.manager = manager;
    }
    public void setDesigner(String designer) {
        this.designer = designer;
    }
    public void setLoginCreate(String loginCreate) {
        this.loginCreate = loginCreate;
    }
    public void setLoginEdit(String loginEdit) {
        this.loginEdit = loginEdit;
    }
    public void setLoginAvailability(String loginAvailability) {
        this.loginAvailability = loginAvailability;
    }
    public void setPositions(List<OrdersPosition> positions) {
        this.positions = new ArrayList<>(positions);
    }
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }
    public void setDateEdit(Date dateEdit) {
        this.dateEdit = dateEdit;
    }
    public void setDateAvailability(Date dateAvailability) {
        this.dateAvailability = dateAvailability;
    }
    public void setTimeCreate(LocalTime timeCreate) {
        this.timeCreate = timeCreate;
    }
    public void setTimeEdit(LocalTime timeEdit) {
        this.timeEdit = timeEdit;
    }
    public void setTimeAvailability(LocalTime timeAvailability) {
        this.timeAvailability = timeAvailability;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getId() {
        return id;
    }
    public Client getClient() {
        return client;
    }
    public int getId_client() {
        return id_client;
    }
    public String getPayment() {
        return payment;
    }
    public String getAmount() {
        return amount;
    }
    public String getManager() {
        return manager;
    }
    public String getDesigner() {
        return designer;
    }
    public String getLoginCreate() {
        return loginCreate;
    }
    public String getLoginEdit() {
        return loginEdit;
    }
    public String getLoginAvailability() {
        return loginAvailability;
    }
    public ArrayList<OrdersPosition> getPositions() {
        return positions;
    }
    public String getAvailability() {
        return availability;
    }
    public Date getDate() {
        return date;
    }
    public Date getDateCreate() {
        return dateCreate;
    }
    public Date getDateEdit() {
        return dateEdit;
    }
    public Date getDateAvailability() {
        return dateAvailability;
    }
    public LocalTime getTimeCreate() {
        return timeCreate;
    }
    public LocalTime getTimeEdit() {
        return timeEdit;
    }
    public LocalTime getTimeAvailability() {
        return timeAvailability;
    }
    public String getRemark() {
        return remark;
    }
}
