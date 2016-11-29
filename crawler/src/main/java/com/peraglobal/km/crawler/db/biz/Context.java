package com.peraglobal.km.crawler.db.biz;

import java.util.List;
import java.util.Map;

/**
 * This abstract class gives access to all available objects. So any
 * component implemented by a user can have the full power of DataImportHandler
 */
public abstract class Context {
  public static final String FULL_DUMP = "FULL_DUMP", DELTA_DUMP = "DELTA_DUMP", FIND_DELTA = "FIND_DELTA";

  /**
   * An object stored in entity scope is valid only for the current entity for the current document only.
   */
  public static final String SCOPE_ENTITY = "entity";

  /**
   * An object stored in global scope is available for the current import only but across entities and documents.
   */
  public static final String SCOPE_GLOBAL = "global";

  /**
   * An object stored in document scope is available for the current document only but across entities.
   */
  public static final String SCOPE_DOC = "document";
  
  public static final String SCOPE_DATASOURCE = "datasource";

  /**
   * Get the value of any attribute put into this entity
   *
   */
  public abstract String getEntityAttribute(String name);

  /**
   * Returns all the fields put into an entity. each item (which is a map ) in
   * the list corresponds to one field. each if the map contains the attribute
   * names and values in a field
   *
   * @return all fields in an entity
   */
  public abstract List<Map<String, String>> getAllEntityFields();


  /**
   * Gets the datasource instance defined for this entity. Do not close() this instance.
   * Transformers should use the getDataSource(String name) method.
   *
   * @return a new DataSource instance as configured for the current entity
   */
  public abstract DataSource getDataSource();


  /**
   * Returns the instance of EntityProcessor used for this entity
   *
   * @return instance of EntityProcessor used for the current entity
   */
  public abstract EntityProcessor getEntityProcessor();
  
  /**
   * Store values in a certain name and scope (entity, document,global)
   *
   * @param name  the key
   * @param val   the value
   * @param scope the scope in which the given key, value pair is to be stored
   */
  public abstract void setSessionAttribute(String name, Object val, String scope);

  /**
   * get a value by name in the given scope (entity, document,global)
   *
   * @param name  the key
   * @param scope the scope from which the value is to be retrieved
   * @return the object stored in the given scope with the given key
   */
  public abstract Object getSessionAttribute(String name, String scope);
  /**
   * Returns the current process FULL_DUMP, DELTA_DUMP, FIND_DELTA
   *
   * @return the type of the current running process
   */
  public abstract String currentProcess();

}
