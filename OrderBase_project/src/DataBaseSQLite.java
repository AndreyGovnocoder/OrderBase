import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseSQLite {

    public static File file = new File("");
    public static String path = file.getAbsolutePath();
    private static String DB_URL_home = "jdbc:sqlite://" + path + "/src/db/OrderBasePrint_dataBase.db";
    private static String DB_URL = "jdbc:sqlite://" + "\\\\192.168.1.32\\backup\\orderBase_DB\\OrderBasePrint_dataBase.db";
    private static String USER = "123";
    private static String PASSWORD = "1415";

    static boolean testConnection(){

        Connection conn;
        try {
            conn = DriverManager.getConnection(DB_URL);
            conn.close();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    static void setClientsToSQLiteDB()
    {
        for (Client client:DataBaseHelper.getClientsList())
        {
            addClientToDB(client);
        }
    }

    static void setAccountsToSQLiteDB()
    {
        for(Account account : DataBaseHelper.getAccountsList())
        {
            addAccountToSQLiteDB(account);
        }
    }

    static void setStaffsToSQLiteDB()
    {
        for(Staff staff : DataBaseHelper.getStaffs("менеджер"))
        {
            addStaffToDB(staff);
        }

        for(Staff staff : DataBaseHelper.getStaffs("дизайнер"))
        {
            addStaffToDB(staff);
        }
    }

    static void setOrdersPositionToSQLiteDB()
    {
        for(Order order : DataBaseHelper.getOrdersList())
        {
            for (OrdersPosition position : order.getPositions())
            {
                String str = Long.toString(order.getId());
                int orderId = Integer.valueOf(str);
                addOrdersPositionToDB(position, orderId);
            }
        }
    }

    static void setOrdersToSQLiteDB()
    {
        for (Order order : DataBaseHelper.getOrdersList())
        {
            addOrderToSQLiteDB(order);
        }
    }

    static ObservableList<Client> getClientsList(){
        List<Client> list = new ArrayList<>();
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }

            PreparedStatement pr = conn.prepareStatement("SELECT id_, client, " +
                    "phone, mail FROM Clients");
            ResultSet rs = pr.executeQuery();

            while (rs.next()){
                list.add(new Client(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4)));
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        ObservableList<Client> clientsList = FXCollections.observableList(list);

        return clientsList;
    }

    static void addClientToDB(Client client){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO clients(_id, name, " +
                    "phone, mail) " +
                    "VALUES (?,?,?,?)");
            pr.setInt(1, client.getId());
            pr.setString(2, client.getClient());
            pr.setString(3, client.getPhone());
            pr.setString(4, client.getMail());

            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void editClientToDB(Client client){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("UPDATE Clients SET client=?, " +
                    " phone=?, mail=? WHERE id_="+client.getId());
            pr.setString(1, client.getClient());
            pr.setString(2, client.getPhone());
            pr.setString(3, client.getMail());
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static Client getClient(int id){
        Client client = new Client();
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT id_, client," +
                    "phone, mail" +
                    " FROM Clients WHERE id_="+id);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                client.setId(rs.getInt(1));
                client.setClient(rs.getString(2));
                client.setPhone(rs.getString(3));
                client.setMail(rs.getString(4));
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return client;
    }

    static void deleteClientFromDB(Client client){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr =  conn.prepareStatement("DELETE FROM Clients WHERE id_ = "+client.getId());
            pr.executeUpdate();
            conn.close();

        }catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void addDate(LocalDate localDate){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO Date_(date_) " +
                    "VALUES (?)");
            pr.setObject(1, localDate);
            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static LocalDate getDate(){
        Date date = null ;
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT Дата FROM Заказы");
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                date = rs.getDate(1);
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        LocalDate localDate = date.toLocalDate();
        return localDate;
    }

    static ObservableList<Staff> getStaffs(String jobPosition){

        PreparedStatement pr;
        String sql = "";
        List<Staff> staffs = new ArrayList<>();

        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }

            switch (jobPosition){
                case "менеджер":
                    sql = "SELECT id_, name_manager FROM Managers";
                    break;
                case "дизайнер":
                    sql = "SELECT id_, name_designer FROM Designers";
                    break;
            }

            pr = conn.prepareStatement(sql);
            ResultSet rs = pr.executeQuery();

            while (rs.next()){
                staffs.add(new Staff(rs.getInt(1), rs.getString(2), jobPosition));
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        ObservableList<Staff> staffList = FXCollections.observableList(staffs);

        return staffList;
    }

    static void addStaffToDB(Staff staff){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO staffs(_id, name, position) " +
                    "VALUES (?,?,?)");
            pr.setInt(1, staff.getId());
            pr.setString(2, staff.getName());
            pr.setString(3, staff.getJobPosition());
            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void deleteStaffFromDB(Staff staff){
        PreparedStatement pr;
        String sql = "";

        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }

            switch (staff.getJobPosition()){
                case "менеджер":
                    sql = "DELETE FROM Managers WHERE id_ =?";
                    break;

                case "дизайнер":
                    sql = "DELETE FROM Designers WHERE id_ =?";
                    break;
            }

            pr = conn.prepareStatement(sql);
            pr.setInt(1, staff.getId());
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }

    }

    static void addOrderToSQLiteDB(Order order){
        Connection conn;
        /*

        LocalTime localTime = LocalTime.now();
        String allPositions = "";
        for (OrdersPosition p:order.getPositions()){
            allPositions = allPositions + p.getDescription() + ";"+p.getIssue()+";"+p.getQuantity()+";";
        }
         */
        LocalDateTime localDateTime = LocalDateTime.now();
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO orders(" +
                    "_id, date, client, amount, payment, manager, designer, availability, remark, " +
                    "loginCreate, loginEdit, loginAvailability, dateTimeCreate, dateTimeEdit, dateTimeAvailability) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pr.setObject(1, order.getId());
            pr.setObject(2, order.getDate().toLocalDate());
            if(order.getClient().getId() == 0)
            {
                pr.setInt(3, -1);
            }
            else
            {
                pr.setInt(3, order.getClient().getId());
            }
            pr.setString(4, order.getAmount());
            pr.setString(5, order.getPayment());
            int managerId = -1;
            int designerId = -1;
            int loginCreateId = -1;
            int loginEditId = -1;
            int loginAvailabilityId = -1;
            for(Staff staff : DataBaseHelper.getStaffs("менеджер"))
            {
                if(order.getManager().equals(staff.getName())) managerId = staff.getId();
            }
            pr.setInt(6, managerId);
            for(Staff staff : DataBaseHelper.getStaffs("дизайнер"))
            {
                if(order.getDesigner().equals(staff.getName())) designerId = staff.getId();
            }
            pr.setInt(7, designerId);
            pr.setString(8, order.getAvailability());
            pr.setString(9, order.getRemark());

            for(Account account : DataBaseHelper.getAccountsList())
            {
                if(order.getLoginCreate() != null)
                {
                    if(order.getLoginCreate().equals(account.getUserName())) loginCreateId = account.getId();
                }

                if(order.getLoginEdit() != null)
                {
                    if(order.getLoginEdit().equals(account.getUserName())) loginEditId = account.getId();
                }

                if(order.getLoginAvailability() != null)
                {
                    if(order.getLoginAvailability().equals(account.getUserName())) loginAvailabilityId = account.getId();
                }
            }

            pr.setInt(10, loginCreateId);
            pr.setInt(11, loginEditId);
            pr.setInt(12, loginAvailabilityId);

            if(order.getDateCreate() != null && order.getTimeCreate() != null) {
                LocalDateTime localDateTimeCreate = LocalDateTime.of(
                        order.getDateCreate().toLocalDate().getYear(),
                        order.getDateCreate().toLocalDate().getMonthValue(),
                        order.getDateCreate().toLocalDate().getDayOfMonth(),
                        order.getTimeCreate().getHour(),
                        order.getTimeCreate().getMinute(),
                        order.getTimeCreate().getSecond()
                );
                pr.setObject(13,localDateTimeCreate );
            } else
            {
                pr.setObject(13,-1 );
            }

            if(order.getDateEdit() != null && order.getTimeEdit() != null)
            {
                LocalDateTime localDateTimeEdit = LocalDateTime.of(
                        order.getDateEdit().toLocalDate().getYear(),
                        order.getDateEdit().toLocalDate().getMonthValue(),
                        order.getDateEdit().toLocalDate().getDayOfMonth(),
                        order.getTimeEdit().getHour(),
                        order.getTimeEdit().getMinute(),
                        order.getTimeEdit().getSecond()
                );

                pr.setObject(14, localDateTimeEdit);
            } else
            {
                pr.setObject(14, -1);
            }

            if (order.getDateAvailability() != null && order.getTimeAvailability() != null)
            {
                LocalDateTime localDateTimeAvailability = LocalDateTime.of(
                        order.getDateAvailability().toLocalDate().getYear(),
                        order.getDateAvailability().toLocalDate().getMonthValue(),
                        order.getDateAvailability().toLocalDate().getDayOfMonth(),
                        order.getTimeAvailability().getHour(),
                        order.getTimeAvailability().getMinute(),
                        order.getTimeAvailability().getSecond()
                );

                pr.setObject(15, localDateTimeAvailability);
            } else
            {
                pr.setObject(15, -1);
            }

            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения "+ex.toString());
        }
    }

    /*
    static void editOrder(Order order){
        Connection conn;
        String allPositions = "";
        LocalTime localTime = LocalTime.now();
        for (OrdersPosition p:order.getPositions()){
            allPositions = allPositions + p.getDescription() + ";"+p.getIssue()+";"+p.getQuantity()+";";
        }
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("UPDATE Заказы SET " +
                    "Дата=?,  Дата_редактирования=?, Заказчик=?, id_client=?, Оплата=?, Сумма=?, " +
                    "Наименование=?, Менеджер=?, Дизайнер=?, LoginEdit=?, Время_редактирования=?, Примечание=? " +
                    "WHERE id_="+order.getId());
            pr.setObject(1, order.getDate());
            pr.setObject(2, order.getDateEdit());
            pr.setString(3, order.getClient().getClient());
            pr.setInt(4, order.getClient().getId());
            pr.setString(5, order.getPayment());
            pr.setString(6, order.getAmount());
            pr.setString(7, allPositions);
            pr.setString(8, order.getManager());
            pr.setString(9, order.getDesigner());
            pr.setString(10, order.getLoginEdit());
            pr.setObject(11, localTime);
            pr.setString(12, order.getRemark());
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения "+ex.toString());
        }
    }

    static void deleteOrderFromDB(Order order){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr =  conn.prepareStatement("DELETE FROM Заказы WHERE id_ = "+order.getId());
            pr.executeUpdate();
            conn.close();

        }catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static Order getOrderFromDB(Order order){
        Order newOrder = new Order();
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT id_, Дата, Дата_создания, Дата_редактирования, " +
                    "Дата_изменения, Заказчик, id_client, Оплата, Сумма, Наименование, Менеджер, Дизайнер," +
                    " LoginCreate, LoginEdit, LoginAvailability, Готовность, Время_создания, Время_редактирования, Время_изменения, " +
                    "Примечание " +
                    "FROM Заказы where id_= "+order.getId());
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                Date date = rs.getDate(2);
                Date dateCreate = rs.getDate(3);
                Date dateEdit = rs.getDate(4);
                Date dateAvailability = rs.getDate(5);
                order.setId(rs.getLong(1));
                order.setDate(date);
                order.setDateEdit(dateEdit);
                order.setDateCreate(dateCreate);
                order.setDateAvailability(dateAvailability);
                if(rs.getInt(7)==0){
                    order.setClient(new Client(rs.getString(6)));
                } else {
                    order.setClient(DataBaseHelper.getClient(rs.getInt(7)));
                }
                order.setPayment(rs.getString(8));
                order.setAmount(rs.getString(9));
                order.setPositions(DataBaseHelper.getOrdersPositions(rs.getString(10)));
                order.setManager(rs.getString(11));
                order.setDesigner(rs.getString(12));
                order.setLoginCreate(rs.getString(13));
                order.setLoginEdit(rs.getString(14));
                order.setLoginAvailability(rs.getString(15));
                order.setAvailability(rs.getString(16));
                if(rs.getTime(17)!=null){
                    order.setTimeCreate(rs.getTime(17).toLocalTime());
                }
                if(rs.getTime(18)!=null){
                    order.setTimeEdit(rs.getTime(18).toLocalTime());
                }
                if(rs.getTime(19)!=null){
                    order.setTimeAvailability(rs.getTime(19).toLocalTime());
                }
                order.setRemark(rs.getString(20));

            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return  newOrder;
    }

    static ArrayList<Order> getOrdersList(){
        ArrayList<Order> ordersList = new ArrayList<>();
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT id_, Дата, Дата_создания, Дата_редактирования, " +
                    "Дата_изменения, Заказчик, id_client, Оплата, Сумма, Наименование, Менеджер, Дизайнер," +
                    " LoginCreate, LoginEdit, LoginAvailability, Готовность, Время_создания, Время_редактирования, Время_изменения, " +
                    "Примечание " +
                    "FROM Заказы ORDER BY Дата");
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                //order.setPositions(DataBaseHelper.getOrdersPositions(rs.getString(1)));
                Order order = new Order();
                Date date = rs.getDate(2);
                Date dateCreate = rs.getDate(3);
                if(rs.getDate(4)!=null) {
                    Date dateEdit = rs.getDate(4);
                    order.setDateEdit(dateEdit);
                }
                if (rs.getDate(5)!=null) {
                    Date dateAvailability = rs.getDate(5);
                    order.setDateAvailability(dateAvailability);
                }
                order.setId(rs.getLong(1));
                order.setDate(date);
                order.setDateCreate(dateCreate);
                if(rs.getInt(7)==0){
                    order.setClient(new Client(rs.getString(6)));
                } else {
                    order.setClient(DataBaseHelper.getClient(rs.getInt(7)));
                }
                order.setPayment(rs.getString(8));
                order.setAmount(rs.getString(9));
                order.setPositions(DataBaseHelper.getOrdersPositions(rs.getString(10)));
                order.setManager(rs.getString(11));
                order.setDesigner(rs.getString(12));
                order.setLoginCreate(rs.getString(13));
                order.setLoginEdit(rs.getString(14));
                order.setLoginAvailability(rs.getString(15));
                order.setAvailability(rs.getString(16));
                if(rs.getTime(17)!=null){
                    order.setTimeCreate(rs.getTime(17).toLocalTime());
                }
                if(rs.getTime(18)!=null){
                    order.setTimeEdit(rs.getTime(18).toLocalTime());
                }
                if(rs.getTime(19)!=null){
                    order.setTimeAvailability(rs.getTime(19).toLocalTime());
                }
                order.setRemark(rs.getString(20));

                ordersList.add(order);

            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return ordersList;
    }
     */

    static void addOrdersPositionToDB(OrdersPosition position, int idOrder)
    {
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO positions(idOrder, description, quantity, issue) " +
                    "VALUES (?,?,?,?)");
            pr.setInt(1, idOrder);
            pr.setString(2, position.getDescription());
            pr.setString(3, position.getQuantity());
            pr.setString(4, position.getIssue());
            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static ArrayList<OrdersPosition> getOrdersPositions(String fromDB){
        ArrayList<OrdersPosition> list = new ArrayList<>();

        String delimeter = ";";
        String[] positions;
        positions = fromDB.split(delimeter);



        for(int x=0; x<positions.length; x=x+3){
            list.add(new OrdersPosition(positions[x],positions[x+1], positions[x+2]));
        }

/*
        for(int x=0; x<positions.length; x=x+2){
            list.add(new OrdersPosition(positions[x],positions[x+1]));
        }
*/
        return list;
    }

    static ObservableList<OrdersPosition> getPositionsFromOrder(Order order){
        ObservableList<OrdersPosition> positionsList = FXCollections.observableArrayList();

        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT Наименование FROM Заказы WHERE id_= "+order.getId());
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                //order.setPositions(DataBaseHelper.getOrdersPositions(rs.getString(1)));
                positionsList.addAll(DataBaseHelper.getOrdersPositions(rs.getString(1)));
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return positionsList;
    }

    static void setAvailabilityOrders(Order order, String loginAvailability){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        Date date = Date.valueOf(localDate);
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("UPDATE Заказы SET " +
                    "Готовность = ?, LoginAvailability = ?, Дата_изменения =?, Время_изменения=? " +
                    "WHERE id_="+order.getId());
            if(order.getAvailability().equals("Готово")) pr.setString(1, "В работе");
            else pr.setString(1, "Готово");
            pr.setString(2, loginAvailability);
            pr.setObject(3, date );
            pr.setObject(4, localTime);
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }

        MainInterface.refreshOrdersList();
    }

    static void setPaymentOrders(Order order, String loginAvailability){
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        Date date = Date.valueOf(localDate);
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("UPDATE Заказы SET " +
                    "Оплата = ?, LoginAvailability = ?, Дата_изменения =?, Время_изменения=? " +
                    "WHERE id_="+order.getId());
            pr.setString(1, order.getPayment());
            pr.setString(2, loginAvailability);
            pr.setObject(3, date );
            pr.setObject(4, localTime);
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }

        MainInterface.refreshOrdersList();
    }

    static  void addAccountToSQLiteDB(Account account){
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("INSERT INTO logins(_id, name, position, login, password) " +
                    "VALUES (?,?,?,?,?)");
            pr.setInt(1, account.getId());
            pr.setString(2, account.getUserName());
            pr.setString(3, account.getUserPositon());
            pr.setString(4, account.getLogin());
            pr.setString(5, account.getPassword());
            pr.execute();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static int getCount(){
        int count = 0;
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("SELECT count " +
                    "FROM Счетчик_накладных WHERE id_=?");
            pr.setInt(1, 1);
            ResultSet rs = pr.executeQuery();
            while (rs.next()){
                count = rs.getInt(1);
            }
            pr.close();
            rs.close();
            conn.close();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return count;
    }

    static void setCount(int count){
        count++;
        Connection conn;
        try {
            if(MainInterface.db.equals("home")){
                conn = DriverManager.getConnection(DB_URL_home);
            } else {
                conn = DriverManager.getConnection(DB_URL);
            }
            PreparedStatement pr = conn.prepareStatement("UPDATE Счетчик_накладных SET " +
                    "count=? " +
                    "WHERE id_= 1");
            pr.setInt(1, count);
            pr.executeUpdate();
            conn.close();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }
}
