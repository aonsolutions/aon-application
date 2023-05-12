package com.esferalia.aon.payroll;

import java.util.Locale;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.esferalia.aon.entity.master.ContractInfoDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="contract_info")
public class ContractInfo extends ContractInfoDB implements IExpression, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		COOPERATIVE_PARTNER("SOCIO_COOP"),
		ACTIVE_RETIREMENT("JUB_ACTIVA"),
		YOUTH_GUARANTEE("GRT_JUVENIL"),
		
		TRAINING_CENTER("CENTRO_FORMATIVO"),
		TRAINING_COURSE("CURSO_FORMATIVO"),
		WORK_SCHEDULE("HORARIO_LABORAL"),
		TRAINING_SCHEDULE("HORARIO_LECTIVO"),
		
		ENTERPRISE_CLAUSES("ENTERPRISE_CLAUSES"),
		
		CONTRACT_MODEL("MODELO_CONTRATO"),
		CONTRACT_MODEL_OPTION("OPCION_CONTRATO"),
		
		// Social Security statuses during contract lifecycle
		SS_MA("SS_ALTA"),
		SS_MB("SS_BAJA"),
		SS_MG("SS_MOD_GRUPO_COTIZACION"),
		SS_MC("SS_MOD_TIPO_COEFICIENTE"),
		SS_MT("SS_MOD_OCUPACION"),
		
		// SEPE statuses during contract lifecycle
		SEPE_CONTRACT("SEPE_CONTRATO"),
		SEPE_EXTENSION("SEPE_PRORROGA"),
		SEPE_TRANSFORM("SEPE_TRANSFORMACION"),
		SEPE_CERTIFICADOS("SEPE_CERTIFICADO_EMPRESA"),

		// SEPE COMMUNICATION IDs
		SEPE_CONTRACT_ID("ID_CONTRATO_SEPE"),
		SEPE_EXTENSION_ID("ID_PRORROGA_SEPE"),
		SEPE_TRANSFORM_ID("ID_TRANSFORMACION_SEPE"), 
		BONUS_REDUCTION_INDICATOR("INDICADOR_REDUCCION_BONUS"), 
		DISABILITY_INDICATOR("INDICADOR_DUSCAPACIDAD"),
		
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

	public enum ContractSepeStatus implements IStringEnum, IResourceable {
		PENDING("PENDING"),
		BATCHED("BATCHED"),
		ACCEPTED("ACCEPTED"),
		ACCEPTED_WITH_ERRORS("ACCEPTED_WITH_ERRORS"),
		DENIED("DENIED"),
		BLOCKED("BLOCKED"),
		MANUAL("MANUAL");
		
		private final String value;
		
		private ContractSepeStatus(String value){
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String getName(Locale locale) {
			if(this==PENDING){
				return "Pendiente";
			} else if(this==BATCHED){
				return "Remesado";
			} else if(this==ACCEPTED){
				return "Aceptado";
			} else if(this==ACCEPTED_WITH_ERRORS){
				return "Aceptado con errores";
			} else if(this==DENIED){
				return "Rechazado";
			} else if(this==BLOCKED){
				return "Bloqueado";
			} else if(this==MANUAL){
				return "Manual";
			}
			return null;
		}
		
	}

	public enum ContractSsStatus implements IStringEnum, IResourceable {
		PENDING("PENDING"),
		BATCHED("BATCHED"),
		RECORDED("RECORDED"),
		DENIED("DENIED"),
		BLOCKED("BLOCKED"),
		MANUAL("MANUAL");
		
		private final String value;
		
		private ContractSsStatus(String value){
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
		@Override
		public String getName(Locale locale) {
			if(this==PENDING){
				return "Pendiente";
			} else if(this==BATCHED){
				return "Remesado";
			} else if(this==RECORDED){
				return "Grabado en AFI";
			} else if(this==DENIED){
				return "Rechazado";
			} else if(this==BLOCKED){
				return "Bloqueado";
			} else if(this==MANUAL){
				return "Manual";
			}
			return null;
		}
	}
	
}
