package com.code.aon.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
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
	@JoinColumn(name="user", nullable=false)
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

    @Column(name="tracking_duration")
    public Double getTrackingDuration() {
        return trackingDuration;
    }

    public void setTrackingDuration(Double trackingDuration) {
        this.trackingDuration = trackingDuration;
    }

    @ManyToOne
    @JoinColumn(name="job_type")
    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    @ManyToOne
    @JoinColumn(name="customer")
    public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne
    @JoinColumn(name="dossier")
    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
    }

    @ManyToOne
    @JoinColumn(name="activity")
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

	@Column(length=65535)
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}