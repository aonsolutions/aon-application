package com.esferalia.aon.gwt.payroll.server;

import java.util.Set;

import com.esferalia.aon.salary.ISalaryBuilder;

public interface ISalaryDraftBuilder extends ISalaryBuilder {
	void setDefined(Set<String>[] defined);
}
