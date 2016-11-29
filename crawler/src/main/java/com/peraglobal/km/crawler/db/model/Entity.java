package com.peraglobal.km.crawler.db.model;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.peraglobal.km.crawler.db.biz.DataImportException;

/**
 * 表或视图实体
 * @author hadoop
 *
			<entity name="datatest" query="select * from datatest"  deltaQuery=""	pk="" >
          		<field name = "id" as="id" type="1"/> 
          		<field name = "name" as="name" type="12"/> 
          	</entity>
 */
public class Entity {
	private final String name;// 表名
	private final String pk;// 指定表的主键
	private final List<EntityField> fields;
	private final Map<String, Set<EntityField>> nameVsField;
	private final Map<String, String> allAttributes; // entity所有属性 name="table1" query="select * from table1"  deltaQuery=""
	private final List<Map<String, String>> allFieldAttributes;// entity中所有field属性
	private final String query;// full query


	public Entity(Element element) {

		String modName = ConfigParseUtil.getStringAttribute(element,
				ConfigNameConstants.NAME, null);
		if (modName == null) {
			throw new DataImportException(SEVERE, "Entity must have a name.");
		}
		if (modName.indexOf(".") != -1) {
			throw new DataImportException(SEVERE,
					"Entity name must not have period (.): '" + modName);
		}
		// 保留关键字
		if (ConfigNameConstants.RESERVED_WORDS.contains(modName)) {
			throw new DataImportException(SEVERE, "Entity name : '" + modName
					+ "' is a reserved keyword. Reserved words are: "
					+ ConfigNameConstants.RESERVED_WORDS);
		}
		this.name = modName;
		this.pk = ConfigParseUtil.getStringAttribute(element, "pk", null);

		Map<String, String> modAttributes = ConfigParseUtil
				.getAllAttributes(element);
		this.allAttributes = Collections.unmodifiableMap(modAttributes);

		List<Element> n = ConfigParseUtil.getChildNodes(element, "field");
		List<EntityField> modFields = new ArrayList<EntityField>(n.size());
		Map<String, Set<EntityField>> modColNameVsField = new HashMap<String, Set<EntityField>>();
		List<Map<String, String>> modAllFieldAttributes = new ArrayList<Map<String, String>>();
		for (Element elem : n) {
			EntityField.Builder fieldBuilder = new EntityField.Builder(elem);

			Set<EntityField> fieldSet = modColNameVsField
					.get(fieldBuilder.name);
			if (fieldSet == null) {
				fieldSet = new HashSet<EntityField>();
				modColNameVsField.put(fieldBuilder.name, fieldSet);
			}
			modAllFieldAttributes.add(fieldBuilder.allAttributes);
			fieldBuilder.entity = this;
			EntityField field = new EntityField(fieldBuilder);
			fieldSet.add(field);
			modFields.add(field);
		}
		Map<String, Set<EntityField>> modColNameVsField1 = new HashMap<String, Set<EntityField>>();
		for (Map.Entry<String, Set<EntityField>> entry : modColNameVsField
				.entrySet()) {
			if (entry.getValue().size() > 0) {
				modColNameVsField1.put(entry.getKey(),
						Collections.unmodifiableSet(entry.getValue()));
			}
		}
		this.nameVsField = Collections.unmodifiableMap(modColNameVsField1);
		this.fields = Collections.unmodifiableList(modFields);
		this.allFieldAttributes = Collections
				.unmodifiableList(modAllFieldAttributes);
		this.query = ConfigParseUtil.getStringAttribute(element,
				ConfigNameConstants.QUERY, null);

	}

	public String getPk() {
		return pk;
	}

	public String getQuery() {
		return query;
	}


	public List<Map<String, String>> getAllFieldAttributes() {
		return allFieldAttributes;
	}

	public String getName() {
		return name;
	}



	public List<EntityField> getFields() {
		return fields;
	}

	public Map<String, Set<EntityField>> getNameVsField() {
		return nameVsField;
	}

	public Map<String, String> getAllAttributes() {
		return allAttributes;
	}

	public List<Map<String, String>> getAllFieldsList() {
		return allFieldAttributes;
	}
	

}
