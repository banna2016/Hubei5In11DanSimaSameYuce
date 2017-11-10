package main.java.com.byl.yuce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogUtil
{
  private static String basePath = "/home/server/logs/yuce/" + App.province + "/5in11/";
  
  public static synchronized void info(String info, String midPath)
  {
    /*File path = new File(basePath + midPath + "/");
    if ((!path.exists()) && (!path.isDirectory())) {
      path.mkdir();
    }
    File file = new File(basePath + midPath + "/info.log");
    if ((!file.exists()) && (!file.isFile())) {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(file, true);
      fos.write(info.getBytes());
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }*/
  }
  
  public static synchronized void error(String error, String midPath)
  {/*
    File path = new File(basePath + midPath + "/");
    if ((!path.exists()) && (!path.isDirectory())) {
      path.mkdir();
    }
    File file = new File(basePath + midPath + "/error.log");
    if ((!file.exists()) && (!file.isFile())) {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(file, true);
      fos.write(error.getBytes());
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  */}
}
