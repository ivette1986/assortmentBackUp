package com.kroger.assortment.testUtilities;

import com.kroger.assortment.InitAssortment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static com.org.yaapita.sqldao.SqlDao.executeQuery;
import static com.org.yaapita.sqldao.SqlDao.getSqlConnection;

public class DBUtils {
    protected  ResourceBundle dbQuery                 = ResourceBundle.getBundle(System.getProperty("db"));
    protected final String dbDriver;
    protected final String urlDB;
    protected final String userNameDB;
    protected final String passwordDB;


    public DBUtils(){
        this.dbDriver = dbQuery.getString("dbDriver");
        this.urlDB    = dbQuery.getString("urlDB");
        this.userNameDB = dbQuery.getString("userNameDB");
        this.passwordDB = dbQuery.getString("passwordDB");
    }



    /***
     * This method create the connection to a Postgres DB
     */


    public void DBExecuteQueries(String query) throws SQLException {
        // oAuth_Token tokenDB = new oAuth_Token();
        Connection assortmentDB = null;
        try {
            assortmentDB = getSqlConnection(dbDriver, urlDB,
                    userNameDB,
                    passwordDB);
            executeQuery(assortmentDB,query);
        } catch (Exception e) {
            System.out.println("Expection Message :" + e.getMessage());
            assortmentDB.close();
        } finally {
            assortmentDB.close();
        }
    }
/*
     /***
     * This method will execute a query and will return result in a result set
     */

    public ResultSet DBSelectQuery(String query, String dbDriver, String urlDB, String userNameDB, String passwordDB) throws SQLException{
        // oAuth_Token tokenDB = new oAuth_Token();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            assortmentDB = getSqlConnection(dbDriver, urlDB,
                    userNameDB,
                    passwordDB);
            rs=executeQuery(assortmentDB,query);
        } catch (SQLException e) {
            System.out.println("Exception Message :" + e.getMessage());
            throw e;
        } finally {
            try {
                assortmentDB.close();
            }catch (SQLException e){System.out.println("DB connection couldn't be closed: " + e.getMessage());throw e;}
        }
        return rs;
    }

    public ResultSet DBSelectQueryParams(String query, String param, String dbDriver, String urlDB, String userNameDB, String passwordDB) throws SQLException{
        // oAuth_Token tokenDB = new oAuth_Token();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            assortmentDB = getSqlConnection(dbDriver, urlDB, userNameDB,
                    passwordDB);
            PreparedStatement statement = assortmentDB.prepareStatement(query);
            if(param != null && !param.isEmpty())
                statement.setString(1,param);
            rs=statement.executeQuery();

        } catch (SQLException e) {
            System.out.println("Exception Message :" + e.getMessage());
            throw e;
        } finally {
            try {
                assortmentDB.close();
            }catch (SQLException e){System.out.println("DB connection couldn't be closed: " + e.getMessage());throw e;}
        }
        return rs;
    }


    public ResultSet DBSelectQueryParams(String query, List<String> params, String dbDriver, String urlDB, String userNameDB, String passwordDB) throws SQLException{
        // oAuth_Token tokenDB = new oAuth_Token();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            assortmentDB = getSqlConnection(dbDriver, urlDB, userNameDB,
                    passwordDB);
            PreparedStatement statement = assortmentDB.prepareStatement(query);
            if(params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++)
                    statement.setString(i + 1, params.get(i));
            }
            rs=statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Exception Message :" + e.getMessage());
            throw e;
        } finally {
            try {
                assortmentDB.close();
            }catch (SQLException e){System.out.println("DB connection couldn't be closed: " + e.getMessage());throw e;}
        }
        return rs;
    }


    public ResultSet DBSelectQueryObjectParams(String query, List<Object> params, String dbDriver, String urlDB, String userNameDB, String passwordDB) throws SQLException{
        // oAuth_Token tokenDB = new oAuth_Token();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            assortmentDB = getSqlConnection(dbDriver, urlDB, userNameDB,
                    passwordDB);
            PreparedStatement statement = assortmentDB.prepareStatement(query);
            if(params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++){
                    if(params.get(i) instanceof String)
                    statement.setString(i + 1, params.get(i).toString());
                    else if(params.get(i) instanceof  Integer)
                        statement.setInt(i + 1, Integer.valueOf(params.get(i).toString()));
                }

            }
            rs=statement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Exception Message :" + e.getMessage());
            throw e;
        } finally {
            try {
                assortmentDB.close();
            }catch (SQLException e){System.out.println("DB connection couldn't be closed: " + e.getMessage());throw e;}
        }
        return rs;
    }




    public List<String> fetchRecordSFromDB(String query, String column)
    {
        List<String> list = new ArrayList<>();
        try {

            ResultSet rs = DBSelectQuery(query, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getString(column));
            rs.close();
        }catch(SQLException e){System.out.println("Error: Data Base error: "+ e.getMessage());}
        return list;
    }

    public List<Integer> fetchIntRecordSFromDB(String query, String column)
    {
        List<Integer> list = new ArrayList<>();
        try {

            ResultSet rs = DBSelectQuery(query, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getInt(column));
            rs.close();
        }catch(SQLException e){System.out.println("Error: Data Base error: "+ e.getMessage());}
        return list;
    }

    public List<Integer> selectQueryParamsReturnInt(String query,String param,List<String> columns)
    {
        List<Integer> list = new ArrayList<Integer>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next()){
                for(String column:columns)
                    list.add(Integer.valueOf(rs.getString(column)));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }


    public List<String> DBSelectQueryParams(String query,String param,List<String> columns)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next()){
                for(String column:columns)
                    list.add(rs.getString(column));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }

    public List<String> DBSelectQueryParams(String query,String param,String column)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {

            rs=DBSelectQueryParams(query, param, this.dbDriver, this.urlDB, this.userNameDB, this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getString(column));
            rs.close();

        } catch (SQLException e) {
            System.out.println("Exception Message :" + e.getMessage());
        }
        return list;
    }


    public List<String> DBSelectQueryParams(String query,List<String> param,List<String> columns)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next()){
                for(String column:columns)
                    list.add(rs.getString(column));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }

    public List<String> DBSelectQueryParams(String query,List<String> param,String column)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getString(column));
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }


    public List<String> DBSelectQueryObjectParams(String query,List<Object> param,String column)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryObjectParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getString(column));
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }


    public List<Integer> returnIntegerDBSelectQueryObjectParams(String query,List<Object> param,String column)
    {
        List<Integer> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryObjectParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return list;
            while ( rs.next())
                list.add( rs.getInt(column));
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }

    public String getRandomDBRecord(String query,String column){
        String record = "";
        List<String> dbList = new ArrayList<String>();
        if(query == null || query.isEmpty() || column == null ||column.isEmpty())
            return record;

        dbList = fetchRecordSFromDB(query,column);
        Random rand = new Random();
        record = !dbList.isEmpty()? dbList.get(rand.nextInt(dbList.size())):"";


        return record;
    }

    public Integer getRandomIntDBRecord(String query,String column){
        int record = 0;
        List<Integer> dbList = new ArrayList<Integer>();
        if(query == null || query.isEmpty() || column == null ||column.isEmpty())
            return record;

        dbList = fetchIntRecordSFromDB(query,column);
        Random rand = new Random();
        record = !dbList.isEmpty()? dbList.get(rand.nextInt(dbList.size())):0;


        return record;
    }

    public List<String> getRandomDBRecords(int numRecords,String query,List<Object> param,String column){
       List<String> records =new ArrayList<>();
        List<String> recordsAux =new ArrayList<>();

        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryObjectParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return records;

            while ( rs.next())
                recordsAux.add(rs.getString(column).toString());
            rs.close();

            if(recordsAux.size()<numRecords) return records;

            for(int i=0; i<numRecords; i++)
                records.add(recordsAux.get(getRandomNumber(recordsAux.size())));

        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return records;
    }

    public List<Integer> getRandomIntDBRecords(int numRecords,String query,List<Object> param,String column){
        List<Integer> records =new ArrayList<>();
        List<Integer> recordsAux =new ArrayList<>();

        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs=DBSelectQueryObjectParams(query, param, this.dbDriver, this.urlDB, this.userNameDB,
                    this.passwordDB);

            if(rs == null)return records;

            while ( rs.next())
                recordsAux.add(rs.getInt(column));
            rs.close();

            if(recordsAux.size()<numRecords) return records;

            for(int i=0; i<numRecords; i++)
                records.add(recordsAux.get(getRandomNumber(recordsAux.size())));

        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return records;
    }



    public Integer getRandomNumber(int bound){
        Random rand = new Random();
        return rand.nextInt(bound);
    }



}