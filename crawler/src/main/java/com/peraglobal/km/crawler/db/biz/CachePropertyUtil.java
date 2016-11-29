package com.peraglobal.km.crawler.db.biz;

public class CachePropertyUtil {
  public static String getAttributeValueAsString(Context context, String attr) {
    Object o = context.getSessionAttribute(attr, Context.SCOPE_ENTITY);
    if (o == null) {
      return null;
    }
    return o.toString();
  }
  
  public static Object getAttributeValue(Context context, String attr) {
    Object o = context.getSessionAttribute(attr, Context.SCOPE_ENTITY);
    if (o == null) {
      return null;
    }
    return o;
  }
  
}
