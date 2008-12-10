package com.code.aon.company;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.WhereJoinTable;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.registry.RegistryAddress;

/**
 * Transfer Object that represents the workPlace.
 * 
 */
@Entity
@Table(name="workplace")
public class WorkPlace implements ITransferObject, IEntity {

	private static final long serialVersionUID = 5078033612665053464L;

	/** Working place identifier */
	private Integer id;

	/** Working place description */
	private String description;

	/** Working place address */
    private RegistryAddress address;

    /** Indicates the working place calendar identifier. */
    private Integer calendar;

    /** Indicates if the working place is currently active. */
	private boolean active;

	/** The activities. */
	private Set<WorkActivity> activities = new HashSet<WorkActivity>();

	/** The employees. */
	private Set<Employee> employees = new HashSet<Employee>();

	/** The resources. */
	private Set<Resource> resources = new HashSet<Resource>();

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(name="description")
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name="address", nullable = false)
	public RegistryAddress getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address the address
	 */
	public void setAddress(RegistryAddress address) {
		this.address = address;
	}

	/**
	 * Return calendar identifier.
	 * 
	 * @return
	 */
	public Integer getCalendar() {
		return calendar;
	}

	/**
	 * Set calendar identifier.
	 * 
	 * @param calendar
	 */
	public void setCalendar(Integer calendar) {
		this.calendar = calendar;
	}

    /**
     * Returns if the employee is currently in the company.
     * 
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the activities.
	 * 
	 * @return the activities
	 */
	@OneToMany()
	@JoinColumn(name="workplace")
	@OrderBy("id")
	public Set<WorkActivity> getActivities() {
		return this.activities;
	}
	
	/**
	 * Set the activities.
	 * 
	 * @param activities the activities
	 */
	public void setActivities( Set<WorkActivity> activities) {
		this.activities = activities;
	}

	/**
	 * Add an activity.
	 * 
	 * @param activitiy
	 */
	public void addActivity(WorkActivity activity) {
		this.activities.add( activity );
	}

	/**
	 * Get the employees.
	 * 
	 * @return the employees
	 */
	@OneToMany()
	@JoinTable( name="resource", 
				joinColumns = { @JoinColumn( name="workplace") }, 
				inverseJoinColumns = @JoinColumn( name="employee" )
			)
	@WhereJoinTable(clause="endingdate is null and workactivity is null")
	public Set<Employee> getEmployees() {
		return this.employees;
	}
	
	/**
	 * Set the employees.
	 * 
	 * @param employees the employees
	 */
	public void setEmployees( Set<Employee> employees) {
		this.employees = employees;
	}

	/**
	 * Add an employee.
	 * 
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
		this.employees.add( employee );
	}

	/**
	 * Get the resources.
	 * 
	 * @return the resources
	 */
	@OneToMany()
	@JoinColumn(name="workplace")
	@OrderBy("workActivity")
	public Set<Resource> getResources() {
		return this.resources;
	}
	
	/**
	 * Set the resources.
	 * 
	 * @param resources the resources
	 */
	public void setResources( Set<Resource> resources) {
		this.resources = resources;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.employee.INode#accept(com.code.aon.employee.INodeVisitor)
	 */
	public void accept(IEntityVisitor visitor) {
		visitor.visitWorkPlace( this );
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof WorkPlace) {
			WorkPlace o = (WorkPlace) obj;
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
