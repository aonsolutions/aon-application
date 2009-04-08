package com.code.aon.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="user_workgroup")
public class UserWorkGroup implements ITransferObject {

	private static final long serialVersionUID = 7804966037720748099L;

	private Integer id;
	
	private User user;
	
	private WorkGroup workGroup;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
    @ForeignKey(name = "FK_USER_WORKGROUP_USER")
    @Index(name = "IDX_USER_WORKGROUP_USER")    			
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name="workgroup", nullable=false)
    @ForeignKey(name = "FK_USER_WORKGROUP_WORKGROUP")
    @Index(name = "IDX_USER_WORKGROUP_WORKGROUP")    				
	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof UserWorkGroup) {
			UserWorkGroup o = (UserWorkGroup) obj;
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