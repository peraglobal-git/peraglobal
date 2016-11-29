package com.peraglobal.km.mongo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

@Component
public class ObjectParams {
	private  String javaType = "java";
	/**
     * 获取查询的参数
     * @param object
     */
    public  Map<String, Object> createParams(Object object) {
        Map<String, Object> params = new HashMap<String, Object>();

        try {
			setIntoParams(params,object, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return params;
    }

    private  void setIntoParams(Map<String, Object> params,Object object, String fatherName){
    	Field[] fields = object.getClass().getDeclaredFields();
    	for (int i = 0; i <= fields.length; i++) {
    		 Object value;
    	try {
         	boolean accessFlag=false;
             if(i==fields.length){
                 return;
             }else if(fields[i].getType().getName().contains(javaType)
             		||fields[i].getType().getName().equals("int")
             		||fields[i].getType().getName().equals("long")
             		||fields[i].getType().getName().equals("byte")
             		||fields[i].getType().getName().equals("boolean")
             		||fields[i].getType().getName().equals("[B")){
             	accessFlag = fields[i].isAccessible();
                 fields[i].setAccessible(true);
             	 String name = fields[i].getName();
					value = fields[i].get(object);
					if(fatherName != null && !fatherName.equals(" ")){
	                     name = fatherName+"."+name;
	                 }
	                 if(value != null){
	                     params.put(name, value);
	                 }
	                }else if (fields[i].getType().getName().contains("DBObject")){
	                	accessFlag = fields[i].isAccessible();
	                    fields[i].setAccessible(true);
	                	String name = fields[i].getName();
	                    value = fields[i].get(object);
	                    DBObject dbObject = (DBObject)value;
	                    @SuppressWarnings("unchecked")
						Map<String, Object> map = dbObject.toMap();
	                    for (String key : map.keySet()) {
	                    	   String keyName=name+"."+key;
	                    	   params.put(keyName, map.get(key));
	                    	  }
	                }else{
	                	accessFlag = fields[i].isAccessible();
	                    fields[i].setAccessible(true);
	                	String name = fields[i].getName();
	                    value = fields[i].get(object);
	                    if(value != null){
	                        setIntoParams(params,fields[i].get(object), name);
	                    }
	                }
             	fields[i].setAccessible(accessFlag); 
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
    	  		 
             }
    }
}
