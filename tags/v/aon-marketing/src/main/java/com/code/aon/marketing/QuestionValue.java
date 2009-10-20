package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Transfer Object that represents the user.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "question_value")
public class QuestionValue extends ValueHolder {

	private static final long serialVersionUID = -7135601793952520234L;
	
}