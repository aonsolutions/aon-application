package com.esferalia.aon.in.payroll.tgss.idc;

import java.io.IOException;

public interface IdcHighlighterListener {
	
	default void onEmployeeIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException {};
	default void onEmployeeName(String name, IdcHighlighter idcHighlighter) throws IOException {};
	default void onEmployeeBirthDate(String date, IdcHighlighter idcHighlighter) throws IOException {};
	default void onEmployeeNaf(String province, String num, IdcHighlighter idcHighlighter) throws IOException {};
	
	default void onEnterpriseCCC(String ccc, IdcHighlighter idcHighlighter) throws IOException{};
	default void onEnterpriseIpf(String type, String ipf, IdcHighlighter idcHighlighter) throws IOException{};
	default void onEnterpriseName(String name, IdcHighlighter idcHighlighter) throws IOException{};
	default void onEnterpriseActivity(String code, String description, IdcHighlighter idcHighlighter) throws IOException{};
	
	default void onIdcPeriod(String start, String end, IdcHighlighter idcHighlighter) throws IOException{};
	default void onContractStart(String date, IdcHighlighter idcHighlighter) throws IOException{};
	default void onContractEnd(String date, IdcHighlighter idcHighlighter) throws IOException{};
	default void onRLCE(String rlce, IdcHighlighter idcHighlighter) throws IOException{};
	default void onQuoteGroup(String group, Boolean monthly, IdcHighlighter idcHighlighter) throws IOException{};
	default void onContractType(String code, String description, IdcHighlighter idcHighlighter) throws IOException{};
	default void onCoefficient(String partial, String reduction, IdcHighlighter idcHighlighter) throws IOException{};
	
	default void onPEC(String code, String description, String tipo, String quota, String start, String end,IdcHighlighter idcHighlighter) throws IOException{};

	default void onQuotes(String it, String ims, String unemployment, IdcHighlighter idcHighlighter) throws IOException{};
	
}
