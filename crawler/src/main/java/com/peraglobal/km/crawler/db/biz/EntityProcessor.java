package com.peraglobal.km.crawler.db.biz;

import java.util.Map;

/**
 * <p>
 * An instance of entity processor serves an entity. It is reused throughout the
 * import process.
 */
public abstract class EntityProcessor {

  /**
   * This method is called when it starts processing an entity. When it comes
   * back to the entity it is called again. So it can reset anything at that point.
   * For a rootmost entity this is called only once for an ingestion. For sub-entities , this
   * is called multiple once for each row from its parent entity
   *
   * @param context The current context
   */
  public abstract void init(Context context);

  /**
   * This method helps streaming the data for each row . The implementation
   * would fetch as many rows as needed and gives one 'row' at a time. Only this
   * method is used during a full import
   *
   * @return A 'row'.  The 'key' for the map is the column name and the 'value'
   *         is the value of that column. If there are no more rows to be
   *         returned, return 'null'
   */
  public abstract Map<String, Object> nextRow();




  /**
   * Invoked for each entity at the very end of the import to do any needed cleanup tasks.
   * 
   */
  public abstract void destroy();

  /**
   * Invoked after the transformers are invoked. EntityProcessors can add, remove or modify values
   * added by Transformers in this method.
   *
   * @param r The transformed row
   */
  public void postTransform(Map<String, Object> r) {
  }

  /**
   * Invoked when the Entity processor is destroyed towards the end of import.
   *
   */
  public void close() {
    //no-op
  }
}
