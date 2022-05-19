package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.in.payroll.tgss.idc.IdcHighlighter.ERROR;

import java.io.IOException;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AllIdcHighlighter implements IdcHighlighterListener {
	@Override
	public void onRLCE(String rlce, IdcHighlighter idcHighlighter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQuotes(String it, String ims, String unemployment, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(it, ERROR);
		idcHighlighter.highlight(ims, ERROR);
		idcHighlighter.highlight(unemployment, ERROR);
	}

	@Override
	public void onQuoteGroup(String group, String monthly, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(group, ERROR);
	}

	@Override
	public void onIdcPeriod(String start, String end, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(start, ERROR);
		idcHighlighter.highlight(end, ERROR);
	}

	@Override
	public void onEnterpriseName(String name, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(name, ERROR);
	}

	@Override
	public void onEnterpriseCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(ccc, ERROR);
	}

	@Override
	public void onEnterpriseIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(ipf, ERROR);
	}

	@Override
	public void onContractCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException {
		//idcHighlighter.highlight(ccc);
	}

	@Override
	public void onEnterpriseRegime(String regime, IdcHighlighter idcHighlighter)
			throws IOException {
		idcHighlighter.highlight(escapeLiteral(regime), ERROR);
	}

	@Override
	public void onEnterpriseActivity(String code, String description, IdcHighlighter idcHighlighter)
			throws IOException {
		idcHighlighter.highlight(code, ERROR);
		idcHighlighter.highlight(description, ERROR);
	}

	@Override
	public void onEmployeeName(String name, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(name, ERROR);
		
	}

	@Override
	public void onEmployeeNaf(String province, String num, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(num, ERROR);
		idcHighlighter.highlight(province, ERROR);
	}

	@Override
	public void onEmployeeIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(type, ERROR);
		idcHighlighter.highlight(ipf, ERROR);
	}

	@Override
	public void onEmployeeBirthDate(String date, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(date, ERROR);
	}

	@Override
	public void onContractType(String code, String description, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(code, ERROR);
		idcHighlighter.highlight(description, ERROR);
	}

	@Override
	public void onStart(String date, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(date, ERROR);
	}

	@Override
	public void onEnd(String date, IdcHighlighter idcHighlighter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContractStart(String date, IdcHighlighter idcHighlighter) throws IOException {
		idcHighlighter.highlight(date, ERROR);
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
		idcHighlighter.highlightAll(ERROR);
		//idcHighlighter.highlight(code, description, tipo, quota, start, end, "", "");

		//idcHighlighter.highlight(description);
		//idcHighlighter.highlight(tipo);
		//idcHighlighter.highlight(quota);
		//idcHighlighter.highlight(start);
		
		// TODO Auto-generated method stub
		
	}

	protected String escapeLiteral(String literal) {
		return AonStringUtils.replaceEach(literal, 
				new String[] {".", "(", ")"}, 
				new String[] {"\\.", "\\(", "\\)"})
				;
	}

}