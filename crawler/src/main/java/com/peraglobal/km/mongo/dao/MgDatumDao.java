package com.peraglobal.km.mongo.dao;
import org.springframework.transaction.annotation.Transactional;

import com.peraglobal.km.mongo.model.KM_Datum;

@Transactional
public interface MgDatumDao extends MgBaseDao<KM_Datum> {
	

}
