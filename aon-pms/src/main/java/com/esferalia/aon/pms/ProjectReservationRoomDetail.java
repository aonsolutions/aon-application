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

import com.code.aon.asset.AssetActivity;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="project_reservation_room_detail")
public class ProjectReservationRoomDetail implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private ProjectReservationRoom projectReservationRoom;
    private AssetActivity assetActivity;

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
	@JoinColumn(name="project_reservation_room", nullable=false)
	public ProjectReservationRoom getProjectReservationRoom() {
		return projectReservationRoom;
	}

	public void setProjectReservationRoom(ProjectReservationRoom projectReservationRoom) {
		this.projectReservationRoom = projectReservationRoom;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="asset_activity", nullable=false)
	public AssetActivity getAssetActivity() {
		return assetActivity;
	}

	public void setAssetActivity(AssetActivity assetActivity) {
		this.assetActivity = assetActivity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectReservationRoomDetail o = (ProjectReservationRoomDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.assetActivity, o.assetActivity)
				.append(this.projectReservationRoom, o.projectReservationRoom)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(assetActivity)
			.append(projectReservationRoom)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}