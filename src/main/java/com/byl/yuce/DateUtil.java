package main.java.com.byl.yuce;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil
{
  public static String getNextDay()
  {
    Calendar calendar = new GregorianCalendar();
    Date date = new Date();
    calendar.setTime(date);
    calendar.add(5, 1);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
    String dateString = formatter.format(date);
    return dateString;
  }
  
  public static String getNextDay(String day)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
    Calendar calendar = new GregorianCalendar();
    String dateString = null;
    try
    {
      Date date = formatter.parse(day);
      calendar.setTime(date);
      calendar.add(5, 1);
      date = calendar.getTime();
      dateString = formatter.format(date);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return dateString;
  }
  
  public static String getNextNDay(int n)
  {
    Calendar calendar = new GregorianCalendar();
    Date date = new Date();
    calendar.setTime(date);
    calendar.add(5, n);
    date = calendar.getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
    String dateString = formatter.format(date);
    return dateString;
  }
  
  public static String getNextNDayByIssueDay(String day, int n)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
    Calendar calendar = new GregorianCalendar();
    String dateString = null;
    try
    {
      Date date = formatter.parse(day);
      calendar.setTime(date);
      calendar.add(5, n);
      date = calendar.getTime();
      dateString = formatter.format(date);
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }
    return dateString;
  }
  
  public static String getNextIssueCodeByCurrentIssue(String issueCode)
  {
    String nextIssueCode = null;
    int next = (Integer.parseInt(issueCode) + 1) % Integer.parseInt(App.lineCount);
    if (next < 10)
    {
      if (next == 0) {
        nextIssueCode = App.lineCount;
      } else {
        nextIssueCode = "0" + next;
      }
    }
    else {
      nextIssueCode = Integer.toString(next);
    }
    return nextIssueCode;
  }
  
  public static String listToString(List<String> stringList)
  {
    if (stringList == null) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    boolean flag = false;
    for (String string : stringList)
    {
      if (flag) {
        result.append(",");
      } else {
        flag = true;
      }
      result.append(string);
    }
    return result.toString();
  }
}
