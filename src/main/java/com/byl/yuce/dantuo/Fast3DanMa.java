package main.java.com.byl.yuce.dantuo;

import java.util.Date;

public class Fast3DanMa
{
  private String issueNumber;
  private String danmaOne;
  private String danmaTwo;
  private Date createTime;
  private char status;
  
  public String getIssueNumber()
  {
    return this.issueNumber;
  }
  
  public void setIssueNumber(String issueNumber)
  {
    this.issueNumber = issueNumber;
  }
  
  public String getDanmaOne()
  {
    return this.danmaOne;
  }
  
  public String getDanmaTwo()
  {
    return this.danmaTwo;
  }
  
  public void setDanmaOne(String danmaOne)
  {
    this.danmaOne = danmaOne;
  }
  
  public void setDanmaTwo(String danmaTwo)
  {
    this.danmaTwo = danmaTwo;
  }
  
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
  public char getStatus()
  {
    return this.status;
  }
  
  public void setStatus(char status)
  {
    this.status = status;
  }
}
