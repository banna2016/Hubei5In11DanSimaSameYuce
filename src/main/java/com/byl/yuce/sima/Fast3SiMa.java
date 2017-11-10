package main.java.com.byl.yuce.sima;

import java.util.Date;

public class Fast3SiMa
{
  private int id;
  private String yuceIssueStart;//开始期号
  private String yuceIssueStop;
  private String drownPlan;
  private String drownIssueNumber;//中奖期
  private String drownNumber;//开奖号码
  private int drownCycle;//周期
  private int status;//中间倍数
  private Date createTime;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }
  
  public String getYuceIssueStart()
  {
    return this.yuceIssueStart;
  }
  
  public void setYuceIssueStart(String yuceIssueStart)
  {
    this.yuceIssueStart = yuceIssueStart;
  }
  
  public String getYuceIssueStop()
  {
    return this.yuceIssueStop;
  }
  
  public void setYuceIssueStop(String yuceIssueStop)
  {
    this.yuceIssueStop = yuceIssueStop;
  }
  
  public String getDrownPlan()
  {
    return this.drownPlan;
  }
  
  public void setDrownPlan(String drownPlan)
  {
    this.drownPlan = drownPlan;
  }
  
  public String getDrownIssueNumber()
  {
    return this.drownIssueNumber;
  }
  
  public void setDrownIssueNumber(String drownIssueNumber)
  {
    this.drownIssueNumber = drownIssueNumber;
  }
  
  public String getDrownNumber()
  {
    return this.drownNumber;
  }
  
  public void setDrownNumber(String drownNumber)
  {
    this.drownNumber = drownNumber;
  }
  
  public int getDrownCycle()
  {
    return this.drownCycle;
  }
  
  public void setDrownCycle(int drownCycle)
  {
    this.drownCycle = drownCycle;
  }
  
  public int getStatus()
  {
    return this.status;
  }
  
  public void setStatus(int status)
  {
    this.status = status;
  }
  
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
}
