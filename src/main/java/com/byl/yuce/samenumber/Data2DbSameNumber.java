package main.java.com.byl.yuce.samenumber;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import main.java.com.byl.yuce.App;
import main.java.com.byl.yuce.ConnectSrcDb;
import main.java.com.byl.yuce.DateUtil;
import main.java.com.byl.yuce.LogUtil;
import main.java.com.byl.yuce.SrcDataBean;

import com.mysql.jdbc.PreparedStatement;

public class Data2DbSameNumber
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
  
  public SrcDataBean getRecordByIssueNumber(String issueNumber)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    SrcDataBean srcDataBean = null;
    String sql = "SELECT issue_number,no1,no2,no3,no4,no5,FIVE_SUM,FIVE_SPAN FROM " + App.srcNumberTbName + " WHERE ISSUE_NUMBER = '" + issueNumber + "'";
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
        srcDataBean.setFiveSum(rs.getInt(7));
        srcDataBean.setFiveSpan(rs.getInt(8));
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
  
  private List<String> getSameNumIssue(SrcDataBean srcDataBean)
  {
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    List<String> srcList = new ArrayList();
    PreparedStatement pstmt = null;
    
    String getIssueList = "SELECT ISSUE_NUMBER FROM " + App.srcNumberTbName + " WHERE NO1*NO2*NO3*NO4*NO5 = ? AND FIVE_SUM = ? AND FIVE_SPAN = ?  ORDER BY ISSUE_NUMBER DESC LIMIT 10 ";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(getIssueList);
      pstmt.setInt(1, srcDataBean.getNo1() * srcDataBean.getNo2() * srcDataBean.getNo3() * srcDataBean.getNo4() * srcDataBean.getNo5());
      pstmt.setInt(2, srcDataBean.getFiveSum());
      pstmt.setInt(3, srcDataBean.getFiveSpan());
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
      {
        String srcBean = rs.getString(1);
        srcList.add(srcBean);
      }
      if ((rs != null) && (!rs.isClosed())) {
        rs.close();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      LogUtil.error(e.getMessage(), "same/");
    }
    return srcList;
  }
  
  private List<SrcDataBean> getNextIssueRecordList(List<String> nextIssue)
  {
    List<SrcDataBean> srcList = new ArrayList();
    Connection srcConn = ConnectSrcDb.getSrcConnection();
    PreparedStatement pstmt = null;
    
    String getIssueList = "SELECT ISSUE_NUMBER,NO1,NO2,NO3,NO4,NO5 FROM  " + App.srcNumberTbName + " WHERE issue_number IN (" + DateUtil.listToString(nextIssue) + ") ORDER BY ISSUE_NUMBER DESC";
    try
    {
      pstmt = (PreparedStatement)srcConn.prepareStatement(getIssueList);
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
      LogUtil.error(e.getMessage(), "same/");
    }
    return srcList;
  }
  
  public List<Fast3SameNumber> getTimesForNumber(List<SrcDataBean> noList)
  {
    List<Fast3SameNumber> fast3CountList = new ArrayList<Fast3SameNumber>();
    int[] arr10 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    for (SrcDataBean no : noList)
    {
      int[] temp = { no.getNo1(), no.getNo2(), no.getNo3(), no.getNo4(), no.getNo5() };
      for (int j = 0; j < temp.length; j++) //循环开奖号码
      {
        arr10[(temp[j] - 1)] += 1;//计算每个开奖号码出现的次数
      }
    }
    for (int j = 0; j < 11; j++)
    {
      Fast3SameNumber fast3Count = new Fast3SameNumber();
      fast3Count.setNumber(Integer.valueOf(j + 1));
      fast3Count.setCount10(Integer.valueOf(arr10[j]));
      fast3CountList.add(fast3Count);
    }
    /*int[] arr10 = new int[12];
    int[] temp;
    int j;
    for (Iterator<SrcDataBean> localIterator = noList.iterator(); localIterator.hasNext();j < temp.length; )//j < temp.length
    {
      SrcDataBean no = (SrcDataBean)localIterator.next();
      temp = new int[] { no.getNo1(), no.getNo2(), no.getNo3(), no.getNo4(), no.getNo5() };
      
      j = 0;
      continue;
      arr10[(temp[j] - 1)] += 1;//arr10[(temp[j] - 1)] = arr10[(temp[j] - 1)] +1;
      j++;
    }
    for (int j1 = 0; j1 < 11; j1++)
    {
      Fast3SameNumber fast3Count = new Fast3SameNumber();
      fast3Count.setNumber(Integer.valueOf(j1 + 1));
      fast3Count.setCount10(Integer.valueOf(arr10[j1]));
      fast3CountList.add(fast3Count);
    }*/
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
  
  @SuppressWarnings("unchecked")
public void execSameNum(String issueNumber)
    throws SQLException
  {
    SrcDataBean srcDataBean = getRecordByIssueNumber(issueNumber);
    
    List<String> issueList = getSameNumIssue(srcDataBean);//获取与当前开奖号码相同的期号，desc排序
    if ((issueList != null) && (issueList.size() > 0))
    {
      List<String> nextIssueList = new ArrayList();
      for (String issue : issueList)
      {
        String nextIssue = App.getNextIssueByCurrentIssue(issue);//获取的当前相同开奖号码期号的下期期号
        nextIssueList.add(nextIssue);
      }
      List<SrcDataBean> noList = getNextIssueRecordList(nextIssueList);//获取所有下期开奖期号的开奖数据
      
      Object fast3CountList = getTimesForNumber(noList);//根据已经开出的同样开奖号码的下期的开奖数据推断当前开奖号码的下期开奖号码
      
      Collections.sort((List)fast3CountList);//排序（已为降序），即，开奖次数高的数字排在最后
      SrcDataBean param = new SrcDataBean();
      param.setIssueId((String)nextIssueList.get(0));
      noList.add(0, param);
      System.out.println(issueList.size());
      if (issueList.size() == 10) {
        insertData2Db(issueList, noList, (List)fast3CountList);
      }
    }
  }
  
  private void insertData2Db(List<String> issueList, List<SrcDataBean> noList, List<Fast3SameNumber> fast3CountList)
    throws SQLException
  {//issueList:当前开奖数据 noList：下期开奖数据 fast3CountList：
    Connection conn = ConnectSrcDb.getSrcConnection();
    
    String truncateTb = "TRUNCATE TABLE " + App.sameNumTbName;
    String sql = "insert into " + App.sameNumTbName + " (CURRENT_ISSUE,LOTTORY_NUMBER,NEXT_ISSUE,NEXT_LOTTORY_NUMBER,CREATE_TIME) values(?,?,?,?,?)";
    
    SrcDataBean currentRecord = getRecordByIssueNumber((String)issueList.get(0));//当前开奖期号的开奖数据
    conn.setAutoCommit(false);
    PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
    pstmt.addBatch(truncateTb);
    for (int i = noList.size() - 1; i >= 0; i--)
    {
    	//放置开出当前开奖号码的期号的下一期的开奖号码
      if (i == 0) {
    	  //当前开奖信息的下一期是预测号码
        pstmt.setString(4, App.translate(((Fast3SameNumber)fast3CountList.get(0)).getNumber().intValue()) + App.translate(((Fast3SameNumber)fast3CountList.get(1)).getNumber().intValue()) + App.translate(((Fast3SameNumber)fast3CountList.get(9)).getNumber().intValue()) + App.translate(((Fast3SameNumber)fast3CountList.get(10)).getNumber().intValue()));
      } else {
        pstmt.setString(4, App.translate(((SrcDataBean)noList.get(i)).getNo1()) + App.translate(((SrcDataBean)noList.get(i)).getNo2()) + App.translate(((SrcDataBean)noList.get(i)).getNo3()) + App.translate(((SrcDataBean)noList.get(i)).getNo4()) + App.translate(((SrcDataBean)noList.get(i)).getNo5()));
      }
      pstmt.setString(1, (String)issueList.get(i));//放置开出当前开奖号码的期号
      //放置开奖号码数据（转换10,11,12为A,J,Q）
      pstmt.setString(2, App.translate(currentRecord.getNo1()) + App.translate(currentRecord.getNo2()) + App.translate(currentRecord.getNo3()) + App.translate(currentRecord.getNo4()) + App.translate(currentRecord.getNo5()));
      //获取当前期号下一期的开奖数据的期号
      pstmt.setString(3, ((SrcDataBean)noList.get(i)).getIssueId());
      //放置添加数据时间
      pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
      pstmt.addBatch();
    }
    pstmt.executeBatch();
    conn.commit();
    pstmt.clearBatch();
    conn.setAutoCommit(true);
  }
}
