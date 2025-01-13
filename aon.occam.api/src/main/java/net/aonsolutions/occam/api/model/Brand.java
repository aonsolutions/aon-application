package net.aonsolutions.occam.api.model;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.BrandMetadata;

public class Brand extends AonEntity<BrandMetadata> {
	
	private static final long serialVersionUID = 4975851969213366192L;
	
	private Integer id;
	private Integer domain;
	private String name;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Brand markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Brand setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public Brand setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}
	
	
	public Integer getId() {
		return id;
	}
	public Brand setId(Integer id) {
		checkIfDirty( this.id,id, BrandMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Brand setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, BrandMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Brand setName(String name) {
		checkIfDirty( this.name,name, BrandMetadata.NAME);
		this.name = name;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Brand) {
			return AonObjectUtils.equals( this.getUuid(),((Brand) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}
