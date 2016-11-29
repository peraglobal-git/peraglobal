package com.peraglobal.km.crawler.db.biz;
/**
 * Exception class for all DataImport exceptions
 */
public class DataImportException extends RuntimeException {
  private int errCode;

  public boolean debugged = false;

  public static final int SEVERE = 500, WARN = 400, SKIP = 300, SKIP_ROW =301;

  public DataImportException(int err) {
    super();
    errCode = err;
  }

  public DataImportException(int err, String message) {
    super(message);
    errCode = err;
  }

  public DataImportException(int err, String message, Throwable cause) {
    super(message, cause);
    errCode = err;
  }

  public DataImportException(int err, Throwable cause) {
    super(cause);
    errCode = err;
  }

  public int getErrCode() {
    return errCode;
  }

  public static void wrapAndThrow(int err, Exception e) {
    if (e instanceof DataImportException) {
      throw (DataImportException) e;
    } else {
      throw new DataImportException(err, e);
    }
  }

  public static void wrapAndThrow(int err, Exception e, String msg) {
    if (e instanceof DataImportException) {
      throw (DataImportException) e;
    } else {
      throw new DataImportException(err, msg, e);
    }
  }
}
