package com.peraglobal.km.mongo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class MongoFactory {
	@Autowired
    private ObjectParams objectParams;

	/**
     * 把实体对象转为MongoDB更新需要的值
     * @param @param obj
     * @param @return
     * @return Map<String,Object>   
     */
    public Map<String, Object> converObjectToParams(Object obj){
        Map<String, Object> map = new HashMap<String, Object>();
        Update update = new Update();
        Map<String, Object> params = objectParams.createParams(obj);
        Object id = params.get("id");
        Set<Entry<String, Object>> set = params.entrySet();
        for (Iterator<Entry<String, Object>> it = set.iterator(); it.hasNext();) {
            Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            if(!entry.getKey().equals("id")){
                update.set(entry.getKey(), entry.getValue());
            }
        }

        map.put("id", id);
        map.put("update", update);

        return map;
    }
}
