package main.java.com.byl.yuce.follownumber;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.com.byl.yuce.App;
import main.java.com.byl.yuce.ConnectSrcDb;
import main.java.com.byl.yuce.DateUtil;
import main.java.com.byl.yuce.LogUtil;
import main.java.com.byl.yuce.SrcDataBean;

import com.mysql.jdbc.PreparedStatement;

public class Data2DbFollowNumber
{
  public SrcDataBean getRecordByIssueNumber(String issueNumber)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    SrcDataBean srcDataBean = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5,three_span,five_sum FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueNumber + "'";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        srcDataBean = new SrcDataBean();
        srcDataBean.setIssueId(rs.getString(1));
        srcDataBean.setNo1(rs.getInt(2));
        srcDataBean.setNo2(rs.getInt(3));
        srcDataBean.setNo3(rs.getInt(4));
        srcDataBean.setNo4(rs.getInt(5));
        srcDataBean.setNo5(rs.getInt(6));
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "same/");
    }
    return srcDataBean;
  }
  
  public boolean isSerial(String currentIssueNumber)
  {
    boolean flag = true;
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    String beforeIssueNumber = null;
    String sql = "SELECT issue_number FROM " + App.followIssue;
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      if ((rs != null) && (rs.next())) {
        beforeIssueNumber = rs.getString(1);
      }
      LogUtil.info("上次计算的期数为......" + beforeIssueNumber, "hot/");
      LogUtil.info("本期为......" + currentIssueNumber, "hot/");
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
      if (beforeIssueNumber.length() == 8)
      {
        String temp = DateUtil.getNextNDayByIssueDay(
        		beforeIssueNumber.substring(0, 6), 0) 
        		+ DateUtil.getNextIssueCodeByCurrentIssue(beforeIssueNumber.substring(6, 8));
        if (!temp.equals(currentIssueNumber)) {
          flag = false;
        }
      }
      else
      {
        flag = false;
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "same/");
    }
    return flag;
  }
  
  public void initFollowIssueNumber()
    throws SQLException
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    String truncateTb = "TRUNCATE TABLE " + App.followIssueNumber;
    //查询出1~11个开奖号码分别出现在不同的5个开奖号码位置的所有期号，例如1期是1,2,3,4,5；2期是1，4,5,6,8
    String sql = "insert into " + App.followIssueNumber + " (NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5) " + 
      " SELECT ? AS NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5 FROM " + App.srcNumberTbName + " WHERE NO1=? OR NO2=? OR NO3=? OR NO4=? OR NO5=? ORDER BY ISSUE_NUMBER DESC LIMIT 30";
    srcConn.setAutoCommit(false);
    PreparedStatement pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
    pstmt.addBatch(truncateTb);
    for (int i = 1; i <= 11; i++)
    {
      pstmt.setInt(1, i);
      pstmt.setInt(2, i);
      pstmt.setInt(3, i);
      pstmt.setInt(4, i);
      pstmt.setInt(5, i);
      pstmt.setInt(6, i);
      pstmt.addBatch();
    }
    pstmt.executeBatch();
    srcConn.commit();
    pstmt.clearBatch();
    srcConn.setAutoCommit(true);
    LogUtil.info("初始化FOLLOW_ISSUE表完成.......", "hot/");
  }
  
  public List<Fast3FollowNumber> getFollowNumberListForLastIssueNumber()
  {
    List<Fast3FollowNumber> rtnList = new ArrayList();
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    Fast3FollowNumber fast3FollowNumber = null;
    String sql = "SELECT NUMBER,FOLLOW_NUMBER,FOLLOW_COUNT,THREE_FOLLOW_COUNT,NO_FOLLOW_COUNT,THREE_NO_FOLLOW_COUNT FROM " + App.followNumTbName;
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        fast3FollowNumber = new Fast3FollowNumber();
        fast3FollowNumber.setNumber(Integer.valueOf(rs.getInt(1)));
        fast3FollowNumber.setFollowNumber(Integer.valueOf(rs.getInt(2)));
        fast3FollowNumber.setFollowCount(Integer.valueOf(rs.getInt(3)));
        fast3FollowNumber.setThreeFollowCount(Integer.valueOf(rs.getInt(4)));
        fast3FollowNumber.setNoFollowCount(Integer.valueOf(rs.getInt(5)));
        fast3FollowNumber.setThreeNoFollowCount(Integer.valueOf(rs.getInt(6)));
        rtnList.add(fast3FollowNumber);
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "follow/");
    }
    return rtnList;
  }
  
  public void execFollowNum(String issueNumber)
    throws SQLException
  {
    if (!isSerial(issueNumber))//isSerial：判断是否为当天开出的期号
    {
      LogUtil.info("初始化开始.....", "hot/");
      initFollowIssueNumber();//将no1~no5分别出现1~11个开奖号码的期号都列取插入到这个表（降序30条数据）
      initAddFollow();//置T_HLJ_5IN11_FOLLOWNUMBER表中的值都为0
    }
    else
    {//非当前开出的期号
      LogUtil.info("连续计算开始.....", "hot/");
      List<Fast3FollowNumber> allList = getFollowNumberListForLastIssueNumber();//获取T_HEBEI_5IN11_FOLLOWNUMBER数据
      execAddFollow(allList, issueNumber);
      insertData2Db(allList);
    }
    updateLastIssueNumber(issueNumber);
  }
  
  private void updateLastIssueNumber(String issueNumber)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String initSql = "UPDATE " + App.followIssue + " SET ISSUE_NUMBER = '" + issueNumber + "'";
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(initSql);
    pstmt.executeUpdate();
    LogUtil.info("更新followissue完成" + initSql, "hot/");
  }
  
  private SrcDataBean getLast30IssueNumber(int i)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    SrcDataBean srcDataBean = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.followIssueNumber + " WHERE NUMBER = '" + i + "' ORDER BY ISSUE_NUMBER ASC LIMIT 1";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      if ((rs != null) && (rs.next()))
      {
        srcDataBean = new SrcDataBean();
        srcDataBean.setIssueId(rs.getString(1));
        srcDataBean.setNo1(rs.getInt(2));
        srcDataBean.setNo2(rs.getInt(3));
        srcDataBean.setNo3(rs.getInt(4));
        srcDataBean.setNo4(rs.getInt(5));
        srcDataBean.setNo5(rs.getInt(6));
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "same/");
    }
    return srcDataBean;
  }
  
  private void initAddFollow()
  {
    try
    {
      initFollowNumberTable();
      LogUtil.info("初始化FOLLOWCOUNT完成....", "hot/");
      
      List<FollowIssueNumber> numberList = getFollowIssueNumber();
      
      List<Fast3FollowNumber> followList = getFollowNumberListForLastIssueNumber();//from:T_HLJ_5IN11_FOLLOWNUMBER
      List<Fast3FollowNumber> temp = null;
      List<FollowIssueNumber> tempNumbers = null;
      for (int i = 1; i <= 11; i++)
      {
        tempNumbers = numberList.subList((i - 1) * 30, i * 30);//1~11分别有30条数据
        for (FollowIssueNumber number : tempNumbers)
        {
          int[] threeNum = { number.getNo1(), number.getNo2(), number.getNo3() };
          int[] numbers = { number.getNo1(), number.getNo2(), number.getNo3(), number.getNo4(), number.getNo5() };
          Arrays.sort(threeNum);
          Arrays.sort(numbers);
          if (Arrays.binarySearch(numbers, i) >= 0)
          {//i存在于numbers（from ：FollowIssueNumber）
            temp = followList.subList((i - 1) * 10, i * 10);//FOLLOWNUMBER:1~11分别有10条数据
            for (Fast3FollowNumber fast3FollowNumber : temp) 
            {
              execForwardAddFollow(fast3FollowNumber, threeNum, numbers);
            }
          }
        }
      }
      insertData2Db(followList);
      LogUtil.info("初始化FOLLOWCOUNT 计算完成....", "hot/");
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  private void initFollowNumberTable()
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String initSql = "UPDATE " + App.followNumTbName + " SET FOLLOW_COUNT = 0,THREE_FOLLOW_COUNT = 0, NO_FOLLOW_COUNT = 0,THREE_NO_FOLLOW_COUNT = 0";
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(initSql);
    pstmt.executeUpdate();
  }
  
  private List<FollowIssueNumber> getFollowIssueNumber()
  {
    List<FollowIssueNumber> rtnList = new ArrayList();
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String execSql = "SELECT NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5 FROM " + App.followIssueNumber + " ORDER BY NUMBER,ISSUE_NUMBER";
    PreparedStatement pstmt = null;
    FollowIssueNumber followIssueNumber = null;
    try
    {
      pstmt = (PreparedStatement)conn.prepareStatement(execSql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        followIssueNumber = new FollowIssueNumber();
        followIssueNumber.setNumber(rs.getInt(1));
        followIssueNumber.setIssueNumber(rs.getString(2));
        followIssueNumber.setNo1(rs.getInt(3));
        followIssueNumber.setNo2(rs.getInt(4));
        followIssueNumber.setNo3(rs.getInt(5));
        followIssueNumber.setNo4(rs.getInt(6));
        followIssueNumber.setNo5(rs.getInt(7));
        rtnList.add(followIssueNumber);
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "follow/");
    }
    return rtnList;
  }
  
  private void execAddFollow(List<Fast3FollowNumber> allList, String issueNumber)
    throws SQLException
  {//allList :T_HEBEI_5IN11_FOLLOWNUMBER
    List<Fast3FollowNumber> temp = null;
    SrcDataBean srcDataBean = getRecordByIssueNumber(issueNumber);
    LogUtil.info("获取当前期正常!", "hot/");
    int[] threeNum = { srcDataBean.getNo1(), srcDataBean.getNo2(), srcDataBean.getNo3() };//前三码
    //5码
    int[] numbers = { srcDataBean.getNo1(), srcDataBean.getNo2(), srcDataBean.getNo3(), srcDataBean.getNo4(), srcDataBean.getNo5() };
    Arrays.sort(threeNum);//排序
    Arrays.sort(numbers);//排序
    for (int i = 1; i <= 11; i++) 
    {
      if (Arrays.binarySearch(numbers, i) >= 0)//如果i在数组中，则返回搜索值的索引；否则返回-1或者"-"(插入点)。插入点是索引键将要插入数组的那一点，即第一个大于该键的元素索引。
      {//当前numbers存在i
        SrcDataBean last30Data = getLast30IssueNumber(i);//获取开出号码i的最早期数据
        LogUtil.info("获取第上三十期数正常!", "hot/");
        int[] lastThreeNum = { last30Data.getNo1(), last30Data.getNo2(), last30Data.getNo3() };
        int[] lastNumbers = { last30Data.getNo1(), last30Data.getNo2(), last30Data.getNo3(), last30Data.getNo4(), last30Data.getNo5() };
        Arrays.sort(lastThreeNum);
        Arrays.sort(lastNumbers);
        temp = allList.subList((i - 1) * 10, i * 10);
        for (Fast3FollowNumber fast3FollowNumber : temp)
        {
          execForwardAddFollow(fast3FollowNumber, threeNum, numbers);
          LogUtil.info("正向添加成功！", "hot/");
          
          execRevertAddFollow(fast3FollowNumber, lastThreeNum, lastNumbers);
          LogUtil.info("逆向添加成功！", "hot/");
        }
        updateLast30DataTable(srcDataBean, last30Data.getIssueId(), i);
      }
    }
  }
  
  public void updateLast30DataTable(SrcDataBean currentIssue, String last30IssueNumber, int number)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String delSql = " DELETE FROM " + App.followIssueNumber + " WHERE ISSUE_NUMBER = '" + last30IssueNumber + "' AND NUMBER = " + number;
    String insertSql = "INSERT INTO " + App.followIssueNumber + "(NUMBER,ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5)VALUES(?,?,?,?,?,?,?)";
    conn.setAutoCommit(false);
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(insertSql);
    pstmt.addBatch(delSql);
    pstmt.setInt(1, number);
    pstmt.setString(2, currentIssue.getIssueId());
    pstmt.setInt(3, currentIssue.getNo1());
    pstmt.setInt(4, currentIssue.getNo2());
    pstmt.setInt(5, currentIssue.getNo3());
    pstmt.setInt(6, currentIssue.getNo4());
    pstmt.setInt(7, currentIssue.getNo5());
    pstmt.addBatch();
    pstmt.executeBatch();
    conn.commit();
    conn.setAutoCommit(true);
    LogUtil.info("数据池删除添加成功!", "hot/");
  }
  
  private void execForwardAddFollow(Fast3FollowNumber fast3FollowNumber, int[] threeNum, int[] numbers)
  {
    if (Arrays.binarySearch(numbers, fast3FollowNumber.getFollowNumber().intValue()) >= 0)//判断除当前follownumber的number外的其他号码是否存在于当前数据中，即1的其他号码就是2~11
    {
      fast3FollowNumber.setFollowCount
      			(Integer.valueOf(fast3FollowNumber.getFollowCount().intValue() + 1));
      if (Arrays.binarySearch(threeNum, fast3FollowNumber.getFollowNumber().intValue()) >= 0) 
      {
        fast3FollowNumber.setThreeFollowCount(Integer.valueOf(fast3FollowNumber.getThreeFollowCount().intValue() + 1));
      } 
      else 
      {
        fast3FollowNumber.setThreeNoFollowCount(Integer.valueOf(fast3FollowNumber.getThreeNoFollowCount().intValue() + 1));
      }
    }
    else
    {
      fast3FollowNumber.setNoFollowCount(Integer.valueOf(fast3FollowNumber.getNoFollowCount().intValue() + 1));
      fast3FollowNumber.setThreeNoFollowCount(Integer.valueOf(fast3FollowNumber.getThreeNoFollowCount().intValue() + 1));
    }
  }
  
  private void execRevertAddFollow(Fast3FollowNumber fast3FollowNumber, int[] threeNum, int[] numbers)
  {
    if (Arrays.binarySearch(numbers, fast3FollowNumber.getFollowNumber().intValue()) >= 0)
    {
      fast3FollowNumber.setFollowCount(Integer.valueOf(fast3FollowNumber.getFollowCount().intValue() - 1));
      if (Arrays.binarySearch(threeNum, fast3FollowNumber.getFollowNumber().intValue()) >= 0) {
        fast3FollowNumber.setThreeFollowCount(Integer.valueOf(fast3FollowNumber.getThreeFollowCount().intValue() - 1));
      } else {
        fast3FollowNumber.setThreeNoFollowCount(Integer.valueOf(fast3FollowNumber.getThreeNoFollowCount().intValue() - 1));
      }
    }
    else
    {
      fast3FollowNumber.setNoFollowCount(Integer.valueOf(fast3FollowNumber.getNoFollowCount().intValue() - 1));
      fast3FollowNumber.setThreeNoFollowCount(Integer.valueOf(fast3FollowNumber.getThreeNoFollowCount().intValue() - 1));
    }
  }
  
  private static void insertData2Db(List<Fast3FollowNumber> allList)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String truncateTb = "TRUNCATE TABLE " + App.followNumTbName;
    String sql = "insert into " + App.followNumTbName + "(NUMBER,FOLLOW_NUMBER, FOLLOW_COUNT,THREE_FOLLOW_COUNT, NO_FOLLOW_COUNT,THREE_NO_FOLLOW_COUNT) values(?,?,?,?,?,?)";
    conn.setAutoCommit(false);
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.addBatch(truncateTb);
    for (Fast3FollowNumber fast3FollowNumber : allList)
    {
      pstmt.setInt(1, fast3FollowNumber.getNumber().intValue());
      pstmt.setInt(2, fast3FollowNumber.getFollowNumber().intValue());
      pstmt.setInt(3, fast3FollowNumber.getFollowCount().intValue());
      pstmt.setInt(4, fast3FollowNumber.getThreeFollowCount().intValue());
      pstmt.setInt(5, fast3FollowNumber.getNoFollowCount().intValue());
      pstmt.setInt(6, fast3FollowNumber.getThreeNoFollowCount().intValue());
      pstmt.addBatch();
    }
    pstmt.executeBatch();
    conn.commit();
    conn.setAutoCommit(true);
  }
}
