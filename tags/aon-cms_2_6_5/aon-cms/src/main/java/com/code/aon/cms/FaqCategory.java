package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "faq_category")
public class FaqCategory implements ITransferObject, IPositionObject {

	private Integer id;

	private String alias;

	private boolean active = true;

	private Integer position = new Integer(0);

	private Section section;
	
	private Set<FaqCategoryDetail> details;

	private Set<Faq> faqs;

	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "position", nullable = false)
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@OneToMany(mappedBy = "faqCategory", cascade={CascadeType.REMOVE})
	public Set<FaqCategoryDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<FaqCategoryDetail> details ) {
		this.details = details;
	}

	@OneToMany(mappedBy = "faqCategory", cascade={CascadeType.REMOVE})
	public Set<Faq> getFaqs() {
		return this.faqs;
	}

	public void setFaqs( Set<Faq> faqs) {
		this.faqs = faqs;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "section")
	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

}
