package com.esferalia.aon.pms.reservation;

import java.util.Date;

import org.opentravel.ota.x2003.x05.HotelReservationType;
import org.opentravel.ota.x2003.x05.HotelReservationsType;
import org.opentravel.ota.x2003.x05.OTAHotelResNotifRQDocument;
import org.opentravel.ota.x2003.x05.TPAExtensionsType;
import org.opentravel.ota.x2003.x05.ProfilesType.ProfileInfo;
import org.opentravel.ota.x2003.x05.ResGuestsType.ResGuest;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationManager implements IReservationConstants {

	private ReservationUtils reservationUtils;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils();
		}
		return reservationUtils;
	}

	public String processReservation(String reservationXml) {
		try {
			OTAHotelResNotifRQDocument document = OTAHotelResNotifRQDocument.Factory.parse(reservationXml);
			HotelReservationsType reservationsType = document.getOTAHotelResNotifRQ().getHotelReservations();
			for (int i=0; i<reservationsType.sizeOfHotelReservationArray(); i++) {
				processReservation(reservationsType.getHotelReservationArray(i));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void processReservation(HotelReservationType reservationType) throws ManagerBeanException {
		String actionType = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), ACTION, TYPE);
		if (actionType.equals(CREATE_RESERVATION)) {
			createReservation(reservationType);
		} else if (actionType.equals(MODIFY_RESERVATION)) {
			modifyReservation(reservationType);
		} else if (actionType.equals(CANCEL_RESERVATION)) {
			cancelReservation(reservationType);
		}
	}

	private void createReservation(HotelReservationType reservationType) throws ManagerBeanException {
		String hotel = reservationType.getRoomStays().getRoomStayArray(0).getBasicPropertyInfo().getHotelCode();
		String reservationId = reservationType.getResGlobalInfo().getHotelReservationIDs().getHotelReservationIDArray(0).getResIDValue();
		String creationDate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, DATE);
		String creationTime = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), OPERATION_TIME_STAMP, TIME);
		Date checkIn = reservationType.getResGlobalInfo().getTimeSpan().getStart().getTime();
		Date checkOut = reservationType.getResGlobalInfo().getTimeSpan().getEnd().getTime();
		ProfileInfo agencyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), AGENCY_TYPE, IATA);
		String agencyRebate = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT_MODE, null);
		ProfileInfo companyInfo = findProfileInfo(reservationType.getResGuests().getResGuestArray(), COMPANY_TYPE, null);
		String discountPercent = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, PERCENT);
		String discountAmount = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), DISCOUNT, AMOUNT);
		String bookingHolder = findTpaExtensionsAttribute(reservationType.getTPAExtensions(), BOOKING_HOLDER, null);
		double taxableBase = reservationType.getResGlobalInfo().getTotal().getAmountBeforeTax().doubleValue();
		double vatQuota = reservationType.getResGlobalInfo().getTotal().getTaxes().getTaxArray(0).getAmount().doubleValue();
		double total = reservationType.getResGlobalInfo().getTotal().getAmountAfterTax().doubleValue();
		
		ProjectReservation reservation = new ProjectReservation();
		//reservation.setHotel(getReservationUtils().obtainHotel(hotel));
		reservation.setCode(reservationId);
		reservation.setCreationDate(getReservationUtils().obtainCreationDate(creationDate, creationTime));
		reservation.setStartDate(checkIn);
		reservation.setEndDate(checkOut);
		reservation.setSeller(null);
		reservation.setAgency(null);
		reservation.setAgencyCommissionPercent(0);
		reservation.setAgencyCommissionAmount(0);
		reservation.setAgencyRebate(false);
		reservation.setCompany(null);
		reservation.setDiscountPercent(0);
		reservation.setDiscountAmount(0);
		reservation.setBookingHolder(null);
		reservation.setTaxableBase(0);
		reservation.setVatQuota(0);
		reservation.setOtherTaxQuota(0);
		reservation.setTotal(0);
		reservation.setComments(null);
		reservation.setStatus(ReservationStatus.PENDING);

		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		//reservationBean.insert(reservation);
	}

	private void modifyReservation(HotelReservationType reservationType) {
		
	}

	private void cancelReservation(HotelReservationType reservationType) {
		
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

}
