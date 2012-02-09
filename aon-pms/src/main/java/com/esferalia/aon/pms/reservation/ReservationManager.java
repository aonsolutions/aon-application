package com.esferalia.aon.pms.reservation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opentravel.ota.x2003.x05.AmountType;
import org.opentravel.ota.x2003.x05.CommentType.Comment;
import org.opentravel.ota.x2003.x05.ErrorType;
import org.opentravel.ota.x2003.x05.HotelReservationIDsType;
import org.opentravel.ota.x2003.x05.HotelReservationIDsType.HotelReservationID;
import org.opentravel.ota.x2003.x05.HotelReservationType;
import org.opentravel.ota.x2003.x05.HotelReservationsType;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRQDocument;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRSDocument;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRSDocument.OTAHotelResNotifRS;
import org.opentravel.ota.x2003.x05.POSType;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.ResGlobalInfoType;
import org.opentravel.ota.x2003.x05.ResGuestsType.ResGuest;
import org.opentravel.ota.x2003.x05.RoomStaysType.RoomStay;
import org.opentravel.ota.x2003.x05.ServicesType.Service;
import org.opentravel.ota.x2003.x05.SourceType;
import org.opentravel.ota.x2003.x05.TPAExtensionsType;
import org.opentravel.ota.x2003.x05.TaxType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationManager implements IReservationConstants {

	private ReservationUtils reservationUtils;
	private IPriceStrategy priceStrategy;
	private Map<String, Integer> successMap;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils();
		}
		return reservationUtils;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public String processReservation(String reservationXml) {
		successMap = new HashMap<String, Integer>();
		try {
			OTAHotelResNotifRQDocument document = OTAHotelResNotifRQDocument.Factory.parse(reservationXml);
			if (document.validate()) {
				POSType posType = document.getOTAHotelResNotifRQ().getPOS();
				HotelReservationsType reservationsType = document.getOTAHotelResNotifRQ().getHotelReservations();
				for (int i=0; i<reservationsType.sizeOfHotelReservationArray(); i++) {
					getReservationUtils().init();
					ProjectReservation reservation = processReservation(reservationsType.getHotelReservationArray(i), posType);
					successMap.put(reservation.getCode(), reservation.getId());
				}
			} else {
				throw new ReservationException("Invalid message", 395);
			}
			return reservationSuccess();
		} catch (ReservationException ex) {
			return reservationError(ex);
		} catch (Exception ex) {
			return reservationError(ex);
		}
	}

	private ProjectReservation processReservation(HotelReservationType reservationType, POSType posType) throws ManagerBeanException, ReservationException {
		String actionType = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), ACTION, TYPE);
		if (actionType.equals(ADD_RESERVATION)) {
			return addReservation(reservationType, posType);
		} else if (actionType.equals(MODIFY_RESERVATION)) {
			return modifyReservation(reservationType, posType);
		} else if (actionType.equals(CANCEL_RESERVATION)) {
			return cancelReservation(reservationType);
		}
		return null;
	}

	private ProjectReservation addReservation(HotelReservationType reservationType, POSType posType) throws ManagerBeanException, ReservationException {
		ProjectReservation reservation = obtainReservation(reservationType);
		if (reservation == null) {
			reservation = new ProjectReservation();
			createReservation(reservationType, posType, reservation);
			return reservation;
		} else {
			throw new ReservationException("Reservation already exists", reservation.getCode(), 127);
		}
	}

	private ProjectReservation modifyReservation(HotelReservationType reservationType, POSType posType) throws ManagerBeanException, ReservationException {
		ProjectReservation reservation = obtainReservation(reservationType);
		if (reservation != null) {
			if (reservation.getStatus() == ReservationStatus.ACTIVE || reservation.getStatus() == ReservationStatus.BLOCKED) {
				if (modificationAvailable(reservation)) {
					removeReservationService(reservation);
					removeReservationRoom(reservation);
					removeReservationGuest(reservation);
	
					createReservation(reservationType, posType, reservation);
					return reservation;
				} else {
					throw new ReservationException("Reservation in use, can not be modified", reservation.getCode(), 255);
				}
			} else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
				throw new ReservationException("Reservation already cancelled", reservation.getCode(), 95);
			} else {
				throw new ReservationException("Reservation in use, can not be modified", reservation.getCode(), 255);
			}
		} else {
			String reservationCode = reservationType.getResGlobalInfo().getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
			throw new ReservationException("No Reservations found with search criteria", reservationCode, 284);
		}
	}

	private ProjectReservation cancelReservation(HotelReservationType reservationType) throws ManagerBeanException, ReservationException {
		ProjectReservation reservation = obtainReservation(reservationType);
		if (reservation != null) {
			if (reservation.getStatus() == ReservationStatus.ACTIVE || reservation.getStatus() == ReservationStatus.BLOCKED) {
				removeReservationRoomDetail(reservation);
				cancelReservation(reservation);
				return reservation;
			} else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
				throw new ReservationException("Reservation already cancelled", reservation.getCode(), 95);
			} else {
				throw new ReservationException("Reservation in use, can not be cancelled", reservation.getCode(), 255);
			}
		} else {
			String reservationCode = reservationType.getResGlobalInfo().getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
			throw new ReservationException("No Reservations found with search criteria", reservationCode, 284);
		}
	}

	private void createReservation(HotelReservationType reservationType, POSType posType, ProjectReservation reservation) throws ReservationException {
		String reservationId = reservationType.getResGlobalInfo().getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
		try {
			String hotelCode = reservationType.getRoomStays().getRoomStayArray(0).getBasicPropertyInfo().getHotelCode();
			String operationDate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, DATE);
			String operationTime = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, TIME);
			Date checkIn = reservationType.getResGlobalInfo().getTimeSpan().getStart().getTime();
			Date checkOut = reservationType.getResGlobalInfo().getTimeSpan().getEnd().getTime();
			SourceType sellerSource = findPosSource(posType.getSourceArray(), CRO_SOURCE);
			ProfileInfo agencyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), AGENCY_TYPE, IATA);
			String agencyRebate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT_MODE, null);
			ProfileInfo companyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), COMPANY_TYPE, null);
			String discountPercent = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, PERCENT);
			String discountAmount = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, AMOUNT);
			String bookingHolder = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), BOOKING_HOLDER, null);
			String remarks =  findComments(reservationType.getResGlobalInfo());
			double taxableBase = CommonUtil.round(reservationType.getResGlobalInfo().getTotal().getAmountBeforeTax().doubleValue());
			double vatQuota = findTaxQuota(reservationType.getResGlobalInfo(), VAT_TAX);
			double otherTaxQuota = findTaxQuota(reservationType.getResGlobalInfo(), OTHER_TAX);
			double total = CommonUtil.round(reservationType.getResGlobalInfo().getTotal().getAmountAfterTax().doubleValue());
			if (CommonUtil.round(taxableBase + vatQuota + otherTaxQuota) != total) {
				throw new ReservationException("Reservation Total is not correct", reservationId, 197);
			}

			Hotel hotel = getReservationUtils().obtainHotel(hotelCode);
			getReservationUtils().setDomain(hotel.getDomain());

			reservation.setHotel(hotel);
			reservation.setCode(reservationId);
			reservation.setStartDate(checkIn);
			reservation.setEndDate(checkOut);
			reservation.setSeller(getReservationUtils().obtainSeller(sellerSource));
			reservation.setAgency(getReservationUtils().obtainAgency(agencyInfo));
			reservation.setAgencyCommissionPercent(getReservationUtils().obtainAgencyCommissionPercent(agencyInfo));
			reservation.setAgencyCommissionAmount(getReservationUtils().obtainAgencyCommissionAmount(agencyInfo));
			reservation.setAgencyRebate(agencyRebate != null && agencyRebate.equals(AGENCY_REBATE));
			reservation.setCompany(getReservationUtils().obtainCompany(companyInfo));
			reservation.setDiscountPercent((discountPercent != null) ? Double.parseDouble(discountPercent) : 0);
			reservation.setDiscountAmount((discountAmount != null) ? Double.parseDouble(discountAmount) : 0);
			reservation.setBookingHolder(getReservationUtils().obtainBookingHolder(bookingHolder));
			reservation.setTaxableBase(taxableBase);
			reservation.setVatQuota(vatQuota);
			reservation.setOtherTaxQuota(otherTaxQuota);
			reservation.setTotal(total);
			reservation.setRemarks(remarks);
			reservation.setCrs(true);
			reservation.setStatus(ReservationStatus.ACTIVE);

			getReservationUtils().fillProject(reservation);
			reservation.getProject().setDomain(hotel.getDomain());
			reservation.setDomain(hotel.getDomain());

			getReservationUtils().verifySellerEntity(reservation, sellerSource);
			getReservationUtils().verifyAgencyEntity(reservation, agencyInfo);
			getReservationUtils().verifyCompanyEntity(reservation, companyInfo);

			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			if (reservation.getId() == null) {
				reservation.setCreationDate(getReservationUtils().obtainCreationDate(operationDate, operationTime));
				reservation = (ProjectReservation)reservationBean.insert(reservation);
			} else {
				reservation.setModificationDate(getReservationUtils().obtainCreationDate(operationDate, operationTime));
				reservation = (ProjectReservation)reservationBean.update(reservation);
			}

			createReservationGuest(reservationType, reservation);
			createReservationRoom(reservationType, reservation);
			createReservationService(reservationType, reservation);
		} catch(Exception ex) {
			String actionType = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), ACTION, TYPE);
			if (actionType.equals(ADD_RESERVATION)) {
				try {
					removeReservationService(reservation);
					removeReservationRoom(reservation);
					removeReservationGuest(reservation);
					removeReservation(reservation);
				} catch (ManagerBeanException exc) {
				}
			}

			if (ex instanceof ReservationException) {
				ReservationException rex = (ReservationException)ex;
				rex.setRecord(reservationId);
				throw rex;
			} else {
				throw new ReservationException("Unknown error", reservationId, 1);
			}
		}
	}

	private void createReservationGuest(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		for (int i=0; i<reservationType.getResGuests().sizeOfResGuestArray(); i++) {
			ResGuest guest = reservationType.getResGuests().getResGuestArray(i);
			if (StringUtils.isNotEmpty(guest.getResGuestRPH()) && guest.getProfiles().sizeOfProfileInfoArray() > 0) {
				ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
				reservationGuest.setProjectReservation(reservation);
				reservationGuest.setDomain(getReservationUtils().getDomain());
				reservationGuest.setGuestIndex(Integer.parseInt(guest.getResGuestRPH()));
				reservationGuest.setSurname(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().getSurname());
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().sizeOfGivenNameArray() > 0) {
					reservationGuest.setName(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().getGivenNameArray(0));
				}
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().sizeOfNamePrefixArray() > 0) {
					reservationGuest.setTreatment(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().getNamePrefixArray(0));
				}
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().sizeOfEmailArray() > 0) {
					reservationGuest.setEmail(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getEmailArray(0).getStringValue());
				}
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().sizeOfTelephoneArray() > 0) {
					reservationGuest.setPhone(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getTelephoneArray(0).getPhoneNumber());
				}
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().sizeOfAddressArray() > 0) {
					reservationGuest.setAddress(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getAddressLineArray(0));
					reservationGuest.setZip(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getPostalCode());
					reservationGuest.setCity(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCityName());
					reservationGuest.setProvince(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getStateProv().getStringValue());
					reservationGuest.setCountry(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCountryName().getCode());
				}
				reservationGuestBean.insert(reservationGuest);
				if (reservationGuest.getGuestIndex() == 1) {
					getReservationUtils().fillProject(reservation);
					IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
					reservation = (ProjectReservation)reservationBean.update(reservation);
				}
			}
		}
	}

	private void createReservationRoom(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		for (int i=0; i<reservationType.getRoomStays().sizeOfRoomStayArray(); i++) {
			RoomStay stay = reservationType.getRoomStays().getRoomStayArray(i);
			if (stay.getRoomTypes() != null && stay.getRoomTypes().sizeOfRoomTypeArray() > 0) {
				for (int j=0; j<stay.getRoomTypes().getRoomTypeArray(0).getNumberOfUnits(); j++) {
					ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
					reservationRoom.setProjectReservation(reservation);
					reservationRoom.setDomain(getReservationUtils().getDomain());
					reservationRoom.setRoomIndex(stay.getIndexNumber());
					reservationRoom.setItem(getReservationUtils().obtainRoomItem(stay.getRoomTypes().getRoomTypeArray(0).getRoomTypeCode()));
					if (stay.getRatePlans() != null && stay.getRatePlans().sizeOfRatePlanArray() > 0) {
						reservationRoom.setTariff(getReservationUtils().obtainTariff(stay.getRatePlans().getRatePlanArray(0).getRatePlanCode()));
					}
					if (j == 0 && stay.getGuestCounts() != null && stay.getGuestCounts().sizeOfGuestCountArray() > 0) {
						for (int k=0; k<stay.getGuestCounts().sizeOfGuestCountArray(); k++) {
							String type = stay.getGuestCounts().getGuestCountArray(k).getAgeQualifyingCode();
							if (type != null && type.equals(CHILDREN_COUNT)) {
								reservationRoom.setChildren(stay.getGuestCounts().getGuestCountArray(k).getCount());
							} else {
								reservationRoom.setAdults(stay.getGuestCounts().getGuestCountArray(k).getCount());
							}
						}
					}
					reservationRoomBean.insert(reservationRoom);
				}
			}
		}
	}

	private void createReservationService(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		double servicesTaxableBase = 0;
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		for (int i=0; i<reservationType.getServices().sizeOfServiceArray(); i++) {
			Service service = reservationType.getServices().getServiceArray(i);
			ProjectReservationService reservationService = new ProjectReservationService();
			reservationService.setProjectReservation(reservation);
			reservationService.setDomain(getReservationUtils().getDomain());
			reservationService.setServiceIndex(Integer.parseInt(service.getServiceRPH()));
			reservationService.setItem(getReservationUtils().obtainServiceItem(service.getServiceInventoryCode()));
			reservationService.setDescription(reservationService.getItem().getProduct().getName());
			reservationService = (ProjectReservationService)reservationServiceBean.insert(reservationService);

			for (int j=0; j<service.sizeOfPriceArray(); j++) {
				AmountType price = service.getPriceArray(j);
				if (price.getEffectiveDate() != null && price.getBase().getAmountBeforeTax().doubleValue() != 0) {
					Calendar currentCalendar = new GregorianCalendar();
					currentCalendar.setTime(price.getEffectiveDate().getTime());
					Calendar nextCalendar = new GregorianCalendar();
					nextCalendar.setTime(reservation.getEndDate());
					if ((j+1) < service.sizeOfPriceArray()) {
						nextCalendar = service.getPriceArray(j+1).getEffectiveDate();
						nextCalendar.set(Calendar.HOUR, 0);
						nextCalendar.set(Calendar.MINUTE, 0);
						nextCalendar.set(Calendar.SECOND, 0);
					}
					while (currentCalendar.compareTo(nextCalendar) < 0) {
						ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
						reservationServiceDetail.setProjectReservationService(reservationService);
						reservationServiceDetail.setDomain(getReservationUtils().getDomain());
						reservationServiceDetail.setEffectiveDate(currentCalendar.getTime());
						reservationServiceDetail.setQuantity(price.getNumberOfUnits());
						reservationServiceDetail.setPrice(price.getBase().getAmountBeforeTax().doubleValue());
						reservationServiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(reservationServiceDetail));
						reservationServiceDetailBean.insert(reservationServiceDetail);
						servicesTaxableBase += CommonUtil.round(reservationServiceDetail.getTaxableBase());
						
						currentCalendar.add(Calendar.DATE, 1);
					}
				}
			}
		}

		if (reservation.getTaxableBase() != servicesTaxableBase) {
			throw new ReservationException("Reservation Taxable Base does not match the sum of Services Taxable Bases", reservation.getCode(), 197);
		}
	}

	private ProjectReservation obtainReservation(HotelReservationType reservationType) throws ManagerBeanException {
		String reservationId = reservationType.getResGlobalInfo().getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), reservationId);
		criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS), new Boolean(true));
		if (reservationBean.getCount(criteria) > 0) {
			return (ProjectReservation)reservationBean.getList(criteria).get(0);
		}
		return null;
	}

	private boolean modificationAvailable(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		return (reservationRoomDetailBean.getCount(criteria) == 0);
	}

	private void removeReservationService(ProjectReservation reservation) throws ManagerBeanException {
		removeReservationServiceDetail(reservation);
		
		IManagerBean reservationServiceBean = BeanManager.getManagerBean(ProjectReservationService.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationServiceBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationServiceBean.getList(criteria)) {
			ProjectReservationService reservationService = (ProjectReservationService)ito;
			reservationServiceBean.remove(reservationService);
		}
	}

	private void removeReservationRoom(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			reservationRoomBean.remove(reservationRoom);
		}
	}

	private void removeReservationGuest(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
			ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
			reservationGuestBean.remove(reservationGuest);
		}
	}

	private void removeReservation(ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.getId() != null) {
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			reservationBean.remove(reservation);
			
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			projectBean.remove(reservation.getProject());
		}
	}

	private void removeReservationServiceDetail(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationServiceDetail reservationServiceDetail = (ProjectReservationServiceDetail)ito;
			reservationServiceDetailBean.remove(reservationServiceDetail);
		}
	}

	private void removeReservationRoomDetail(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			getReservationUtils().removeProjectReservationRoomDetails(reservationRoom, true);
		}
	}

	private void cancelReservation(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		reservation.setStatus(ReservationStatus.CANCELLED);
		reservation.setModificationDate(new Date());
		reservationBean.update(reservation);
	}

	private SourceType findPosSource(SourceType[] sources, String type) {
		for (int i=0; i<sources.length; i++) {
			SourceType source = sources[i];
			if (source.getRequestorID() != null && source.getRequestorID().getType() != null && source.getRequestorID().getType().equals(type)) {
				return source;
			}
		}
		return null;
	}

	private ProfileInfo findProfileInfo(ResGuest[] guests, String type, String context) {
		for (int i=0; i<guests.length; i++) {
			ResGuest guest = guests[i];
			for (int j=0; j<guest.getProfiles().getProfileInfoArray().length; j++) {
				ProfileInfo profileInfo = guest.getProfiles().getProfileInfoArray(j);
				if (profileInfo.getUniqueID() != null && profileInfo.getUniqueID().getType().equals(type)) {
					String idContext = profileInfo.getUniqueID().getIDContext();
					if ((idContext == null && context == null) || (context.equals(idContext))) {
						return profileInfo;
					}
				}
			}
		}
		return null;
	}

	private String findTpaExtensionsAttribute(TPAExtensionsType tpaExtensionsType, String node, String attribute) {
		Node tpaExtension = findNode(tpaExtensionsType.getDomNode(), node, true);
		if (tpaExtension != null) {
			if (attribute == null) {
				Node tpaValue = tpaExtension.getFirstChild();
				if (tpaValue.getNodeType() == Node.TEXT_NODE) {
					return tpaValue.getNodeValue();
				}
			} else {
				return findAttribute(tpaExtension, attribute).getNodeValue();
			}
		}
		return null;
	}

	private String findComments(ResGlobalInfoType resGlobalInfoType) {
		String comments = "";
		if (resGlobalInfoType.getComments() != null) {
			for (int i=0; i<resGlobalInfoType.getComments().sizeOfCommentArray(); i++) {
				Comment comment = resGlobalInfoType.getComments().getCommentArray(i);
				for (int j=0; j<comment.sizeOfTextArray(); j++) {
					comments += comment.getTextArray(j).getStringValue() + " ";
				}
			}
		}
		return comments;
	}

	private double findTaxQuota(ResGlobalInfoType resGlobalInfoType, String type) {
		double taxQuota = 0;
		if (resGlobalInfoType.getTotal().getTaxes() != null) {
			for (int i=0; i<resGlobalInfoType.getTotal().getTaxes().sizeOfTaxArray(); i++) {
				TaxType taxType = resGlobalInfoType.getTotal().getTaxes().getTaxArray(i);
				if (taxType.getTaxDescriptionArray(0) != null && taxType.getTaxDescriptionArray(0).getTextArray(0).getStringValue().equals(type)) {
					taxQuota += taxType.getAmount().doubleValue();
				}
			}
		}
		return CommonUtil.round(taxQuota);
	}

	private Node findNode(Node parent, String nodeName, boolean deep) {
		Node nodeFound = null;

		NodeList childNodes = parent.getChildNodes();
		for (int i=0; i<childNodes.getLength(); i++) {
			if (nodeFound == null) {
				Node child = childNodes.item(i);
				if (child.getNodeName() != null && child.getNodeName().equals(nodeName)) {
					return child;
				}
				if (deep) {
					nodeFound = findNode(child, nodeName, deep);
				}
			} else {
				break;
			}
		}

		return nodeFound;
	}

	private Node findAttribute(Node parent, String nodeName) {
		NamedNodeMap childAttrs = parent.getAttributes();
		for (int i=0; i<childAttrs.getLength(); i++) {
			Node child = childAttrs.item(i);
			if (child.getNodeName() != null && child.getNodeName().equals(nodeName)) {
				return child;
			}
		}
		return null;
	}

	private String reservationSuccess() {
		OTAHotelResNotifRSDocument document = OTAHotelResNotifRSDocument.Factory.newInstance();
		OTAHotelResNotifRS response = document.addNewOTAHotelResNotifRS();
		response.addNewSuccess();
		HotelReservationIDsType reservationIds = response.addNewHotelReservations().addNewHotelReservation().addNewResGlobalInfo().addNewHotelReservationIDs();
		for (String reservationCode : successMap.keySet()) {
			HotelReservationID reservationId1 = reservationIds.addNewHotelReservationID();
			reservationId1.setResIDSource(SIRIUS);
			reservationId1.setResIDValue(reservationCode);

			HotelReservationID reservationId2 = reservationIds.addNewHotelReservationID();
			reservationId2.setResIDSource(PLS);
			reservationId2.setResIDValue(successMap.get(reservationCode).toString());
		}
		return document.toString();
	}

	private String reservationError(Exception ex) {
		return reservationError(new ReservationException(ex.getMessage(), 1));
	}

	private String reservationError(ReservationException ex) {
		OTAHotelResNotifRSDocument document = OTAHotelResNotifRSDocument.Factory.newInstance();
		OTAHotelResNotifRS response = document.addNewOTAHotelResNotifRS();
		ErrorType errorType = response.addNewErrors().addNewError();
		errorType.setType(Integer.toString(ex.getType()));
		errorType.setRecordID(ex.getRecord());
		errorType.setStringValue(ex.getMessage());
		return document.toString();
	}

}