package com.peraglobal.km.crawler.db.biz;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;
import static com.peraglobal.km.crawler.db.biz.DataImportException.wrapAndThrow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> A DataSource implementation which can fetch data using JDBC. </p> 
 */
public class JdbcDataSource extends
        DataSource<Iterator<Map<String, Object>>> {
	
  private static final Logger LOG = LoggerFactory.getLogger(JdbcDataSource.class);
  protected Callable<Connection> factory;
  private long connLastUsed = 0;
  private Connection conn;
  public  static Map<String, Integer> fieldNameVsType = new HashMap<String, Integer>();
 
  
  @Override
  public void init(Properties initProps) {
	  factory = createConnectionFactory(initProps);
  }
/**
 * 创建数据库链接
 * @param initProps
 * @return
 */
  protected Callable<Connection> createConnectionFactory(final Properties initProps) {
    final String jndiName = initProps.getProperty(JNDI_NAME);
    final String url = initProps.getProperty(URL);
    final String driver = initProps.getProperty(DRIVER);

    if (url == null && jndiName == null)
      throw new DataImportException(SEVERE,
              "JDBC URL or JNDI name has to be specified");

    return factory = new Callable<Connection>() {
      @Override
      public Connection call() throws Exception {
    	  
        long start = System.nanoTime();
        Connection c = null;

        if (jndiName != null) {
          c = getFromJndi(initProps, jndiName);
        } else if (url != null) {
          try {
        	Class.forName(driver);
            c = DriverManager.getConnection(url, initProps);
          } catch (SQLException e) {
        	  throw new DataImportException(SEVERE, "Exception initializing  connection", e);
          }
        }
        if (c != null) {
          try {
            initializeConnection(c, initProps);
          } catch (SQLException e) {
            try {
              c.close();
            } catch (SQLException e2) {
              LOG.warn("Exception closing connection during cleanup", e2);
            }

            throw new DataImportException(SEVERE, "Exception initializing SQL connection", e);
          }
        }
        LOG.info("Time taken for getConnection(): "
            + TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)+" ms");
        return c;
      }

      private void initializeConnection(Connection c, final Properties initProps)
          throws SQLException {
        if (Boolean.parseBoolean(initProps.getProperty("readOnly"))) {
          c.setReadOnly(true);
          // Add other sane defaults
          c.setAutoCommit(true);
          c.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
          c.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        }
        if (!Boolean.parseBoolean(initProps.getProperty("autoCommit"))) {
          c.setAutoCommit(false);
        }
        String transactionIsolation = initProps.getProperty("transactionIsolation");
        if ("TRANSACTION_READ_UNCOMMITTED".equals(transactionIsolation)) {
          c.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } else if ("TRANSACTION_READ_COMMITTED".equals(transactionIsolation)) {
          c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } else if ("TRANSACTION_REPEATABLE_READ".equals(transactionIsolation)) {
          c.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        } else if ("TRANSACTION_SERIALIZABLE".equals(transactionIsolation)) {
          c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } else if ("TRANSACTION_NONE".equals(transactionIsolation)) {
          c.setTransactionIsolation(Connection.TRANSACTION_NONE);
        }
        String holdability = initProps.getProperty("holdability");
        if ("CLOSE_CURSORS_AT_COMMIT".equals(holdability)) {
          c.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        } else if ("HOLD_CURSORS_OVER_COMMIT".equals(holdability)) {
          c.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        }
      }

      /**
       * JNDI方式
       * @param initProps
       * @param jndiName
       * @return
       * @throws NamingException
       * @throws SQLException
       */
      private Connection getFromJndi(final Properties initProps, final String jndiName) throws NamingException,
          SQLException {

        Connection c = null;
        InitialContext ctx =  new InitialContext();
        Object jndival =  ctx.lookup(jndiName);
        if (jndival instanceof javax.sql.DataSource) {
          javax.sql.DataSource dataSource = (javax.sql.DataSource) jndival;
          String user = (String) initProps.get("user");
          String pass = (String) initProps.get("password");
          if(user == null || user.trim().equals("")){
            c = dataSource.getConnection();
          } else {
            c = dataSource.getConnection(user, pass);
          }
        } else {
          throw new DataImportException(SEVERE,
                  "the jndi name : '"+jndiName +"' is not a valid javax.sql.DataSource");
        }
        return c;
      }
    };
  }
  private ResultSetIterator r ;
  @Override
  public Iterator<Map<String, Object>> getData(String query,int markSize,int fetchSize) {
     r = new ResultSetIterator(query,markSize,fetchSize);
    return r.getIterator();
  }
  public void closeAll() {
	  if(null!=r){
		  r.closeAll();
	  }
 }

  private void logError(String msg, Exception e) {
    LOG.warn(msg, e);
  }

  private List<String> readFieldNames(ResultSetMetaData metaData)
          throws SQLException {
    List<String> colNames = new ArrayList<String>();
    int count = metaData.getColumnCount();
    for (int i = 0; i < count; i++) {
      colNames.add(metaData.getColumnLabel(i + 1));
    }
    return colNames;
  }
  private Map<String,Integer> readFieldNamesVsType(ResultSetMetaData metaData)
	      throws SQLException {
	    Map<String,Integer> colNamesVsType = new HashMap<String,Integer>();
	    int count = metaData.getColumnCount();
	    for (int i = 0; i < count; i++) {
	      colNamesVsType.put(metaData.getColumnLabel(i + 1), metaData.getColumnType(i+1));
	    }
	    return colNamesVsType;
	}

  private class ResultSetIterator {
    ResultSet resultSet;

    Statement stmt = null;

    List<String> colNames;

    Iterator<Map<String, Object>> rSetIterator;
    
    public void closeAll(){
      	this.close();
      	JdbcDataSource.this.close();
      }
    public ResultSetIterator(String query,int markSize,int fetchSize) {

      try {
        Connection c = getConnection();
        stmt = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(fetchSize);
        LOG.info("Executing SQL: " + query);
        long start = System.nanoTime();
        if (stmt.execute(query)) {
          resultSet = stmt.getResultSet();
          /*int i = 0;
          while(resultSet.next()){
        	  System.out.println(resultSet.getString("id"));
        	  i++;
          }*/
    	  if(markSize>0){
        	  resultSet.absolute(markSize);  
          }else{
        	  resultSet.beforeFirst();
          }
        }
        LOG.trace("Time taken for sql :"
                + TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
        colNames = readFieldNames(resultSet.getMetaData());
        fieldNameVsType = readFieldNamesVsType(resultSet.getMetaData());
      } catch (Exception e) {
        wrapAndThrow(SEVERE, e, "Unable to execute query: " + query);
      }
      if (resultSet == null) {
        rSetIterator = new ArrayList<Map<String, Object>>().iterator();
        return;
      }
      //自定义Iterator实现
      rSetIterator = new Iterator<Map<String, Object>>() {
        @Override
        public boolean hasNext() {
          return hasnext();
        }

        @Override
        public Map<String, Object> next() {
          return getARow();
        }

        @Override
        public void remove() {/* do nothing */
        }
      };
    }

    private Iterator<Map<String, Object>> getIterator() {
      return rSetIterator;
    }

    /**
     * 获取一行数据
     * @return
     */
    private Map<String, Object> getARow() {
      if (resultSet == null)
        return null;
      Map<String, Object> result = new HashMap<String, Object>();
      for (String colName : colNames) {
        try {
            Integer type = fieldNameVsType.get(colName);
            if (type == null)
              type = Types.VARCHAR;
            switch (type) {
              case Types.INTEGER:
                result.put(colName, resultSet.getInt(colName));
                break;
              case Types.FLOAT:
                result.put(colName, resultSet.getFloat(colName));
                break;
              case Types.BIGINT:
                result.put(colName, resultSet.getLong(colName));
                break;
              case Types.DOUBLE:
                result.put(colName, resultSet.getDouble(colName));
                break;
              case Types.DATE:
                result.put(colName, resultSet.getTimestamp(colName));
                break;
              case Types.BOOLEAN:
                result.put(colName, resultSet.getBoolean(colName));
                break;
              case Types.BLOB:
                result.put(colName, null!=resultSet.getBlob(colName)? resultSet.getBlob(colName).getBinaryStream():null);
                break;
              case Types.CLOB:
            	  result.put(colName,  null!=resultSet.getClob(colName)?resultSet.getClob(colName).getAsciiStream():null);
                  break;
              default:
                result.put(colName, resultSet.getString(colName));
                break;
            }

        } catch (SQLException e) {
          logError("Error reading data ", e);
          wrapAndThrow(SEVERE, e, "Error reading data from database");
        }
      }
      return result;
    }

    private boolean hasnext() {
      if (resultSet == null)
        return false;
      try {
        if (resultSet.next()) {
          return true;
        } else {
          close();
          return false;
        }
      } catch (SQLException e) {
        close();
        wrapAndThrow(SEVERE,e);
        return false;
      }
    }


    private void close() {
      try {
        if (resultSet != null)
          resultSet.close();
        LOG.info("resultSet is close!");
        if (stmt != null)
          stmt.close();
        LOG.info("stmt is close!");
      } catch (Exception e) {
        logError("Exception while closing result set", e);
      } finally {
        resultSet = null;
        stmt = null;
      }
    }
  }
/**
 * 获取数据库connection
 * 超时重新链接 10s
 * @return
 * @throws Exception
 */
  private Connection getConnection() throws Exception {
    long currTime = System.nanoTime();
    if (currTime - connLastUsed > CONN_TIME_OUT) {
      synchronized (this) {
        Connection tmpConn = factory.call();
        closeConnection();
        connLastUsed = System.nanoTime();
        return conn = tmpConn;
      }

    } else {
      connLastUsed = currTime;
      return conn;
    }
  }

  @Override
  protected void finalize() throws Throwable {
    try {
      if(!isClosed){
        LOG.error("JdbcDataSource was not closed prior to finalize(), indicates a bug -- POSSIBLE RESOURCE LEAK!!!");
        close();
      }
    } finally {
      super.finalize();
    }
  }

  private boolean isClosed = false;

  @Override
  public void close() {
    try {
      closeConnection();
    } finally {
      isClosed = true;
    }
  }

  private void closeConnection()  {
    try {
      if (conn != null) {
        try {
          conn.commit();
        } catch(Exception ex) {
          //ignore.
        }
        conn.close();
        LOG.info("connect is close!");
      }
    } catch (Exception e) {
      LOG.error("Ignoring Error when closing connection", e);
    }
  }

  private static final long CONN_TIME_OUT = TimeUnit.NANOSECONDS.convert(10, TimeUnit.SECONDS);

  public static final String URL = "url";

  public static final String JNDI_NAME = "jndiName";

  public static final String DRIVER = "driver";

}
