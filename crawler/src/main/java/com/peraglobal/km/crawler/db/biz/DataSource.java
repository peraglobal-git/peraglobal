package com.peraglobal.km.crawler.db.biz;

import java.util.Properties;

/**
 * <p>
 * Provides data from a source with a given query.
 * </p>
 * <p>
 * Implementation of this abstract class must provide a default no-arg constructor
 * </p>
 */
public abstract class DataSource<T> {

  /**
   * initialization properties.
   */
  public abstract void init(Properties initProps);

  /**
   * Get records for the given query.The return type depends on the
   * implementation .
   *
   * @param query The query string. It can be a SQL for JdbcDataSource or a URL
   *              for HttpDataSource or a file location for FileDataSource or a custom
   *              format for your own custom DataSource.
   * @return Depends on the implementation. For instance JdbcDataSource returns
   *         an Iterator&lt;Map &lt;String,Object&gt;&gt;
   */
  public abstract T getData(String query,int firstSize,int maxRow);

  /**
   * Cleans up resources of this DataSource after use.
   */
  public abstract void close();
}
