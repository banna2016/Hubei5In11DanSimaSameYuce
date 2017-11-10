package main.java.com.byl.yuce;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import main.java.com.byl.yuce.dantuo.Data2Db;
import main.java.com.byl.yuce.follownumber.Data2DbFollowNumber;
import main.java.com.byl.yuce.samenumber.Data2DbSameNumber;
import main.java.com.byl.yuce.sima.Data2DbSima;

public class App
{
  static Connection conn = null;
  static String maxIssueId = "";
  public static String lineCount = "0";
  public static String srcNumberTbName = null;
  public static String danMaTbName = null;
  public static String simaTbName = null;
  public static String sameNumTbName = null;
  public static String followNumTbName = null;
  public static String followIssue = null;
  public static String followIssueNumber = null;
  public static String province = null;
  
  private static void initParam()
  {
    Properties p = new Properties();
    InputStream is = App.class.getClassLoader().getResourceAsStream("db.properties");
    try
    {
      p.load(is);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    lineCount = p.getProperty("lineCount", "80");
    srcNumberTbName = p.getProperty("srcNumberTbName");
    danMaTbName = p.getProperty("danMaTbName");
    simaTbName = p.getProperty("simaTbName");
    sameNumTbName = p.getProperty("sameNumTbName");
    followNumTbName = p.getProperty("followNumTbName");
    followIssue = p.getProperty("followIssue");
    followIssueNumber = p.getProperty("followIssueNumber");
    province = p.getProperty("province");
  }
  
  public static void main(String[] args)
  {
    initParam();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
		
		@Override
		public void run() {
			try
			{
				execData();			
			}
			catch(Exception e )
			{
				e.printStackTrace();
			}
			
		}
	}, new Date(), 20000L);
  }
  
  private static void execData()
  {
    Data2Db data2Db = new Data2Db();
    String maxIssueNumber = data2Db.findMaxIssueIdFromSrcDb();
    if (!maxIssueId.equals(maxIssueNumber))
    {
      maxIssueId = maxIssueNumber;
      System.out.println(maxIssueId);
      startDanMa(maxIssueId);//胆码
      
      startSiMa(maxIssueId);//任三四码复式
      
      startSameNumber(maxIssueId);//相同开奖号码组合统计，根据开出的和当前开奖号码相同的号码的期号推理出下一期最可能会开的号码和最不可能开出的号码
      
      startHotColdNumber(maxIssueId);//号码相生相克表，（方法看了，有些业务不理解）
    }
  }
  
  private static void startDanMa(String issueNumber)
  {
    try
    {
      Data2Db data2Db = new Data2Db();
      //在预测前先判断当前期号是否已预测
      data2Db.execDrawnPrize(issueNumber);//判断当前期号是否已经预测胆码，若已预测判断胆码预测成功情况
      
      String nextIssueNumber = getNextIssueByCurrentIssue(issueNumber);//获取当前期的下一期期号
      data2Db.execDanMa(nextIssueNumber);
      LogUtil.info(issueNumber + "预测成功！", "/danma");
    }
    catch (Exception sqlEx)
    {
      sqlEx.printStackTrace();
      LogUtil.error(issueNumber + "预测失败！" + sqlEx.getMessage(), "/danma");
    }
  }
  
  public static String getNextIssueByCurrentIssue(String issueNumber)
  {
    String issueCode = issueNumber.substring(issueNumber.length() - 2, issueNumber.length());
    int issue = Integer.parseInt(issueCode);
    int nextIssue = (issue + 1) % Integer.parseInt(lineCount);
    if (nextIssue > 9) 
    {
      return issueNumber.substring(0, issueNumber.length() - 2) + nextIssue;
    }
    if (nextIssue == 0)
    {
      return issueNumber.substring(0, issueNumber.length() - 2) + lineCount;
    }
    if (nextIssue == 1) 
    {
      return DateUtil.getNextDay(issueNumber.substring(0, issueNumber.length() - 2)) + "01";
    }
    return issueNumber.substring(0, issueNumber.length() - 2) + "0" + nextIssue;
  }
  
  public static void startSiMa(String issueNumber)
  {
    try
    {
      Data2DbSima data2DbSima = new Data2DbSima();
      data2DbSima.execDrawnSima(issueNumber);
      LogUtil.info(issueNumber + "预测成功！", "/sima");
    }
    catch (Exception sqlEx)
    {
    	sqlEx.printStackTrace();
      LogUtil.info(issueNumber + "预测失败！", "/sima");
    }
  }
  
  public static void startSameNumber(String issueNumber)
  {
    try
    {
      Data2DbSameNumber data2DbSameNumber = new Data2DbSameNumber();
      data2DbSameNumber.execSameNum(issueNumber);
      LogUtil.info(issueNumber + "预测成功！", "/same");
    }
    catch (SQLException sqlEx)
    {
      sqlEx.printStackTrace();
      LogUtil.error(issueNumber + "预测失败！", "/same");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void startHotColdNumber(String issueNumber)
  {
    try
    {
      Data2DbFollowNumber data2DbFollowNumber = new Data2DbFollowNumber();
      LogUtil.info("进入冷热号分析流中...", "/follow");
      data2DbFollowNumber.execFollowNum(issueNumber);
      LogUtil.info(issueNumber + "预测成功！", "/follow");
    }
    catch (SQLException sqlEx)
    {
      sqlEx.printStackTrace();
      LogUtil.error(issueNumber + "预测失败！", "/follow");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static String translate(int temp)
  {
    String rtn = null;
    if (temp < 10) {
      rtn = temp+"";
    } else if (temp == 10) {
      rtn = "A";
    } else if (temp == 11) {
      rtn = "J";
    } else if (temp == 12) {
      rtn = "Q";
    }
    return rtn;
  }
}
