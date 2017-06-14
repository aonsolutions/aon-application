package com.esferalia.aon.pms.reservation;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.opentravel.ota.x2003.x05.AmountType;
import org.opentravel.ota.x2003.x05.CommentType.Comment;
import org.opentravel.ota.x2003.x05.ErrorType;
import org.opentravel.ota.x2003.x05.GuaranteeType.GuaranteesAccepted.GuaranteeAccepted;
import org.opentravel.ota.x2003.x05.HotelReservationIDsType;
import org.opentravel.ota.x2003.x05.HotelReservationIDsType.HotelReservationID;
import org.opentravel.ota.x2003.x05.HotelReservationType;
import org.opentravel.ota.x2003.x05.HotelReservationsType;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRQDocument;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRSDocument;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRSDocument.OTAHotelResNotifRS;
import org.opentravel.ota.x2003.x05.POSType;
import org.opentravel.ota.x2003.x05.ParagraphType;
import org.opentravel.ota.x2003.x05.PaymentCardType;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.RequiredPaymentsType.GuaranteePayment.GuaranteeType;
import org.opentravel.ota.x2003.x05.ResGlobalInfoType;
import org.opentravel.ota.x2003.x05.ResGuestsType.ResGuest;
import org.opentravel.ota.x2003.x05.RoomStaysType;
import org.opentravel.ota.x2003.x05.RoomStaysType.RoomStay;
import org.opentravel.ota.x2003.x05.ServicesType;
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
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.data.DataResponse;
import com.code.aon.data.DataResponseDetail;
import com.code.aon.data.enumeration.DataResponseSource;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectAttachment;
import com.code.aon.project.enumeration.ProjectAttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.seller.Seller;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.enumeration.CreditCardType;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.sql.SQLBooking;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ReservationManager implements IReservationConstants {

	private static String FIRST_ROOM = "00";

	private ReservationUtils reservationUtils;
	private IPriceStrategy priceStrategy;
	private String xmlData;
	private boolean calculateCommission;
	private boolean selfBooking;
	private boolean multipleVat;
	private Map<String, List<Integer>> roomServicesMap;
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
		xmlData = reservationXml;
		successMap = new HashMap<String, Integer>();
		try {
			OTAHotelResNotifRQDocument document = OTAHotelResNotifRQDocument.Factory.parse(reservationXml.replaceAll("&", "&amp;"));
			if (document.validate()) {
				POSType posType = document.getOTAHotelResNotifRQ().getPOS();
				HotelReservationsType reservationsType = document.getOTAHotelResNotifRQ().getHotelReservations();
				for (int i=0; i<reservationsType.sizeOfHotelReservationArray(); i++) {
					getReservationUtils().init();
					ProjectReservation reservation = processReservation(reservationsType.getHotelReservationArray(i), posType);
					successMap.put(reservation.getCrsCode(), reservation.getId());
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
		ProjectReservation reservation = obtainReservation(reservationType);
		boolean attachSaved = createReservationAttach(reservation, actionType);

		if (actionType.equals(ADD_RESERVATION)) {
			reservation = addReservation(reservationType, posType, reservation);
		} else if (actionType.equals(MODIFY_RESERVATION)) {
			reservation = modifyReservation(reservationType, posType, reservation);
		} else if (actionType.equals(CANCEL_RESERVATION)) {
			reservation = cancelReservation(reservationType, posType, reservation);
		}

		if (!attachSaved) {
			createReservationAttach(reservation, actionType);
		}

		return reservation;
	}

	private ProjectReservation addReservation(HotelReservationType reservationType, POSType posType, ProjectReservation reservation) throws ReservationException {
		if (reservation == null) {
			reservation = new ProjectReservation();
			createReservation(reservationType, posType, reservation);
			return reservation;
		} else {
			return modifyReservation(reservationType, posType, reservation);
		}
	}

	private ProjectReservation modifyReservation(HotelReservationType reservationType, POSType posType, ProjectReservation reservation) throws ReservationException {
		if (reservation != null) {
			if (reservation.isActive() || reservation.isBlocked()) {
				try {
					if (isInvalidCheckInDate(reservationType.getResGlobalInfo().getTimeSpan().getStart().getTime())) {
						throw new ReservationException("Invalid Check-in Date", reservation.getCrsCode(), 381);
					}
					delayForConcurrence(reservation);
					if (isReservationRoomAssigned(reservation)) {
						removeReservationRoomDetail(reservation, true);
					}
					removeReservationPromotion(reservation);
					removeReservationService(reservation);
					removeReservationRoom(reservation);
					removeReservationGuest(reservation);
					createReservation(reservationType, posType, reservation);
				} catch (ManagerBeanException ex) {
					throw new ReservationException("Unknown error: " + ex.getMessage(), reservation.getCrsCode(), 1);
				}
				return reservation;
			} else if (reservation.isCancelled()) {
				throw new ReservationException("Reservation already cancelled, can not be modified", reservation.getCrsCode(), 255);
			} else {
				throw new ReservationException("Reservation already invoiced, can not be modified", reservation.getCrsCode(), 255);
			}
		} else {
			return addReservation(reservationType, posType, reservation);
		}
	}

	private ProjectReservation cancelReservation(HotelReservationType reservationType, POSType posType, ProjectReservation reservation) throws ReservationException {
		if (reservation == null) {
			reservation = addReservation(reservationType, posType, reservation);
		}

		if (reservation != null) {
			if (reservation.isActive() || reservation.isBlocked()) {
				try {
					removeReservationRoomDetail(reservation, false);
					cancelReservation(reservation);
				} catch (ManagerBeanException ex) {
					throw new ReservationException("Unknown error: " + ex.getMessage(), reservation.getCrsCode(), 1);
				}
				return reservation;
			} else if (reservation.isCancelled()) {
				return reservation;
			} else {
				throw new ReservationException("Reservation already invoiced, can not be cancelled", reservation.getCrsCode(), 255);
			}
		} else {
			String reservationCrsCode = findReservationId(reservationType.getResGlobalInfo(), SIRIUS); 
			throw new ReservationException("Reservation not found with search criteria, can not be cancelled", reservationCrsCode, 284);
		}
	}

	private void createReservation(HotelReservationType reservationType, POSType posType, ProjectReservation reservation) throws ReservationException {
		boolean isNewReservation = reservation.getId() == null;
		String reservationCrsCode = findReservationId(reservationType.getResGlobalInfo(), SIRIUS);
		try {
			Hotel hotel = getReservationUtils().obtainHotel(reservationType.getRoomStays().getRoomStayArray(0).getBasicPropertyInfo().getHotelCode());
			getReservationUtils().setDomain(hotel.getDomain());
			String reservationCode = findReservationId(reservationType.getResGlobalInfo(), EXT, TYPE_GROUP);
			String operationDate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, DATE);
			String operationTime = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, TIME);
			Date checkIn = reservationType.getResGlobalInfo().getTimeSpan().getStart().getTime();
			Date checkOut = reservationType.getResGlobalInfo().getTimeSpan().getEnd().getTime();
			if (isInvalidCheckInDate(checkIn)) {
				throw new ReservationException("Invalid Check-in Date", reservationCrsCode, 381);
			}
			SourceType sellerSource = findPosSource(posType.getSourceArray(), CRO_SOURCE);
			Seller seller = getReservationUtils().obtainSeller(sellerSource);
			ProfileInfo agencyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), AGENCY_TYPE, SOLRES);
			if (agencyInfo == null) {
				agencyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), AGENCY_TYPE, IATA);
			}
			Customer agency = reservation.getAgency();
			if (agency == null || agency.getId() == null) {
				agency = getReservationUtils().obtainAgency(agencyInfo);
			}
			double agencyCommissionPercent = getReservationUtils().obtainAgencyCommissionPercent(agencyInfo);
			double agencyCommissionAmount = getReservationUtils().obtainAgencyCommissionAmount(agencyInfo);
			calculateCommission = agencyCommissionAmount != 0 && getReservationUtils().isAgencyCommission(agency);
			selfBooking = getReservationUtils().isAgencySelfBooking(agency);
			String agencyRebate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT_MODE, null);
			ProfileInfo companyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), COMPANY_TYPE, SOLRES);
			Customer company = reservation.getCompany();
			if (company == null || company.getId() == null) {
				company = getReservationUtils().obtainCompany(companyInfo);
			}
			String discountPercent = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, PERCENT);
			String discountAmount = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, AMOUNT);
			String bookingHolder = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), BOOKING_HOLDER, null);
			String token = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), TOKEN_CONEX_FLOW, null);
			String remarks =  findComments(reservationType.getResGlobalInfo());
			List<String> tariffList = getTariffList(reservationType.getRoomStays());
			boolean prepay = isTariffPrepaid(reservationType.getResGlobalInfo(), tariffList) && !isTariffPrepaidException(tariffList, seller, agency, company);
			String bankTransaction = findPrepayInfo(reservationType.getResGlobalInfo(), BANK_TRANSACTION);
			if (bankTransaction == null && StringUtils.indexOf(remarks, BANK_TRANSACTION_COMMENT) >= 0) {
				bankTransaction = StringUtils.substringBetween(remarks, BANK_TRANSACTION_COMMENT + "-", "_");
			}
			String prepayPayment = findPrepayInfo(reservationType.getResGlobalInfo(), PAYMENT_TRANSACTION);
			boolean notRefundable = (!prepay) ? isTariffNotRefundable(reservationType.getResGlobalInfo(), tariffList) : false;
			PaymentCardType creditCard = findCreditCard(reservationType.getResGlobalInfo());
			double taxableBase = CommonUtil.round(reservationType.getResGlobalInfo().getTotal().getAmountBeforeTax().doubleValue());
			double vatQuota = findTaxQuota(reservationType.getResGlobalInfo(), VAT_TAX);
			double vatPercent = findTaxPercent(reservationType.getResGlobalInfo(), VAT_TAX);
			double otherTaxQuota = findTaxQuota(reservationType.getResGlobalInfo(), OTHER_TAX);
			double total = CommonUtil.round(reservationType.getResGlobalInfo().getTotal().getAmountAfterTax().doubleValue());
			if (selfBooking && total > 0 && total < 1) {
				total = 0;
			}
			if (taxableBase == total && total > 0 && vatPercent != 0) {
				throw new ReservationException("Reservation Total is not correct", reservation.getCrsCode(), 197);
			}


			reservation.setHotel(hotel);
			reservation.setHotelReservation(hotel);
			reservation.setCode(reservationCode);
			reservation.setStartDate(DateUtils.truncate(checkIn, Calendar.DATE));
			reservation.setEndDate(DateUtils.truncate(checkOut, Calendar.DATE));
			reservation.setStartTime(DateUtils.addHours(reservation.getStartDate(), 14));
			reservation.setEndTime(DateUtils.addHours(reservation.getEndDate(), 12));
			reservation.setSeller(seller);
			reservation.setAgency(agency);
			reservation.setAgencyCommissionPercent(agencyCommissionPercent);
			reservation.setAgencyCommissionAmount(agencyCommissionAmount);
			reservation.setAgencyRebate(agencyRebate != null && agencyRebate.equals(AGENCY_REBATE));
			reservation.setCompany(company);
			reservation.setDiscountPercent((discountPercent != null) ? Double.parseDouble(discountPercent) : 0);
			reservation.setDiscountAmount((discountAmount != null) ? Double.parseDouble(discountAmount) : 0);
			reservation.setBookingHolder(getReservationUtils().obtainBookingHolder(bookingHolder));
			reservation.setTaxableBase(taxableBase);
			reservation.setVatQuota(vatQuota);
			reservation.setVatPercent(vatPercent);
			reservation.setOtherTaxQuota(otherTaxQuota);
			reservation.setTotal(calculateCommission ? CommonUtil.round(total - agencyCommissionAmount) : total);
			reservation.setRemarks(remarks);
			reservation.setSource(isNewReservation ? ReservationSource.CRS : reservation.getSource());
			reservation.setCrsCode(reservationCrsCode);
			reservation.setAdvance((NumberUtils.isNumber(prepayPayment)) ? Double.parseDouble(prepayPayment) : 0);
			reservation.setPrepay(prepay);
			reservation.setBankTransaction(bankTransaction);
			reservation.setNotRefundable(notRefundable);
			reservation.setToken((!prepay) ? token : null);
			reservation.setCreditCardNumber((creditCard != null) ? creditCard.getCardNumber() : null);
			reservation.setCreditCardExpirationMonth((creditCard != null) ? StringUtils.substring(creditCard.getExpireDate(), 0, 2) : null);
			reservation.setCreditCardExpirationYear((creditCard != null) ? StringUtils.substring(creditCard.getExpireDate(), -2) : null);
			reservation.setCreditCardType((creditCard != null && creditCard.getCardType() != null) ? CreditCardType.valueOf(creditCard.getCardType()) : null);
			reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
			reservation.setStatus(ReservationStatus.ACTIVE);

			getReservationUtils().fillProject(reservation);
			reservation.getProject().setDomain(hotel.getDomain());
			reservation.setDomain(hotel.getDomain());

			getReservationUtils().verifySellerEntity(reservation, sellerSource);
			getReservationUtils().verifyAgencyEntity(reservation, agencyInfo);
			getReservationUtils().verifyCompanyEntity(reservation, companyInfo);
			getReservationUtils().verifyOperationDate(reservation, operationDate, operationTime);

			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			if (reservation.getId() == null) {
				reservation.setCreationUser(CRS);
				reservation.setCreationDate(new Date());
				reservation = (ProjectReservation)reservationBean.insert(reservation);
			} else {
				reservation.setModificationUser(CRS);
				reservation.setModificationDate(new Date());
				reservation = (ProjectReservation)reservationBean.update(reservation);
			}

			createReservationGuest(reservationType, reservation);
			createReservationRoom(reservationType, reservation);
			createReservationService(reservationType, reservation);
			createReservationPromotion(reservationType, reservation);

			reservation = finalizeReservation(reservation);
		} catch (Exception ex) {
			if (isNewReservation && reservation.getId() != null) {
				try {
					removeReservationPromotion(reservation);
					removeReservationAttach(reservation);
					removeReservationService(reservation);
					removeReservationRoom(reservation);
					removeReservationGuest(reservation);
					removeReservation(reservation);
				} catch (ManagerBeanException exc) {
				}
			}

			if (ex instanceof ReservationException) {
				ReservationException rex = (ReservationException)ex;
				rex.setRecord(reservationCrsCode);
				throw rex;
			} else {
				throw new ReservationException("Unknown error: " + ex.getMessage(), reservationCrsCode, 1);
			}
		}
	}

	private boolean createReservationAttach(ProjectReservation reservation, String actionType) throws ManagerBeanException {
		if (reservation != null) {
			ProjectAttachment projectAttach = new ProjectAttachment();
			projectAttach.setDomain(reservation.getDomain());
			projectAttach.setProject(reservation.getProject());
			projectAttach.setMimeType(MimeType.MIME_XML);
			projectAttach.setDescription(getReservationUtils().obtainCrsAttachDescription(actionType) + "#");
			projectAttach.setData(xmlData.getBytes());
			projectAttach.setSecurityLevel(SecurityLevel.OFFICIAL);
			projectAttach.setAttachDate(new Date());
			projectAttach.setAttachType(ProjectAttachmentType.CRS);
			projectAttach.setCreationUser(CRS);
			projectAttach.setCreationDate(new Date());

			BeanManager.getManagerBean(ProjectAttachment.class).insert(projectAttach);
			return true;
		}
		return false;
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
				reservationGuest.setSurname(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().getSurname().toUpperCase());
				if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().sizeOfGivenNameArray() > 0) {
					reservationGuest.setName(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getPersonName().getGivenNameArray(0).toUpperCase());
				} else {
					reservationGuest.setName(StringUtils.EMPTY);
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
					if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).sizeOfAddressLineArray() > 0) {
						reservationGuest.setAddress(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getAddressLineArray(0));
					}
					if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getPostalCode() != null) {
						reservationGuest.setZip(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getPostalCode());
					}
					if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCityName() != null) {
						reservationGuest.setCity(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCityName());
					}
					if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getStateProv() != null) {
						reservationGuest.setProvince(guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getStateProv().getStringValue());
					}
					if (guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCountryName() != null) {
						String countryValue = guest.getProfiles().getProfileInfoArray(0).getProfile().getCustomer().getAddressArray(0).getCountryName().getCode();
						reservationGuest.setCountry(Country.obtainCountry(countryValue));
					}
				}
				reservationGuest.setCreationUser(CRS);
				reservationGuest.setCreationDate(new Date());
				reservationGuestBean.insert(reservationGuest);
				if (reservationGuest.getGuestIndex() == 1) {
					getReservationUtils().fillProject(reservation);
				}
			}
		}
	}

	private void createReservationRoom(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		roomServicesMap = new HashMap<String, List<Integer>>();
		int totalPax = 0;
		for (int i=0; i<reservationType.getRoomStays().sizeOfRoomStayArray(); i++) {
			RoomStay stay = reservationType.getRoomStays().getRoomStayArray(i);
			if (stay.getRoomTypes() != null && stay.getRoomTypes().sizeOfRoomTypeArray() > 0) {
				int totalAdults= 0;
				int totalChildren= 0;
				int roomUnits = stay.getRoomTypes().getRoomTypeArray(0).getNumberOfUnits();
				if (stay.getGuestCounts() != null && stay.getGuestCounts().sizeOfGuestCountArray() > 0) {
					for (int k=0; k<stay.getGuestCounts().sizeOfGuestCountArray(); k++) {
						String type = stay.getGuestCounts().getGuestCountArray(k).getAgeQualifyingCode();
						int guestCount = stay.getGuestCounts().getGuestCountArray(k).getCount();
						if (type != null && type.equals(CHILDREN_COUNT)) {
							totalChildren += guestCount;
						} else {
							totalAdults += guestCount;
						}
					}
				}
				totalPax += totalAdults + totalChildren;

				int adults = totalAdults / roomUnits;
				int children = totalChildren / roomUnits;
				for (int j=0; j<roomUnits; j++) {
					int roomAdults = (j==0) ? totalAdults - (adults * (roomUnits - 1)) : adults;
					int roomChildren = (j==0) ? totalChildren - (children * (roomUnits - 1)) : children;
					ProjectReservationRoom reservationRoom = insertReservationRoom(reservation, stay, roomAdults, roomChildren);

					if (!roomServicesMap.containsKey(FIRST_ROOM)) {
						List<Integer> roomList = new LinkedList<Integer>();
						roomList.add(reservationRoom.getId());
						roomServicesMap.put(FIRST_ROOM, roomList);
					}
					if (stay.getServiceRPHs() != null) {
						for (int k=0; k<stay.getServiceRPHs().sizeOfServiceRPHArray(); k++) {
							List<Integer> roomList = roomServicesMap.get(stay.getServiceRPHs().getServiceRPHArray(k).getRPH());
							if (roomList == null) {
								roomList = new LinkedList<Integer>();
							}
							roomList.add(reservationRoom.getId());
							roomServicesMap.put(stay.getServiceRPHs().getServiceRPHArray(k).getRPH(), roomList);
						}
					}
				}
			}
		}

		reservation.setRemarks(reservation.getRemarks() + "NUMERO TOTAL DE PERSONAS: " + totalPax + "\n");
	}

	private void createReservationService(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		double agreedPrice = getReservationUtils().getAgreedPriceValue(reservation.getCrsCode());
		Item agreedPriceItem = (agreedPrice > 0) ? getReservationUtils().obtainBestPriceDiscountItem() : null;
		double autoDiscount = getReservationUtils().getAutoDiscountValue(reservation.getAgency());
		Item autoDiscountItem = (autoDiscount > 0) ? getReservationUtils().obtainAutoDiscountItem() : null;

		calculateRealDiscountPercent(reservationType.getServices(), reservation, (agreedPriceItem == null) ? agreedPrice : 0);

		ProjectReservationServiceDetail reservationServiceDetail = null;
		Date fromDate = DateUtils.truncate(reservation.getStartDate(), Calendar.DATE);
		Date toDate = DateUtils.truncate(reservation.getEndDate(), Calendar.DATE);
		double calculatedTaxableBase = 0;
		for (int i=0; i<reservationType.getServices().sizeOfServiceArray(); i++) {
			Service service = reservationType.getServices().getServiceArray(i);
			Map<Date, List<Double>> pricesMap = getReservationUtils().obtainPricesMap(reservation, service, fromDate, toDate);
			if (pricesMap != null) {
				Item item = getReservationUtils().obtainServiceItem(reservation, service);
				int totalQuantity = getReservationUtils().obtainPricesMapSize(pricesMap);
				boolean breakdown = getReservationUtils().isServiceBreakdown(item);
				if (breakdown) {
					for (int j=0; j<totalQuantity; j++) {
						ProjectReservationService reservationService = insertReservationService(reservation, service, item);
						for (Date date=DateUtils.truncate(fromDate, Calendar.DATE); isServiceDateValid(date, toDate); date=DateUtils.addDays(date, 1)) {
							if (pricesMap.containsKey(date)) {
								double price = (pricesMap.get(date).size() > j) ? pricesMap.get(date).get(j) : 0;
								reservationServiceDetail = insertReservationServiceDetail(reservationService, date, 1, price);
								calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + reservationServiceDetail.getTaxableBase(), 4);
							}
						}
					}
				} else {
					ProjectReservationService reservationService = insertReservationService(reservation, service, item);
					for (Date date=DateUtils.truncate(fromDate, Calendar.DATE); isServiceDateValid(date, toDate); date=DateUtils.addDays(date, 1)) {
						if (pricesMap.containsKey(date)) {
							Map<Double, Integer> quantityPerPriceMap = getReservationUtils().obtainQuantityPerPriceMap(pricesMap.get(date));
							for (double price : quantityPerPriceMap.keySet()) {
								int quantity = quantityPerPriceMap.get(price);
								reservationServiceDetail = insertReservationServiceDetail(reservationService, date, quantity, price);
								calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + reservationServiceDetail.getTaxableBase(), 4);
							}
						}
					}
				}
			}
		}

		if (agreedPrice > 0) {
			if (agreedPriceItem != null) {
				calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + createAgreedPriceDiscountService(agreedPriceItem, agreedPrice, reservation), 4);
			}
			reservation.setTotal(CommonUtil.round(agreedPrice));
		}
		if (autoDiscount > 0 && autoDiscountItem != null) {
			calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + createAutoDiscountService(autoDiscountItem, autoDiscount, reservation), 4);

			double autoDiscountTotal = CommonUtil.round(reservation.getTotal() * autoDiscount / 100);
			reservation.setTotal(CommonUtil.round(reservation.getTotal() - autoDiscountTotal));
		}

		/** Chequeos de integridad de Bases y Totales. **/
		if (!multipleVat) {
			double taxableBase = CommonUtil.round(reservation.getTotal() / (1 + reservation.getVatPercent() / 100), 4);
			if (taxableBase != calculatedTaxableBase) {
				double baseDiff = CommonUtil.round(taxableBase - calculatedTaxableBase, 4);
				/** Errores por redondeos. **/
				if (Math.abs(baseDiff) <= 0.01) {
					reservationServiceDetail.setPrice(CommonUtil.round(reservationServiceDetail.getPrice() + baseDiff / reservationServiceDetail.getQuantity(), 4));
					reservationServiceDetail.setTaxableBase(CommonUtil.round(reservationServiceDetail.getTaxableBase() + baseDiff, 4));
					BeanManager.getManagerBean(ProjectReservationServiceDetail.class).update(reservationServiceDetail);
					calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + baseDiff, 4);
				} else {
					throw new ReservationException("Reservation Total is not correct", reservation.getCrsCode(), 197);
				}
			}
		} else {
			double total = getPriceStrategy().getTotalPrice(reservation, reservation.getCustomer());
			if (total != reservation.getTotal()) {
				double totalDiff = CommonUtil.round(reservation.getTotal() - total, 2);
				/** Errores por redondeos. **/
				if (Math.abs(totalDiff) == 0.01) {
					reservationServiceDetail.setPrice(CommonUtil.round(reservationServiceDetail.getPrice() + totalDiff / reservationServiceDetail.getQuantity(), 4));
					reservationServiceDetail.setTaxableBase(CommonUtil.round(reservationServiceDetail.getTaxableBase() + totalDiff, 4));
					BeanManager.getManagerBean(ProjectReservationServiceDetail.class).update(reservationServiceDetail);
					calculatedTaxableBase = CommonUtil.round(calculatedTaxableBase + totalDiff, 4);
				} else {
					throw new ReservationException("Reservation Total is not correct", reservation.getCrsCode(), 197);
				}
			}
		}

		reservation.setTaxableBase(CommonUtil.round(calculatedTaxableBase));
		reservation.setVatQuota(CommonUtil.round(reservation.getTotal() - reservation.getTaxableBase() - reservation.getOtherTaxQuota()));
		if (agreedPrice > 0) {
			NumberFormat formatter = new DecimalFormat("#,##0.00");
			reservation.setRemarks(reservation.getRemarks() + "PRECIO PACTADO: " + formatter.format(agreedPrice) + "\n");
		}
	}

	private void createReservationPromotion(HotelReservationType reservationType, ProjectReservation reservation) throws ManagerBeanException {
		Node promotionNode = findNode(reservationType.getTPAExtensions().getDomNode(), PROMOTION_CODE, true);
		if (promotionNode != null) {
			IManagerBean dataResponseBean = BeanManager.getManagerBean(DataResponse.class);
			DataResponse dataResponse = new DataResponse();
			dataResponse.setDomain(reservation.getDomain());
			dataResponse.setCode(PROMOTION);
			dataResponse.setResponseDate(new Date());
			dataResponse.setSource(DataResponseSource.PROJECT);
			dataResponse.setSourceId(reservation.getId());
			dataResponse.setCreationUser(CRS);
			dataResponse.setCreationDate(new Date());
			dataResponse = (DataResponse)dataResponseBean.insert(dataResponse);

			IManagerBean dataResponseDetailBean = BeanManager.getManagerBean(DataResponseDetail.class);
			DataResponseDetail dataResponseDetail = new DataResponseDetail();
			dataResponseDetail.setDomain(reservation.getDomain());
			dataResponseDetail.setDataResponse(dataResponse);
			dataResponseDetail.setDataVariable(CODE);
			dataResponseDetail.setDataValue(findAttribute(promotionNode, NAME).getNodeValue());
			dataResponseDetail.setCreationUser(CRS);
			dataResponseDetail.setCreationDate(new Date());
			dataResponseDetailBean.insert(dataResponseDetail);

			Node discountNode = promotionNode.getFirstChild();
			if (discountNode != null) {
				dataResponseDetail = new DataResponseDetail();
				dataResponseDetail.setDomain(reservation.getDomain());
				dataResponseDetail.setDataResponse(dataResponse);
				dataResponseDetail.setDataVariable(DESCRIPTION);
				dataResponseDetail.setDataValue(findAttribute(discountNode, DESCRIPTION).getNodeValue());
				dataResponseDetail.setCreationUser(CRS);
				dataResponseDetail.setCreationDate(new Date());
				dataResponseDetailBean.insert(dataResponseDetail);

				Node percentNode = findAttribute(discountNode, PERCENT);
				if (percentNode != null) {
					dataResponseDetail.setDataVariable(DISCOUNT + PERCENT);
					dataResponseDetail.setDataValue(percentNode.getNodeValue());
				}
				Node amountBeforeTaxNode = findAttribute(discountNode, AMOUNT_BEFORE_TAX);
				if (amountBeforeTaxNode != null) {
					dataResponseDetail.setDataVariable(DISCOUNT + AMOUNT_BEFORE_TAX);
					dataResponseDetail.setDataValue(amountBeforeTaxNode.getNodeValue());
				}
				Node amountAfterTaxNode = findAttribute(discountNode, AMOUNT_AFTER_TAX);
				if (amountAfterTaxNode != null) {
					dataResponseDetail.setDataVariable(DISCOUNT + AMOUNT_AFTER_TAX);
					dataResponseDetail.setDataValue(amountAfterTaxNode.getNodeValue());
				}
				dataResponseDetailBean.insert(dataResponseDetail);
			}
		}
	}

	private ProjectReservation finalizeReservation(ProjectReservation reservation) throws ManagerBeanException{
		if (reservation.isPrepay() || reservation.isNotRefundable()) {
			reservation.setAdvance((reservation.getAdvance() != 0) ? reservation.getAdvance() : reservation.getTotal());
		}
		reservation.setPenaltyAmount(getReservationUtils().obtainCancellationPenaltyAmount(reservation));
		reservation.setPenaltyDate(getReservationUtils().obtainCancellationPenaltyDate(reservation));
		return (ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).update(reservation);
	}

	private boolean isInvalidCheckInDate(Date checkIn) {
		return DateUtils.truncate(checkIn, Calendar.DATE).before(DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -1));
	}

	private void calculateRealDiscountPercent(ServicesType servicesType, ProjectReservation reservation, double agreedPrice) throws ReservationException {
		double discountPercent = 0;
		if (calculateCommission || reservation.getDiscountAmount() != 0 || agreedPrice > 0) {
			double totalServices = 0;
			for (int i=0; i<servicesType.sizeOfServiceArray(); i++) {
				Service service = servicesType.getServiceArray(i);
				for (int j=0; j<service.sizeOfPriceArray(); j++) {
					AmountType price = service.getPriceArray(j);
					if (price.getEffectiveDate() == null) {
						totalServices = CommonUtil.round(totalServices + price.getTotal().getAmountBeforeTax().doubleValue());
					}
				}
			}

			if (agreedPrice > 0) {
				discountPercent = (1 - agreedPrice / totalServices) * 100;
			} else if (calculateCommission || reservation.getDiscountAmount() != 0) {
				double totalDiscount = calculateCommission ? reservation.getAgencyCommissionAmount() : reservation.getDiscountAmount();
				/** Tendria que ser cero, pero se admite un error de +- 1 centimo por error de redondeo en los calculos de Idiso al enviar la Reserva.
					El discountAmount VIENE YA aplicado sobre el Total en el XML, no asi el agencyCommissionAmount (por eso se descuenta previamente del Total) **/
				if (Math.abs(CommonUtil.round(totalServices - reservation.getTotal() - totalDiscount)) <= 0.01) {
					discountPercent = (1 - reservation.getTotal() / totalServices) * 100;
				} else {
					if (calculateCommission) {
						throw new ReservationException("Reservation Commission Amount is not correct", reservation.getCrsCode(), 197);
					} else {
						throw new ReservationException("Reservation Discount Amount is not correct", reservation.getCrsCode(), 197);
					}
				}
			}
		}
		reservation.setRealDiscountPercent(discountPercent);
	}

	private boolean isServiceDateValid(Date serviceDate, Date reservationEndDate) {
		return !DateUtils.isSameDay(serviceDate, reservationEndDate) && serviceDate.before(reservationEndDate);
	}

	private double createAgreedPriceDiscountService(Item item, double agreedPrice, ProjectReservation reservation) throws ManagerBeanException {
		String serviceRPH = "01";
		for (String key : roomServicesMap.keySet()) {
			if (Integer.parseInt(key) >= Integer.parseInt(serviceRPH)) {
				serviceRPH = StringUtils.leftPad(Integer.toString(Integer.parseInt(key) + 1), 2, "0") ;
			}
		}
		roomServicesMap.put(serviceRPH, roomServicesMap.get(FIRST_ROOM));
		String serviceInventaryCode = item.getProduct().getCode();
		ProjectReservationService reservationService = insertReservationService(reservation, serviceRPH, serviceInventaryCode, item);

		Date date = DateUtils.truncate(reservation.getStartDate(), Calendar.DATE);
		double agreedPriceTotal = CommonUtil.round(reservation.getTotal() - agreedPrice);
		ProjectReservationServiceDetail reservationServiceDetail = insertReservationServiceDetail(reservationService, date, -1, agreedPriceTotal);
		return reservationServiceDetail.getTaxableBase();
	}

	private double createAutoDiscountService(Item item, double autoDiscount, ProjectReservation reservation) throws ManagerBeanException {
		String serviceRPH = "01";
		for (String key : roomServicesMap.keySet()) {
			if (Integer.parseInt(key) >= Integer.parseInt(serviceRPH)) {
				serviceRPH = StringUtils.leftPad(Integer.toString(Integer.parseInt(key) + 1), 2, "0") ;
			}
		}
		roomServicesMap.put(serviceRPH, roomServicesMap.get(FIRST_ROOM));
		String serviceInventaryCode = item.getProduct().getCode();
		ProjectReservationService reservationService = insertReservationService(reservation, serviceRPH, serviceInventaryCode, item);

		Date date = DateUtils.truncate(reservation.getStartDate(), Calendar.DATE);
		double autoDiscountTotal = CommonUtil.round(reservation.getTotal() * autoDiscount / 100);
		ProjectReservationServiceDetail reservationServiceDetail = insertReservationServiceDetail(reservationService, date, -1, autoDiscountTotal);
		return reservationServiceDetail.getTaxableBase();
	}

	private ProjectReservationRoom insertReservationRoom(ProjectReservation reservation, RoomStay stay, int adults, int children) 
			throws ManagerBeanException, ReservationException {
		ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
		reservationRoom.setProjectReservation(reservation);
		reservationRoom.setDomain(getReservationUtils().getDomain());
		reservationRoom.setRoomIndex(stay.getIndexNumber());
		reservationRoom.setRoomCode(stay.getRoomTypes().getRoomTypeArray(0).getRoomTypeCode());
		reservationRoom.setItem(getReservationUtils().obtainRoomItem(reservation, stay.getRoomTypes().getRoomTypeArray(0)));
		reservationRoom.setAllotmentRateCode(getReservationUtils().obtainAllotmentRateCode(reservation));
		if (stay.getRatePlans() != null && stay.getRatePlans().sizeOfRatePlanArray() > 0) {
			reservationRoom.setRatePlan(stay.getRatePlans().getRatePlanArray(0).getRatePlanCode());
			reservationRoom.setTariff(getReservationUtils().obtainRoomTariff(reservation, stay.getRatePlans().getRatePlanArray(0)));
		}
		reservationRoom.setAdults(adults);
		reservationRoom.setChildren(children);
		reservationRoom.setCreationUser(CRS);
		reservationRoom.setCreationDate(new Date());
		reservationRoom = (ProjectReservationRoom)BeanManager.getManagerBean(ProjectReservationRoom.class).insert(reservationRoom);

		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(reservation.getDomain()));
			SQLBooking.insert(connection, reservationRoom);
		} catch (Throwable ex) {
			throw new ReservationException("Unknown error: " + ex.getMessage(), reservation.getCrsCode(), 1);
		} finally {
			SQLUtils.closeQuietly(connection);
		}

		return reservationRoom;
	}

	private ProjectReservationService insertReservationService(ProjectReservation reservation, Service service, Item item) throws ManagerBeanException {
		return insertReservationService(reservation, service.getServiceRPH(), service.getServiceInventoryCode(), item);
	}

	private ProjectReservationService insertReservationService(ProjectReservation reservation, String serviceRPH, String serviceInventoryCode, Item item) 
			throws ManagerBeanException {
		ProjectReservationService reservationService = new ProjectReservationService();
		reservationService.setProjectReservation(reservation);
		reservationService.setDomain(getReservationUtils().getDomain());
		reservationService.setServiceIndex(Integer.parseInt(serviceRPH));
		reservationService.setServiceCode(serviceInventoryCode);
		reservationService.setItem(item);
		reservationService.setDescription(item.getProduct().getName());
		reservationService.setMealPlan(getReservationUtils().obtainMealPlan(item.getDetail()));
		if (roomServicesMap.containsKey(serviceRPH)) {
			List<Integer> roomList = roomServicesMap.get(serviceRPH);
			if (roomList.size() > 0) {
				reservationService.setProjectReservationRoom(roomList.get(0));
				roomList.remove(0);
			}
		}
		reservationService.setCreationUser(CRS);
		reservationService.setCreationDate(new Date());

		return (ProjectReservationService)BeanManager.getManagerBean(ProjectReservationService.class).insert(reservationService);
	}

	private ProjectReservationServiceDetail insertReservationServiceDetail(ProjectReservationService reservationService, Date date, double quantity, double price)
			throws ManagerBeanException {
		if (selfBooking && price > 0 && price < 1) {
			price = 0;
		}
		Date taxDate = reservationService.getProjectReservation().getStartDate();
		double vatPercent = reservationService.getItem().getProduct().getVat().getDatedPercentage(taxDate);
		price = CommonUtil.round(price / (1 + vatPercent / 100), 4);
		if (price != 0 && vatPercent != reservationService.getProjectReservation().getVatPercent()) {
			multipleVat = true;
		}

		ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
		reservationServiceDetail.setProjectReservationService(reservationService);
		reservationServiceDetail.setDomain(getReservationUtils().getDomain());
		reservationServiceDetail.setEffectiveDate(date);
		reservationServiceDetail.setQuantity(quantity);
		reservationServiceDetail.setPrice(price);
		reservationServiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(reservationServiceDetail));

		return (ProjectReservationServiceDetail)BeanManager.getManagerBean(ProjectReservationServiceDetail.class).insert(reservationServiceDetail);
	}

	private ProjectReservation obtainReservation(HotelReservationType reservationType) throws ManagerBeanException {
		String reservationCrsCode = findReservationId(reservationType.getResGlobalInfo(), SIRIUS);
		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), reservationCrsCode);
		List<ITransferObject> reservationList = reservationBean.getList(criteria);
		return (reservationList.size() > 0) ? (ProjectReservation)reservationList.get(0) : null;
	}

	private void delayForConcurrence(ProjectReservation reservation) throws ManagerBeanException {
		try {
			if (reservation.isSourceRequest()) {
				int rooms = reservation.getRoomCount();
				IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
				Criteria criteria = new Criteria();
				String alias = IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID;
				criteria.addEqualExpression(reservationServiceDetailBean.getFieldName(alias), reservation.getId());
				for (int i=0; i<30; i++) {
					if (reservationServiceDetailBean.getCount(criteria) >= (rooms * reservation.getNights())) {
						break;
					}
					Thread.sleep(1000);
				}
			} else {
				long timeToSleep = new Date().getTime() - reservation.getCreationDate().getTime();
				if (timeToSleep > 0 && timeToSleep < 5000) {
					Thread.sleep(timeToSleep);
				}
			}
		} catch (InterruptedException e) {}
	}

	private boolean isReservationRoomAssigned(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservation.getId());
		return (reservationRoomDetailBean.getCount(criteria) > 0);
	}

	private void removeReservationAttach(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean projectAttachBean = BeanManager.getManagerBean(ProjectAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(projectAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID), reservation.getId());
		for (ITransferObject ito : projectAttachBean.getList(criteria)) {
			ProjectAttachment projectAttachment = (ProjectAttachment)ito;
			projectAttachBean.remove(projectAttachment);
		}
	}

	private void removeReservationPromotion(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean dataResponseDetailBean = BeanManager.getManagerBean(DataResponseDetail.class);
		IManagerBean dataResponseBean = BeanManager.getManagerBean(DataResponse.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(dataResponseBean.getFieldName(IEntityAlias.DATA_RESPONSE_SOURCE), DataResponseSource.PROJECT);
		criteria.addEqualExpression(dataResponseBean.getFieldName(IEntityAlias.DATA_RESPONSE_SOURCE_ID), reservation.getId());
		criteria.addEqualExpression(dataResponseBean.getFieldName(IEntityAlias.DATA_RESPONSE_CODE), PROMOTION);
		for (ITransferObject ito : dataResponseBean.getList(criteria)) {
			DataResponse dataResponse = (DataResponse)ito;
			criteria = new Criteria();
			criteria.addEqualExpression(dataResponseDetailBean.getFieldName(IEntityAlias.DATA_RESPONSE_DETAIL_DATA_RESPONSE_ID), dataResponse.getId());
			for (ITransferObject itr : dataResponseDetailBean.getList(criteria)) {
				DataResponseDetail dataResponseDetail = (DataResponseDetail)itr;
				dataResponseDetailBean.remove(dataResponseDetail);
			}
			dataResponseBean.remove(dataResponse);
		}
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

	private void removeReservationRoom(ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(reservation.getDomain()));
			IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
			for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
				ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
				SQLBooking.delete(connection, reservationRoom);
				reservationRoomBean.remove(reservationRoom);
			}
		} catch (ManagerBeanException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new ReservationException("Unknown error: " + ex.getMessage(), reservation.getCrsCode(), 1);
		} finally {
			SQLUtils.closeQuietly(connection);
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
			BeanManager.getManagerBean(ProjectReservation.class).remove(reservation);
			BeanManager.getManagerBean(Project.class).remove(reservation.getProject());
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

	private void removeReservationRoomDetail(ProjectReservation reservation, boolean removeService) throws ManagerBeanException {
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), reservation.getId());
		for (ITransferObject ito : reservationRoomBean.getList(criteria)) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)ito;
			getReservationUtils().removeProjectReservationRoomDetails(reservationRoom, removeService, null);
		}
	}

	private void cancelReservation(ProjectReservation reservation) throws ManagerBeanException, ReservationException {
		Connection connection = null;
		try {
			getReservationUtils().setDomain(reservation.getDomain());
			reservation.getProject().setActive(false);
			reservation.setStatus(ReservationStatus.CANCELLED);
			reservation.setModificationUser(CRS);
			reservation.setModificationDate(new Date());
			reservation.setCancellationUser(CRS);
			reservation.setCancellationDate(new Date());
			reservation.setPenaltyValue(getReservationUtils().obtainCancellationPenaltyValue(reservation, reservation.getCancellationDate()));
			reservation.setPenaltyAmount(getReservationUtils().obtainCancellationPenaltyAmount(reservation));
			reservation.setPenaltyDate(getReservationUtils().obtainCancellationPenaltyDate(reservation));
			if (reservation.getPenaltyDays() != null && reservation.getPenaltyDays() == 0 && reservation.getAdvancedAmount() == 0) {
				reservation.setCheckStatus(ReservationCheckStatus.CANCEL_NO_INVOICEABLE);
			} else {
				reservation.setCheckStatus(ReservationCheckStatus.CANCEL_INVOICEABLE);
			}
			BeanManager.getManagerBean(ProjectReservation.class).update(reservation);

			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(reservation.getDomain()));
			SQLBooking.delete(connection, reservation);
		} catch (ManagerBeanException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new ReservationException("Unknown error: " + ex.getMessage(), reservation.getCrsCode(), 1);
		} finally {
			SQLUtils.closeQuietly(connection);
		}
	}

	private String findReservationId(ResGlobalInfoType resGlobalInfoType, String source, String type) {
		if (resGlobalInfoType.getHotelReservationIDs() != null) {
			for (int i=0; i<resGlobalInfoType.getHotelReservationIDs().sizeOfHotelReservationIDArray(); i++) {
				HotelReservationID reservationCode = resGlobalInfoType.getHotelReservationIDs().getHotelReservationIDArray(i);
				if (reservationCode.getResIDSource() == null && reservationCode.getResIDType().equals(type)) {
					return reservationCode.getResIDValue();
				}
			}
		}
		return findReservationId(resGlobalInfoType, source);
	}

	private String findReservationId(ResGlobalInfoType resGlobalInfoType, String source) {
		if (resGlobalInfoType.getHotelReservationIDs() != null) {
			for (int i=0; i<resGlobalInfoType.getHotelReservationIDs().sizeOfHotelReservationIDArray(); i++) {
				HotelReservationID reservationCode = resGlobalInfoType.getHotelReservationIDs().getHotelReservationIDArray(i);
				if (reservationCode.getResIDSource().equals(source)) {
					return reservationCode.getResIDValue();
				}
			}

			if (resGlobalInfoType.getHotelReservationIDs().sizeOfHotelReservationIDArray() > 0) {
				return resGlobalInfoType.getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
			}
		}
		return null;
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
					if ((idContext == null && context == null) || (context.equalsIgnoreCase(idContext))) {
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
				if (tpaValue != null && tpaValue.getNodeType() == Node.TEXT_NODE) {
					return tpaValue.getNodeValue();
				}
			} else {
				Node tpaValue = findAttribute(tpaExtension, attribute);
				if (tpaValue != null) {
					return tpaValue.getNodeValue();
				}
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
					comments += comment.getTextArray(j).getStringValue() + "\n";
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
					taxQuota += (taxType.getAmount() != null) ? taxType.getAmount().doubleValue() : 0;
				}
			}
		}
		return CommonUtil.round(taxQuota);
	}

	private double findTaxPercent(ResGlobalInfoType resGlobalInfoType, String type) {
		double taxPercent = 0;
		if (resGlobalInfoType.getTotal().getTaxes() != null && resGlobalInfoType.getTotal().getTaxes().sizeOfTaxArray() == 1) {
			TaxType taxType = resGlobalInfoType.getTotal().getTaxes().getTaxArray(0);
			taxPercent = taxType.getPercent().doubleValue();
		}
		return CommonUtil.round(taxPercent);
	}

	private List<String> getTariffList(RoomStaysType roomStays) {
		List<String> tariffList = new LinkedList<String>();
		for (int i=0; i<roomStays.sizeOfRoomStayArray(); i++) {
			RoomStay stay = roomStays.getRoomStayArray(i);
			String tariffCode = stay.getRatePlans().getRatePlanArray(0).getRatePlanCode();
			if (!tariffList.contains(tariffCode)) {
				tariffList.add(tariffCode);
			}
		}
		return tariffList;
	}

	private boolean isTariffNotRefundable(ResGlobalInfoType resGlobalInfoType, List<String> tariffList) throws ManagerBeanException {
		return getReservationUtils().isTariffNoRefundable(tariffList);
	}

	private boolean isTariffPrepaid(ResGlobalInfoType resGlobalInfoType, List<String> tariffList) throws ManagerBeanException {
		boolean prepaid = isGuaranteePrepay(resGlobalInfoType);
		if (!prepaid) {
			prepaid = getReservationUtils().isTariffPrepaid(tariffList);
		}
		return prepaid;
	}

	private boolean isGuaranteePrepay(ResGlobalInfoType resGlobalInfoType) {
		if (resGlobalInfoType.getGuarantee() != null && resGlobalInfoType.getGuarantee().getGuaranteeType() != null) {
			return (resGlobalInfoType.getGuarantee().getGuaranteeType().toString().equals(GuaranteeType.PRE_PAY.toString()));
		}
		return false;
	}

	private String findPrepayInfo(ResGlobalInfoType resGlobalInfoType, String guaranteeDescription) {
		String value = null;
		if (isGuaranteePrepay(resGlobalInfoType)) {
			for (int i=0; i<resGlobalInfoType.getGuarantee().sizeOfGuaranteeDescriptionArray(); i++) {
				ParagraphType paragraphType = resGlobalInfoType.getGuarantee().getGuaranteeDescriptionArray(i);
				if (paragraphType.getName().equals(guaranteeDescription)) {
					value = paragraphType.getTextArray(0).getStringValue();
				}
			}
		}
		return value;
	}

	private boolean isTariffPrepaidException(List<String> tariffList, Seller seller, Customer agency, Customer company) throws ManagerBeanException {
		return getReservationUtils().isTariffPrepaidException(tariffList, seller, agency, company);
	}

	private boolean isGuaranteeVoucher(ResGlobalInfoType resGlobalInfoType) {
		if (resGlobalInfoType.getGuarantee() != null && resGlobalInfoType.getGuarantee().getGuaranteeType() != null) {
			return (resGlobalInfoType.getGuarantee().getGuaranteeType().toString().equals(GuaranteeType.CC_DC_VOUCHER.toString()));
		}
		return false;
	}

	private PaymentCardType findCreditCard(ResGlobalInfoType resGlobalInfoType) {
		if (isGuaranteeVoucher(resGlobalInfoType)) {
			for (int i=0; i<resGlobalInfoType.getGuarantee().getGuaranteesAccepted().sizeOfGuaranteeAcceptedArray(); i++) {
				GuaranteeAccepted guaranteeAccepted = resGlobalInfoType.getGuarantee().getGuaranteesAccepted().getGuaranteeAcceptedArray(0);
				if (guaranteeAccepted != null && guaranteeAccepted.getPaymentCard() != null) {
					return guaranteeAccepted.getPaymentCard();
				}
			}
		}
		return null;
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