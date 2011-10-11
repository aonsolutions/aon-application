package com.code.aon.tas;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.commercial.Target;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.project.Project;
import com.code.aon.tas.enumeration.ProjectStatus;

@Entity
@Table(name="project_tas")
@PrimaryKeyJoinColumn(name="project")
public class ProjectTas implements ITransferObject, IHeaderObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private Project project;
    private String series;
    private int number;
	private Target target;
	private TasItem tasItem;
    private double counter;
	private TaskHolder taskHolder;
	private String comments;
	private ProjectStatus status;
	private Date statusDate;
	private WorkPlace workPlace;

	public ProjectTas() {
		this.status = ProjectStatus.PENDING;
	}

	@Id
	@Column(name="project")
	@GeneratedValue(generator="project_id")
	@GenericGenerator(name="project_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="project")})
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

    @Column(length=5)
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }

    @Column(nullable = false)
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="target", nullable=false)
	@ForeignKey(name = "FK_PROJECT_TAS_TARGET")
	@Index(name = "IDX_PROJECT_TAS_TARGET")
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="tas_item", nullable=false)
	@ForeignKey(name = "FK_PROJECT_TAS_TAS_ITEM")
	@Index(name = "IDX_PROJECT_TAS_TAS_ITEM")
	public TasItem getTasItem() {
		return tasItem;
	}

	public void setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
	}

	@Column(precision=15, scale=3)
    public double getCounter() {
        return counter;
    }
    public void setCounter(double counter) {
        this.counter = counter;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="task_holder")
	@ForeignKey(name = "FK_PROJECT_TAS_TASK_HOLDER")
	@Index(name = "IDX_PROJECT_TAS_TASK_HOLDER")
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(nullable=false)
	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name="status_date")	
	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="workplace", nullable=false)
	@ForeignKey(name = "FK_PROJECT_TAS_WORKPLACE")
	@Index(name = "IDX_PROJECT_TAS_WORKPLACE")
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

    @Transient
    public Date getDate() {
    	return getProject().getDate();
    }

    @Transient
    public SecurityLevel getSecurityLevel() {
    	return SecurityLevel.OFFICIAL;
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectTas o = (ProjectTas) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.comments, o.comments)
				.append(this.counter, o.counter)
				.append(this.number, o.number)
				.append(this.project, o.project)
				.append(this.series, o.series)
				.append(this.status, o.status)
				.append(this.statusDate, o.statusDate)
				.append(this.target, o.target)
				.append(this.tasItem, o.tasItem)
				.append(this.taskHolder, o.taskHolder)
				.append(this.workPlace, o.workPlace)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(comments)
			.append(counter)
			.append(id)
			.append(number)
			.append(project)
			.append(series)
			.append(status)
			.append(statusDate)
			.append(target)
			.append(tasItem)
			.append(taskHolder)
			.append(workPlace)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}