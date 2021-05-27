package com.esferalia.aon.payroll.ctsql2mysql;

public interface IEnterprises {

	public Integer getEnterprise(Integer oldCdg);

	public Integer getCalendar(Integer workplace);
	
	public String getIngEspEmp( Integer oldCdgAct);
	
	public String getIndRegimen( Integer oldCdgAct);
	
	public Integer getActivityId( Integer oldCdgAct);
	
	public Integer getCCC( Integer oldCdgAct, String oldCdgCCC);
	
	public Integer getWorkplace(Integer oldCdgEmp, Integer oldCdgDomicilio);
}
