package com.esferalia.aon.pms;

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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.esferalia.aon.pms.dao.IPmsAlias;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

@Entity
@Table(name="project_reservation")
@PrimaryKeyJoinColumn(name="project")
public class ProjectReservation implements ITransferObject {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private Project project;
	private Hotel hotel;
    private String code;
	private Date creationDate;
	private Date startDate;
	private Date endDate;
	private Seller seller;
	private Customer agency;
    private double agencyCommissionPercent;
    private double agencyCommissionAmount;
    private boolean agencyRebate;
	private Customer company;
    private double discountPercent;
    private double discountAmount;
	private BookingHolder bookingHolder;
	private Tariff tariff;
    private double taxableBase;
    private double vatQuota;
    private double otherTaxQuota;
    private double total;
	private String remarks;
	private ReservationStatus status;

	public ProjectReservation() {
		this.status = ReservationStatus.ACTIVE;
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

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="hotel", nullable=false)
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

    @Column(length=16, nullable=false)
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

	@Temporal(TemporalType.DATE)
	@Column(name="creation_date")	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="start_date")	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="end_date")	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="seller")
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="agency")
	public Customer getAgency() {
		return agency;
	}

	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	@Column(name="agency_commission_percent", precision=5, scale=2)
    public double getAgencyCommissionPercent() {
        return agencyCommissionPercent;
    }
    public void setAgencyCommissionPercent(double agencyCommissionPercent) {
        this.agencyCommissionPercent = agencyCommissionPercent;
    }

	@Column(name="agency_commission_amount", precision=15, scale=2)
    public double getAgencyCommissionAmount() {
        return agencyCommissionAmount;
    }
    public void setAgencyCommissionAmount(double agencyCommissionAmount) {
        this.agencyCommissionAmount = agencyCommissionAmount;
    }

	@Column(name="agency_rebate")
    public boolean getAgencyRebate() {
        return agencyRebate;
    }
    public void setAgencyRebate(boolean agencyRebate) {
        this.agencyRebate = agencyRebate;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="company")
	public Customer getCompany() {
		return company;
	}

	public void setCompany(Customer company) {
		this.company = company;
	}

	@Column(name="discount_percent", precision=5, scale=2)
    public double getDiscountPercent() {
        return discountPercent;
    }
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

	@Column(name="discount_amount", precision=15, scale=2)
    public double getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

	@Column(name="booking_holder", nullable=false)
	public BookingHolder getBookingHolder() {
		return bookingHolder;
	}

	public void setBookingHolder(BookingHolder bookingHolder) {
		this.bookingHolder = bookingHolder;
	}
	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="tariff")
	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	@Column(name="taxable_base", precision=15, scale=2)
    public double getTaxableBase() {
        return taxableBase;
    }
    public void setTaxableBase(double taxableBase) {
        this.taxableBase = taxableBase;
    }

	@Column(name="vat_quota", precision=15, scale=2)
    public double getVatQuota() {
        return vatQuota;
    }
    public void setVatQuota(double vatQuota) {
        this.vatQuota = vatQuota;
    }

	@Column(name="other_tax_quota", precision=15, scale=2)
    public double getOtherTaxQuota() {
        return otherTaxQuota;
    }
    public void setOtherTaxQuota(double otherTaxQuota) {
        this.otherTaxQuota = otherTaxQuota;
    }

	@Column(precision=15, scale=2)
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }

	@Lob
	@Type(type="stringClob")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(nullable=false)
	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	@Transient
	public String getGuestFullName() throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IPmsAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), getId());
		criteria.addOrder(reservationGuestBean.getFieldName(IPmsAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
		for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
			ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
			return reservationGuest.getFullName();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectReservation o = (ProjectReservation) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.agency, o.agency)
				.append(this.agencyCommissionAmount, o.agencyCommissionAmount)
				.append(this.agencyCommissionPercent, o.agencyCommissionPercent)
				.append(this.agencyRebate, o.agencyRebate)
				.append(this.bookingHolder, o.bookingHolder)
				.append(this.code, o.code)
				.append(this.company, o.company)
				.append(this.creationDate, o.creationDate)
				.append(this.discountAmount, o.discountAmount)
				.append(this.discountPercent, o.discountPercent)
				.append(this.endDate, o.endDate)
				.append(this.hotel, o.hotel)
				.append(this.otherTaxQuota, o.otherTaxQuota)
				.append(this.project, o.project)
				.append(this.remarks, o.remarks)
				.append(this.seller, o.seller)
				.append(this.startDate, o.startDate)
				.append(this.status, o.status)
				.append(this.tariff, o.tariff)
				.append(this.taxableBase, o.taxableBase)
				.append(this.total, o.total)
				.append(this.vatQuota, o.vatQuota)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(agency)
			.append(agencyCommissionAmount)
			.append(agencyCommissionPercent)
			.append(agencyRebate)
			.append(bookingHolder)
			.append(code)
			.append(company)
			.append(creationDate)
			.append(discountAmount)
			.append(discountPercent)
			.append(endDate)
			.append(hotel)
			.append(id)
			.append(otherTaxQuota)
			.append(project)
			.append(remarks)
			.append(seller)
			.append(startDate)
			.append(status)
			.append(tariff)
			.append(taxableBase)
			.append(total)
			.append(vatQuota)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}