package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.IStringEnum;
import com.esferalia.aon.entity.master.ContractInfoDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="contract_info")
public class ContractInfo extends ContractInfoDB implements IExpression, IAuditable {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}
	
	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
	
	
	/*
	 * INNER CLASSES
	 */
	
	public enum ContractVariable implements IStringEnum {
	
		SELF_EMPLOYED("RETA"),
		
		TRAINING_CENTER("CENTRO_FORMATIVO"),
		TRAINING_COURSE("CURSO_FORMATIVO"),
		WORK_SCHEDULE("HORARIO_LABORAL"),
		TRAINING_SCHEDULE("HORARIO_LECTIVO"),
		
		SEPE_CONTRACT_ID("ID_CONTRATO_SEPE"),
		SEPE_EXTENSION_ID("ID_PRORROGA_SEPE"),
		SEPE_TRANSFORM_ID("ID_TRANSFORMACION_SEPE"),
		
		SS_CONTRACT_ID("ID_CONTRATO_SS"),
		SS_EXTENSION_ID("ID_PRORROGA_SS"),
		SS_TRANSFORM_ID("ID_TRANSFORMACION_SS"),
		
		ENTERPRISE_CLAUSES("ENTERPRISE_CLAUSES"),
		
		CONTRACT_MODEL("MODELO_CONTRATO"),
		CONTRACT_MODEL_OPTION("OPCION_CONTRATO"),
		
		;
			
		private final String value;
		
		private ContractVariable(String value){
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
			    
	    public static ContractVariable getVariableByName(String name){
	    	for(ContractVariable cv: values()){
	    		if(cv.getValue().equals(name)){
	    			return cv;
	    		}
	    	}
	    	return null;
	    }

	}

	
}
