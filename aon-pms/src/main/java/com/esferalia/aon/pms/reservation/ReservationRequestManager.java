package com.esferalia.aon.pms.reservation;

import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.messaging.Endpoint;
import javax.xml.messaging.URLEndpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

import noNamespace.AvailabilitySummaryRecordDocument.AvailabilitySummaryRecord;
import noNamespace.AvailabilitySummaryRecordsDocument.AvailabilitySummaryRecords;
import noNamespace.BookingRulesDocument.BookingRules;
import noNamespace.CancelPenaltyType;
import noNamespace.CustProfileDocument.CustProfile;
import noNamespace.ErrorsDocument.Errors;
import noNamespace.GuestCountsDocument.GuestCounts;
import noNamespace.HITISMessageDocument;
import noNamespace.HITISMessageDocument.HITISMessage;
import noNamespace.HITISOperationType;
import noNamespace.HITISOperationType.OperationType;
import noNamespace.PaymentInstructionsDocument.PaymentInstructions;
import noNamespace.PaymentInstructionsDocument.PaymentInstructions.PaymentInstruction.PaymentMethodType;
import noNamespace.ProfileDocument.Profile.ProfileType;
import noNamespace.RateDescriptionsDocument.RateDescriptions.RateDescription;
import noNamespace.RatePlansDocument.RatePlans;
import noNamespace.ResProfilesDocument.ResProfiles.ResProfile;
import noNamespace.ReservationRequestTypeDocument.ReservationRequestType.ReservationRequestType2;
import noNamespace.ReservationTransactionDocument.ReservationTransaction.ActionCode;
import noNamespace.ReservationTransactionDocument.ReservationTransaction.ReservationTransactionType;
import noNamespace.RoomInformationsDocument.RoomInformations;
import noNamespace.RoomInformationsDocument.RoomInformations.RoomInformation;
import noNamespace.RoomStaysDocument.RoomStays;
import noNamespace.RoomStaysDocument.RoomStays.RoomStay;

public class ReservationRequestManager implements IReservationConstants {

	private ReservationUtils reservationUtils;
	private String messageId;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public List<AvailableRoomStay> processAvailabilityQuery(ReservationRequestRoom requestRoom) {
		try {
			List<AvailableRoomStay> availableRoomStayList = new LinkedList<AvailableRoomStay>();
			String pagingKey = null;
			int page = 0;
			do {
				pagingKey = sendAvailabilityQuery(createAvailabilityMessage(requestRoom, pagingKey, ++page), availableRoomStayList);
			} while (StringUtils.isNotEmpty(pagingKey) && page<9);
			return availableRoomStayList;
		} catch (Exception ex) {
			return null;
		}
	}

	private String createAvailabilityMessage(ReservationRequestRoom requestRoom, String pagingKey, int page) throws ManagerBeanException {
		ReservationRequest request = requestRoom.getReservationRequest();

		setMessageId(getReservationUtils().getRequestMessageId(request, page));
		Calendar startInstant = Calendar.getInstance();
		startInstant.setTime(request.getStartDate());
		String nights = "+0000-00-" + StringUtils.leftPad(""+request.getNights(), 2, "0") + "T00:00:00";

		HITISMessageDocument document = HITISMessageDocument.Factory.newInstance();
		HITISMessage message = document.addNewHITISMessage();

		message.addNewHeader();
		message.getHeader().setMessageID(getMessageId());
		message.getHeader().setOriginalMessageID(getMessageId());

		message.addNewBody();
		HITISOperationType operation = HITISOperationType.Factory.newInstance();
		operation.setOperationName(AVAILABILITY_QUERY_REQUEST);
		operation.setOperationType(OperationType.AVAILABILITY);
		operation.setAvailabilityOriginatorCode(GP);
		operation.addNewAvailabilityQuery();
		operation.getAvailabilityQuery().setExternalWebCode(GPS);
		operation.getAvailabilityQuery().addNewHotelReference().setHotelCode(request.getHotel().getCode());
		operation.getAvailabilityQuery().addNewStayDateRange().addNewDateTimeSpan();
		operation.getAvailabilityQuery().getStayDateRange().getDateTimeSpan().setStartInstant(startInstant);
		operation.getAvailabilityQuery().getStayDateRange().getDateTimeSpan().setDuration(nights);
		if (request.isCompanyHolder() || request.isAgencyHolder()) {
			String profileId = null;
			if (request.isCompanyHolder() && request.getCompany() != null && request.getCompany().getId() != null) {
				profileId = getReservationUtils().obtainCustomerCode(request.getCompany(), REQRES);
			} else if (request.isAgencyHolder() && request.getAgency() != null && request.getAgency().getId() != null) {
				profileId = getPromoCodeProfile(request);
			}

			if (profileId != null) {
				operation.getAvailabilityQuery().addNewProfiles().addNewProfile();
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileType(ProfileType.REP_COMPANY);
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileID(profileId);
			}
		}
		if (StringUtils.isNotEmpty(pagingKey)) {
			operation.getAvailabilityQuery().setPagingKey(pagingKey);
		}
		operation.getAvailabilityQuery().setRequestedCurrencyCode(EUR);

		int rateRPH = 0;
		RatePlans ratePlans = RatePlans.Factory.newInstance();
		for (ITransferObject ito : getRatePlans(request)) {
			RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
			ratePlans.addNewRatePlan();
			ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanRPH(new BigInteger(Integer.toString(++rateRPH)));
			ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanCode(rAddInfo.getValue());
		}

		GuestCounts guestCounts = GuestCounts.Factory.newInstance();
		guestCounts.addNewGuestCount();
		guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(ADT);
		guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getAdults());
		if (requestRoom.getChildren() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("11"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getChildren());
		}
		if (requestRoom.getBabies() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("5"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getBabies());
		}

		int roomRPH = 0;
		RoomStays roomStays = RoomStays.Factory.newInstance();
		for (int i=0; i<requestRoom.getUnits(); i++) {
			roomStays.addNewRoomStay();
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRoomStayRPH(new BigInteger(Integer.toString(++roomRPH)));
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRatePlans(ratePlans);
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setGuestCounts(guestCounts);
		}
		operation.getAvailabilityQuery().setRoomStays(roomStays);

		if (request.isAgencyHolder() && request.getAgency() != null && request.getAgency().getId() != null) {
			String code = getReservationUtils().obtainCustomerCode(request.getAgency(), REQRES);
			if (code != null) {
				operation.getAvailabilityQuery().addNewAgencyCode().setStringValue(code);
			}
		}
		operation.getAvailabilityQuery().setLanguageID(ES);
		operation.addNewDistributor().setCode(TR);
		message.getBody().setHITISOperationAbstract(operation);
		return document.toString();
	}

	public String getPromoCodeProfile(ReservationRequest request) throws ManagerBeanException {
		Date today = DateUtils.truncate(new Date(), Calendar.DATE);
		String profileId = null;
		if (StringUtils.isNotEmpty(request.getHotel().getCode())) {
			for (String profileTmp : getReservationUtils().obtainCustomerCodes(request.getAgency(), PROMO_CODE + "_" + request.getHotel().getCode())) {
				if (profileTmp.contains("|")) {
					profileTmp = getReservationUtils().obtainProfileData(profileTmp, today);
					if (profileTmp != null) {
						profileId = profileTmp;
						break;
					}
				} else if (profileId == null) {
					profileId = profileTmp;
				}
			}
		}
		if (profileId == null) {
			for (String profileTmp : getReservationUtils().obtainCustomerCodes(request.getAgency(), PROMO_CODE)) {
				if (profileTmp.contains("|")) {
					profileTmp = getReservationUtils().obtainProfileData(profileTmp, today);
					if (profileTmp != null) {
						profileId = profileTmp;
						break;
					}
				} else if (profileId == null) {
					profileId = profileTmp;
				}
			}
		}
		return profileId;
	}

	private List<ITransferObject> getRatePlans(ReservationRequest request) throws ManagerBeanException {
		Customer customer = (request.isGuestHolder()) ? request.getHotel().getCustomer() : (request.isAgencyHolder()) ? request.getAgency() : request.getCompany();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isNotEmpty(request.getHotel().getCode())) {
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), RATE_PLAN + "_" + request.getHotel().getCode());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), getReservationUtils().getDomain());
			if (rAddInfoBean.getCount(criteria) > 0) {
				return rAddInfoBean.getList(criteria);
			}
		}

		criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), RATE_PLAN);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), getReservationUtils().getDomain());
		return rAddInfoBean.getList(criteria);
	}

	public String getRatePlansList(ReservationRequest request) throws ManagerBeanException {
		List<String> ratePlansList = new LinkedList<String>();
		for (ITransferObject ito : getRatePlans(request)) {
			RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
			ratePlansList.add(rAddInfo.getValue());
		}
		return (ratePlansList.size() > 0) ? ArrayUtils.toString(ratePlansList) : null;
	}

	private String sendAvailabilityQuery(String message, List<AvailableRoomStay> availableRoomStayList) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(getReservationUtils().obtainUrl(CRS_SIMPLE_AVAILABILITY_URL)).toString());
			SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
			soapRequest.getMimeHeaders().addHeader("Content-Type", "text/xml;charset=UTF-8");
			soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));

			SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);

			HITISMessageDocument hitisDocument = HITISMessageDocument.Factory.parse(soapResponse.getSOAPBody().extractContentAsDocument());
			return obtainAvailableRoomStayList(hitisDocument.getHITISMessage(), availableRoomStayList);
		} catch (Exception ex) {
			AvailableRoomStay availableRoomStay = new AvailableRoomStay();
			availableRoomStay.setError(true);
			availableRoomStay.setErrorMessage(ex.getMessage());
			availableRoomStayList.add(availableRoomStay);
			ex.printStackTrace();
		}
		return null;
	}

	private Document obtainMessageDocument(String message) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
		return documentBuilder.parse(new InputSource(new StringReader(convertMessage(message))));  
	}

	private String convertMessage(String message) {
		String value = message;
		value = value.replaceAll("HITISOperationAbstract", "HITISOperation");
		value = value.replaceAll("T00:00:00\\.000\\+0[0-9]:00", "T00:00:00");
		return value;
	}

	private String obtainAvailableRoomStayList(HITISMessage message, List<AvailableRoomStay> availableRoomStayList) {
		String pagingKey = null;
		if (message.getHeader().getOriginalMessageID().equals(getMessageId())) {
System.out.println(message.toString());
			HITISOperationType operation = (HITISOperationType)message.getBody().getHITISOperationAbstract();
			if (operation.getErrors() != null && operation.getErrors().sizeOfErrorArray() > 0) {
				for (Errors.Error error : operation.getErrors().getErrorArray()) {
					AvailableRoomStay availableRoomStay = new AvailableRoomStay();
					availableRoomStay.setError(true);
					availableRoomStay.setErrorMessage(error.getStringValue());
					if (StringUtils.isEmpty(availableRoomStay.getErrorMessage())) {
						availableRoomStay.setErrorMessage(error.getHITISCode());
					}
					availableRoomStayList.add(availableRoomStay);
				}
			} else {
				AvailabilitySummaryRecords records = operation.getAvailabilitySummaryResponses().getAvailabilitySummaryResponse().getAvailabilitySummaryRecords();
				for (AvailabilitySummaryRecord record : records.getAvailabilitySummaryRecordArray()) {
					String rateCode = record.getRatePlanCode();
					RateDescription rateDescription = (record.sizeOfRateDescriptionsArray() > 0) ? record.getRateDescriptionsArray(0).getRateDescriptionArray(0) : null;
					CancelPenaltyType cancelPenalty = record.getCancelPenalty();
					for (RoomStay roomStay : record.getRoomStays().getRoomStayArray()) {
						RoomInformation roomInfo = null;
						for (RoomInformations roomInfos : roomStay.getRoomInformationsArray()) {
							roomInfo = roomInfos.getRoomInformationArray(roomInfos.sizeOfRoomInformationArray()-1);
						}

						AvailableRoomStay availableRoomStay = new AvailableRoomStay();
						availableRoomStay.setIndex(availableRoomStayList.size());
						availableRoomStay.setTariffCode(rateCode);
						availableRoomStay.setTariffDescription(rateDescription.getDetailDescription());
						availableRoomStay.setInventoryCode(roomStay.getInventoryCode());
						availableRoomStay.setRoomCode(roomStay.getRoomCodes().getBaseRoomCode());
						availableRoomStay.setRoomDescription((roomInfo != null) ? roomInfo.getDetailDescription() : null);
						availableRoomStay.setMealPlan(roomStay.getRoomCodes().getMealPlan());
						availableRoomStay.setDailyPrice(parseDouble(roomStay.getRateQuotes().getRateQuote().getQuotedRateAmount().getCurrency().getStringValue()));
						availableRoomStay.setTotalPrice(parseDouble(roomStay.getRateQuotes().getRateQuote().getTotalAmountWithTax().getStringValue()));
						availableRoomStay.setAvailability(roomStay.getAmount().getDomNode().getFirstChild().getNodeValue());
						availableRoomStay.setCancelPenalty(obtainPenaltyConditions(cancelPenalty));
						availableRoomStayList.add(availableRoomStay);
					}
				}
				pagingKey = operation.getAvailabilitySummaryResponses().getAvailabilitySummaryResponse().getPagingKey();
			}
		}
		return pagingKey;
	}

	private double parseDouble(String value) {
		value = value.replace(',', '.');
		return Double.parseDouble(value);
	}

	private String obtainPenaltyConditions(CancelPenaltyType penalty) {
		String conditions = null;
		if (penalty != null) {
			if (StringUtils.isNotEmpty(penalty.getDescription())) {
				conditions = penalty.getDescription();
			} else {
				conditions = penalty.getDeadline().getOffsetUnitMultiplier().toString() + " " + penalty.getDeadline().getOffsetUnit().toString() + " ";
				conditions += penalty.getDeadline().getOffsetDropTime().toString() + "\n";
				conditions += penalty.getDuePayment().getQuantity() + " " + penalty.getDuePayment().getUnit().toString();
			}
		}
		return conditions;
	}


	public AvailableRoomStay processBookingRequest(ReservationRequestRoom requestRoom, ReservationRequestGuest requestGuest, AvailableRoomStay availableRoomStay) {
		try {
			return sendBookingQuery(createBookingMessage(requestRoom, requestGuest, availableRoomStay), availableRoomStay);
		} catch (Exception ex) {
			return null;
		}
	}

	private String createBookingMessage(ReservationRequestRoom requestRoom, ReservationRequestGuest requestGuest, AvailableRoomStay availableRoomStay) 
			throws ManagerBeanException {
		ReservationRequest request = requestRoom.getReservationRequest();

		setMessageId(getReservationUtils().getRequestMessageId(request));
		Calendar startInstant = Calendar.getInstance();
		startInstant.setTime(request.getStartDate());
		String nights = "+0000-00-" + StringUtils.leftPad(""+request.getNights(), 2, "0") + "T00:00:00";

		HITISMessageDocument document = HITISMessageDocument.Factory.newInstance();
		HITISMessage message = document.addNewHITISMessage();

		message.addNewHeader();
		message.getHeader().setMessageID(getMessageId());
		message.getHeader().setOriginalMessageID(getMessageId());

		message.addNewBody();
		HITISOperationType operation = HITISOperationType.Factory.newInstance();
		operation.setOperationName(RESERVATION_BOOKING_REQUEST);
		operation.addNewReservationRequestType().setReservationRequestType(ReservationRequestType2.INITIATE);
		operation.addNewReservationTransaction().setReservationTransactionType(ReservationTransactionType.NEW);
		operation.getReservationTransaction().setActionCode(ActionCode.SS);
		operation.getReservationTransaction().addNewReservation().setReservationOriginatorCode(GP);
		operation.getReservationTransaction().getReservation().setExternalWebCode(GPS);
		operation.getReservationTransaction().getReservation().addNewHotelReference().setHotelCode(request.getHotel().getCode());
		operation.getReservationTransaction().getReservation().addNewStayDateRange().addNewDateTimeSpan();
		operation.getReservationTransaction().getReservation().getStayDateRange().getDateTimeSpan().setStartInstant(startInstant);
		operation.getReservationTransaction().getReservation().getStayDateRange().getDateTimeSpan().setDuration(nights);

		RatePlans ratePlans = RatePlans.Factory.newInstance();
		ratePlans.addNewRatePlan();
		ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanRPH(new BigInteger("1"));
		ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanCode(availableRoomStay.getTariffCode());

		GuestCounts guestCounts = GuestCounts.Factory.newInstance();
		if (requestRoom.getAdults() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(ADT);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getAdults());
		}
		if (requestRoom.getChildren() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("11"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getChildren());
		}
		if (requestRoom.getBabies() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("5"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getBabies());
		}

		PaymentInstructions paymentInstructions = PaymentInstructions.Factory.newInstance();
		if (request.isAgencyHolder() && request.getAgency() != null && request.getAgency().getId() != null) {
			String payment = getReservationUtils().obtainCustomerCode(request.getAgency(), BOOKING_PAYMENT);
			if (payment != null && !payment.equalsIgnoreCase(VOUCHER)) {
				paymentInstructions.addNewPaymentInstruction().setPaymentMethodType(PaymentMethodType.POS);
			} else {
				paymentInstructions.addNewPaymentInstruction().setPaymentMethodType(PaymentMethodType.VOUCHER);
			}
		} else {
			paymentInstructions.addNewPaymentInstruction().setPaymentMethodType(PaymentMethodType.POS);
		}

		int roomRPH = 0;
		RoomStays roomStays = RoomStays.Factory.newInstance();
		for (int i=0; i<requestRoom.getUnits(); i++) {
			roomStays.addNewRoomStay();
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRoomStayRPH(new BigInteger(Integer.toString(++roomRPH)));
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRoomInventoryCode(availableRoomStay.getInventoryCode());
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRatePlans(ratePlans);
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setGuestCounts(guestCounts);
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).addNewPaymentInstructions();
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setPaymentInstructionsArray(0, paymentInstructions);
		}
		operation.getReservationTransaction().getReservation().addNewRoomStays();
		operation.getReservationTransaction().getReservation().setRoomStaysArray(0, roomStays);

		if (request.isAgencyHolder() && request.getAgency() != null && request.getAgency().getId() != null) {
			String code = getReservationUtils().obtainCustomerCode(request.getAgency(), REQRES);
			if (code != null) {
				operation.getReservationTransaction().getReservation().addNewAgencyCode().setStringValue(code);
			}
		}

		CustProfile custProfile = CustProfile.Factory.newInstance();
		custProfile.addNewCustomer();
		custProfile.getCustomer().addNewPersonName();
		custProfile.getCustomer().getPersonNameArray(0).setSurname(requestGuest.getSurname());
		custProfile.getCustomer().getPersonNameArray(0).setGivenName(requestGuest.getName());
		if (StringUtils.isNotEmpty(requestGuest.getPhone())) {
			custProfile.getCustomer().addNewCustTelephone().addNewTelephone().setPhoneNumber(requestGuest.getPhone());
		}
		if (StringUtils.isNotEmpty(requestGuest.getEmail())) {
			custProfile.getCustomer().addNewCustEmail().setStringValue(requestGuest.getEmail());
		}
		if (StringUtils.isNotEmpty(requestGuest.getAddress())) {
			custProfile.getCustomer().addNewCustAddress().addNewAddress();
			custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewStreetNmbr().setStringValue(requestGuest.getStreetNumber());
			if (StringUtils.isNotEmpty(requestGuest.getCity())) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewCityName().setStringValue(requestGuest.getCity());
				if (StringUtils.isNotEmpty(requestGuest.getZip())) {
					custProfile.getCustomer().getCustAddressArray(0).getAddress().getCityName().setPostalCode(requestGuest.getZip());
				}
			}
			if (StringUtils.isNotEmpty(requestGuest.getProvince())) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewStateProv().setStringValue(requestGuest.getProvince());
			}
			if (requestGuest.getCountry() != null) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewCountryName().setStringValue(requestGuest.getCountry().getValue());
			}
		}
		if (request.isCompanyHolder() || request.isAgencyHolder()) {
			String profileId = null;
			if (request.isCompanyHolder() && request.getCompany() != null && request.getCompany().getId() != null) {
				profileId = getReservationUtils().obtainCustomerCode(request.getCompany(), REQRES);
			} else if (request.isAgencyHolder() && request.getAgency() != null && request.getAgency().getId() != null) {
				profileId = getPromoCodeProfile(request);
			}

			if (profileId != null) {
				custProfile.addNewAffiliations().addNewEmployer().addNewEmployerName().addNewCompanyName().setCompanyCode(profileId);
			}
		}

		ResProfile resProfile = ResProfile.Factory.newInstance();
		resProfile.setResProfileRPH(new BigInteger("1"));
		resProfile.addNewCustProfileCreateRQ();
		resProfile.getCustProfileCreateRQ().addNewUniqueId().setType(CUST_PROFILE);
		resProfile.getCustProfileCreateRQ().setCustProfile(custProfile);
		ResProfile[] resProfiles = { resProfile };
		operation.getReservationTransaction().getReservation().addNewResProfiles().setResProfileArray(resProfiles);

		operation.getReservationTransaction().getReservation().addNewResComments().addNewResComment().setComment(request.getRemarks());
		operation.getReservationTransaction().getReservation().setExternalReservationID(request.getCode());
		operation.getReservationTransaction().getReservation().addNewDistributor().setCode(TR);
		message.getBody().setHITISOperationAbstract(operation);
		return document.toString();
	}

	private AvailableRoomStay sendBookingQuery(String message, AvailableRoomStay availableRoomStay) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(getReservationUtils().obtainUrl(CRS_BOOKING_URL)).toString());
			SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
			soapRequest.getMimeHeaders().addHeader("Content-Type", "text/xml;charset=UTF-8");
			soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));

			SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);

			HITISMessageDocument hitisDocument = HITISMessageDocument.Factory.parse(soapResponse.getSOAPBody().extractContentAsDocument());
			return obtainReservationId(hitisDocument.getHITISMessage(), availableRoomStay);
		} catch (Exception ex) {
			availableRoomStay.setError(true);
			availableRoomStay.setErrorMessage(ex.getMessage());
			return availableRoomStay;
		}
	}

	private AvailableRoomStay obtainReservationId(HITISMessage message, AvailableRoomStay availableRoomStay) {
		if (message.getHeader().getOriginalMessageID().equals(getMessageId())) {
			HITISOperationType operation = (HITISOperationType)message.getBody().getHITISOperationAbstract();
			if (operation.getErrors() != null && operation.getErrors().sizeOfErrorArray() > 0) {
				for (Errors.Error error : operation.getErrors().getErrorArray()) {
					availableRoomStay.setError(true);
					availableRoomStay.setErrorMessage(error.getStringValue());
					if (StringUtils.isEmpty(availableRoomStay.getErrorMessage())) {
						availableRoomStay.setErrorMessage(error.getHITISCode());
					}
				}
			} else {
				availableRoomStay.setReservationId(operation.getReservation().getConfirmationID());
				BookingRules bookingRules = operation.getReservation().getBookingRules();
				if (bookingRules.getCancelPenalties() != null && bookingRules.getCancelPenalties().sizeOfCancelPenaltyArray() > 0) {
					availableRoomStay.setCancelPenalty(obtainPenaltyConditions(bookingRules.getCancelPenalties().getCancelPenaltyArray(0)));
				}
			}
		}
		return availableRoomStay;
	}


	public boolean processBookingCancelRequest(ProjectReservation reservation) {
		try {
			return sendBookingCancelQuery(createBookingCancelMessage(reservation));
		} catch (Exception ex) {
			return false;
		}
	}

	private String createBookingCancelMessage(ProjectReservation reservation) throws ManagerBeanException {
		setMessageId(getReservationUtils().getRequestMessageId(reservation));
		Calendar startInstant = Calendar.getInstance();
		startInstant.setTime(reservation.getStartDate());
		String nights = "+0000-00-" + StringUtils.leftPad(""+reservation.getNights(), 2, "0") + "T00:00:00";

		HITISMessageDocument document = HITISMessageDocument.Factory.newInstance();
		HITISMessage message = document.addNewHITISMessage();

		message.addNewHeader();
		message.getHeader().setMessageID(getMessageId());
		message.getHeader().setOriginalMessageID(getMessageId());

		message.addNewBody();
		HITISOperationType operation = HITISOperationType.Factory.newInstance();
		operation.setOperationName(RESERVATION_BOOKING_REQUEST);
		operation.addNewReservationRequestType().setReservationRequestType(ReservationRequestType2.INITIATE);
		operation.addNewReservationTransaction().setReservationTransactionType(ReservationTransactionType.CANCEL);
		operation.getReservationTransaction().setActionCode(ActionCode.SS);
		operation.getReservationTransaction().addNewReservation().setReservationOriginatorCode(GP);
		operation.getReservationTransaction().getReservation().setExternalWebCode(GPS);
		operation.getReservationTransaction().getReservation().addNewHotelReference().setHotelCode(reservation.getHotelReservation().getCode());
		operation.getReservationTransaction().getReservation().addNewStayDateRange().addNewDateTimeSpan();
		operation.getReservationTransaction().getReservation().getStayDateRange().getDateTimeSpan().setStartInstant(startInstant);
		operation.getReservationTransaction().getReservation().getStayDateRange().getDateTimeSpan().setDuration(nights);
		operation.getReservationTransaction().getReservation().setReservationID(reservation.getCrsCode());
		message.getBody().setHITISOperationAbstract(operation);
		return document.toString();
	}

	private boolean sendBookingCancelQuery(String message) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(getReservationUtils().obtainUrl(CRS_BOOKING_URL)).toString());
			SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
			soapRequest.getMimeHeaders().addHeader("Content-Type", "text/xml;charset=UTF-8");
			soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));

			SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
		
			HITISMessageDocument hitisDocument = HITISMessageDocument.Factory.parse(soapResponse.getSOAPBody().extractContentAsDocument());
			return obtainCancellationResult(hitisDocument.getHITISMessage());
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean obtainCancellationResult(HITISMessage message) {
		if (message.getHeader().getOriginalMessageID().equals(getMessageId())) {
			HITISOperationType operation = (HITISOperationType)message.getBody().getHITISOperationAbstract();
			return (operation.getErrors() == null || operation.getErrors().sizeOfErrorArray() == 0);
		}
		return false;
	}


	public void processNewReservation(ReservationRequestRoom requestRoom) throws ManagerBeanException {
		createReservation(requestRoom, requestRoom.getReservationRequest().getHotel());
	}

	private void createReservation(ReservationRequestRoom requestRoom, Hotel hotel) throws ManagerBeanException {
		Tariff roomTariff = getReservationUtils().obtainTariff(requestRoom.getTariffCode());
		Item serviceItem = getReservationUtils().obtainServiceItem(requestRoom.getItem(), requestRoom.getInventoryCode(), requestRoom.getMealPlan());

		ProjectReservation reservation = new ProjectReservation();
		reservation.setHotel(hotel);
		reservation.setHotelReservation(hotel);
		reservation.setCode(requestRoom.getReservationRequest().getCode());
		reservation.setStartDate(requestRoom.getReservationRequest().getStartDate());
		reservation.setEndDate(requestRoom.getReservationRequest().getEndDate());
		reservation.setStartTime(DateUtils.addHours(reservation.getStartDate(), 14));
		reservation.setEndTime(DateUtils.addHours(reservation.getEndDate(), 12));
		reservation.setSeller(getReservationUtils().obtainRequestSeller());
		reservation.setAgency(requestRoom.getReservationRequest().getAgency());
		reservation.setAgencyCommissionPercent(0);
		reservation.setAgencyCommissionAmount(0);
		reservation.setAgencyRebate(false);
		reservation.setCompany(requestRoom.getReservationRequest().getCompany());
		reservation.setDiscountPercent(0);
		reservation.setDiscountAmount(0);
		reservation.setBookingHolder(requestRoom.getReservationRequest().getBookingHolder());
		reservation.setVatPercent(requestRoom.getItem().getVat().getDatedPercentage(requestRoom.getReservationRequest().getStartDate()));
		reservation.setTotal((requestRoom.getAgreedPrice() == 0) ? CommonUtil.round(requestRoom.getTotalPrice()) : CommonUtil.round(requestRoom.getAgreedPrice()));
		reservation.setTaxableBase(CommonUtil.round(reservation.getTotal() * (1 - reservation.getVatPercent() / 100)));
		reservation.setVatQuota(CommonUtil.round(reservation.getTotal() - reservation.getTaxableBase()));
		reservation.setOtherTaxQuota(0);
		reservation.setRemarks(requestRoom.getReservationRequest().getRemarks());
		reservation.setSource(ReservationSource.REQUEST);
		reservation.setCrsCode(requestRoom.getCrsCode());
		reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
		reservation.setStatus(ReservationStatus.BLOCKED);

		getReservationUtils().fillProject(reservation);
		reservation.getProject().setDomain(hotel.getDomain());
		reservation.setDomain(hotel.getDomain());

		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		reservationBean.restoreNullSubPOJOs(reservation);
		reservation = (ProjectReservation)reservationBean.insert(reservation);

		createReservationGuest(requestRoom.getReservationRequest(), reservation);
		createReservationRoomAndService(requestRoom, reservation, roomTariff, serviceItem);
		reservation = finalizeReservation(reservation);

		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), reservation.getId());
		criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), reservation.getCrsCode());
		if (reservationBean.getCount(criteria) > 0) {
			reservation.setPenaltyValue("0");
			reservation.setPenaltyAmount(0);
			reservation.setCheckStatus(ReservationCheckStatus.CANCEL_NO_INVOICEABLE);
			reservation.setStatus(ReservationStatus.CANCELLED);
			reservation = (ProjectReservation)reservationBean.update(reservation);
		}
	}

	private void createReservationGuest(ReservationRequest request, ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		IManagerBean requestGuestBean = BeanManager.getManagerBean(ReservationRequestGuest.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_RESERVATION_REQUEST_ID), request.getId());
		criteria.addOrder(requestGuestBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_GUEST_GUEST_INDEX));
		for (ITransferObject ito : requestGuestBean.getList(criteria)) {
			ReservationRequestGuest requestGuest = (ReservationRequestGuest)ito;

			ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
			reservationGuest.setProjectReservation(reservation);
			reservationGuest.setDomain(reservation.getDomain());
			reservationGuest.setGuestIndex(requestGuest.getGuestIndex());
			reservationGuest.setSurname(requestGuest.getSurname());
			reservationGuest.setName(requestGuest.getName());
			reservationGuest.setEmail(requestGuest.getEmail());
			reservationGuest.setPhone(requestGuest.getPhone());
			reservationGuest.setAddress(requestGuest.getAddress());
			reservationGuest.setNumber(requestGuest.getNumber());
			reservationGuest.setAddress2(requestGuest.getAddress2());
			reservationGuest.setZip(requestGuest.getZip());
			reservationGuest.setCity(requestGuest.getCity());
			reservationGuest.setProvince(requestGuest.getProvince());
			reservationGuest.setCountry(requestGuest.getCountry());

			reservationGuestBean.insert(reservationGuest);
			if (reservationGuest.getGuestIndex() == 1) {
				getReservationUtils().fillProject(reservation);
			}
		}
	}

	private void createReservationRoomAndService(ReservationRequestRoom requestRoom, ProjectReservation reservation, Tariff roomTariff, Item serviceItem) 
			throws ManagerBeanException {
		if (roomTariff == null) {
			roomTariff = getReservationUtils().obtainDefaultTariff();
		}
		if (serviceItem == null) {
			serviceItem = getReservationUtils().obtainDefaultServiceItem();
		}
		double reservationBase = CommonUtil.round(reservation.getTotal() / (1 + reservation.getVatPercent() / 100), 4);
		double serviceBase = CommonUtil.round(reservationBase / requestRoom.getUnits(), 4);

		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		for (int i=0; i<requestRoom.getUnits(); i++) {
			ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
			reservationRoom.setProjectReservation(reservation);
			reservationRoom.setDomain(reservation.getDomain());
			reservationRoom.setRoomIndex(1);
			reservationRoom.setRoomCode(requestRoom.getRoomCode());
			reservationRoom.setItem(requestRoom.getItem());
			reservationRoom.setTariff(roomTariff);
			reservationRoom.setAdults(requestRoom.getAdults());
			reservationRoom.setChildren(requestRoom.getChildren() + requestRoom.getBabies());
			reservationRoom = (ProjectReservationRoom)reservationRoomBean.insert(reservationRoom);

			if (i == (requestRoom.getUnits() - 1)) {
				serviceBase = CommonUtil.round(reservationBase - (serviceBase * (requestRoom.getUnits() - 1)), 4);
			}
			createReservationService(reservationRoom, requestRoom.getInventoryCode(), serviceItem, serviceBase);
		}
	}

	private void createReservationService(ProjectReservationRoom reservationRoom, String serviceCode, Item serviceItem, double serviceBase) 
			throws ManagerBeanException {
		ProjectReservationService reservationService = new ProjectReservationService();
		reservationService.setProjectReservation(reservationRoom.getProjectReservation());
		reservationService.setDomain(reservationRoom.getDomain());
		reservationService.setServiceIndex(1);
		reservationService.setServiceCode(serviceCode);
		reservationService.setItem(serviceItem);
		reservationService.setDescription(serviceItem.getProduct().getName());
		reservationService.setMealPlan(getReservationUtils().obtainMealPlan(serviceItem.getDetail()));
		reservationService.setProjectReservationRoom(reservationRoom.getId());
		reservationService = (ProjectReservationService)BeanManager.getManagerBean(ProjectReservationService.class).insert(reservationService);

		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Date fromDate = reservationRoom.getProjectReservation().getStartDate();
		Date toDate = reservationRoom.getProjectReservation().getEndDate();
		double serviceDetailBase = CommonUtil.round(serviceBase / CommonUtil.getDaysBetweenDates(fromDate, toDate), 4);
		for (Date date=DateUtils.truncate(fromDate, Calendar.DATE); date.before(DateUtils.truncate(toDate, Calendar.DATE)); date=DateUtils.addDays(date, 1)) {
			ProjectReservationServiceDetail reservationServiceDetail = new ProjectReservationServiceDetail();
			reservationServiceDetail.setProjectReservationService(reservationService);
			reservationServiceDetail.setDomain(reservationService.getDomain());
			reservationServiceDetail.setEffectiveDate(date);
			reservationServiceDetail.setQuantity(1);
			reservationServiceDetail.setPrice((DateUtils.isSameDay(toDate, DateUtils.addDays(date, -1))) ? serviceBase : serviceDetailBase);
			reservationServiceDetail.setTaxableBase(serviceDetailBase);
			reservationServiceDetailBean.insert(reservationServiceDetail);

			serviceBase = CommonUtil.round(serviceBase - serviceDetailBase, 4); 
		}
	}

	private ProjectReservation finalizeReservation(ProjectReservation reservation) throws ManagerBeanException{
		reservation.setPenaltyAmount(getReservationUtils().obtainCancellationPenaltyAmount(reservation));
		return (ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).update(reservation);
	}

}
