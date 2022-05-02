package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.IOException;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AllIdcHighlighter implements IdcHighlighterListener {
	@Override
	public void onRLCE(String rlce, IdcHighlighter idcHighlighter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQuotes(String it, String ims, String unemployment, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(it);
		idcHighlighter.highlight(ims);
		idcHighlighter.highlight(unemployment);
	}

	@Override
	public void onQuoteGroup(String group, Boolean monthly, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(group);
	}

	@Override
	public void onIdcPeriod(String start, String end, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(start);
		idcHighlighter.highlight(end);
	}

	@Override
	public void onEnterpriseName(String name, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(name);
	}

	@Override
	public void onEnterpriseIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(ipf);
	}

	@Override
	public void onEnterpriseCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException {
		//idcHighlighter.highlight(ccc);
	}

	@Override
	public void onEnterpriseActivity(String code, String description, IdcHighlighter idcHighlighter)
			throws IOException {
		idcHighlighter.highlight(code);
		idcHighlighter.highlight(description);
	}

	@Override
	public void onEmployeeName(String name, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(name);
		
	}

	@Override
	public void onEmployeeNaf(String province, String num, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(num);
		idcHighlighter.highlight(province);
	}

	@Override
	public void onEmployeeIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(type);
		idcHighlighter.highlight(ipf);
	}

	@Override
	public void onEmployeeBirthDate(String date, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(date);
	}

	@Override
	public void onContractType(String code, String description, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(code);
		idcHighlighter.highlight(description);
	}

	@Override
	public void onContractStart(String date, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(date);
	}

	@Override
	public void onContractEnd(String date, IdcHighlighter idcHighlighter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCoefficient(String partial, String reduction, IdcHighlighter idcHighlighter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPEC(String code, String description, String tipo, String quota, String start, String end,
			IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(code);
		idcHighlighter.highlight(description);
		idcHighlighter.highlight(tipo);
		idcHighlighter.highlight(quota);
		idcHighlighter.highlight(start);
		// TODO Auto-generated method stub
		
	}
}