package com.code.aon.cms;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "modular_page_option_i18n")
public class ModularPageOptionDetail implements ITransferObject {

	private static final long serialVersionUID = 3126075624601506447L;

	private Integer id;

	private ModularPageOption modular_page_option;
	
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
	@JoinColumn(name = "modular_page_option", nullable = false)
	public ModularPageOption getModular_page_option() {
		return modular_page_option;
	}

	public void setModular_page_option(ModularPageOption modular_page_option) {
		this.modular_page_option = modular_page_option;
	}

	@ManyToOne
	@JoinColumn(name = "language", nullable = false)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Column(name = "label", nullable = false, length = 64)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ModularPageOptionDetail o = (ModularPageOptionDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.label, o.label)
				.append(this.language, o.language)
				.append(this.modular_page_option, o.modular_page_option)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(label)
			.append(language)
			.append(modular_page_option)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("id", id).
			append("label", StringUtils.abbreviate(label, 32)).
			append("language", language.getId()).
			append("modular_page_option", modular_page_option.getId()).
			toString();
	}
	
}