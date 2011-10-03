package com.esferalia.aon.ui.payroll.controller;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;

public class PaymentConceptController extends BasicController {

	private static final String IPREM_FORMMULA = "EXCESO_IPREM";
	private static final String ZERO_VALUE = "0";
	
	private TaxationType taxation;
	private QuoteType quote;
	private List<String> systemDataVariables;
	
	
	public TaxationType getTaxation() {
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	public QuoteType getQuote() {
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(quote==QuoteType.QUOTE){
			pc.setQuoteExpression(pc.getCode());
		}else if(quote==QuoteType.NO_QUOTE){
			pc.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			pc.setQuoteExpression(IPREM_FORMMULA);
		}
	}

	private void changeIrpfExpression() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(taxation==TaxationType.TAXED){
			pc.setIrpfExpression(pc.getCode());
		}else if(taxation==TaxationType.NO_TAXED){
			pc.setIrpfExpression(ZERO_VALUE);
		}
	}

	private void obtainQuoteType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getQuoteExpression()==null){
			quote=QuoteType.NO_QUOTE;
		} else if(pc.getQuoteExpression().equals(pc.getCode())){
			quote=QuoteType.QUOTE;
		} else if(pc.getQuoteExpression().equals(ZERO_VALUE)){
			quote=QuoteType.NO_QUOTE;
		} else if(pc.getQuoteExpression().equals(IPREM_FORMMULA)){
			quote=QuoteType.IPREM_EXCESS;
		} else {
			quote=QuoteType.MANUAL;
		}
	}
	
	private void obtainIrpfType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getIrpfExpression()==null){
			taxation=TaxationType.NO_TAXED;
		} else if(pc.getIrpfExpression().equals(pc.getCode())){
			taxation=TaxationType.TAXED;
		} else if(pc.getIrpfExpression().equals(ZERO_VALUE)){
			taxation=TaxationType.NO_TAXED;
		} else {
			taxation=TaxationType.MANUAL;
		}
	}

	public List<String> getSystemDataVariables() {
		if (systemDataVariables == null) {
			systemDataVariables = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				Date date = new Date();
				String alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					SystemData sd = (SystemData) to;
					systemDataVariables.add(sd.getName());					
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return systemDataVariables;
	}

	public void setSystemDataVariables(List<String> systemDataVariables) {
		this.systemDataVariables = systemDataVariables;
	}

	public List<?> expressionContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		for (String systemDataVariable :getSystemDataVariables()){
			if (systemDataVariable.startsWith(filter)) {
				list.add(systemDataVariable);
			}
		}
		for (ContractVariables cv :ContractVariables.values() ){
			if (cv.getName().startsWith(filter)) {
				list.add(cv.getName());		
			}
		}
		Collections.sort(list);
		return list;
	}
	
	@Override
	public void select(ActionEvent event) {
		super.select(event);
		obtainIrpfType();
		obtainQuoteType();		
	}
	
}
