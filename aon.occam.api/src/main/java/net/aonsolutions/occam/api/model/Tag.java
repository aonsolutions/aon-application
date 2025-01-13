package net.aonsolutions.occam.api.model;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.TagMetadata;
import net.aonsolutions.occam.api.model.type.TagType;

public class Tag extends AonEntity<TagMetadata> {

	private static final long serialVersionUID = -8701101078851170978L;
	
	private Integer id;
	private Integer domain;
	private TagType type;
	private String name;
	private String color;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Tag markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Tag setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public Tag setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public Tag setId(Integer id) {
		checkIfDirty( this.id,id, TagMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return this.domain;
	}
	public Tag setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, TagMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public TagType getType() {
		return type;
	}
	public Tag setType(TagType type) {
		checkIfDirty( this.type,type, TagMetadata.TYPE);
		this.type = type;
		return this;
	}

	public String getName() {
		return this.name;
	}
	public Tag setName(String name) {
		checkIfDirty( this.name,name, TagMetadata.NAME);
		this.name = name;
		return this;
	}

	public String getColor() {
		return color;
	}
	public Tag setColor(String color) {
		checkIfDirty( this.color,color, TagMetadata.COLOR);
		this.color = color;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Tag) {
			return AonObjectUtils.equals( this.getUuid(),((Tag) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
