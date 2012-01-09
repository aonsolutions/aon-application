package com.esferalia.aon.pms;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;

@Entity
@Table(name="project_reservation_room")
public class ProjectReservationRoom implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private ProjectReservation projectReservation;
	private int roomIndex;
    private Item item;
	private int nights;
	private int adults;
	private int children;

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
	@JoinColumn(name="project_reservation", nullable=false)
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
	}

    @Column(name="room_index", nullable=false)
    public int getRoomIndex() {
        return roomIndex;
    }
    public void setRoomIndex(int roomIndex) {
        this.roomIndex = roomIndex;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="item", nullable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

    public int getNights() {
        return nights;
    }
    public void setNights(int nights) {
        this.nights = nights;
    }

    public int getAdults() {
        return adults;
    }
    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }
    public void setChildren(int children) {
        this.children = children;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectReservationRoom o = (ProjectReservationRoom) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.adults, o.adults)
				.append(this.children, o.children)
				.append(this.item, o.item)
				.append(this.nights, o.nights)
				.append(this.projectReservation, o.projectReservation)
				.append(this.roomIndex, o.roomIndex)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(adults)
			.append(children)
			.append(id)
			.append(item)
			.append(nights)
			.append(projectReservation)
			.append(roomIndex)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}