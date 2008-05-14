package com.code.aon.tas;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.commercial.Target;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.SupportOrderOperation;
import com.code.aon.tas.enumeration.SupportOrderStatus;

/**
 * Transfer Object that represents an entity of a Support Order.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name = "support_order")
public class SupportOrder implements ITransferObject, ILookupObject, IHeaderObject {

	/**
	 * Unique key
	 */
	private Integer id;

	/**
	 * The item linked to this support order 
	 */
	private TasItem tasItem;

	/**
	 * Description of the support order
	 */
	private String description;
	
	/**
	 * The entity that needed the support order
	 */
	private Target target;
	
	/**
	 * The serie of the support order
	 */
	private String series;
	
	/**
	 * the numeber of the serie
	 */
	private int number;

	/**
	 * the support order starts
	 */
	private Date startDate;

	/**
	 * the support order ends
	 */
	private Date finalDate;

    /**
     * Actual status of the support order
     */
    private SupportOrderStatus status;
    
    /**
     * Actual operation of the support order
     */
    private SupportOrderOperation operation;
    
    /**
     * The solver of the support order
     */
    private Employee employee;

	/**
	 * A counter of the Tas Item
	 */
	private double counterti;
	
	private String levelti;

	private WorkPlace workPlace;

	/**
	 * Void constructor
	 */
	public SupportOrder() {
	}

	/**
	 * Constructor for this unique key.
	 * 
	 * @param id
	 */
	public SupportOrder(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the unique key.
	 * 
	 * @return unique key.
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	/**
	 * Assigns the unique key.
	 * 
	 * @param id
	 *            unique key.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the start date
	 * 
	 * @return the start date
	 */
	@Column(name="start_date")
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Assigns the start date
	 * 
	 * @param startDate the start date to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Return the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Assign the description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the finalize date
	 * 
	 * @return the finalDate
	 */
	@Column(name="final_date")
	public Date getFinalDate() {
		return finalDate;
	}

	/**
	 * Assigns the finalize date
	 * 
	 * @param finalDate the finalDate to set
	 */
	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	/**
	 * Returns the target
	 * 
	 * @return the target
	 */
	@ManyToOne
	@JoinColumn(name = "target", nullable = false)
	public Target getTarget() {
		return target;
	}

	/**
	 * Assign the target
	 * 
	 * @param target the target to set
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * Return the series
	 * 
	 * @return the series
	 */
	@Column(length=5)
	public String getSeries() {
		return series;
	}

	/**
	 * Assigns the series
	 * 
	 * @param series the serie to set
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	
	/**
	 * Returns the number
	 * 
	 * @return the number
	 */
	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	/**
	 * Assigns the number
	 * 
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

    /**
     * Returns the status
     * 
     * @return the status
     */
    public SupportOrderStatus getStatus() {
        return status;
    }

    /**
     * Assigns the status
     * 
     * @param status the status to set
     */
    public void setStatus(SupportOrderStatus status) {
        this.status = status;
    }

    /**
     * Returns the operation
     * 
     * @return the operation
     */
    public SupportOrderOperation getOperation() {
        return operation;
    }

    /**
     * Assigns th operation
     * 
     * @param operation the operation to set
     */
    public void setOperation(SupportOrderOperation operation) {
        this.operation = operation;
    }

	/**
	 * Returns the tas item
	 * 
	 * @return the tasItem
	 */
	@ManyToOne
	@JoinColumn(name = "tas_item", nullable = false)
	public TasItem getTasItem() {
		return tasItem;
	}

	/**
	 * Assigns the tas item
	 * 
	 * @param tasItem the tasItem to set
	 */
	public void setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
	}
	
	/**
	 * Return the employee
	 * 
	 * @return employee
	 */
	@ManyToOne
	@JoinColumn(name = "employee")
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * Assgins an employee
	 * 
	 * @param employee
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	/**
	 * Returns the counter
	 * 
	 * @return the counter
	 */
	@Column(nullable = false)
	public double getCounterti() {
		return counterti;
	}

	/**
	 * Assigns the counter
	 * 
	 * @param counter the counter to set
	 */
	public void setCounterti(double counterti) {
		this.counterti = counterti;
	}
	
	/**
	 * Returns the level
	 * 
	 * @return the level
	 */
    public String getLevelti() {
		return levelti;
	}

	/**
	 * Assigns the level
	 * 
	 * @param counter the level to set
	 */
	public void setLevelti(String levelti) {
		this.levelti = levelti;
	}

	@ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.common.ILookupObject#getLookups()
	 */
	@Transient
    public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(ITASAlias.SUPPORT_ORDER_ID, getId());
        map.put(ITASAlias.SUPPORT_ORDER_DESCRIPTION, getDescription());
        map.put(ITASAlias.SUPPORT_ORDER_TARGET_ID, getTarget().getId());
        return map;
    }

	/* (non-Javadoc)
	 * @see com.code.aon.common.IHeaderObject#getSecurityLevel()
	 */
	@Transient
	public SecurityLevel getSecurityLevel() {
        return SecurityLevel.OFFICIAL;
	}
}
