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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.ebackoffice.enumeration.CatalogueType;
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
	private byte[] catalogueIcon;
	private CatalogueType type;
	private boolean visible;
	
	
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
	
	@Lob
	@Column(name = "catalogue_icon")	
	public byte[] getCatalogueIcon() {
		return catalogueIcon;
	}
	public void setCatalogueIcon(byte[] iconImg) {
		this.catalogueIcon = iconImg;
	}
	@Column(name = "type", length = 1)
	public CatalogueType getType() {
		return type;
	}
	public void setType(CatalogueType type) {
		this.type = type;
	}
	
	@Transient
	public String getCapitalLetter(){
		return this.catalogue.getName().substring(0, 1);
	}
	
	
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
	
	
	
	
	
	
	
	
	
	
	
	

	