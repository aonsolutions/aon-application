package com.esferalia.aon.pms.reservation;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.messaging.Endpoint;
import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.solmelia.namespaces.solres.AvailabilitySummaryRecordDocument.AvailabilitySummaryRecord;
import com.solmelia.namespaces.solres.AvailabilitySummaryRecordsDocument.AvailabilitySummaryRecords;
import com.solmelia.namespaces.solres.CancelPenaltyType;
import com.solmelia.namespaces.solres.GuestCountsDocument.GuestCounts;
import com.solmelia.namespaces.solres.HITISMessageDocument;
import com.solmelia.namespaces.solres.HITISMessageDocument.HITISMessage;
import com.solmelia.namespaces.solres.HITISOperationType;
import com.solmelia.namespaces.solres.HITISOperationType.OperationType;
import com.solmelia.namespaces.solres.ProfileDocument.Profile.ProfileType;
import com.solmelia.namespaces.solres.RateDescriptionsDocument.RateDescriptions.RateDescription;
import com.solmelia.namespaces.solres.RatePlansDocument.RatePlans;
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
			return sendAvailabilityQuery(createAvailabilityMessage(requestRoom));
		} catch (Exception ex) {
			return null;
		}
	}

	private String createAvailabilityMessage(ReservationRequestRoom requestRoom) throws ManagerBeanException {
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
		operation.setOperationName(AVAILABILITY_QUERY_REQUEST);
		operation.setOperationType(OperationType.AVAILABILITY);
		operation.setAvailabilityOriginatorCode(GP);
		operation.addNewAvailabilityQuery();
		operation.getAvailabilityQuery().addNewHotelReference();
		operation.getAvailabilityQuery().getHotelReference().setHotelCode(request.getHotel().getCode());
		operation.getAvailabilityQuery().addNewStayDateRange().addNewDateTimeSpan();
		operation.getAvailabilityQuery().getStayDateRange().getDateTimeSpan().setStartInstant(startInstant);
		operation.getAvailabilityQuery().getStayDateRange().getDateTimeSpan().setDuration(nights);
		if (request.isCompanyHolder() && request.getCompany() != null && request.getCompany().getId() != null) {
			String code = getReservationUtils().obtainCustomerCode(request.getCompany(), SOLRES);
			if (code != null) {
				operation.getAvailabilityQuery().addNewProfiles().addNewProfile();
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileType(ProfileType.REP_COMPANY);
				operation.getAvailabilityQuery().getProfiles().getProfile().setProfileID(code);
			}
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
			String code = getReservationUtils().obtainCustomerCode(request.getAgency(), SOLRES);
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
		if (!request.isGuestHolder()) {
			IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), customer.getRegistry().getId());
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), RATE_PLAN);
			criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_DOMAIN), getReservationUtils().getDomain());
			rAddInfoList = rAddInfoBean.getList(criteria);
		} else {
			if (customer.getTariff() != null && customer.getTariff().getId() != null) {
				RegistryAddInfo rAddInfo = new RegistryAddInfo();
				rAddInfo.setRegistry(customer.getRegistry());
				rAddInfo.setAttribute(RATE_PLAN);
				rAddInfo.setValue(customer.getTariff().getCode());
				rAddInfoList.add(rAddInfo);
			}
		}
		return rAddInfoList;
	}

	private List<AvailableRoomStay> sendAvailabilityQuery(String message) throws MalformedURLException, SOAPException, XmlException, IOException {
		Endpoint endpoint = new URLEndpoint(new URL(SOAP_SERVER_URL).toString());
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapRequest = messageFactory.createMessage();
		soapRequest.getSOAPBody().setValue(convertMessage(message));

		SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
		
		HITISMessageDocument hitisDocument = HITISMessageDocument.Factory.parse(soapResponse.getSOAPBody().extractContentAsDocument());
		return obtainAvailableRoomStayList(hitisDocument.getHITISMessage());
	}

	private String convertMessage(String message) {
		String value = message;
		value = value.replaceAll("HITISOperationAbstract", "HITISOperation");
		value = value.replaceAll("T00:00:00\\.000\\+0[0-9]:00", "T00:00:00");
		return value;
	}

	private List<AvailableRoomStay> obtainAvailableRoomStayList(HITISMessage message) {
		List<AvailableRoomStay> availableRoomStayList = new LinkedList<AvailableRoomStay>();
		if (message.getHeader().getOriginalMessageID().equals(getMessageId())) {
			HITISOperationType operation = (HITISOperationType)message.getBody().getHITISOperationAbstract();
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
					availableRoomStay.setTariffCode(rateCode);
					availableRoomStay.setTariffDescription(rateDescription.getDetailDescription());
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
		}
		return availableRoomStayList;
	}

	private double parseDouble(String value) {
		value = value.replace(',', '.');
		return Double.parseDouble(value);
	}

	private String obtainPenaltyConditions(CancelPenaltyType penalty) {
		String conditions = null;
		if (penalty != null) {
			conditions = penalty.getDeadline().getOffsetUnitMultiplier().toString() + " " + penalty.getDeadline().getOffsetUnit().toString() + " ";
			conditions += penalty.getDeadline().getOffsetDropTime().toString() + "\n";
			conditions += penalty.getDuePayment().getQuantity() + " " + penalty.getDuePayment().getUnit().toString();
		}
		
		return conditions;
	}

}
