package com.esferalia.aon.occam.api.model.config;

import static com.esferalia.aon.occam.api.model.config.ConfigBlock.ACCOUNTING;
import static com.esferalia.aon.occam.api.model.config.ConfigBlock.ALL;
import static com.esferalia.aon.occam.api.model.config.ConfigBlock.FISCAL;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;

public class ConfigParams implements Serializable {

	private static final long serialVersionUID = 7427598155727053547L;
	
	private Date atDate;
	private EnumSet<ConfigBlock> configBlocks = EnumSet.noneOf(ConfigBlock.class);

	
	public Date getAtDate() {
		return atDate;
	}
	public ConfigParams setAtDate(Date atDate) {
		this.atDate = atDate;
		return this;
	}

	public EnumSet<ConfigBlock> getConfigBlocks() {
		return configBlocks;
	}
	public ConfigParams setConfigBlocks(EnumSet<ConfigBlock> configBlocks) {
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
