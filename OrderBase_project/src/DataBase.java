import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class DataBase
{
    public static File file = new File("");
    public static String path = file.getAbsolutePath();
    private static final String DB_URL_home = "jdbc:sqlite://" + path + "/src/db/OrderBasePrint_dataBase.db";
    private static final String DB_URL = "jdbc:sqlite://" + "\\192.168.1.32\\backup\\orderBase_DB\\OrderBasePrint_dataBase.db";
    static final String ACCOUNTS_TABLE = "accounts";
    static final String ORDERS_TABLE = "orders";
    static final String CLIENTS_TABLE = "clients";
    static final String POSITIONS_TABLE = "positions";
    static final String STAFFS_TABLE = "staffs";
    static final String RECEIPTCOUNTER_TABLE = "receiptCounter";
    static final String IMAGES_TABLE = "images";
    private static Connection connection;
    private static PreparedStatement pr;
    private static ResultSet rs;


    private static Connection getConnection()
    {
        try
        {
            if(connection == null || connection.isClosed())
            {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("open connection");
            }
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        return connection;
    }

    static void   closeConnection()
    {
        try
        {
            if(connection != null)
            {
                connection.close();
                System.out.println("close connection");
            }

            if(pr != null) pr.close();
            if(rs != null) rs.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static int getLastId(final String TABLE)
    {
        int lastId = -1;

        try
        {
            pr = getConnection().prepareStatement("SELECT _id " +
                    " FROM " + TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                lastId = rs.getInt(1);
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return lastId;
    }

    static void addOrder(Order order)
    {
        try
        {
            pr = getConnection().prepareStatement("INSERT INTO " +
                    ORDERS_TABLE +
                    "(date, client, amount, payment, manager, designer, availability, remark, " +
                    "accountCreate, accountEdit, accountAvailability, " +
                    "dateTimeCreate, dateTimeEdit, dateTimeAvailability) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pr.setObject(1, order.get_date().toLocalDate());
            pr.setInt(2, order.get_client());
            pr.setString(3, order.get_amount());
            pr.setString(4, order.get_payment());
            pr.setInt(5, order.get_manager());
            pr.setInt(6, order.get_designer());
            pr.setString(7, order.get_availability());
            pr.setString(8, order.get_remark());
            pr.setInt(9, order.get_accountCreate());
            pr.setInt(10, order.get_accountEdit());
            pr.setInt(11, order.get_accountAvailability());
            pr.setObject(12, order.get_dateTimeCreate());
            pr.setObject(13, order.get_dateTimeEdit());
            pr.setObject(14, order.get_dateTimeAvailability());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static void editOrder(Order order)
    {
        try
        {
            pr = getConnection().prepareStatement("UPDATE " +
                    ORDERS_TABLE +
                    " SET date=?, client=?, amount=?, payment=?," +
                    " manager=?, designer=?, availability=?, remark=?," +
                    " accountCreate=?, accountEdit=?, accountAvailability=?," +
                    " dateTimeCreate=?, dateTimeEdit=?, dateTimeAvailability=?" +
                    "WHERE _id=" + order.get_id());
            pr.setObject(1, order.get_date().toLocalDate());
            pr.setInt(2, order.get_client());
            pr.setString(3, order.get_amount());
            pr.setString(4, order.get_payment());
            pr.setInt(5, order.get_manager());
            pr.setInt(6, order.get_designer());
            pr.setString(7, order.get_availability());
            pr.setString(8, order.get_remark());
            pr.setInt(9, order.get_accountCreate());
            pr.setInt(10, order.get_accountEdit());
            pr.setInt(11, order.get_accountAvailability());
            pr.setObject(12, order.get_dateTimeCreate());
            pr.setObject(13, order.get_dateTimeEdit());
            pr.setObject(14, order.get_dateTimeAvailability());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static ArrayList<Order> getOrdersList()
    {
        ArrayList<Order> ordersList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, date, client, amount, " +
                    "payment, manager, designer, availability, remark, accountCreate, accountEdit, accountAvailability," +
                    " dateTimeCreate, dateTimeEdit, dateTimeAvailability FROM " +
                    ORDERS_TABLE +
                    " ORDER BY date");
            rs = pr.executeQuery();
            while (rs.next())
            {
                Order order = new Order();
                order.set_id(rs.getInt(1));
                order.set_date(Date.valueOf(rs.getObject(2).toString()));
                order.set_client(rs.getInt(3));
                order.set_amount(rs.getString(4));
                order.set_payment(rs.getString(5));
                order.set_manager(rs.getInt(6));
                order.set_designer(rs.getInt(7));
                order.set_availability(rs.getString(8));
                order.set_remark(rs.getString(9));
                order.set_accountCreate(rs.getInt(10));
                order.set_accountEdit(rs.getInt(11));
                order.set_accountAvailability(rs.getInt(12));
                if(rs.getObject(13) != null && !rs.getObject(13).toString().equals("-1"))
                {
                    dateTime = rs.getObject(13).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1]), 00);
                    order.set_dateTimeCreate(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(14) != null && !rs.getObject(14).toString().equals("-1"))
                {
                    dateTime = rs.getObject(14).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1]), 00);
                    order.set_dateTimeEdit(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(15) != null && !rs.getObject(15).toString().equals("-1"))
                {
                    dateTime = rs.getObject(15).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.valueOf(timeArr[0]), Integer.valueOf(timeArr[1]), 00);
                    order.set_dateTimeAvailability(LocalDateTime.of(date.toLocalDate(), localTime));
                }
                ordersList.add(order);
            }
            //closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }


        return ordersList;
    }

    static void addOrderPosition(OrderPosition position)
    {
        try
        {
            pr = getConnection().prepareStatement("INSERT INTO " +
                    POSITIONS_TABLE +
                    "(idOrder, description, quantity, issue) " +
                    "VALUES (?,?,?,?)");
            pr.setInt(1, position.get_idOrder());
            pr.setString(2, position.get_description());
            pr.setString(3, position.get_quantity());
            pr.setString(4, position.get_issue());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static ArrayList<OrderPosition> getOrderPositions(int idOrder)
    {
        ArrayList<OrderPosition> positionsList = new ArrayList<>();
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, idOrder, description, quantity, issue " +
                    " FROM " + POSITIONS_TABLE +
                    " WHERE idOrder=" + idOrder);
            rs = pr.executeQuery();
            while (rs.next())
            {
                OrderPosition position = new OrderPosition(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                );
                positionsList.add(position);
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return positionsList;
    }

    static ArrayList<OrderPosition> getOrderPositionsList()
    {
        ArrayList<OrderPosition> allPositions = new ArrayList<>();
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, idOrder, description, quantity, issue " +
                    " FROM " + POSITIONS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                OrderPosition position = new OrderPosition(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                );
                allPositions.add(position);
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return allPositions;
    }

    static void removeOrderPositions(int idOrder)
    {
        try
        {
            pr = getConnection().prepareStatement("DELETE FROM " +
                    POSITIONS_TABLE +
                    " WHERE idOrder = " + idOrder);
            pr.executeUpdate();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void addClient(Client client)
    {
        try
        {
            pr = getConnection().prepareStatement("INSERT INTO " +
                    CLIENTS_TABLE +
                    "(name, phone, mail, contactPerson) " +
                    "VALUES (?,?,?,?)");
            pr.setString(1, client.get_name());
            pr.setString(2, client.get_phone());
            pr.setString(3, client.get_mail());
            pr.setString(4, client.get_contactPerson());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static Client getClient(int idClient)
    {
        Client client = null;

        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, phone, mail, contactPerson " +
                    " FROM " + CLIENTS_TABLE +
                    " WHERE _id=" + idClient);
            rs = pr.executeQuery();
            while (rs.next())
            {

                client = new Client(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5));
            }
            //closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return client;
    }

    static ArrayList<Client> getClientsList()
    {
        ArrayList<Client> clients = new ArrayList<>();
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, phone, mail, contactPerson " +
                    " FROM " + CLIENTS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                clients.add(new Client(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)));
            }
            //closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return clients;
    }

    static void editClient(Client client)
    {
        try
        {
            pr = getConnection().prepareStatement("UPDATE " +
                    CLIENTS_TABLE +
                    " SET name=?, phone=?, mail=?, contactPerson=?" +
                    " WHERE _id = "+ client.get_id());
            pr.setString(1, client.get_name());
            pr.setString(2, client.get_phone());
            pr.setString(3, client.get_mail());
            pr.setString(4, client.get_contactPerson());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void addStaff(Staff staff)
    {
        try
        {
            pr = getConnection().prepareStatement("INSERT INTO " +
                    STAFFS_TABLE +
                    "(name, position) " +
                    "VALUES (?,?)");
            pr.setString(1, staff.get_name());
            pr.setString(2, staff.get_position());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static Staff getStaff(int idStaff)
    {
        Staff staff = null;

        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, position " +
                    " FROM " + STAFFS_TABLE +
                    " WHERE _id=" + idStaff);
            rs = pr.executeQuery();
            while (rs.next())
            {

                staff = new Staff(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3));
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return staff;
    }

    static ArrayList<Staff> getStaffsList()
    {
        ArrayList<Staff> staffs = new ArrayList<>();
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, position " +
                    " FROM " + STAFFS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {

                staffs.add(new Staff(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)));
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return staffs;
    }

    static void removeObject(int id, final String TABLE)
    {
        try
        {
            pr = getConnection().prepareStatement("DELETE FROM " +
                    TABLE +
                    " WHERE _id = " + id);
            pr.executeUpdate();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void editStaff(Staff staff)
    {
        try
        {
            pr = getConnection().prepareStatement("UPDATE " +
                    STAFFS_TABLE +
                    " SET name=?, position=? WHERE _id="+staff.get_id());
            pr.setString(1, staff.get_name());
            pr.setString(2, staff.get_position());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        }
    }

    static void addAccount(Account account)
    {
        try
        {
            pr = getConnection().prepareStatement("INSERT INTO " +
                    ACCOUNTS_TABLE +
                    "(name, position, login, password) " +
                    "VALUES (?,?,?,?)");
            pr.setString(1, account.get_name());
            pr.setString(2, account.get_position());
            pr.setString(3, account.get_login());
            pr.setString(4, account.get_password());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static Account getAccount(int idAccount)
    {
        Account account = null;

        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, position, login, password " +
                    " FROM " + ACCOUNTS_TABLE +
                    " WHERE _id=" + idAccount);
            rs = pr.executeQuery();
            while (rs.next())
            {

                account = new Account(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5));
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return account;
    }

    static ArrayList<Account> getAccountsList()
    {
        ArrayList<Account> accounts = new ArrayList<>();
        try
        {
            pr = getConnection().prepareStatement("SELECT _id, name, position, login, password " +
                    " FROM " + ACCOUNTS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {

                accounts.add(new Account(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)));
            }
            // closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return accounts;
    }

    static int getReceiptCount()
    {
        int count = 0;

        try
        {
            pr = getConnection().prepareStatement("SELECT count " +
                    " FROM " + RECEIPTCOUNTER_TABLE +
                    " WHERE _id=1");
            rs = pr.executeQuery();
            while (rs.next())
            {
                count = rs.getInt(1);
            }
            //closeConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return count;
    }

    static void setReceiptCount()
    {
        int count = getReceiptCount();
        try
        {
            pr = getConnection().prepareStatement("UPDATE " +
                    RECEIPTCOUNTER_TABLE + " SET " +
                    "count = ?" +
                    "WHERE _id=1");
            pr.setInt(1, ++count);
            pr.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    static InputStream getImage(int _idImage)
    {
        InputStream binaryStream = null;

        try
        {
            Statement st = getConnection().createStatement();
            rs = st.executeQuery("SELECT image FROM " +
                    IMAGES_TABLE + " WHERE _id = " + _idImage);
            if(rs.next())
            {
                binaryStream = rs.getBinaryStream(1);
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return binaryStream;
    }
}
