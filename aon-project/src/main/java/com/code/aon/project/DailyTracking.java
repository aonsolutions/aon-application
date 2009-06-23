package com.code.aon.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;
import com.code.aon.customer.Customer;

@Entity
@Table(name="daily_tracking")
public class DailyTracking implements ITransferObject {
	
	private static final long serialVersionUID = -9211987133575207355L;

	private Integer id;
	
	private User user;

    private Date trackingDate;

    private Double trackingDuration;

    private JobType jobType;
    
    private Customer customer;

    private Dossier dossier;
    
    private Activity activity;
    
	private String comments;
	
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
	@ForeignKey(name = "FK_DAILY_TRACKING_USER_ID")
	@Index(name = "IDX_DAILY_TRACKING_USER_ID")						
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    @Column(name="tracking_date", nullable=false)
    @Temporal(TemporalType.DATE)
    public Date getTrackingDate() {
        return trackingDate;
    }

    public void setTrackingDate(Date trackingDate) {
        this.trackingDate = trackingDate;
    }

    @Column(name="tracking_duration", nullable=false)
    public Double getTrackingDuration() {
        return trackingDuration;
    }

    public void setTrackingDuration(Double trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    @ManyToOne
    @JoinColumn(name="job_type", nullable=false)
	@ForeignKey(name = "FK_DAILY_TRACKING_JOB_TYPE")
	@Index(name = "IDX_DAILY_TRACKING_JOB_TYPE")						        
    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @ManyToOne
    @JoinColumn(name="customer")
	@ForeignKey(name = "FK_DAILY_TRACKING_CUSTOMER")
	@Index(name = "IDX_DAILY_TRACKING_CUSTOMER")						            
    public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne
    @JoinColumn(name="dossier")
	@ForeignKey(name = "FK_DAILY_TRACKING_DOSSIER")
	@Index(name = "IDX_DAILY_TRACKING_DOSSIER")						    
    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    @ManyToOne
    @JoinColumn(name="activity")
	@ForeignKey(name = "FK_DAILY_TRACKING_ACTIVITY")
	@Index(name = "IDX_DAILY_TRACKING_ACTIVITY")						        
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DailyTracking o = (DailyTracking) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.activity, o.activity)
				.append(this.comments, o.comments)
				.append(this.customer, o.customer)
				.append(this.dossier, o.dossier)
				.append(this.jobType, o.jobType)
				.append(this.trackingDate, o.trackingDate)
				.append(this.trackingDuration, o.trackingDuration)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(activity).append(comments).
			append(customer).append(dossier).
			append(id).append(jobType).
			append(trackingDate).append(trackingDuration).
			append(user).
			toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}