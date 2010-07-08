/**
 * 
 */
package com.code.aon.company;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.WhereJoinTable;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;

@Entity
@Table(name="workactivity")
public class WorkActivity implements ITransferObject, IEntity {
	
	/** Working activity identifier */
	private Integer id;
	
	/** Working activity description */
    private String description;

	/** Indicates the working place that this activity belongs to */
    private WorkPlace workPlace; 

    /** Indicates the working activity calendar identifier. */
    private Integer calendar;

    /** Indicates if the working activity is currently active. */
	private boolean active;

	/** The employee. */
	private Set<Employee> employees = new HashSet<Employee>();

	/** The resources. */
	private Set<Resource> resources = new HashSet<Resource>();

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne
    @JoinColumn(name="workplace", nullable = false, updatable = false)    
    public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
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
	 * Get the employees.
	 * 
	 * @return the employees
	 */
	@OneToMany()
	@JoinTable( name="resource", 
				joinColumns = { @JoinColumn( name="workactivity") }, 
				inverseJoinColumns = @JoinColumn( name="employee" )
			)
	@WhereJoinTable(clause="endingdate is null and workactivity is not null")
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
	@JoinColumn(name="workactivity")
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

	/*(non-Javadoc)
	 * @see com.code.aon.employee.INode#accept(com.code.aon.employee.INodeVisitor)
	 */
	public void accept(IEntityVisitor visitor) {
		visitor.visitWorkActivity( this );
	}
}
