package com.esferalia.aon.ui.pms.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.project.ProjectAttachment;
import com.code.aon.project.enumeration.ProjectAttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.ui.pms.controller.AdvanceInvoiceController;

public class AdvanceInvoiceSearchListener extends ControllerSearchListener implements IReservationConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean guestReservationSearch;
	private Hotel hotelReservation;
	private Customer agency;
	private Tariff tariff;
	private Integer conexFlowOperation;
	private String conexFlowAmount;
	private Date conexFlowDateFrom;
	private Date conexFlowDateTo;

	public boolean isGuestReservationSearch() {
		return guestReservationSearch;
	}
	public void setGuestReservationSearch(boolean guestReservationSearch) {
		this.guestReservationSearch = guestReservationSearch;
	}
	
	public Hotel getHotelReservation() {
		return hotelReservation;
	}
	public void setHotelReservation(Hotel hotelReservation) {
		this.hotelReservation = hotelReservation;
	}

	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
	}
	
	public Tariff getTariff() {
		return tariff;
	}
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	public Integer getConexFlowOperation() {
		return conexFlowOperation;
	}

	public void setConexFlowOperation(Integer conexFlowOperation) {
		this.conexFlowOperation = conexFlowOperation;
	}

	public String getConexFlowAmount() {
		return conexFlowAmount;
	}

	public void setConexFlowAmount(String conexFlowAmount) {
		this.conexFlowAmount = conexFlowAmount;
	}

	public Date getConexFlowDateFrom() {
		return conexFlowDateFrom;
	}

	public void setConexFlowDateFrom(Date conexFlowDateFrom) {
		this.conexFlowDateFrom = conexFlowDateFrom;
	}

	public Date getConexFlowDateTo() {
		return conexFlowDateTo;
	}

	public void setConexFlowDateTo(Date conexFlowDateTo) {
		this.conexFlowDateTo = conexFlowDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setGuestReservationSearch(true);
		setHotelReservation((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setTariff((Tariff)BeanManager.getManagerBean(Tariff.class).createNewTo());
		setConexFlowOperation(null);
		setConexFlowAmount(null);
		setConexFlowDateFrom(null);
		setConexFlowDateTo(null);

		((AdvanceInvoiceController)getController()).clearCheckedReservations();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.ACTIVE);
		criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_TOTAL), 0.0);
		if (getHotelReservation() != null && getHotelReservation().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_RESERVATION_ID), getHotelReservation().getId());			
		}
		if (isGuestReservationSearch()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ADVANCE_INVOICED), false);
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ADVANCE), 0.0);		
		} else {
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
		}
		if (getTariff() != null && getTariff().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.rooms.tariff.id"), getTariff().getId());			
		}
		completeConexFlowCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	private void completeConexFlowCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getConexFlowOperation() != null) {
			if (getConexFlowOperation().intValue() == 0) {
				criteria.addNullExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_TOKEN));
			} else {
				criteria.addNotNullExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_TOKEN));
				criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.attachments.attachType"), ProjectAttachmentType.CONEXFLOW);

				IManagerBean pAttachBean = BeanManager.getManagerBean(ProjectAttachment.class);
				Projection prjReservation = Projection.property(getFieldName(IEntityAlias.PROJECT_RESERVATION_ID));
				List<Integer> reservationIds = getController().getManagerBean().getList(new ProjectionList(prjReservation), criteria);
				if (reservationIds.size() > 0) {
					Criteria pAttachCriteria = new Criteria();
					pAttachCriteria.addInExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID), reservationIds);
					pAttachCriteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ATTACH_TYPE), ProjectAttachmentType.CONEXFLOW);
					Projection prjAttach = Projection.max(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ID));
					Projection prjGroup = Projection.group(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID));
					List<Integer> attachIds = new LinkedList<Integer>();
					for (Object id : pAttachBean.getList(new ProjectionList(prjAttach, prjGroup), pAttachCriteria)) {
						Object[] obj = (Object[])id;
						attachIds.add((Integer)obj[0]);
					}

					criteria.addInExpression(getController().resolveAlias("ProjectReservation.attachments.id"), attachIds);
					String alias = getController().resolveAlias("ProjectReservation.attachments.description");
					switch (getConexFlowOperation().intValue()) {
						case 1: Expression exp1 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_OK_PATTERN);
								Expression exp2 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_CHECK_OK_PATTERN);
								criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
								break;
						case 2: exp1 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_FAIL_PATTERN);
								exp2 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_CHECK_FAIL_PATTERN);
								criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
								break;
						case 3: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_CONFIRM_OK_PATTERN));
								break;
						case 4: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_CONFIRM_FAIL_PATTERN));
								break;
						case 5: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_SALE_OK_PATTERN));
								break;
						case 6: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_SALE_FAIL_PATTERN));
								break;
						case 7: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_REFUND_OK_PATTERN));
								break;
						case 8: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_REFUND_FAIL_PATTERN));
								break;
					}
				}
			}
		}
		if (StringUtils.isNotBlank(getConexFlowAmount())) {
			String alias = getController().resolveAlias("ProjectReservation.attachments.description");
			if (getConexFlowAmount().contains(".")) {
				if (getConexFlowAmount().endsWith("0") && !getConexFlowAmount().endsWith(".0")) {
					setConexFlowAmount(StringUtils.substring(getConexFlowAmount(), 0, getConexFlowAmount().indexOf(".")+1));
				}
			} else {
				setConexFlowAmount(getConexFlowAmount() + ".0");
			}
			criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, "%#" + getConexFlowAmount() + "%"));
		}
		if (getConexFlowDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().resolveAlias("ProjectReservation.attachments.attachDate"), getConexFlowDateFrom());
		}
		if (getConexFlowDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().resolveAlias("ProjectReservation.attachments.attachDate"), getConexFlowDateTo());
		}
	}
}