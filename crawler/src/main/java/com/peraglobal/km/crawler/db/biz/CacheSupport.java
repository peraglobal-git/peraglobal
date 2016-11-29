package com.peraglobal.km.crawler.db.biz;

import static com.peraglobal.km.crawler.db.biz.DataImportException.wrapAndThrow;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheSupport {
  private static final Logger log = LoggerFactory.getLogger(CacheSupport.class);
  private String cacheImplName;
  private Map<String,Cache> queryVsCache = new HashMap<String,Cache>();
  private Map<String,Iterator<Map<String,Object>>> queryVsCacheIterator = new HashMap<String,Iterator<Map<String,Object>>>();
  private Iterator<Map<String,Object>> dataSourceRowCache;
  
  public CacheSupport(String cacheImplName) {
    this.cacheImplName = cacheImplName;
  }
  private Cache instantiateCache(Context context) {
    Cache cache = null;
    try {
      @SuppressWarnings("unchecked")
      Class<Cache> cacheClass = (Class<Cache>) Class.forName(cacheImplName);
      Constructor<Cache> constr = cacheClass.getConstructor();
      cache = constr.newInstance();
      cache.open(context);
    } catch (Exception e) {
      throw new DataImportException(DataImportException.SEVERE,
          "Unable to load Cache implementation:" + cacheImplName, e);
    }
    return cache;
  }
  
  public void initNew() {
    dataSourceRowCache = null;
  }
  
  public void destroyAll() {
    if (queryVsCache != null) {
      for (Cache cache : queryVsCache.values()) {
        cache.destroy();
      }
    }
    queryVsCache = null;
    dataSourceRowCache = null;
  }
  
  /**
   * <p>
   * Get all the rows from the datasource for the given query and cache them
   * </p>
   */
  public void populateCache(String query,
      Iterator<Map<String,Object>> rowIterator) {
    Map<String,Object> aRow = null;
    Cache cache = queryVsCache.get(query);
    while ((aRow = getNextFromCache(query, rowIterator)) != null) {
      cache.add(aRow);
    }
  }
  
  private Map<String,Object> getNextFromCache(String query,
      Iterator<Map<String,Object>> rowIterator) {
    try {
      if (rowIterator == null) return null;
      if (rowIterator.hasNext()) return rowIterator.next();
      return null;
    } catch (Exception e) {
      wrapAndThrow(DataImportException.WARN, e);
      return null;
    }
  }
  
  public Map<String,Object> getCacheData(Context context, String query,
      Iterator<Map<String,Object>> rowIterator) {
      return getSimpleCacheData(context, query, rowIterator);
  }
  
  
  /**
   * If where clause is not present the cache is a Map of query vs List of Rows.
   * 
   * @param query
   *          string for which cached row is to be returned
   * 
   * @return the cached row corresponding to the given query
   */
  protected Map<String,Object> getSimpleCacheData(Context context,
      String query, Iterator<Map<String,Object>> rowIterator) {
    if (dataSourceRowCache == null) {      
      Cache cache = queryVsCache.get(query);      
      if (cache == null) {        
        cache = instantiateCache(context);        
        queryVsCache.put(query, cache);        
        populateCache(query, rowIterator);        
        queryVsCacheIterator.put(query, cache.iterator());        
      }      
      Iterator<Map<String,Object>> cacheIter = queryVsCacheIterator.get(query);      
      dataSourceRowCache = cacheIter;
      Map<String,Object> m = null;
      /*int i = 0;
      while(dataSourceRowCache.hasNext()){
    	 m = dataSourceRowCache.next();
    	 i++;
      }
      System.out.println(i);*/
    }
    
    return getFromRowCacheTransformed();
  }
  
  protected Map<String,Object> getFromRowCacheTransformed() {
    if (dataSourceRowCache == null || !dataSourceRowCache.hasNext()) {
      dataSourceRowCache = null;
      return null;
    }
    Map<String,Object> r = dataSourceRowCache.next();
    return r;
  }
  
  /**
   * <p>
   * Specify the class for the cache implementation
   * </p>
   */
  //public static final String CACHE_IMPL = "com.peraglobal.dataimport.CacheImp";
  public static final String CACHE_IMPL = "com.peraglobal.km.crawler.db.biz.CacheImp";

  /**
   * <p>
   * Specify the Primary Key field from this Entity to map the input records
   * with
   * </p>
   */
  public static final String CACHE_PRIMARY_KEY = "cacheKey";

}
