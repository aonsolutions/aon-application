package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "modular_page_i18n")
public class ModularPageDetail implements ITransferObject {

	private Integer id;

	private ModularPage modular_page;
	
	private Language language;

	private String label;

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
	@JoinColumn(name = "modular_page", nullable = false)
	public ModularPage getModular_page() {
		return modular_page;
	}

	public void setModular_page(ModularPage modular_page) {
		this.modular_page = modular_page;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "label", nullable = false, length = 128)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
