package com.esferalia.aon.pms.reservation;

import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.messaging.Endpoint;
import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestGuest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.solmelia.namespaces.solres.AvailabilitySummaryRecordDocument.AvailabilitySummaryRecord;
import com.solmelia.namespaces.solres.AvailabilitySummaryRecordsDocument.AvailabilitySummaryRecords;
import com.solmelia.namespaces.solres.BookingRulesDocument.BookingRules;
import com.solmelia.namespaces.solres.CancelPenaltyType;
import com.solmelia.namespaces.solres.CustProfileDocument.CustProfile;
import com.solmelia.namespaces.solres.GuestCountsDocument.GuestCounts;
import com.solmelia.namespaces.solres.HITISMessageDocument;
import com.solmelia.namespaces.solres.HITISMessageDocument.HITISMessage;
import com.solmelia.namespaces.solres.HITISOperationType;
import com.solmelia.namespaces.solres.HITISOperationType.OperationType;
import com.solmelia.namespaces.solres.ProfileDocument.Profile.ProfileType;
import com.solmelia.namespaces.solres.RateDescriptionsDocument.RateDescriptions.RateDescription;
import com.solmelia.namespaces.solres.RatePlansDocument.RatePlans;
import com.solmelia.namespaces.solres.ResProfilesDocument.ResProfiles.ResProfile;
import com.solmelia.namespaces.solres.ReservationRequestTypeDocument.ReservationRequestType.ReservationRequestType2;
import com.solmelia.namespaces.solres.ReservationTransactionDocument.ReservationTransaction.ActionCode;
import com.solmelia.namespaces.solres.ReservationTransactionDocument.ReservationTransaction.ReservationTransactionType;
import com.solmelia.namespaces.solres.RoomInformationsDocument.RoomInformations;
import com.solmelia.namespaces.solres.RoomInformationsDocument.RoomInformations.RoomInformation;
import com.solmelia.namespaces.solres.RoomStaysDocument.RoomStays;
import com.solmelia.namespaces.solres.RoomStaysDocument.RoomStays.RoomStay;

public class ReservationRequestManager implements IReservationConstants {

	private ReservationUtils reservationUtils;
	private String messageId;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils();
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
		getReservationUtils().init();
		getReservationUtils().setDomain(requestRoom.getReservationRequest().getHotel().getDomain());
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
		if (request.isCompanyHolder() && request.getCompany() != null && request.getCompany().getId() != null) {
			String code = getReservationUtils().obtainCustomerCode(request.getCompany(), REQRES);
			if (code != null) {
				operation.getAvailabilityQuery().addNewProfiles().addNewProfile();
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileType(ProfileType.REP_COMPANY);
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileID(code);
			}
		}
		if (StringUtils.isNotEmpty(pagingKey)) {
			operation.getAvailabilityQuery().setPagingKey(pagingKey);
		}
		operation.getAvailabilityQuery().setRequestedCurrencyCode(EUR);

		int rateRPH = 0;
		RatePlans ratePlans = RatePlans.Factory.newInstance();
		for (ITransferObject ito : getRequestRatePlans(request)) {
			RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
			ratePlans.addNewRatePlan();
			ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanRPH(new BigInteger(Integer.toString(++rateRPH)));
			ratePlans.getRatePlanArray(ratePlans.sizeOfRatePlanArray()-1).setRatePlanCode(rAddInfo.getValue());
		}

		GuestCounts guestCounts = GuestCounts.Factory.newInstance();
		if (requestRoom.getAdults() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(ADT);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getAdults());
		}
		if (requestRoom.getChildren() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("12"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getChildren());
		}
		if (requestRoom.getBabies() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("6"));
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

	private List<ITransferObject> getRequestRatePlans(ReservationRequest request) throws ManagerBeanException {
		List<ITransferObject> rAddInfoList = new LinkedList<ITransferObject>();
		Customer customer = (request.isGuestHolder()) ? request.getHotel().getCustomer() : (request.isAgencyHolder()) ? request.getAgency() : request.getCompany();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), RATE_PLAN);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), getReservationUtils().getDomain());
		rAddInfoList = rAddInfoBean.getList(criteria);
		return rAddInfoList;
	}

	private String sendAvailabilityQuery(String message, List<AvailableRoomStay> availableRoomStayList) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(obtainSoapServerUrl()).toString());
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapRequest = messageFactory.createMessage();
			soapRequest.getSOAPBody().setValue(convertMessage(message));

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);

			HITISMessageDocument hitisDocument = HITISMessageDocument.Factory.parse(soapResponse.getSOAPBody().extractContentAsDocument());
			return obtainAvailableRoomStayList(hitisDocument.getHITISMessage(), availableRoomStayList);
		} catch (Exception ex) {
			AvailableRoomStay availableRoomStay = new AvailableRoomStay();
			availableRoomStay.setError(true);
			availableRoomStay.setErrorMessage(ex.getMessage());
			availableRoomStayList.add(availableRoomStay);
		}
		return null;
	}

	private String obtainSoapServerUrl() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), SOAP_SERVER_URL);
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			return ((ApplicationParameter)ito).getValue();
		}
		return null;
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
			HITISOperationType operation = (HITISOperationType)message.getBody().getHITISOperationAbstract();
			if (operation.getErrors() != null && operation.getErrors().sizeOfErrorArray() > 0) {
				for (com.solmelia.namespaces.solres.ErrorsDocument.Errors.Error error : operation.getErrors().getErrorArray()) {
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
		getReservationUtils().init();
		getReservationUtils().setDomain(requestRoom.getReservationRequest().getHotel().getDomain());
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
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("12"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getChildren());
		}
		if (requestRoom.getBabies() > 0) {
			guestCounts.addNewGuestCount();
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAgeQualifyingCode(CHD);
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setAge(new BigInteger("6"));
			guestCounts.getGuestCountArray(guestCounts.sizeOfGuestCountArray()-1).setCount(requestRoom.getBabies());
		}

		//PaymentInstructions paymentInstructions = PaymentInstructions.Factory.newInstance();
		//paymentInstructions.addNewPaymentInstruction().setPaymentMethodType(PaymentMethodType.VOUCHER);

		int roomRPH = 0;
		RoomStays roomStays = RoomStays.Factory.newInstance();
		for (int i=0; i<requestRoom.getUnits(); i++) {
			roomStays.addNewRoomStay();
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRoomStayRPH(new BigInteger(Integer.toString(++roomRPH)));
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRoomInventoryCode(availableRoomStay.getInventoryCode());
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setRatePlans(ratePlans);
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setGuestCounts(guestCounts);
			roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).addNewPaymentInstructions();
			//roomStays.getRoomStayArray(roomStays.sizeOfRoomStayArray()-1).setPaymentInstructionsArray(0, paymentInstructions);
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
			custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewStreetNmbr().setStringValue(requestGuest.getAddress());
			if (StringUtils.isNotEmpty(requestGuest.getCity())) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewCityName().setStringValue(requestGuest.getCity());
				if (StringUtils.isNotEmpty(requestGuest.getZip())) {
					custProfile.getCustomer().getCustAddressArray(0).getAddress().getCityName().setPostalCode(requestGuest.getZip());
				}
			}
			if (StringUtils.isNotEmpty(requestGuest.getProvince())) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewStateProv().setStringValue(requestGuest.getProvince());
			}
			if (StringUtils.isNotEmpty(requestGuest.getCountry())) {
				custProfile.getCustomer().getCustAddressArray(0).getAddress().addNewCountryName().setStringValue(requestGuest.getCountry());
			}
		}
		if (request.isCompanyHolder() && request.getCompany() != null && request.getCompany().getId() != null) {
			String code = getReservationUtils().obtainCustomerCode(request.getCompany(), REQRES);
			if (code != null) {
				custProfile.addNewAffiliations().addNewEmployer().addNewEmployerName().addNewCompanyName().setCompanyCode(code);
			}
		}
		ResProfile resProfile = ResProfile.Factory.newInstance();
		resProfile.setResProfileRPH(new BigInteger("1"));
		resProfile.addNewCustProfileCreateRQ();
		resProfile.getCustProfileCreateRQ().addNewUniqueId().setType(CUST_PROFILE);
		resProfile.getCustProfileCreateRQ().setCustProfile(custProfile);
		ResProfile[] resProfiles = { resProfile };
		operation.getReservationTransaction().getReservation().addNewResProfiles().setResProfileArray(resProfiles);

		operation.getReservationTransaction().getReservation().setExternalReservationID(request.getCode());
		operation.getReservationTransaction().getReservation().addNewDistributor().setCode(TR);
		message.getBody().setHITISOperationAbstract(operation);
		return document.toString();
	}

	private AvailableRoomStay sendBookingQuery(String message, AvailableRoomStay availableRoomStay) {
		try {
			Endpoint endpoint = new URLEndpoint(new URL(obtainSoapServerUrl()).toString());
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapRequest = messageFactory.createMessage();
			soapRequest.getSOAPBody().setValue(convertMessage(message));

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
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
				for (com.solmelia.namespaces.solres.ErrorsDocument.Errors.Error error : operation.getErrors().getErrorArray()) {
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
		getReservationUtils().init();
		getReservationUtils().setDomain(reservation.getHotel().getDomain());
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
			Endpoint endpoint = new URLEndpoint(new URL(obtainSoapServerUrl()).toString());
			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage soapRequest = messageFactory.createMessage();
			soapRequest.getSOAPBody().setValue(convertMessage(message));

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
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

}
