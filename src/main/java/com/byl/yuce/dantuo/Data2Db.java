package main.java.com.byl.yuce.dantuo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import main.java.com.byl.yuce.App;
import main.java.com.byl.yuce.ConnectSrcDb;
import main.java.com.byl.yuce.LogUtil;
import main.java.com.byl.yuce.SrcDataBean;

import com.mysql.jdbc.PreparedStatement;

public class Data2Db
{
  public boolean hasRecordByIssueNumber(String issueNumber, String tbName)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    boolean flag = false;
    int count = 0;
    PreparedStatement pstmt = null;
    String sql = "SELECT count(*) count FROM " + tbName + " where issue_number = '" + issueNumber + "'";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        count = rs.getInt(1);
      }
      if (count > 0) {
        flag = true;
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "danma");
    }
    return flag;
  }
  
  public String findMaxIssueIdFromSrcDb()
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    String issueId = null;
    PreparedStatement pstmt = null;
    String sql = "SELECT max(issue_number) FROM " + App.srcNumberTbName;
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        issueId = rs.getString(1);
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "danma");
    }
    return issueId;
  }
  
  public SrcDataBean getRecordByIssueCode(String issueCode)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    SrcDataBean srcDataBean = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueCode + "'";
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
      LogUtil.error(e.getMessage(), "danma");
    }
    return srcDataBean;
  }
  
  public Fast3DanMa getYuceRecordByIssueCode(String issueCode)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    Fast3DanMa data = null;
    String sql = "SELECT ISSUE_NUMBER,DANMA_ONE,DANMA_TWO,CREATE_TIME FROM " + App.danMaTbName + " WHERE ISSUE_NUMBER = '" + issueCode + "'";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        data = new Fast3DanMa();
        data.setIssueNumber(rs.getString(1));
        data.setDanmaOne(rs.getString(2));
        data.setDanmaTwo(rs.getString(3));
        data.setCreateTime(rs.getDate(4));
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "danma");
    }
    return data;
  }
  
  public List<SrcDataBean> getLast8Record(String issueCode)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    List<SrcDataBean> srcList = new ArrayList();
    PreparedStatement pstmt = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + "  where issue_number like '%" + issueCode + "' order by issue_number desc limit 8 ";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        SrcDataBean srcDataBean = new SrcDataBean();
        srcDataBean.setIssueId(rs.getString(1));
        srcDataBean.setNo1(rs.getInt(2));
        srcDataBean.setNo2(rs.getInt(3));
        srcDataBean.setNo3(rs.getInt(4));
        srcDataBean.setNo4(rs.getInt(5));
        srcDataBean.setNo5(rs.getInt(6));
        srcList.add(srcDataBean);
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "danma");
    }
    return srcList;
  }
  
  public List<FiveInCount> getTimesForNumber(List<SrcDataBean> noList)
  {
    List<FiveInCount> fast3CountList = new ArrayList();
    
    int[] arr6 = new int[11];
    int[] arr7 = new int[11];
    int[] arr8 = new int[11];
    int i = 0;
    for (SrcDataBean no : noList)
    {//i越小，越是距离当前最近的同样期号尾号相同的开奖数据
      int[] numIntArr = { no.getNo1(), no.getNo2(), no.getNo3(), no.getNo4(), no.getNo5() };
      if (i < 6) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr6[(numIntArr[j] - 1)] += 1;
        }
      }
      if (i < 7) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr7[(numIntArr[j] - 1)] += 1;
        }
      }
      if (i < 8) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr8[(numIntArr[j] - 1)] += 1;
        }
      }
      i++;
    }
    for (int j = 0; j < 11; j++)
    {
      FiveInCount fiveInCount = new FiveInCount();
      fiveInCount.setNumber(Integer.valueOf(j + 1));
      fiveInCount.setCount1(Integer.valueOf(arr6[j]));
      fiveInCount.setCount2(Integer.valueOf(arr8[j]));
      fiveInCount.setCount3(Integer.valueOf(arr7[j]));
      fast3CountList.add(fiveInCount);
    }
    return fast3CountList;
  }
  
  private Integer[] getUniqueArr(int[] a)
  {
    List<Integer> list = new LinkedList();
    for (int i = 0; i < a.length; i++) {
      if (!list.contains(Integer.valueOf(a[i]))) {
        list.add(Integer.valueOf(a[i]));
      }
    }
    return (Integer[])list.toArray(new Integer[list.size()]);
  }
  
  public void execDanMa(String issueNumber)
    throws SQLException
  {
    String issueCode = issueNumber.substring(issueNumber.length() - 2, issueNumber.length());//截取期号最后两位序列号
    List<SrcDataBean> noList = getLast8Record(issueCode);//获取同样期号结尾的开奖期号的最近8期的开奖数据
    
    List<FiveInCount> fast3CountList = getTimesForNumber(noList);
    Collections.sort(fast3CountList);//比较顺序：8期出现次数，7期出现次数，6期出现次数
    if (!hasRecordByIssueNumber(issueNumber, App.danMaTbName)) //判断当前期号是否已经预测
    {
      insertData2Db(issueNumber, fast3CountList);
    }
  }
  
  private void insertData2Db(String issueNumber, List<FiveInCount> fast3CountList)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    String sql = "insert into " + App.danMaTbName + " (issue_number,DANMA_ONE,DANMA_TWO,CREATE_TIME) values(?,?,?,?)";
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.setString(1, issueNumber);
    pstmt.setString(2, App.translate(((FiveInCount)fast3CountList.get(0)).getNumber().intValue()) + App.translate(((FiveInCount)fast3CountList.get(1)).getNumber().intValue()) + App.translate(((FiveInCount)fast3CountList.get(2)).getNumber().intValue()));
    pstmt.setString(3, App.translate(((FiveInCount)fast3CountList.get(0)).getNumber().intValue()) + App.translate(((FiveInCount)fast3CountList.get(1)).getNumber().intValue()));
    pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
    pstmt.executeUpdate();
  }
  
  public void execDrawnPrize(String issueCode)
    throws SQLException
  {
    SrcDataBean number = getRecordByIssueCode(issueCode);//获取当前开奖号码
    

    Fast3DanMa fast3DanMa = getYuceRecordByIssueCode(issueCode);//判断当前期号是否在预测胆码表中
    if (fast3DanMa != null)
    {
      String status = judgeDownStatus(number, fast3DanMa);//判断中奖率
      updateDanMaStatus(status, number, issueCode);//将判断的中奖结果更新到预测表中
    }
  }
  
  private String judgeDownStatus(SrcDataBean number, Fast3DanMa fast3DanMa)
  {
    String status = null;
    String numStr = App.translate(number.getNo1()) + App.translate(number.getNo2()) + App.translate(number.getNo3()) + App.translate(number.getNo4()) + App.translate(number.getNo5());
    String danmaOne = fast3DanMa.getDanmaOne();
    int oneDan = 0;int twoDan = 0;
    for (int i = 0; i < danmaOne.length(); i++)
    {
      char temp = danmaOne.charAt(i);
      if ((numStr.indexOf(temp) >= 0) && (i < 2))
      {
        oneDan++;
        twoDan++;
      }
      else if (numStr.indexOf(temp) >= 0)
      {
        oneDan++;
      }
    }
    if (oneDan == 3) {
      status = "1";
    } else if (twoDan == 2) {
      status = "2";
    } else if (oneDan == 2) {
      status = "3";
    } else if (twoDan == 1) {
      status = "4";
    } else if (oneDan == 1) {
      status = "5";
    } else {
      status = "6";
    }
    return status;
  }
  
  private void updateDanMaStatus(String status, SrcDataBean srcDataBean, String issueNumber)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    String sql = "UPDATE " + App.danMaTbName + " SET status = ?,drown_number=?  where issue_number = ?";
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.setString(1, status);
    pstmt.setString(2, App.translate(srcDataBean.getNo1()) + App.translate(srcDataBean.getNo2()) + App.translate(srcDataBean.getNo3()) + App.translate(srcDataBean.getNo4()) + App.translate(srcDataBean.getNo5()));
    pstmt.setString(3, issueNumber);
    pstmt.executeUpdate();
  }
}
