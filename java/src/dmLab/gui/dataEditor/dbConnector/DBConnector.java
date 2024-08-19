/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # dmLab 2003-2019
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Public License v3.0
 * # which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/gpl.html
 * # 
 * #-------------------------------------------------------------------------------
 * # @description: data mining (dmLab) library that implements MCFS-ID algorithm
 * # @author: Michal Draminski [mdramins@ipipan.waw.pl]
 * # @company: Polish Academy of Sciences - Institute of Computer Science
 * # @homepage: http://www.ipipan.eu/
 * #-------------------------------------------------------------------------------
 * # Algorithm 'SLIQ' developed by Mariusz Gromada
 * # R Package developed by Michal Draminski & Julian Zubek
 * #-------------------------------------------------------------------------------
 *******************************************************************************/
package dmLab.gui.dataEditor.dbConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnector 
{
    protected Connection connection;
    protected Statement stmtUpdateQuery;
    protected Statement stmtQuery;
    protected ResultSet rs;
    protected int dbType;
    public boolean verbose=true;

    public static int ORACLE=0;
    public static int POSTGRES=1;
    public static int MYSQL=2;
    public static int MSSQL=3;
    
    public static String[] DB_CFG_FILE_NAME=new String[]{"db_oracle.cfg","db_postgres.cfg","db_mysql.cfg","db_mssql.cfg"};
    
//  ******************************    
    public DBConnector()
    {

    }
//  *****************************
    public boolean connect(DBProps dbProps)
    {
        String connString;
        try {
            dbType=dbProps.DBType;

            if(dbType==ORACLE)
            {
                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                
                connString="jdbc:oracle:thin:"+dbProps.host+":"+dbProps.dbName;
                //connString="jdbc:oracle:thin:@moon:1521:moondb";
                
                connection = DriverManager.getConnection(connString,dbProps.user,dbProps.password);
            }
            else if(dbType==POSTGRES)
            {
                DriverManager.registerDriver(new org.postgresql.Driver());
                
                connString="jdbc:postgresql://"+dbProps.host+"/"+dbProps.dbName;
                connString+="?user="+dbProps.user+"?password="+dbProps.password;
                //connString="jdbc:postgresql://10.1.0.183/wgi?user=wgi&password=wgi";
                
                connection = DriverManager.getConnection(connString);
            }
            else if(dbType==MYSQL)
            {               
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                connString = "jdbc:mysql://" + dbProps.host + "/"+dbProps.dbName; 
                connString+="?user=" + dbProps.user + "&password=" + dbProps.password;
                connString+="&useUnicode=true" +"&characterEncoding=" + dbProps.encoding+"&characterSetResults=" + dbProps.encoding;
                connection = DriverManager.getConnection(connString);
            }
            else if(dbType==MSSQL)
            {
                DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
                connString = "jdbc:sqlserver://" + dbProps.host + ";DatabaseName="+dbProps.dbName;
                //jdbc:microsoft:sqlserver://<host_name>:<port_number>;DatabaseName=<database_name>
                connection = DriverManager.getConnection(connString,dbProps.user,dbProps.password);
            }
            stmtUpdateQuery = connection.createStatement();
            stmtQuery = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);        
        }
        catch (Exception error) {
            System.err.println("Conection Failed!");
            error.printStackTrace();
            return false;
        }
        return true;
    }
//  *******************************
    public void close()
    {
        try {
            if(rs!=null)
                rs.close();
            if(stmtQuery!=null)
                stmtQuery.close();
            if(stmtUpdateQuery!=null)
                stmtUpdateQuery.close();            
            if(connection!=null)
                connection.close();
        } catch (SQLException e) {
            System.err.println("Error in closing DB connection!");
            e.printStackTrace();
        }
    }
//  *******************************
    public ResultSet executeQuery(String mySQLquery)
    {
        try{
            rs=stmtQuery.executeQuery(mySQLquery);
        }
        catch(SQLException error){
            System.err.println("Error in query! Query: "+mySQLquery);
            error.printStackTrace();
            return null;
        }
        return rs;
    }
//  *******************************
    public boolean executeUpdateQuery(String mySQLquery)
    {
        try{
            stmtUpdateQuery.executeUpdate(mySQLquery);
        }
        catch(SQLException error){
            System.err.println("Error in query! Query: "+mySQLquery);
            error.printStackTrace();
            return false;
        }
        return true;
    }
//  *******************************    
    public static void test()
    {
        DBConnector dbConnector=new DBConnector();
        DBProps dbProps;
        
        dbProps=new DBProps("db_mysql.cfg");
        //dbProps=new DBProps("db_oracle.cfg");
        //dbProps=new DBProps("db_postgres.cfg");
        //dbProps=new DBProps("db_mssql.cfg");
        
        dbConnector.connect(dbProps);
        System.out.println(dbProps.toString());
        dbConnector.executeQuery("Select 2+4");
        System.out.print("DB OK!");        
    }
//  *****************************
    //public static void main(String[] args)
//  *****************************
}
