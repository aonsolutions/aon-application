package net.aonsolutions.occam.api.model;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.ProductCategoryMetadata;

public class ProductCategory extends AonEntity<ProductCategoryMetadata> {
	
	private static final long serialVersionUID = -9167695204686231161L;
	
	private Integer id;
	private Integer domain;
	private String name;
	
	private String detail;
	private String detail2;
	private String detail3;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public ProductCategory markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public ProductCategory setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public ProductCategory setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}
	
	public Integer getId() {
		return id;
	}
	public ProductCategory setId(Integer id) {
		checkIfDirty( this.id,id, ProductCategoryMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public ProductCategory setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, ProductCategoryMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public ProductCategory setName(String name) {
		checkIfDirty( this.name,name, ProductCategoryMetadata.NAME);
		this.name = name;
		return this;
	}
	
	public String getDetail() {
		return detail;
	}
	public ProductCategory setDetail(String detail) {
		checkIfDirty( this.detail,detail, ProductCategoryMetadata.DETAIL);
		this.detail = detail;
		return this;
	}
	
	public String getDetail2() {
		return detail2;
	}
	public ProductCategory setDetail2(String detail2) {
		checkIfDirty( this.detail2,detail2, ProductCategoryMetadata.DETAIL2);
		this.detail2 = detail2;
		return this;
	}
	
	public String getDetail3() {
		return detail3;
	}
	public ProductCategory setDetail3(String detail3) {
		checkIfDirty( this.detail3,detail3, ProductCategoryMetadata.DETAIL3);
		this.detail3 = detail3;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof ProductCategory) {
			return AonObjectUtils.equals( this.getUuid(),((ProductCategory) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
