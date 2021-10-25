import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DataBase
{
    public static File file = new File("");
    private static final String DB_NAME = "OrderBasePrint_dataBase.db";
    //static final String URL = "jdbc:sqlite://\\192.168.1.32\\backup\\orderBase_DB\\";
    //static final String URL = "jdbc:sqlite://\\192.168.1.252\\inetpub\\OrderBase_DataBases\\";
    static final String URL = "jdbc:sqlite://" + Main.getPathToDB();
    public static String path = file.getAbsolutePath();
    static final String DB_URL_home = "jdbc:sqlite://" + path + "\\OrderBasePrint_dataBase.db";
    private static final String DB_URL = URL + DB_NAME;
    //private static final String DB_URL = "jdbc:sqlite://" + "\\\\192.168.1.252\\inetpub\\OrderBase_DataBases\\OrderBasePrint_dataBase.db";
    static final String ACCOUNTS_TABLE = "accounts";
    static final String ORDERS_TABLE = "orders";
    static final String CLIENTS_TABLE = "clients";
    static final String POSITIONS_TABLE = "positions";
    static final String STAFFS_TABLE = "staffs";
    static final String RECEIPTCOUNTER_TABLE = "receiptCounter";
    static final String IMAGES_TABLE = "images";
    static final String VERSION_TABLE = "versions";
    static final String RECEIPTS_TABLE = "receipts";
    static final String DOLLAR_RATE_TABLE = "dollarRate";
    static final String PAPERFORMAT_TABLE = "paperFormat";

    static boolean testConnection()
    {
        Connection testConnection = null;
        PreparedStatement testPr = null;
        try
        {
            testConnection = DriverManager.getConnection(DB_URL);

            testPr = testConnection.prepareStatement("SELECT " +
                " _primary " +
                " FROM " +  VERSION_TABLE +
                " WHERE _id = 1");
            testPr.executeQuery();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            try
            {
                if(testPr != null)
                    testPr.close();
                if(testConnection != null)
                    testConnection.close();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return true;
    }

    static void closePrRsAndConnection(PreparedStatement pr, ResultSet rs, Connection conn)
    {

        if(rs != null)
            try
            {
                rs.close();
                System.out.println("close rs");
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        if(pr != null)
            try
            {
                pr.close();
                System.out.println("close pr");
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        if(conn != null)
            try
            {
                conn.close();
                System.out.println("close conn");
            } catch(Exception e)
            {
                e.printStackTrace();
            }
    }

    static int getPrimaryVersion()
    {
        int primaryVersion = 0;
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    " _primary " +
                    " FROM " +  VERSION_TABLE +
                    " WHERE _id = 1");
            rs = pr.executeQuery();
            while (rs.next())
            {
                primaryVersion = rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return primaryVersion;
    }

    static int getSecondaryVersion()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        int secondaryVersion = 0;

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    " _secondary" +
                    " FROM " +  VERSION_TABLE +
                    " WHERE _id = 1");
            rs = pr.executeQuery();
            while (rs.next())
            {
                secondaryVersion = rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return secondaryVersion;
    }

    static int getLastId(final String TABLE)
    {
        int lastId = -1;
        PreparedStatement pr = null;
        ResultSet rs = null;

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id " +
                    " FROM " + TABLE + " ORDER BY _id DESC LIMIT 1");
            rs = pr.executeQuery();

            if (rs.next())
                lastId = rs.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        System.out.println("lastId: " + lastId);
        return lastId;
    }

    static boolean removeObject(int id, final String TABLE)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    TABLE +
                    " WHERE _id = " + id);
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean addOrder(Order order)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    ORDERS_TABLE +
                    "(date, client, amount, payment, manager, designer, availability, remark, " +
                    "accountCreate, accountEdit, accountAvailability, " +
                    "dateTimeCreate, dateTimeEdit, dateTimeAvailability, isPrintReceipt) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            pr.setBoolean(15, order.get_isPrintReceipt());
            pr.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean editOrder(Order order)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
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
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<Order> getOrdersList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        ArrayList<Order> ordersList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, date, client, amount, " +
                    "payment, manager, designer, availability, remark, accountCreate, accountEdit, accountAvailability," +
                    " dateTimeCreate, dateTimeEdit, dateTimeAvailability, isPrintReceipt FROM " +
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
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 00);
                    order.set_dateTimeCreate(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(14) != null && !rs.getObject(14).toString().equals("-1"))
                {
                    dateTime = rs.getObject(14).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 00);
                    order.set_dateTimeEdit(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(15) != null && !rs.getObject(15).toString().equals("-1"))
                {
                    dateTime = rs.getObject(15).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 00);
                    order.set_dateTimeAvailability(LocalDateTime.of(date.toLocalDate(), localTime));
                }
                order.set_isPrintReceipt(rs.getBoolean(16));
                ordersList.add(order);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return ordersList;
    }

    static boolean addOrderPosition(OrderPosition position)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
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
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<OrderPosition> getOrderPositions(int idOrder)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        ArrayList<OrderPosition> positionsList = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, idOrder, description, quantity, issue " +
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
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return positionsList;
    }

    static ArrayList<OrderPosition> getOrderPositionsList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        ArrayList<OrderPosition> allPositions = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, idOrder, description, quantity, issue " +
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
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return allPositions;
    }

    static boolean removeOrderPositions(int idOrder)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    POSITIONS_TABLE +
                    " WHERE idOrder = " + idOrder);
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean addClient(Client client)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    CLIENTS_TABLE +
                    "(name, phone, mail, contactPerson, active) " +
                    "VALUES (?,?,?,?,?)");
            pr.setString(1, client.get_name());
            pr.setString(2, client.get_phone());
            pr.setString(3, client.get_mail());
            pr.setString(4, client.get_contactPerson());
            pr.setBoolean(5, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static ArrayList<Client> getClientsList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        ArrayList<Client> clients = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, name, phone, mail, contactPerson, active " +
                    " FROM " + CLIENTS_TABLE + " ORDER BY name ASC");
            rs = pr.executeQuery();
            while (rs.next())
            {
                clients.add(new Client(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getBoolean(6)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return clients;
    }

    static boolean editClient(Client client)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    CLIENTS_TABLE +
                    " SET name=?, phone=?, mail=?, contactPerson=?, active=?" +
                    " WHERE _id = "+ client.get_id());
            pr.setString(1, client.get_name());
            pr.setString(2, client.get_phone());
            pr.setString(3, client.get_mail());
            pr.setString(4, client.get_contactPerson());
            pr.setBoolean(5, client.is_active());
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editActive(final int id, final boolean active, final String table)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    table +
                    " SET active=?" +
                    " WHERE _id = "+ id);
            pr.setBoolean(1, active);
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean addStaff(Staff staff)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    STAFFS_TABLE +
                    "(name, position, active) " +
                    "VALUES (?,?,?)");
            pr.setString(1, staff.get_name());
            pr.setString(2, staff.get_position());
            pr.setBoolean(3, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static ArrayList<Staff> getStaffsList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        ArrayList<Staff> staffs = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, name, position, active " +
                    " FROM " + STAFFS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {

                staffs.add(new Staff(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getBoolean(4)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return staffs;
    }

    static boolean editStaff(Staff staff)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    STAFFS_TABLE +
                    " SET name=?, position=?, active=? WHERE _id="+staff.get_id());
            pr.setString(1, staff.get_name());
            pr.setString(2, staff.get_position());
            pr.setBoolean(3, staff.is_active());
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean addAccount(Account account)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        String informed = "";
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    ACCOUNTS_TABLE +
                    "(name, position, login, password, informed, staffId) " +
                    "VALUES (?,?,?,?,?,?)");
            pr.setString(1, account.get_name());
            pr.setString(2, account.get_position());
            pr.setString(3, account.get_login());
            pr.setString(4, account.get_password());
            for (int ver : account.get_informedList())
                informed += String.valueOf(ver) + "^";
            pr.setString(5, informed);
            pr.setInt(6, account.get_staffId());
            pr.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs,connection);
        }

        return true;
    }

    static void setAccountInformed(int idAccount, List<Integer> informedList)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        String informed = "";
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    ACCOUNTS_TABLE +
                    " SET informed=? " +
                    " WHERE _id = "+ idAccount);
            for (int ver : informedList)
                informed += String.valueOf(ver)+"^";
            pr.setString(1, informed);
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
    }

    static Account getAccount(int idAccount)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Account account = null;

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, position, login, password, informed, staffId " +
                    " FROM " + ACCOUNTS_TABLE +
                    " WHERE _id=" + idAccount);
            rs = pr.executeQuery();
            while (rs.next())
            {
                List<Integer> informedList = new ArrayList<>();
                String[] versList = rs.getString(6).split("\\^");
                for (String vers : versList)
                {
                    informedList.add(Integer.parseInt(vers));
                }
                account = new Account(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        informedList,
                        rs.getInt(7));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return account;
    }

    static ArrayList<Account> getAccountsList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;

        ArrayList<Account> accounts = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, position, login, password, informed, staffId " +
                    " FROM " + ACCOUNTS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                List<Integer> informedList = new ArrayList<>();
                String[] versList = rs.getString(6).split("\\^");
                for (String vers : versList)
                {
                    informedList.add(Integer.parseInt(vers));
                }
                accounts.add(new Account(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        informedList,
                        rs.getInt(7)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accounts;
    }

    static int getReceiptCount()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        int count = 0;

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT count " +
                    " FROM " + RECEIPTCOUNTER_TABLE +
                    " WHERE _id=1");
            rs = pr.executeQuery();
            while (rs.next())
            {
                count = rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return count;
    }

    static boolean setReceiptCount()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        int count = getReceiptCount();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    RECEIPTCOUNTER_TABLE + " SET " +
                    "count = ?" +
                    "WHERE _id=1");
            pr.setInt(1, ++count);
            pr.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean setReceiptPrint(int orderId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    ORDERS_TABLE +
                    " SET isPrintReceipt=?" +
                    "WHERE _id=" + orderId);
            pr.setBoolean(1, true);
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean addReceipt(Receipt receipt)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    RECEIPTS_TABLE +
                    "(idOrder, positions, manager, date, client) " +
                    "VALUES (?,?,?,?,?)");
            pr.setInt(1, receipt.get_orderId());
            pr.setString(2, receipt.get_positions());
            pr.setString(3, receipt.get_manager());
            pr.setString(4, receipt.get_date());
            pr.setString(5, receipt.get_client());
            pr.execute();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs,connection);
        }

        return true;
    }

    static ArrayList<Receipt> getReceiptsList()
    {
        PreparedStatement pr = null;
        ResultSet rs = null;

        ArrayList<Receipt> receipts = new ArrayList<>();
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    " _id, idOrder, positions, manager, date, client " +
                    " FROM " + RECEIPTS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {

                receipts.add(new Receipt(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return receipts;
    }

    static boolean setDollarRate(DollarRate dollarRate)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    DOLLAR_RATE_TABLE +
                    " SET date=?, dollar=? " +
                    " WHERE _id = 1");
            pr.setObject(1, dollarRate.get_date());
            pr.setDouble(2, dollarRate.get_dollar());
            pr.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex)
        {
            System.out.println("Ошибка соединения в setDollarRate");
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static DollarRate getDollarRate()
    {
        System.out.println("get dollarRate from DB");
        PreparedStatement pr = null;
        ResultSet rs = null;
        DollarRate dollarRate = new DollarRate();
        Date date = null;
        String dateString = "";

        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "date, dollar " +
                    " FROM " + DOLLAR_RATE_TABLE +
                    " WHERE _id = 1");
            rs = pr.executeQuery();
            while (rs.next())
            {
                if (rs.getObject(1) != null)
                {
                    dateString = rs.getObject(1).toString();
                    date = Date.valueOf(dateString);
                    dollarRate.set_date(date.toLocalDate());
                }
                dollarRate.set_dollar(rs.getDouble(2));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return dollarRate;
    }

    static InputStream getImage(int _idImage)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        InputStream binaryStream = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            Statement st = connection.createStatement();
            rs = st.executeQuery("SELECT image FROM " +
                    IMAGES_TABLE + " WHERE _id = " + _idImage);
            if(rs.next())
            {
                binaryStream = rs.getBinaryStream(1);
            }
            st.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return binaryStream;
    }

    static boolean setPaperFormat(final int format, boolean value)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    PAPERFORMAT_TABLE + " SET " +
                    "value = ?" +
                    "WHERE _id =" + format);
            pr.setBoolean(1, value);
            pr.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean getPaperFormat(final int format)
    {
        System.out.println("in getPaperFormat");
        PreparedStatement pr = null;
        ResultSet rs = null;
        boolean value = false;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT value " +
                    " FROM " + PAPERFORMAT_TABLE +
                    " WHERE _id = " + format);
            rs = pr.executeQuery();
            while (rs.next())
                value = rs.getBoolean(1);

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return value;
    }
}
