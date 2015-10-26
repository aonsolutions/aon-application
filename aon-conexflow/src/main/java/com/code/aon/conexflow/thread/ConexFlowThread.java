package com.code.aon.conexflow.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowPost;
import com.esferalia.aon.occam.api.model.Domain;

public class ConexFlowThread extends Thread{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ConexFlowThread.class);
	private final static String CONEXFLOW_RESULT_OK = "000";
	
	Query query;
	String domainName;
	Integer domainId;
	Integer projectId;
	ConexFlowConnection connection;
	
	public ConexFlowThread(ConexFlowConnection connection, Query query, String domainName, Integer domainId, Integer projectId){
		this.query = query;
		this.domainName = domainName;
		this.domainId = domainId;
		this.projectId = projectId;
		this.connection = connection;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Query getQuery() {
		return query;
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	
	public Domain getDomain(){
		Domain domain = new Domain();
		domain.setName(getDomainName());
		domain.setId(getDomainId());
		return domain;
	}

	@Override
	public void run() {
		ConexFlow cf = ConexFlowPost.execute(connection, query.getOperacion(), query, getProjectId(), getDomain(), false);
		if(cf != null){
			if(!cf.getRespuesta().getResultado().equals(CONEXFLOW_RESULT_OK)){
				LOGGER.error("*Error " + cf.getRespuesta().getResultado() + ": " + cf.getRespuesta().getDesResultado()+".");
			}
		}
		else{
			LOGGER.error("*Error: Los datos de conexión a conexFlow son incorrectos.");
		}
	}
}
