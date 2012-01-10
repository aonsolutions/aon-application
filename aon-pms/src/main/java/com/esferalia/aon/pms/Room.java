package com.esferalia.aon.pms;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.asset.Asset;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;

@Entity
@Table(name="room")
public class Room implements ITransferObject {

	private static final long serialVersionUID = 9050712689078733462L;

	private Integer id;
	private Asset asset;
	private Hotel hotel;
	private Item item;
	
//    @Id
//	@GeneratedValue
//	@Column(nullable=false)
	@Id
	@Column(name="asset")
	@GeneratedValue(generator="asset_id")
	@GenericGenerator(name="asset_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="asset")})
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
//	@ManyToOne
//    @JoinColumn(name="asset", nullable = false)
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn
    public Asset getAsset() {
		return asset;
	}
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	
	@ManyToOne
	@JoinColumn(name="hotel", nullable = false)
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	@ManyToOne
	@JoinColumn(name="item", nullable = false)
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Room o = (Room) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.asset, o.asset)
				.append(this.hotel, o.hotel)			
				.append(this.item, o.item)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(asset)
			.append(hotel)
			.append(item)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
