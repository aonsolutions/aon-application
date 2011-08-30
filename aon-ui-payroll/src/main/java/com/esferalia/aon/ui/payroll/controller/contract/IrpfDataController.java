package com.esferalia.aon.ui.payroll.controller.contract;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.IrpfCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLIrpfCalculatorContext;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfDataController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataController.class.getName());
	
	private static final char[] DNI_LETTERS = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D',
		'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E' };
	
	private Double grossSalary;
	private Double currentIrpf;
	private Double newIrpf;
	
	public Double getGrossSalary() {
		return grossSalary;
	}
	public void setGrossSalary(Double grossSalary) {
		this.grossSalary = grossSalary;
	}
	public Double getCurrentIrpf() {
		return currentIrpf;
	}
	public void setCurrentIrpf(Double currentIrpf) {
		this.currentIrpf = currentIrpf;
	}
	public Double getNewIrpf() {
		return newIrpf;
	}
	public void setNewIrpf(Double newIrpf) {
		this.newIrpf = newIrpf;
	}
	
	public Integer getDescendientLinesCount() {
		try {
			IrpfData data = (IrpfData) getTo();
			IManagerBean bean = BeanManager.getManagerBean(IrpfDataDescendients.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.IRPF_DATA_DESCENDIENTS_IRPF_DATA_ID), data.getId());
			List<ITransferObject> list = bean.getList(criteria);
			return list.size();
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener los descendientes";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public boolean isValidNIF() {
		IrpfData m = (IrpfData) getTo();
		if (m.getspouseDocument() == null || m.getspouseDocument().length() == 0) {
			return false;
		}
		char[] doc = m.getspouseDocument().toCharArray();
		if (doc == null || doc.length == 0) {
			return false;
		}
		doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
		String numbers = new String(doc, 0, 8);
		if (!StringUtils.isNumeric(numbers)) {
			return false;
		}
		return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
	}
	
	private SQLIrpfBuilder irpfBuilder;
	private static final String PERSON_ALIAS = "person_registry.id";
	private void refreshIrpfData(){
		try {
			irpfBuilder = new SQLIrpfBuilder(getConnection());
			IrpfCalculator calculator = new IrpfCalculator(new Date());
			calculator.setIrpfBuilder(irpfBuilder);
			SQLIrpfCalculatorContext sqlCtx = new SQLIrpfCalculatorContext(getConnection(),new Date(), getCtxCriteria());
			while ( sqlCtx.next() ) {
				setCurrentIrpf(Double.parseDouble(sqlCtx.getOldPercent()));
				setNewIrpf(sqlCtx.getPercent());
				setGrossSalary(CommonUtil.round(sqlCtx.getGrossSalary()));
			}
		} catch (ExpressionException e) {
			String msg = "Error al calcular el irpf";
			LOGGER.error(msg);
		} catch (SQLException e) {
			String msg = "Error al calcular el irpf";
			LOGGER.error(msg);
		}
	}
	protected Connection getConnection(){
		String sessionFactory = HibernateUtil.getSessionFactoryName(Salary.class.getName());
		return  HibernateUtil.getSQLConnection(sessionFactory);
	}
	public Criteria getCtxCriteria() {
		Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
		Criteria criteria = new Criteria();
		if (contract.getPerson() != null && contract.getPerson().getId() != null) {
			criteria = criteria == null ? new Criteria() : criteria;
			criteria.addEqualExpression(PERSON_ALIAS, contract.getPerson().getId());
		}
		return criteria;
	}
	
	public void onSelectContract(ActionEvent event){
		IController controller = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		Contract contract = (Contract) controller.getTo();
		try {
			this.getCriteria().addEqualExpression(this.getFieldName(IPayrollAlias.IRPF_DATA_CONTRACT_ID), contract.getId());
			this.getCriteria().addOrder(this.getFieldName(IPayrollAlias.IRPF_DATA_START_DATE), false);
			this.onSearch(event);
			if(this.getRowCount()<=0){
				this.onReset(event);
			} else {
				this.onSelectFirst(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al obtener el modelo 145";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
		refreshIrpfData();
	}
	
	public void onCalculateIrpf(ActionEvent event){
		refreshIrpfData();
	}
	
}
