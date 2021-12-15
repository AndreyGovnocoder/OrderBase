import javax.swing.table.TableColumn;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBaseStorehouse
{
    public static File file = new File("");
    public static String path = file.getAbsolutePath();
    private static final String DB_NAME = "Storehouse.db";
    private static final String DB_URL_home = "jdbc:sqlite://" + path + "/";
    //private static final String DB_URL = "jdbc:sqlite://" + "\\192.168.1.32\\backup\\orderBase_DB\\Storehouse.db";
    //private static final String DB_URL = "jdbc:sqlite://" + "\\192.168.1.252\\inetpub\\OrderBase_DataBases\\Storehouse.db";
    private static final String DB_URL = DataBase.URL + DB_NAME;
    static final String MATERIALS_TABLE = "materials";
    static final String KINDS_TABLE = "kinds";
    static final String MANUFACTURERS_TABLE = "manufacturers";
    static final String COLORS_TABLE = "colors";
    static final String PROPERTIES_TABLE = "properties";
    static final String ATTRIBUTES_TABLE = "attributes";
    static final String MATERIAL_ACCOUNTINGS_TABLE = "materialAccounting";
    static final String INK_ACCOUNTING_TABLE = "inkAccounting";
    static final String MACHINES_TABLE = "machines";
    static final String INKS_TABLE = "inks";
    static final String POWERMODULES_TABLE = "powerModules";
    static final String BODIES_TABLE = "bodies";
    static final String POWERS_TABLE = "powers";
    static final String LEDS_TABLE = "leds";
    static final String CONSTRUCTIONS_TABLE = "constructions";
    static final String CONSTR_ACCOUNTINGS_TABLE = "constructionAccountings";
    static final String LED_ACCOUNTINGS_TABLE = "ledAccountings";
    static final String LED_KINDS_TABLE = "ledKinds";
    static final String POW_MODULE_ACCOUNTINGS_TABLE = "powModAccountings";
    static final String POLYGRAPHY_TABLE = "polygraphy";
    static final String POLYGRAPHY_ACCOUNTINGS_TABLE = "polygraphyAccountings";
    static final String REQUESTS_TABLE = "requests";
    static final String REQUESTS_STATUS_TABLE = "requestStatus";
    static final String REQUESTS_KINDS_TABLE = "requestKinds";
    static final String LEDSTRIP_ACCOUNTINGS_TABLE = "ledStripAccounting";
    static final String LEDSTRIP_DEGPROTECT_TABLE = "ledStripDegProtections";
    static final String LEDSTRIPS_TABLE = "ledStrips";
    static final String LEDSTRIP_TYPES_TABLE = "ledStripTypes";

    static void closePrRsAndConnection(PreparedStatement pr, ResultSet rs, Connection conn)
    {
        if(rs != null)
            try
            {
                rs.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        if(pr != null)
            try
            {
                pr.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        if(conn != null)
            try
            {
                conn.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
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
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return lastId;
    }

    static boolean changeMaterialQuantity(int materialId, int quantity)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET quantity=? WHERE _id = " + materialId);
            pr.setInt(1, quantity);
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean changeInkQuantity(int inkId, int quantity)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    INKS_TABLE +
                    " SET quantity=? WHERE _id = "+inkId);
            pr.setInt(1, quantity);
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean changeInkConsumption(Ink ink)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    INKS_TABLE +
                    " SET consumption=? WHERE _id = "+ink.get_id());
            pr.setFloat(1, ink.get_consumption());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean addMachine(Machine machine)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    MACHINES_TABLE +
                    "(name, active) " +
                    "VALUES (?,?)");
            pr.setString(1, machine.get_name());
            pr.setBoolean(2, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean editMachine(Machine machine)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MACHINES_TABLE +
                    " SET name=?, active=?" +
                    " WHERE _id = " + machine.get_id());
            pr.setString(1, machine.get_name());
            pr.setBoolean(2, machine.is_active());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteMachine(int machineId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    MACHINES_TABLE +
                    " WHERE _id = " + machineId);
            pr.executeUpdate();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<Machine> getMachinesList()
    {
        ArrayList<Machine> machines = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, name, active " +
                    " FROM " +  MACHINES_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                machines.add(new Machine(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getBoolean(3)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return machines;
    }

    static boolean addInk(Ink ink)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    INKS_TABLE +
                    "(name, color, machine, volume, consumption, quantity, active) " +
                    " VALUES (?,?,?,?,?,?,?)");
            pr.setString(1, ink.get_name());
            pr.setString(2, ink.get_color());
            pr.setInt(3, ink.get_machine());
            pr.setFloat(4, ink.get_volume());
            pr.setFloat(5, ink.get_consumption());
            pr.setInt(6, ink.get_quantity());
            pr.setBoolean(7, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean editInk(Ink ink)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    INKS_TABLE +
                    " SET name=?, color=?, machine=?, " +
                    "volume=?, consumption=?, quantity=?, active=?" +
                    " WHERE _id = "+ ink.get_id());
            pr.setString(1, ink.get_name());
            pr.setString(2, ink.get_color());
            pr.setInt(3, ink.get_machine());
            pr.setFloat(4, ink.get_volume());
            pr.setFloat(5, ink.get_consumption());
            pr.setInt(6, ink.get_quantity());
            pr.setBoolean(7, ink.is_active());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteInk(int inkId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    INKS_TABLE +
                    " WHERE _id = " + inkId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<Ink> getInksList()
    {
        ArrayList<Ink> inks = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, color, machine, volume, consumption, quantity, active " +
                    " FROM " +  INKS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                inks.add(new Ink(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getFloat(6),
                        rs.getInt(7),
                        rs.getBoolean(8)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return inks;
    }

    static boolean addMaterialsValue(String name, final String TABLE)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    TABLE +
                    "(name, active) " +
                    "VALUES (?,?)");
            pr.setString(1, name);
            pr.setBoolean(2, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editMaterialsValue(final String TABLE, MaterialsValue value)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    TABLE +
                    " SET name=?, active=? " +
                    " WHERE _id = "+ value.get_id());
            pr.setString(1, value.get_name());
            pr.setBoolean(2, value.is_active());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteMaterialsValue(final String TABLE, int valueId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    TABLE +
                    " WHERE _id = " + valueId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<MaterialsValue> getMaterialsValuesList(final String TABLE)
    {
        ArrayList<MaterialsValue> valuesList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT _id, name, active " +
                    " FROM " +  TABLE + " ORDER BY name ");
            rs = pr.executeQuery();
            while (rs.next())
            {
                valuesList.add(new MaterialsValue(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getBoolean(3)));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return valuesList;
    }

    static boolean addMaterialsKind(MaterialsKind kind)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    KINDS_TABLE +
                    "(name, manufacturer, width, height, color, " +
                    "property, thickness, attribute, colorNumber, columns, active) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            pr.setString(1, kind.get_name());
            pr.setBoolean(2, kind.get_manufacturer());
            pr.setBoolean(3, kind.get_width());
            pr.setBoolean(4, kind.get_height());
            pr.setBoolean(5, kind.get_color());
            pr.setBoolean(6, kind.get_property());
            pr.setBoolean(7, kind.get_thickness());
            pr.setBoolean(8, kind.get_attribute());
            pr.setBoolean(9, kind.get_colorNumber());
            pr.setString(10, kind.get_columns());
            pr.setBoolean(11, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean editMaterialsKind(MaterialsKind kind)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    KINDS_TABLE +
                    " SET name=?, manufacturer=?, width=?, height=?," +
                    " color=?, property=?, thickness=?, attribute=?, " +
                    "colorNumber=?, columns=?, active=?" +
                    " WHERE _id = "+ kind.get_id());
            pr.setString(1, kind.get_name());
            pr.setBoolean(2, kind.get_manufacturer());
            pr.setBoolean(3, kind.get_width());
            pr.setBoolean(4, kind.get_height());
            pr.setBoolean(5, kind.get_color());
            pr.setBoolean(6, kind.get_property());
            pr.setBoolean(7, kind.get_thickness());
            pr.setBoolean(8, kind.get_attribute());
            pr.setBoolean(9, kind.get_colorNumber());
            pr.setString(10, kind.get_columns());
            pr.setBoolean(11, kind.is_active());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static boolean deleteMaterialsKind(int kindId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement pr2 = null;
        Connection connection2 = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    KINDS_TABLE +
                    " WHERE _id = " + kindId);
            pr.executeUpdate();
        } catch (SQLException e){
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

        try
        {
            connection2 = DriverManager.getConnection(DB_URL);
            pr2 = connection2.prepareStatement("DELETE FROM " +
                    MATERIALS_TABLE +
                    " WHERE kind = " + kindId);
            pr2.executeUpdate();
        } catch (SQLException e){
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        } finally
        {
            closePrRsAndConnection(pr2, rs, connection2);
        }

        return true;
    }

    static ArrayList<MaterialsKind> getMaterialsKindsList()
    {
        ArrayList<MaterialsKind> kindsList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, manufacturer, width, height, color, " +
                    "property, thickness, attribute, colorNumber, active " +
                    " FROM " +  KINDS_TABLE + " ORDER BY name ASC");
            rs = pr.executeQuery();
            while (rs.next())
            {
                MaterialsKind kind = new MaterialsKind();
                kind.set_id(rs.getInt(1));
                kind.set_name(rs.getString(2));
                kind.set_manufacturer(rs.getBoolean(3));
                kind.set_width(rs.getBoolean(4));
                kind.set_height(rs.getBoolean(5));
                kind.set_color(rs.getBoolean(6));
                kind.set_property(rs.getBoolean(7));
                kind.set_thickness(rs.getBoolean(8));
                kind.set_attribute(rs.getBoolean(9));
                kind.set_colorNumber(rs.getBoolean(10));
                kind.set_active(rs.getBoolean(11));
                kindsList.add(kind);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return kindsList;
    }

    static boolean addMaterial(Material material)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    MATERIALS_TABLE +
                    "(kind, manufacturer, width, height, color, " +
                    "property, thickness, attribute, quantity, price, sellPrice, colorNumber, active, absence) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pr.setInt(1, material.get_kind());
            pr.setInt(2, material.get_manufacturer());
            pr.setInt(3, material.get_width());
            pr.setInt(4, material.get_height());
            pr.setInt(5, material.get_color());
            pr.setInt(6, material.get_property());
            pr.setFloat(7, material.get_thickness());
            pr.setInt(8, material.get_attribute());
            pr.setInt(9, material.get_quantity());
            pr.setInt(10, material.get_price());
            pr.setInt(11, material.get_sellPrice());
            pr.setInt(12, material.get_colorNumber());
            pr.setBoolean(13, true);
            pr.setBoolean(14, true);
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

    static boolean editMaterial(Material material)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET kind=?, manufacturer=?, width=?, height=?, color=?, " +
                    "property=?, thickness=?, attribute=?, quantity=?, price=?, " +
                    "sellPrice=?, colorNumber=?, active=?, absence=?" +
                    " WHERE _id = "+ material.get_id());
            pr.setInt(1, material.get_kind());
            pr.setInt(2, material.get_manufacturer());
            pr.setInt(3, material.get_width());
            pr.setInt(4, material.get_height());
            pr.setInt(5, material.get_color());
            pr.setInt(6, material.get_property());
            pr.setFloat(7, material.get_thickness());
            pr.setInt(8, material.get_attribute());
            pr.setInt(9, material.get_quantity());
            pr.setInt(10, material.get_price());
            pr.setInt(11, material.get_sellPrice());
            pr.setInt(12, material.get_colorNumber());
            pr.setBoolean(13, material.is_active());
            pr.setBoolean(14, material.is_absence());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteMaterial(int materialId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    MATERIALS_TABLE +
                    " WHERE _id = " + materialId);
            pr.executeUpdate();
        } catch (SQLException e){
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

        //return deleteAccountingByMaterial(materialId);
        return true;
    }

    static ArrayList<Material> getMaterialsList()
    {
        ArrayList<Material> materialsList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, kind, manufacturer, width, height, color, " +
                    "property, thickness, attribute, quantity, price, " +
                    "sellPrice, colorNumber, active, absence" +
                    " FROM " +  MATERIALS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                Material material = new Material();
                material.set_id(rs.getInt(1));
                material.set_kind(rs.getInt(2));
                material.set_manufacturer(rs.getInt(3));
                material.set_width(rs.getInt(4));
                material.set_height(rs.getInt(5));
                material.set_color(rs.getInt(6));
                material.set_property(rs.getInt(7));
                material.set_thickness(rs.getFloat(8));
                material.set_attribute(rs.getInt(9));
                material.set_quantity(rs.getInt(10));
                material.set_price(rs.getInt(11));
                material.set_sellPrice(rs.getInt(12));
                material.set_colorNumber(rs.getInt(13));
                material.set_active(rs.getBoolean(14));
                material.set_absence(rs.getBoolean(15));
                materialsList.add(material);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return materialsList;
    }

    static boolean addColumnsToKind(MaterialsKind kind, String columns)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    KINDS_TABLE +
                    " SET columns=?" +
                    " WHERE _id = "+ kind.get_id());
            pr.setString(1, columns);
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }
        return true;
    }

    static ArrayList<String> getKindColumnsArray(int kindId)
    {
        ArrayList<String> columns = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT columns " +
                    " FROM " +  KINDS_TABLE +
                    " WHERE _id = " + kindId);
            rs = pr.executeQuery();
            while (rs.next())
            {
                if(rs.getString(1) != null)
                {
                    String[] strings = rs.getString(1).split("~");
                    columns.addAll(Arrays.asList(strings));
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return columns;
    }

    static void setAttributesToZero(int attributeId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET attribute = 0" +
                    " WHERE attribute = " + attributeId);
            pr.executeUpdate();
        } catch (SQLException e)
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

    static void setColorsToZero(int colorId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET color = 0" +
                    " WHERE color = " + colorId);
            pr.executeUpdate();
        } catch (SQLException e)
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

    static void setManufacturersToZero(int manufacturerId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET manufacturer = 0" +
                    " WHERE manufacturer = " + manufacturerId);
            pr.executeUpdate();
        } catch (SQLException e)
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

    static void setPropertiesToZero(int propertyId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    MATERIALS_TABLE +
                    " SET property = 0" +
                    " WHERE property = " + propertyId);
            pr.executeUpdate();
        } catch (SQLException e)
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

    static boolean addMaterialAccounting(MaterialAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    MATERIAL_ACCOUNTINGS_TABLE +
                    "(material, accountId, quantity, procedure, dateTime) " +
                    " VALUES (?,?,?,?,?)");
            pr.setInt(1, accounting.get_material());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<MaterialAccounting> getMaterialAccountingList()
    {
        ArrayList<MaterialAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, material, accountId, quantity, procedure, dateTime" +
                    " FROM " +  MATERIAL_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                MaterialAccounting accounting = new MaterialAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_material(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));

                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static boolean addInkAccounting(InkAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    INK_ACCOUNTING_TABLE +
                    "(ink, quantity, procedure, dateTime, dateTimeOpen) " +
                    " VALUES (?,?,?,?,?)");
            pr.setInt(1, accounting.get_ink());
            pr.setInt(2, accounting.get_quantity());
            pr.setString(3, accounting.get_procedure());
            pr.setObject(4, accounting.get_dateTime());
            pr.setObject(5, accounting.get_dateTimeOpen());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editInkAccounting(InkAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    INK_ACCOUNTING_TABLE +
                    " SET ink=?, quantity=?, procedure=?, " +
                    "dateTime=?, dateTimeOpen=?, dateTimeClose=?" +
                    " WHERE _id = "+ accounting.get_id());
            pr.setInt(1, accounting.get_ink());
            pr.setInt(2, accounting.get_quantity());
            pr.setString(3, accounting.get_procedure());
            pr.setObject(4, accounting.get_dateTime());
            pr.setObject(5, accounting.get_dateTimeOpen());
            pr.setObject(6, accounting.get_dateTimeClose());
            pr.executeUpdate();
        } catch (SQLException e)
        {
            System.out.println("Ошибка SQL");
            e.printStackTrace();
            return false;
        } catch (Exception ex){
            System.out.println("Ошибка соединения");
            return false;
        }  finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<InkAccounting> getInkAccountingList()
    {
        ArrayList<InkAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, ink, quantity, procedure, dateTime, dateTimeOpen, dateTimeClose" +
                    " FROM " +  INK_ACCOUNTING_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                InkAccounting accounting = new InkAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_ink(rs.getInt(2));
                accounting.set_quantity(rs.getInt(3));
                accounting.set_procedure(rs.getString(4));

                if(rs.getObject(5) != null)
                {
                    dateTime = rs.getObject(5).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                    accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(6) != null)
                {
                    dateTime = rs.getObject(6).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                    accounting.set_dateTimeOpen(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                if(rs.getObject(7) != null)
                {
                    dateTime = rs.getObject(7).toString().split("T");
                    date = Date.valueOf(dateTime[0]);
                    timeArr = dateTime[1].split(":");
                    localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                    accounting.set_dateTimeClose(LocalDateTime.of(date.toLocalDate(), localTime));
                }

                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static boolean addPowerModule(PowerModule powerModule)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    POWERMODULES_TABLE +
                    "(name, body, power, quantity, active, price) " +
                    " VALUES (?,?,?,?,?,?)");
            pr.setString(1, powerModule.get_name());
            pr.setInt(2, powerModule.get_body());
            pr.setInt(3, powerModule.get_power());
            pr.setInt(4, powerModule.get_quantity());
            pr.setBoolean(5, true);
            pr.setDouble(6, powerModule.get_price());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editPowerModule(PowerModule powerModule)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    POWERMODULES_TABLE +
                    " SET name=?, body=?, power=?, quantity=?, active=?, price=?" +
                    " WHERE _id = "+ powerModule.get_id());
            pr.setString(1, powerModule.get_name());
            pr.setInt(2, powerModule.get_body());
            pr.setInt(3, powerModule.get_power());
            pr.setInt(4, powerModule.get_quantity());
            pr.setBoolean(5, powerModule.is_active());
            pr.setDouble(6, powerModule.get_price());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deletePowerModule(final int powerModuleId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    POWERMODULES_TABLE +
                    " WHERE _id = " + powerModuleId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<PowerModule> getPowerModulesList()
    {
        ArrayList<PowerModule> powerModules = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, body, power, quantity, active, price" +
                    " FROM " +  POWERMODULES_TABLE );
            rs = pr.executeQuery();
            while (rs.next())
            {
                PowerModule powerModule = new PowerModule();
                powerModule.set_id(rs.getInt(1));
                powerModule.set_name(rs.getString(2));
                powerModule.set_body(rs.getInt(3));
                powerModule.set_power(rs.getInt(4));
                powerModule.set_quantity(rs.getInt(5));
                powerModule.set_active(rs.getBoolean(6));
                powerModule.set_price(rs.getDouble(7));

                powerModules.add(powerModule);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return powerModules;
    }

    static boolean addPowerModuleAccounting(PowerModuleAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    POW_MODULE_ACCOUNTINGS_TABLE +
                    "(powModuleId, accountId, quantity, procedure, dateTime, remark) " +
                    " VALUES (?,?,?,?,?,?)");
            pr.setInt(1, accounting.get_powModuleId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editPowerModuleAccounting(PowerModuleAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    POW_MODULE_ACCOUNTINGS_TABLE +
                    " SET powModuleId=?, accountId=?, quantity=?, procedure=?, dateTime=?, remark=?" +
                    " WHERE _id = "+ accounting.get_id());
            pr.setInt(1, accounting.get_powModuleId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<PowerModuleAccounting> getPowerModuleAccountingList()
    {
        ArrayList<PowerModuleAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, powModuleId, accountId, quantity, procedure, dateTime, remark" +
                    " FROM " +  POW_MODULE_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                PowerModuleAccounting accounting = new PowerModuleAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_powModuleId(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));
                accounting.set_remark(rs.getString(7));
                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static boolean deletePowerModulesValue(int valueId, final String TABLE)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    TABLE +
                    " WHERE _id = " + valueId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static boolean addLed(Led led)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    LEDS_TABLE +
                    "(name, kind, luminousFlux, power, color, quantity, active, price) " +
                    " VALUES (?,?,?,?,?,?,?,?)");
            pr.setString(1, led.get_name());
            pr.setInt(2, led.get_kind());
            pr.setInt(3, led.get_luminousFlux());
            pr.setFloat(4, led.get_power());
            pr.setString(5, led.get_color());
            pr.setInt(6, led.get_quantity());
            pr.setBoolean(7, true);
            pr.setDouble(8, led.get_price());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editLed(Led led)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    LEDS_TABLE +
                    " SET name=?, kind=?, luminousFlux=?, power=?, color=?, quantity=?, active=?, price=?" +
                    " WHERE _id = "+ led.get_id());
            pr.setString(1, led.get_name());
            pr.setInt(2, led.get_kind());
            pr.setInt(3, led.get_luminousFlux());
            pr.setFloat(4, led.get_power());
            pr.setString(5, led.get_color());
            pr.setInt(6, led.get_quantity());
            pr.setBoolean(7, led.isActive());
            pr.setDouble(8, led.get_price());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteLed(int ledId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    LEDS_TABLE +
                    " WHERE _id = " + ledId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<Led> getLedsArrayList()
    {
        ArrayList<Led> ledArrayList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, kind, luminousFlux, power, color, quantity, active, price" +
                    " FROM " +  LEDS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                Led led = new Led();
                led.set_id(rs.getInt(1));
                led.set_name(rs.getString(2));
                led.set_kind(rs.getInt(3));
                led.set_luminousFlux(rs.getInt(4));
                led.set_power(rs.getFloat(5));
                led.set_color(rs.getString(6));
                led.set_quantity(rs.getInt(7));
                led.set_active(rs.getBoolean(8));
                led.set_price(rs.getDouble(9));

                ledArrayList.add(led);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return ledArrayList;
    }

    static boolean addConstruction(Construction construction)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    CONSTRUCTIONS_TABLE +
                    "(name, manufacturer, description, price, quantity) " +
                    " VALUES (?,?,?,?,?)");
            pr.setString(1, construction.get_name());
            pr.setString(2,construction.get_manufacturer());
            pr.setString(3, construction.get_description());
            pr.setInt(4, construction.get_price());
            pr.setInt(5, construction.get_quantity());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editConstruction(Construction construction)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    CONSTRUCTIONS_TABLE +
                    " SET name=?, manufacturer=?, description=?, price=?, quantity=?, active=?" +
                    " WHERE _id = "+ construction.get_id());
            pr.setString(1, construction.get_name());
            pr.setString(2, construction.get_manufacturer());
            pr.setString(3, construction.get_description());
            pr.setInt(4, construction.get_price());
            pr.setFloat(5, construction.get_quantity());
            pr.setBoolean(6, construction.isActive());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteConstruction(int constructionId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    CONSTRUCTIONS_TABLE +
                    " WHERE _id = " + constructionId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static boolean deletePolygraphy(int polygraphyId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    POLYGRAPHY_TABLE +
                    " WHERE _id = " + polygraphyId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<Construction> getConstructionsArrayList()
    {
        ArrayList<Construction> constructionsArrayList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, manufacturer, description, price, quantity, active" +
                    " FROM " +  CONSTRUCTIONS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                Construction construction = new Construction();
                construction.set_id(rs.getInt(1));
                construction.set_name(rs.getString(2));
                construction.set_manufacturer(rs.getString(3));
                construction.set_description(rs.getString(4));
                construction.set_price(rs.getInt(5));
                construction.set_quantity(rs.getInt(6));
                construction.set_active(rs.getBoolean(7));

                constructionsArrayList.add(construction);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return constructionsArrayList;
    }

    static boolean addConstructionAccounting(ConstructionAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    CONSTR_ACCOUNTINGS_TABLE +
                    "(constructionId, accountId, quantity, procedure, dateTime) " +
                    " VALUES (?,?,?,?,?)");
            pr.setInt(1, accounting.get_constructionId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<ConstructionAccounting> getConstructionAccountingList()
    {
        ArrayList<ConstructionAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, constructionId, accountId, quantity, procedure, dateTime" +
                    " FROM " +  CONSTR_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                ConstructionAccounting accounting = new ConstructionAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_constructionId(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));

                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static boolean addLedAccounting(LedAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    LED_ACCOUNTINGS_TABLE +
                    "(ledId, accountId, quantity, procedure, dateTime, remark) " +
                    " VALUES (?,?,?,?,?,?)");
            pr.setInt(1, accounting.get_ledId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editLedAccounting(LedAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    LED_ACCOUNTINGS_TABLE +
                    " SET ledId=?, accountId=?, quantity=?, procedure=?, dateTime=?, remark=?" +
                    " WHERE _id = "+ accounting.get_id());
            pr.setInt(1, accounting.get_ledId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<LedAccounting> getLedAccountingList()
    {
        ArrayList<LedAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, ledId, accountId, quantity, procedure, dateTime, remark" +
                    " FROM " +  LED_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                LedAccounting accounting = new LedAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_ledId(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));
                accounting.set_remark(rs.getString(7));
                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static boolean addPolygraphy(Polygraphy polygraphy)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    POLYGRAPHY_TABLE +
                    "(name, description, price, quantity) " +
                    " VALUES (?,?,?,?)");
            pr.setString(1, polygraphy.get_name());
            pr.setString(2, polygraphy.get_description());
            pr.setInt(3, polygraphy.get_price());
            pr.setInt(4, polygraphy.get_quantity());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editPolygraphy(Polygraphy polygraphy)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    POLYGRAPHY_TABLE +
                    " SET name=?, description=?, price=?, quantity=?, active=?" +
                    " WHERE _id = "+ polygraphy.get_id());
            pr.setString(1, polygraphy.get_name());
            pr.setString(2, polygraphy.get_description());
            pr.setInt(3, polygraphy.get_price());
            pr.setFloat(4, polygraphy.get_quantity());
            pr.setBoolean(5, polygraphy.isActive());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<Polygraphy> getPolygraphyArrayList()
    {
        ArrayList<Polygraphy> polygraphyArrayList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, name, description, price, quantity, active" +
                    " FROM " +  POLYGRAPHY_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                Polygraphy polygraphy = new Polygraphy();
                polygraphy.set_id(rs.getInt(1));
                polygraphy.set_name(rs.getString(2));
                polygraphy.set_description(rs.getString(3));
                polygraphy.set_price(rs.getInt(4));
                polygraphy.set_quantity(rs.getInt(5));
                polygraphy.set_active(rs.getBoolean(6));

                polygraphyArrayList.add(polygraphy);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return polygraphyArrayList;
    }

    static boolean addPolygraphyAccounting(PolygraphyAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    POLYGRAPHY_ACCOUNTINGS_TABLE +
                    "(polygraphyId, accountId, quantity, procedure, dateTime) " +
                    " VALUES (?,?,?,?,?)");
            pr.setInt(1, accounting.get_polygraphyId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<PolygraphyAccounting> getPolygraphyAccountingList()
    {
        ArrayList<PolygraphyAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, polygraphyId, accountId, quantity, procedure, dateTime" +
                    " FROM " +  POLYGRAPHY_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                PolygraphyAccounting accounting = new PolygraphyAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_polygraphyId(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));

                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }

    static ArrayList<RequestStatus> getRequestStatusList()
    {
        ArrayList<RequestStatus> requestStatusList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, status" +
                    " FROM " +  REQUESTS_STATUS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                RequestStatus status = new RequestStatus();
                status.set_id(rs.getInt(1));
                status.set_status(rs.getString(2));

                requestStatusList.add(status);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return requestStatusList;
    }

    static ArrayList<RequestsKind> getRequestsKindsList()
    {
        ArrayList<RequestsKind> requestsKindsList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, kind" +
                    " FROM " +  REQUESTS_KINDS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                RequestsKind kind = new RequestsKind();
                kind.set_id(rs.getInt(1));
                kind.set_kind(rs.getString(2));
                requestsKindsList.add(kind);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return requestsKindsList;
    }

    static boolean addRequest(Request request)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        request.set_dateTime(LocalDateTime.now());
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    REQUESTS_TABLE +
                    "(kind, valueId, description, status, dateTime, viewed) " +
                    " VALUES (?,?,?,?,?,?)");
            pr.setInt(1, request.get_kind());
            pr.setInt(2, request.get_valueId());
            pr.setString(3, request.get_description());
            pr.setInt(4, request.get_status());
            pr.setObject(5, request.get_dateTime());
            pr.setBoolean(6, request.is_viewed());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editRequest(Request request)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    REQUESTS_TABLE +
                    " SET kind=?, valueId=?, description=?, status=?, dateTime=?, viewed=?" +
                    " WHERE _id = "+ request.get_id());
            pr.setInt(1, request.get_kind());
            pr.setInt(2, request.get_valueId());
            pr.setString(3, request.get_description());
            pr.setInt(4, request.get_status());
            pr.setObject(5, request.get_dateTime());
            pr.setBoolean(6, request.is_viewed());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<Request> getRequestList()
    {
        ArrayList<Request> requestArrayList = new ArrayList<>();

        String[] dateTime;
        Date date;
        String[] timeArr;
        LocalTime localTime;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, kind, valueId, description, status, dateTime, viewed" +
                    " FROM " +  REQUESTS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                Request request = new Request();
                request.set_id(rs.getInt(1));
                request.set_kind(rs.getInt(2));
                request.set_valueId(rs.getInt(3));
                request.set_description(rs.getString(4));
                request.set_status(rs.getInt(5));
                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                request.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));
                request.set_viewed(rs.getBoolean(7));
                requestArrayList.add(request);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return requestArrayList;
    }

    static boolean deleteRequest(int requestId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    REQUESTS_TABLE +
                    " WHERE _id = " + requestId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static boolean addLedStrip(LedStrip newLedStrip)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    LEDSTRIPS_TABLE +
                    "(quantityAndType, powerConsump, lumFlux, colorfulTemper, color, degProtection," +
                    "quantity, price, active) " +
                    " VALUES (?,?,?,?,?,?,?,?,?)");
            pr.setInt(1, newLedStrip.get_quantityAndType());
            pr.setString(2, newLedStrip.get_powerConsump());
            pr.setInt(3, newLedStrip.get_lumFlux());
            pr.setString(4, newLedStrip.get_colorfulTemper());
            pr.setString(5, newLedStrip.get_color());
            pr.setInt(6, newLedStrip.get_degProtection());
            pr.setInt(7, newLedStrip.get_quantity());
            pr.setDouble(8, newLedStrip.get_price());
            pr.setBoolean(9, true);
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editLedStrip(LedStrip ledStrip)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    LEDSTRIPS_TABLE +
                    " SET quantityAndType=?, powerConsump=?, lumFlux=?, colorfulTemper=?, color=?, " +
                    "degProtection=?, quantity=?, price=?, active=? " +
                    " WHERE _id = " + ledStrip.get_id());
            pr.setInt(1, ledStrip.get_quantityAndType());
            pr.setString(2, ledStrip.get_powerConsump());
            pr.setInt(3, ledStrip.get_lumFlux());
            pr.setString(4, ledStrip.get_colorfulTemper());
            pr.setString(5, ledStrip.get_color());
            pr.setInt(6, ledStrip.get_degProtection());
            pr.setInt(7, ledStrip.get_quantity());
            pr.setDouble(8, ledStrip.get_price());
            pr.setBoolean(9, ledStrip.is_active());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean deleteLedStrip(int ledStripId)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("DELETE FROM " +
                    LEDSTRIPS_TABLE +
                    " WHERE _id = " + ledStripId);
            pr.executeUpdate();
        } catch (SQLException e){
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

    static ArrayList<LedStrip> getLedStripsArrayList()
    {
        ArrayList<LedStrip> ledStripArrayList = new ArrayList<>();

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, quantityAndType, powerConsump, lumFlux, colorfulTemper, color, " +
                    "degProtection, quantity, price, active" +
                    " FROM " +  LEDSTRIPS_TABLE);
            rs = pr.executeQuery();
            while (rs.next())
            {
                LedStrip ledStrip = new LedStrip();
                ledStrip.set_id(rs.getInt(1));
                ledStrip.set_quantityAndType(rs.getInt(2));
                ledStrip.set_powerConsump(rs.getString(3));
                ledStrip.set_lumFlux(rs.getInt(4));
                ledStrip.set_colorfulTemper(rs.getString(5));
                ledStrip.set_color(rs.getString(6));
                ledStrip.set_degProtection(rs.getInt(7));
                ledStrip.set_quantity(rs.getInt(8));
                ledStrip.set_price(rs.getDouble(9));
                ledStrip.set_active(rs.getBoolean(10));

                ledStripArrayList.add(ledStrip);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }
        return ledStripArrayList;
    }

    static boolean addLedStripAccounting(LedStripAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("INSERT INTO " +
                    LEDSTRIP_ACCOUNTINGS_TABLE +
                    "(ledStripId, accountId, quantity, procedure, dateTime, remark) " +
                    " VALUES (?,?,?,?,?,?)");
            pr.setInt(1, accounting.get_ledStripId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static boolean editLedStripAccounting(LedStripAccounting accounting)
    {
        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("UPDATE " +
                    LEDSTRIP_ACCOUNTINGS_TABLE +
                    " SET ledStripId=?, accountId=?, quantity=?, procedure=?, dateTime=?, remark=?" +
                    " WHERE _id = "+ accounting.get_id());
            pr.setInt(1, accounting.get_ledStripId());
            pr.setInt(2, accounting.get_accountId());
            pr.setInt(3, accounting.get_quantity());
            pr.setString(4, accounting.get_procedure());
            pr.setObject(5, accounting.get_dateTime());
            pr.setString(6, accounting.get_remark());
            pr.executeUpdate();
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
            closePrRsAndConnection(pr, rs, connection);
        }

        return true;
    }

    static ArrayList<LedStripAccounting> getLedStripAccountingList()
    {
        ArrayList<LedStripAccounting> accountingArrayList = new ArrayList<>();
        String[] dateTime = null;
        Date date = null;
        String[] timeArr = null;
        LocalTime localTime = null;

        PreparedStatement pr = null;
        ResultSet rs = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            pr = connection.prepareStatement("SELECT " +
                    "_id, ledStripId, accountId, quantity, procedure, dateTime, remark" +
                    " FROM " +  LEDSTRIP_ACCOUNTINGS_TABLE + " ORDER BY dateTime");
            rs = pr.executeQuery();
            while (rs.next())
            {
                LedStripAccounting accounting = new LedStripAccounting();
                accounting.set_id(rs.getInt(1));
                accounting.set_ledStripId(rs.getInt(2));
                accounting.set_accountId(rs.getInt(3));
                accounting.set_quantity(rs.getInt(4));
                accounting.set_procedure(rs.getString(5));

                dateTime = rs.getObject(6).toString().split("T");
                date = Date.valueOf(dateTime[0]);
                timeArr = dateTime[1].split(":");
                localTime = LocalTime.of(Integer.parseInt(timeArr[0]), Integer.parseInt(timeArr[1]), 0);
                accounting.set_dateTime(LocalDateTime.of(date.toLocalDate(), localTime));
                accounting.set_remark(rs.getString(7));
                accountingArrayList.add(accounting);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        } finally
        {
            closePrRsAndConnection(pr, rs, connection);
        }

        return accountingArrayList;
    }
}
