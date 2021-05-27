package com.code.aon.fiscal.config;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.config.ModelStatus.Status;
import com.code.aon.fiscal.enumeration.Period;

public class ModelConfig implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static enum PERIOD {
		MONTHLY,QUATERLY,YEARLY;
	}
	
	private Model model;
	private int year;
	private PERIOD period;
	private Administration administration;
	private ModelStatus[] statuses;
	private int domainId;
	private String domainName;
	private String document;
	private String name;
	
	public ModelConfig(Model model, int year, Period period,Administration administration) {
		this.model = model;
		this.year = year;
		this.administration = administration;
		if (period.isMonthPeriod()) {
			this.period = PERIOD.MONTHLY;
		} else if (period.isQuarterPeriod()) {
			this.period = PERIOD.QUATERLY;
		} else {
			this.period = PERIOD.YEARLY;
		}
		this.statuses = new ModelStatus[getNumberOfStatuses()];
	}

	public Model getModel() {
		return model;
	}
	public int getYear() {
		return year;
	}
	public Administration getAdministration() {
		return administration;
	}
	public ModelStatus[] getStatuses() {
		return statuses;
	}
	public List<ModelStatus> getStatusList() {
		return Arrays.asList(getStatuses());
	}
	public void setStatuses(Period period, Status status) {
		ModelStatus st = new ModelStatus(status, period);
		if (isMonthly()) {
			getStatuses()[period.getStartMonth()] = st;	
		} else if (isYearly()) {
			getStatuses()[0] = st;
		} else {
			getStatuses()[period.ordinal() - 12] = st;
		}
	}
	public boolean isDeprecated() {
		return (model.getDeprecatedYear() != null && getYear() > model.getDeprecatedYear()); 
	}
	public boolean isMonthly() {
		return period == PERIOD.MONTHLY;
	}
	public boolean isQuaterly() {
		return period == PERIOD.QUATERLY;
	}
	public boolean isYearly() {
		return period == PERIOD.YEARLY;
	}
	public int getNumberOfStatuses() {
		if (isMonthly()) {
			return 12;
		} else if (isQuaterly()) {
			if (getModel() == Model.M202) {
				return 3;	
			} else {
				return 4;
			}
		} 
		return 1;
	}

	public String getAdministrationStyle() {
		if (getAdministration() == Administration.COMMON_TERRITORY) {
			return "aon-icon-aeat";
		} else if (getAdministration() == Administration.ALAVA) {
			return "aon-icon-araba";
		} else if (getAdministration() == Administration.BIZKAIA) {
			return "aon-icon-bizkaia";
		} else if (getAdministration() == Administration.NAVARRA) {
			return "aon-icon-navarra";
		} else if (getAdministration() == Administration.GIPUZKOA) {
			return "aon-icon-gipuzkoa";
		}
		return "";
	}

	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isNameEnabled() {
		return (StringUtils.isNotBlank(this.name) && getModel() == Model.M130);
	}
	
}