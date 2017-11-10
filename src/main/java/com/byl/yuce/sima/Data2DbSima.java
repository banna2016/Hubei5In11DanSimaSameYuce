package main.java.com.byl.yuce.sima;

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
import main.java.com.byl.yuce.DateUtil;
import main.java.com.byl.yuce.LogUtil;
import main.java.com.byl.yuce.SrcDataBean;
import main.java.com.byl.yuce.dantuo.FiveInCount;

import com.mysql.jdbc.PreparedStatement;

public class Data2DbSima
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
      LogUtil.error(e.getMessage(), "sima");
    }
    return flag;
  }
  
  private String getIssueCodeByIssueNumber(String issueNumber)
  {
    if ((issueNumber != null) && (issueNumber.length() > 2)) {
      return issueNumber.substring(issueNumber.length() - 2, issueNumber.length());
    }
    LogUtil.error("获取期号错误！", "sima");
    return null;
  }
  
  public void execDrawnSima(String issueNumber)
    throws SQLException
  {
    SrcDataBean srcDataBean = getRecordByIssueNumber(issueNumber);
    
    Fast3SiMa fast3SiMa = getSiMaYuceRecordByIssueCode(issueNumber);//获取当前期号所属预测范围内的预测数据
    if (fast3SiMa != null)
    {
      if (!issueNumber.equals(fast3SiMa.getDrownIssueNumber()))//判断当前开奖期号是否和中奖期号值相同
      {
        int status = judgeDownStatus(srcDataBean, fast3SiMa);//判断当前期的中奖倍数
        if (status > 0)
        {//中奖
          fast3SiMa.setDrownCycle(fast3SiMa.getDrownCycle() + 1);//如果中奖了，这个周期就是7期计划中的第几个周期中奖
          fast3SiMa.setDrownIssueNumber(issueNumber);
          fast3SiMa.setDrownNumber(App.translate(srcDataBean.getNo1()) + App.translate(srcDataBean.getNo2()) + App.translate(srcDataBean.getNo3()) + App.translate(srcDataBean.getNo4()) + App.translate(srcDataBean.getNo5()));
          fast3SiMa.setStatus(status);
          
          updateDanMaStatus(fast3SiMa);
          
          execSima(issueNumber, fast3SiMa);
        }
        else
        {
          fast3SiMa.setDrownCycle(fast3SiMa.getDrownCycle() + 1);
          fast3SiMa.setDrownIssueNumber(issueNumber);
          fast3SiMa.setDrownNumber(App.translate(srcDataBean.getNo1()) + App.translate(srcDataBean.getNo2()) + App.translate(srcDataBean.getNo3()) + App.translate(srcDataBean.getNo4()) + App.translate(srcDataBean.getNo5()));
          fast3SiMa.setStatus(status);
          updateDanMaStatus(fast3SiMa);
          if (fast3SiMa.getDrownCycle() == 7) 
          {
            execSima(issueNumber, fast3SiMa);
          }
        }
        
        
      }
      
    }
    else 
    {
      execSima(issueNumber, fast3SiMa);
    }
  }
  
  public SrcDataBean getRecordByIssueNumber(String issueNumber)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    SrcDataBean srcDataBean = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueNumber + "'";
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
      LogUtil.error(e.getMessage(), "sima");
    }
    return srcDataBean;
  }
  
  public Fast3SiMa getSiMaYuceRecordByIssueCode(String issueNumber)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    Fast3SiMa data = null;
    String sql = "SELECT ID,YUCE_ISSUE_START,YUCE_ISSUE_STOP,DROWN_PLAN,DROWN_CYCLE,DROWN_ISSUE_NUMBER  FROM " + App.simaTbName + " WHERE " + issueNumber + " BETWEEN YUCE_ISSUE_START AND YUCE_ISSUE_STOP   ORDER BY ID DESC LIMIT 1";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        data = new Fast3SiMa();
        data.setId(rs.getInt(1));
        data.setYuceIssueStart(rs.getString(2));
        data.setYuceIssueStop(rs.getString(3));
        data.setDrownPlan(rs.getString(4));
        data.setDrownCycle(rs.getInt(5));
        data.setDrownIssueNumber(rs.getString(6));
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      LogUtil.error(e.getMessage(), "sima");
    }
    return data;
  }
  
  public List<SrcDataBean> getYucePool(String issueCode)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    List<SrcDataBean> srcList = new ArrayList();
    PreparedStatement pstmt = null;
    String startDay = null;
    String endDay = null;
    String code = issueCode;
    String code1 = DateUtil.getNextIssueCodeByCurrentIssue(issueCode);//getNextIssueCodeByCurrentIssue:根据当前期号尾号获取下一期的期号尾号
    String code2 = DateUtil.getNextIssueCodeByCurrentIssue(code1);
    String code3 = DateUtil.getNextIssueCodeByCurrentIssue(code2);
    String code4 = DateUtil.getNextIssueCodeByCurrentIssue(code3);
    String code5 = DateUtil.getNextIssueCodeByCurrentIssue(code4);
    String code6 = DateUtil.getNextIssueCodeByCurrentIssue(code5);
    if (Integer.parseInt(code) > Integer.parseInt(App.lineCount) - 7)
    {
      startDay = DateUtil.getNextNDay(-2);
      endDay = DateUtil.getNextNDay(0);
    }
    else
    {
      startDay = DateUtil.getNextNDay(-3);
      endDay = DateUtil.getNextNDay(-1);
    }
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5 FROM " + App.srcNumberTbName + " WHERE substr(ISSUE_NUMBER,1,6) between '" + startDay + "' and '" + endDay + "' AND substr(ISSUE_NUMBER,7) IN ('" + code1 + "','" + code2 + "','" + code3 + "','" + code4 + "','" + code5 + "','" + code6 + "','" + code + "') ORDER BY ISSUE_NUMBER DESC";
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
      e.printStackTrace();
      LogUtil.error(e.getMessage(), "sima/");
    }
    return srcList;
  }
  
  public List<FiveInCount> getTimesForNumber(List<SrcDataBean> noList)
  {
    List<FiveInCount> fiveInCountList = new ArrayList();
    int[] arr1 = new int[11];
    int[] arr2 = new int[11];
    int[] arr3 = new int[11];
    int i = 0;
    for (SrcDataBean no : noList)
    {
      int[] numIntArr = { no.getNo1(), no.getNo2(), no.getNo3(), no.getNo4(), no.getNo5() };
      if (i < 7) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr1[(numIntArr[j] - 1)] += 1;
        }
      }
      if (i < 14) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr2[(numIntArr[j] - 1)] += 1;
        }
      }
      if (i < 21) 
      {
        for (int j = 0; j < numIntArr.length; j++) 
        {
          arr3[(numIntArr[j] - 1)] += 1;
        }
      }
      i++;
    }
    for (int j = 0; j < 11; j++)
    {
      FiveInCount fiveInCount = new FiveInCount();
      fiveInCount.setNumber(Integer.valueOf(j + 1));
      fiveInCount.setCount1(Integer.valueOf(arr1[j]));
      fiveInCount.setCount2(Integer.valueOf(arr2[j]));
      fiveInCount.setCount3(Integer.valueOf(arr3[j]));
      fiveInCountList.add(fiveInCount);
    }
    return fiveInCountList;
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
  
  public void execSima(String issueNumber, Fast3SiMa fast3SiMa)
    throws SQLException
  {
    List<SrcDataBean> noList = getYucePool(issueNumber.substring(issueNumber.length() - 2, issueNumber.length()));
    
    List<FiveInCount> fiveInCountList = getTimesForNumber(noList);
    Collections.sort(fiveInCountList);
    if ((fast3SiMa == null) || (fast3SiMa.getDrownCycle() > 0)) 
    {
      insertData2Db(issueNumber, fiveInCountList);
    }
  }
  
  private void insertData2Db(String issueNumber, List<FiveInCount> fast3CountList)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    String sql = "insert into " + App.simaTbName + " (YUCE_ISSUE_START,YUCE_ISSUE_STOP,DROWN_PLAN,CREATE_TIME) values(?,?,?,?)";
    String code1 = App.getNextIssueByCurrentIssue(issueNumber);
    String code2 = App.getNextIssueByCurrentIssue(code1);
    String code3 = App.getNextIssueByCurrentIssue(code2);
    String code4 = App.getNextIssueByCurrentIssue(code3);
    String code5 = App.getNextIssueByCurrentIssue(code4);
    String code6 = App.getNextIssueByCurrentIssue(code5);
    String code7 = App.getNextIssueByCurrentIssue(code6);
    int[] numArr = { ((FiveInCount)fast3CountList.get(0)).getNumber().intValue(), ((FiveInCount)fast3CountList.get(1)).getNumber().intValue(), ((FiveInCount)fast3CountList.get(2)).getNumber().intValue(), ((FiveInCount)fast3CountList.get(3)).getNumber().intValue() };
    
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.setString(1, code1);
    pstmt.setString(2, code7);
    pstmt.setString(3, App.translate(numArr[0]) + App.translate(numArr[1]) + App.translate(numArr[2]) + App.translate(numArr[3]));
    pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
    pstmt.executeUpdate();
  }
  
  private int judgeDownStatus(SrcDataBean number, Fast3SiMa fast3SiMa)
  {
    int count = 0;
    String numStr = App.translate(number.getNo1()) + App.translate(number.getNo2()) + App.translate(number.getNo3()) + App.translate(number.getNo4()) + App.translate(number.getNo5());
    String drownPlan = fast3SiMa.getDrownPlan();
    for (int i = 0; i < drownPlan.length(); i++)
    {
      char temp = drownPlan.charAt(i);
      if (numStr.indexOf(temp) >= 0) 
      {
        count++;
      }
    }
    int status;
    if (count == 3)
    {
      status = 1;
    }
    else
    {
      if (count == 4) 
      {
        status = 2;
      }
      else 
      {
        status = 0;
      }
    }
    return status;
  }
  
  private void updateDanMaStatus(Fast3SiMa fast3SiMa)
    throws SQLException
  {
    Connection conn = ConnectSrcDb.getSrcConnection();
    String sql = "UPDATE " + App.simaTbName + " SET DROWN_ISSUE_NUMBER=?,DROWN_NUMBER=?,STATUS = ?,DROWN_CYCLE=?  where ID = ?";
    
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.setString(1, fast3SiMa.getDrownIssueNumber());
    pstmt.setString(2, fast3SiMa.getDrownNumber());
    pstmt.setString(3, Integer.toString(fast3SiMa.getStatus()));
    pstmt.setInt(4, fast3SiMa.getDrownCycle());
    pstmt.setInt(5, fast3SiMa.getId());
    pstmt.executeUpdate();
  }
}
