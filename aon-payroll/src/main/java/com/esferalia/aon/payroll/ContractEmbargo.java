package com.esferalia.aon.payroll;

import java.text.MessageFormat;
import java.text.ParsePosition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractEmbargoDB;
import com.esferalia.aon.payroll.enumeration.EmbargableType;

@Entity
@Table(name="contract_embargo")
public class ContractEmbargo extends ContractEmbargoDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String EXPRESSION_PATTERN = "{0}?((PENDIENTE>EMBARGABLE)?EMBARGABLE:PENDIENTE):0.00";
	private static String SALARY = "NOMINA";
	private static String EXTRA = "PAGA_EXTRA";
	private static String BOTH = "( NOMINA || PAGA_EXTRA )";
	private static String DELAY = "ATRASOS";

	@Transient
	public EmbargableType getEmbargableType(){
		return expressionToType();
	}
	@Transient
	public void setEmbargableType(EmbargableType type){
		typeToExpression(type);
	}

	private EmbargableType expressionToType(){
		if(getExpression()!=null){
			String s = (String) ((new MessageFormat(EXPRESSION_PATTERN)).parse(StringUtils.deleteWhitespace(getExpression()),new ParsePosition(0))[0]);
			if(s.equals(StringUtils.deleteWhitespace(SALARY))){
				return EmbargableType.SALARY;
			} else if(s.equals(StringUtils.deleteWhitespace(EXTRA))){
				return EmbargableType.EXTRA;
			} else if(s.equals(StringUtils.deleteWhitespace(BOTH))){
				return EmbargableType.BOTH;
			} else if(s.equals(StringUtils.deleteWhitespace(DELAY))){
				return EmbargableType.DELAY;
			}
		}
		return null;
	}
	private void typeToExpression(EmbargableType type){
		if(type==EmbargableType.SALARY){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, SALARY));
		} else if(type==EmbargableType.EXTRA){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, EXTRA));
		} else if(type==EmbargableType.BOTH){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, BOTH));
		} else if(type==EmbargableType.DELAY){
			setExpression(MessageFormat.format(EXPRESSION_PATTERN, DELAY));
		}
	}

}
