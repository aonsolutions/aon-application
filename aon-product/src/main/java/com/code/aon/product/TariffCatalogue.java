package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Tariff;

@Entity
@Table(name="tariff_catalogue")
public class TariffCatalogue implements ITransferObject {

	private static final long serialVersionUID = 3292328259769166649L;

	private Integer id;
	private Tariff tariff;
	private Catalogue catalogue;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="tariff", nullable=false)
    @ForeignKey(name = "FK_TARIFF_CATALOGUE_TARIFF")
    @Index(name = "IDX_TARIFF_CATALOGUE_TARIFF")    	        		
	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="catalogue", nullable=false)
    @ForeignKey(name = "FK_TARIFF_CATALOGUE_CATALOGUE")
    @Index(name = "IDX_TARIFF_CATALOGUE_CATALOGUE")    	        	
	public Catalogue getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TariffCatalogue o = (TariffCatalogue) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.tariff, o.tariff)				
				.append(this.catalogue, o.catalogue)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)								
			.append(tariff)						
			.append(catalogue)		
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
