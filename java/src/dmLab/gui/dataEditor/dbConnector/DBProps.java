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

import dmLab.utils.helpers.Props;

public class DBProps extends Props
{
    protected String user;
    protected String password;
    protected String encoding;
    protected String host;
    protected String dbName;    
    protected int DBType;
    //*********************************
    public DBProps()
    {
        super();
    }
    //*********************************
    public DBProps(String cfgFileName)
    {
        super(cfgFileName);
    }
    //*********************************
    @Override
    public boolean setDefault()
    {
        //default is mysql
        DBType=DBConnector.MYSQL;
        user = "root";
        password = "aaa";
        host = "127.0.0.1";
        encoding = "UTF-8";
        dbName="test";
        return true;
    }
    //*********************************
    @Override
    public String toString()
    {
        StringBuffer tmp=new StringBuffer();
        tmp.append("#DBProps properties").append('\n');
        tmp.append("# ORACLE=0; POSTGRES=1; MYSQL=2; MSSQL=3").append('\n');
        tmp.append("DBType = "+DBType).append('\n');
        tmp.append("host = "+host).append('\n');
        tmp.append("dbName = "+dbName).append('\n');
        tmp.append("user = "+user).append('\n');
        tmp.append("password = ").append('\n');
        //tmp.append("password = "+encodePassword(password)).append('\n');
        tmp.append("encoding = "+encoding).append('\n');        
        return tmp.toString();
    }
    //*********************************
    @Override
    public boolean updateProperties()
    {
        DBType=Integer.valueOf(prop.getProperty("DBType", Integer.toString(DBConnector.MYSQL))).intValue();
        user = prop.getProperty("user","root");
        password = prop.getProperty("password","");
        host = prop.getProperty("host","127.0.0.1");//localhost        
        encoding = prop.getProperty("encoding","UTF-8");
        dbName = prop.getProperty("dbName","test");
        return true;
    }
    //*********************************
    public String getDbName() {
        return dbName;
    }
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public int getDBType() {
        return DBType;
    }
    public void setDBType(int type) {
        DBType = type;
    }
    public String getEncoding() {
        return encoding;
    }
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    //******************
    
}
