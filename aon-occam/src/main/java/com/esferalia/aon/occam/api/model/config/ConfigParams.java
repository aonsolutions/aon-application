package com.esferalia.aon.occam.api.model.config;

import static com.esferalia.aon.occam.api.model.config.ConfigBlock.ACCOUNTING;
import static com.esferalia.aon.occam.api.model.config.ConfigBlock.ALL;
import static com.esferalia.aon.occam.api.model.config.ConfigBlock.FISCAL;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ConfigParams implements Serializable {

	private static final long serialVersionUID = 7427598155727053547L;
	
	private String token;
	private Date atDate;
	
	// Deberia ser un EnumSet pero .... 
	// https://code.google.com/archive/p/google-web-toolkit/issues/3028
	private HashSet<ConfigBlock> configBlocks = new HashSet<>();

	
	public String getToken() {
		return token;
	}
	public ConfigParams  setToken(String token) {
		this.token = token;
		return this;
	}
	public boolean isByToken() {
		return AonStringUtils.isNotBlank(getToken());
	}
	
	public Date getAtDate() {
		return atDate;
	}
	public ConfigParams setAtDate(Date atDate) {
		this.atDate = atDate;
		return this;
	}

	public HashSet<ConfigBlock> getConfigBlocks() {
		return configBlocks;
	}
	public ConfigParams setConfigBlocks(HashSet<ConfigBlock> configBlocks) {
		this.configBlocks = configBlocks;
		return this;
	}
	public ConfigParams setBlocks(ConfigBlock ... blocks ) {
		if (blocks == null || blocks.length == 0) {
			return setConfigBlocks(null);	
		}
		for (ConfigBlock block : blocks) {
			getConfigBlocks().add(block);
		}
		return setConfigBlocks( getConfigBlocks() );
	}
	
	private boolean isEmpty() {
		return getConfigBlocks() == null || getConfigBlocks().isEmpty();
	}
	public boolean hasAll() {
		return isEmpty() || getConfigBlocks().contains(ALL);
	}
	public boolean hasFiscal() {
		return hasAll() || getConfigBlocks().contains(FISCAL);
	}
	public boolean hasAccounting() {
		return hasAll() || getConfigBlocks().contains(ACCOUNTING);
	}
	
}
