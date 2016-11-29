package com.peraglobal.km.mongo.dao;
import org.springframework.transaction.annotation.Transactional;

import com.peraglobal.km.mongo.model.KM_KnowledgeMetadata;

@Transactional
public interface MgKnowledgeMetadataDao extends MgBaseDao<KM_KnowledgeMetadata> {
	

}
