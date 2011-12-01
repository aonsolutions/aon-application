package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the user.
 * 
 * @author Consulting & Development. Aimar Tellitu - 19-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "question_value")
public class QuestionValue extends ValueQuestionHolder {

	private static final long serialVersionUID = -7135601793952520234L;
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final QuestionValue o = (QuestionValue) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(getDate(), o.getDate())
				.append(getNumber(), o.getNumber())				
				.append(getQuestion(), o.getQuestion())
				.append(getText(), o.getText())				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getDate())
			.append(getNumber())
			.append(getId())	
			.append(getQuestion())			
			.append(getText())		
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}