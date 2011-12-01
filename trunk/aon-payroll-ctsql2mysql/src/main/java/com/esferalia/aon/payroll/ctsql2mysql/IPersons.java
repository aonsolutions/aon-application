package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Date;


public interface IPersons {

	public String getName(Integer oldCdg);

	public String getNumDoc(Integer oldCdg);

	public Integer getPerson(Integer oldCdg);
}
