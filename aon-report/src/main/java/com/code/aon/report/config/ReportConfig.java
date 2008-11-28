package com.code.aon.report.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Configuration file <code>report</code> element.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * 
 */
public class ReportConfig {
	/**
	 * Report identifier.
	 */
	private String id;

	/**
	 * Description of the report.
	 */
	private String description;

	/**
	 * Fecth Mode of the report.
	 */
	private ReportFetchMode fetchMode;

	/**
	 * Template of the record.
	 */
	private String template;

	/**
	 * Bean Key for obtaining a <code>com.code.aon.common.IFinderBean</code> in
	 * order to get the report's data.
	 */
	private String beanKey;

	/**
	 * Class name of the <code>com.code.aon.common.ICriteriaProvider</code> to obtain the
	 * <code>com.code.aon.ql.Criteria</code>. 
	 */
	private String criteriaProvider;
	
	private boolean forceRefresh;

	/**
	 * Class name of the <code>com.code.aon.common.ICollectionProvider</code> to obtain the
	 * Collection. 
	 */
	private String collectionProvider;


	/**
	 * Map of report parameters.
	 */
	private Map<Object,Object> params;

	/**
	 * Map of dynamic report parameters.
	 */
	private String dynamicParamsProvider;

	/**
	 * Collection of the nested reports.
	 */
	private List<String> nestedReports;

	/**
	 * Void contructor.
	 */
	public ReportConfig() {
	}

	/**
	 * Returns the identifier of the report.
	 * @return The identifier of the report.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the identifier of the report.
	 * @param id The identifier of the report.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the description of the report.
	 * @return The description of the report.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the report.
	 * @param description The description of the report.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the fetch mode of the report.
	 * @return The fetch mode of the report.
	 */
	public ReportFetchMode getFetchMode() {
		return fetchMode;
	}

	/**
	 * Sets the fetch mode of the report.
	 * @param fetchMode The fetch mode of the report.
	 */
	public void setFetchMode(ReportFetchMode fetchMode) {
		this.fetchMode = fetchMode;
	}

	/**
	 * Returns the bean key of the report.
	 * @return The bean key of the report.
	 */
	public String getBeanKey() {
		return beanKey;
	}

	/**
	 * Sets the bean key of the report.
	 * @param beanKey The bean key of the report.
	 */
	public void setBeanKey(String beanKey) {
		this.beanKey = beanKey;
	}

	/**
	 * Returns the criteria provider of the report.
	 * @return the criteria provider of the report.
	 */
	public String getCriteriaProvider() {
		return criteriaProvider;
	}

	/**
	 * Sets the criteria provider of the report.
	 * @param criteriaProvider The criteria provider of the report.
	 */
	public void setCriteriaProvider(String criteriaProvider) {
		this.criteriaProvider = criteriaProvider;
	}

	public boolean isForceRefresh() {
		return forceRefresh;
	}

	public void setForceRefresh(boolean forceRefresh) {
		this.forceRefresh = forceRefresh;
	}

	/**
	 * Returns the collection provider of the report.
	 * @return the collection provider of the report.
	 */
	public String getCollectionProvider() {
		return collectionProvider;
	}

	/**
	 * Sets the collection provider of the report.
	 * @param collectionProvider The collection provider of the report.
	 */
	public void setCollectionProvider(String collectionProvider) {
		this.collectionProvider = collectionProvider;
	}

	/**
	 * Returns the template of the report.
	 * @return The template of the report.
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Sets the template of the report.
	 * @param template The template of the report.
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Adds a id-value pair parameter to the report's parameter map.
	 * @param id Identifier of the parameter. 
	 * @param value Value of the parameter.
	 */
	public void addParam(String id, String value) {
		if (params == null) {
			params = new HashMap<Object,Object>();
		}
		params.put(id, value);
	}

	/**
	 * Returns the parameters map.
	 * @return The parameters map.
	 */
	public Map<Object, Object> getParams() {
		return params;
	}

	/**
	 * Adds a report identifier to the nested report list.
	 * @param id Report identifier. 
	 */
	public void addNestedReport(String id) {
		if (nestedReports == null) {
			nestedReports = new LinkedList<String>();
		}
		nestedReports.add(id);
	}

	/**
	 * Returns the nested report list.
	 * @return The nested report list.
	 */
	public List<String> getNestedReports() {
		return nestedReports;
	}

	/**
	 * @return
	 */
	public String getDynamicParamsProvider() {
		return dynamicParamsProvider;
	}

	/**
	 * @param dynamicParamsProvider
	 */
	public void setDynamicParamsProvider(String dynamicParamsProvider) {
		this.dynamicParamsProvider = dynamicParamsProvider;
	}
}
