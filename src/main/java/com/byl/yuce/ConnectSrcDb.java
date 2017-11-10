package main.java.com.byl.yuce;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectSrcDb
{
  private static Connection connection = null;
  
  public static synchronized Connection getSrcConnection()
  {
    try
    {
      if ((connection == null) || (connection.isClosed())) {
        connection = _getConnection();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "danma");
    }
    return connection;
  }
  
  private static Connection _getConnection()
  {
    try
    {
      String driver = "com.mysql.jdbc.Driver";
      String url = "jdbc:mysql://192.168.1.253:3306/echart3";
      String username = "echart";
      String password = "echart";
      Properties p = new Properties();
      InputStream is = ConnectSrcDb.class.getClassLoader().getResourceAsStream("db.properties");
      p.load(is);
      driver = p.getProperty("driver", driver);
      url = p.getProperty("url", url);
      username = p.getProperty("username", username);
      password = p.getProperty("password", password);
      Properties pr = new Properties();
      pr.put("user", username);
      pr.put("password", password);
      pr.put("characterEncoding", "GB2312");
      pr.put("useUnicode", "TRUE");
      Class.forName(driver).newInstance();
      LogUtil.info("源数据库连接成功！", "danma/");
      return DriverManager.getConnection(url, pr);
    }
    catch (Exception se)
    {
      se.printStackTrace();
    }
    return null;
  }
}
