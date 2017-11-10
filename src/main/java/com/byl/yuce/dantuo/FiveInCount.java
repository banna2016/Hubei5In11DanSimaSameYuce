package main.java.com.byl.yuce.dantuo;

public class FiveInCount
  implements Comparable<FiveInCount>
{
  public String issueId;
  public Integer number;
  public Integer count3;
  public Integer count2;
  public Integer count1;
  
  public String getIssueId()
  {
    return this.issueId;
  }
  
  public void setIssueId(String issueId)
  {
    this.issueId = issueId;
  }
  
  public Integer getNumber()
  {
    return this.number;
  }
  
  public void setNumber(Integer number)
  {
    this.number = number;
  }
  
  public Integer getCount3()
  {
    return this.count3;
  }
  
  public Integer getCount2()
  {
    return this.count2;
  }
  
  public Integer getCount1()
  {
    return this.count1;
  }
  
  public void setCount3(Integer count3)
  {
    this.count3 = count3;
  }
  
  public void setCount2(Integer count2)
  {
    this.count2 = count2;
  }
  
  public void setCount1(Integer count1)
  {
    this.count1 = count1;
  }
  
  public int compareTo(FiveInCount o)
  {
    int flag = -1;
    flag = o.getCount3().compareTo(getCount3());
    if (flag == 0) {
      flag = o.getCount2().compareTo(getCount2());
    }
    if (flag == 0) {
      flag = o.getCount1().compareTo(getCount1());
    }
    if (flag == 0) {
      flag = o.getNumber().compareTo(getNumber());
    }
    return flag;
  }
}
