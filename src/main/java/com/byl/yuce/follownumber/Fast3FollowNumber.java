package main.java.com.byl.yuce.follownumber;

public class Fast3FollowNumber
  implements Comparable<Fast3FollowNumber>
{
  public Integer number;
  public Integer followNumber;
  public Integer followCount;
  public Integer threeFollowCount;
  public Integer noFollowCount;
  public Integer threeNoFollowCount;
  public String sortStyle;
  
  public Integer getNumber()
  {
    return this.number;
  }
  
  public void setNumber(Integer number)
  {
    this.number = number;
  }
  
  public Integer getFollowNumber()
  {
    return this.followNumber;
  }
  
  public void setFollowNumber(Integer followNumber)
  {
    this.followNumber = followNumber;
  }
  
  public Integer getFollowCount()
  {
    return this.followCount;
  }
  
  public void setFollowCount(Integer followCount)
  {
    this.followCount = followCount;
  }
  
  public Integer getNoFollowCount()
  {
    return this.noFollowCount;
  }
  
  public void setNoFollowCount(Integer noFollowCount)
  {
    this.noFollowCount = noFollowCount;
  }
  
  public String getSortStyle()
  {
    return this.sortStyle;
  }
  
  public void setSortStyle(String sortStyle)
  {
    this.sortStyle = sortStyle;
  }
  
  public Integer getThreeFollowCount()
  {
    return this.threeFollowCount;
  }
  
  public void setThreeFollowCount(Integer threeFollowCount)
  {
    this.threeFollowCount = threeFollowCount;
  }
  
  public Integer getThreeNoFollowCount()
  {
    return this.threeNoFollowCount;
  }
  
  public void setThreeNoFollowCount(Integer threeNoFollowCount)
  {
    this.threeNoFollowCount = threeNoFollowCount;
  }
  
  public int compareTo(Fast3FollowNumber o)
  {
    int flag = -1;
    if ("1".equals(o.getSortStyle())) {
      flag = o.getFollowCount().compareTo(getFollowCount());
    } else if ("2".equals(o.getSortStyle())) {
      flag = o.getThreeFollowCount().compareTo(getThreeFollowCount());
    } else if ("3".equals(o.getSortStyle())) {
      flag = o.getNoFollowCount().compareTo(getNoFollowCount());
    } else {
      flag = o.getThreeNoFollowCount().compareTo(getThreeNoFollowCount());
    }
    if (flag == 0) {
      flag = o.getFollowNumber().compareTo(getFollowNumber());
    }
    return flag;
  }
}
