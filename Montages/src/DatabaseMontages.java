import java.io.File;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class DatabaseMontages
{
    public static File file = new File("");
    public static String path = file.getAbsolutePath();
    private static final String DB_NAME = "montages.db";
    private static final String DB_URL_home = "jdbc:sqlite://" + path + "\\OrderBasePrint_dataBase.db";
    //private static final String DB_URL = "jdbc:sqlite://" + "\\192.168.1.32\\backup\\orderBase_DB\\montages.db";
    //private static final String DB_URL = "jdbc:sqlite://" + "\\\\192.168.1.252\\inetpub\\OrderBase_DataBases\\montages.db";
    private static final String DB_URL = DataBase.URL + DB_NAME;
    static final String MONTAGES_TABLE = "montages";

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

    static void closePrRsAndConnection(PreparedStatement pr, ResultSet rs, Connection conn)
    {

        if (rs != null)
            try
            {
                rs.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        if (pr != null)
            try
            {
                pr.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        if (conn != null)
            try
            {
                conn.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    static boolean addMontage(Montage montage)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    MONTAGES_TABLE +
                    "(object, description, contactPerson, date, time, measure) " +
                    "VALUES (?,?,?,?,?,?)");
            pr.setString(1, montage.get_object());
            pr.setString(2, montage.get_description());
            pr.setString(3, montage.get_contactPerson());
            pr.setObject(4, montage.get_date().toLocalDate());
            pr.setObject(5, montage.get_time());
            pr.setBoolean(6, montage.get_measure());
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

    static boolean editMontage(Montage montage)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MONTAGES_TABLE +
                    " SET object=?, description=?, " +
                    "contactPerson=?, date=?, time=?, measure=?" +
                    "WHERE _id=" + montage.get_id());
            pr.setString(1, montage.get_object());
            pr.setString(2, montage.get_description());
            pr.setString(3, montage.get_contactPerson());
            pr.setObject(4, montage.get_date().toLocalDate());
            pr.setObject(5, montage.get_time());
            pr.setBoolean(6, montage.get_measure());
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

    static ArrayList<Montage> getMontagesList()
    {
        ArrayList<Montage> montagesList = new ArrayList<>();
        PreparedStatement pr = null;
        ResultSet rs = null;
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;
        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, object, description, " +
                    "contactPerson, date, time, measure FROM " +
                    MONTAGES_TABLE +
                    " ORDER BY time");
            rs = pr.executeQuery();
            while (rs.next())
            {
                Montage montage = new Montage();
                montage.set_id(rs.getInt(1));
                montage.set_object(rs.getString(2));
                montage.set_description(rs.getString(3));
                montage.set_contactPerson(rs.getString(4));
                if(rs.getObject(5) != null && !rs.getObject(5).toString().equals("-1"))
                {
                    montage.set_date(Date.valueOf(rs.getObject(5).toString()));
                }

                if(rs.getObject(6) != null && !rs.getObject(6).toString().equals("-1"))
                {
                    timeArr = rs.getObject(6).toString().split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 00);
                    montage.set_time(localTime);
                }
                montage.set_measure(rs.getBoolean(7));

                montagesList.add(montage);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return montagesList;
    }
}
