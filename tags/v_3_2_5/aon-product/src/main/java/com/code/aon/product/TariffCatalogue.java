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

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a TariffCatalogue.
 * 
 * @author Consulting & Development. Gorka Irazu - 18/07/2008
 */
@Entity
@Table(name="tariff_catalogue")
public class TariffCatalogue implements ITransferObject {

	private static final long serialVersionUID = 3292328259769166649L;

	/** The id. */
	private Integer id;

	/** The tariff. */
	private Tariff tariff;

	/** The catalogue. */
	private Catalogue catalogue;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the tariff.
	 * 
	 * @return the tariff
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="tariff", nullable=false)
	public Tariff getTariff() {
		return tariff;
	}

	/**
	 * Sets the tariff.
	 * 
	 * @param tariff the tariff
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	/**
	 * Gets the catalogue.
	 * 
	 * @return the catalogue
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="catalogue", nullable=false)
	public Catalogue getCatalogue() {
		return catalogue;
	}

	/**
	 * Sets the catalogue.
	 * 
	 * @param catalogue the catalogue
	 */
	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof TariffCatalogue) {
			TariffCatalogue o = (TariffCatalogue) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}
