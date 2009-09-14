package com.code.aon.ebackoffice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.ebackoffice.enumeration.CatalogueType;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.OriginalPrice;
import com.code.aon.ebackoffice.enumeration.PriceType;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ebackoffice.enumeration.WishList;
import com.code.aon.product.Catalogue;


/**
 * Transfer Object that represents a eCommerce Target.
 * 
 * @author Esferalia Networks. David Uriarte - 3/09/2009
 */
@Entity
@Table(name="ec_catalogue")
public class Eccatalogue implements ITransferObject {
	
	
	private Integer id;	
	private Catalogue catalogue;	
	private byte[] catalogueImg;
	private CatalogueType type;
	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="catalogue", nullable=false)
	@ForeignKey(name = "FK_ECCATALOGUE_CATALOGUE")
	@Index(name = "IDX_ECCATALOGUE_CATALOGUE")
	public Catalogue getCatalogue() {
		return catalogue;
	}
	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}
	
	@Lob
	@Column(name = "catalogue_img")	
	public byte[] getCatalogueImg() {
		return catalogueImg;
	}
	public void setCatalogueImg(byte[] catalogueImg) {
		this.catalogueImg = catalogueImg;
	}
	
	@Column(name = "type", length = 1)
	public CatalogueType getType() {
		return type;
	}
	public void setType(CatalogueType type) {
		this.type = type;
	}
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	

	