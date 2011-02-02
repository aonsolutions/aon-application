package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "faq_i18n")
public class FaqDetail implements ITransferObject {

	private static final long serialVersionUID = -943382832412062818L;

	private Integer id;

	private Faq faq;
	
	private Language language;

	private String question;
	
	private String answer;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "faq", nullable = false)
	public Faq getFaq() {
		return faq;
	}

	public void setFaq(Faq faq) {
		this.faq = faq;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "question", nullable = false, length = 255)
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Lob
	@Type(type="stringClob")   
	@Column(name = "answer", nullable = false)
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FaqDetail o = (FaqDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.answer, o.answer)
				.append(this.faq, o.faq)
				.append(this.language, o.language)
				.append(this.question, o.question)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(answer)
			.append(faq)
			.append(id)	
			.append(language)
			.append(question)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("answer", StringUtils.abbreviate(answer, 32)).
			append("faq", faq.getId()).
			append("id", id).
			append("language", language.getId()).
			append("question", question).
			toString();
	}	
	
}
