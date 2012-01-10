package com.esferalia.aon.pms.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.Room;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IPmsAlias {



	/** 
	* DAOConstantsEntry for Hotel entity.
	*/ 
	DAOConstantsEntry HOTEL_ENTRY = DAOConstants.getDAOConstant(Hotel.class);

	/** 
	* Alias value: Hotel_id
	* Hibernate value: Hotel.id
	*/
	String  HOTEL_ID = HOTEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Hotel_code
	* Hibernate value: Hotel.code
	*/
	String  HOTEL_CODE = HOTEL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Hotel_scope_id
	* Hibernate value: Hotel.scope.id
	*/
	String  HOTEL_SCOPE_ID = HOTEL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Hotel_workPlace_id
	* Hibernate value: Hotel.workPlace.id
	*/
	String  HOTEL_WORK_PLACE_ID = HOTEL_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Hotel_workPlace_description
	* Hibernate value: Hotel.workPlace.description
	*/
	String  HOTEL_WORK_PLACE_DESCRIPTION = HOTEL_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Hotel_customer_id
	* Hibernate value: Hotel.customer.id
	*/
	String  HOTEL_CUSTOMER_ID = HOTEL_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Hotel_active
	* Hibernate value: Hotel.active
	*/
	String  HOTEL_ACTIVE = HOTEL_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ProjectReservation entity.
	*/ 
	DAOConstantsEntry PROJECT_RESERVATION_ENTRY = DAOConstants.getDAOConstant(ProjectReservation.class);

	/** 
	* Alias value: ProjectReservation_id
	* Hibernate value: ProjectReservation.id
	*/
	String  PROJECT_RESERVATION_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectReservation_project_id
	* Hibernate value: ProjectReservation.project.id
	*/
	String  PROJECT_RESERVATION_PROJECT_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectReservation_hotel_id
	* Hibernate value: ProjectReservation.hotel.id
	*/
	String  PROJECT_RESERVATION_HOTEL_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectReservation_hotel_scope_id
	* Hibernate value: ProjectReservation.hotel.scope.id
	*/
	String  PROJECT_RESERVATION_HOTEL_SCOPE_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProjectReservation_code
	* Hibernate value: ProjectReservation.code
	*/
	String  PROJECT_RESERVATION_CODE = PROJECT_RESERVATION_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProjectReservation_creationDate
	* Hibernate value: ProjectReservation.creationDate
	*/
	String  PROJECT_RESERVATION_CREATION_DATE = PROJECT_RESERVATION_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProjectReservation_startDate
	* Hibernate value: ProjectReservation.startDate
	*/
	String  PROJECT_RESERVATION_START_DATE = PROJECT_RESERVATION_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProjectReservation_endDate
	* Hibernate value: ProjectReservation.endDate
	*/
	String  PROJECT_RESERVATION_END_DATE = PROJECT_RESERVATION_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProjectReservation_seller_id
	* Hibernate value: ProjectReservation.seller.id
	*/
	String  PROJECT_RESERVATION_SELLER_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProjectReservation_agency_id
	* Hibernate value: ProjectReservation.agency.id
	*/
	String  PROJECT_RESERVATION_AGENCY_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProjectReservation_agencyCommissionPercent
	* Hibernate value: ProjectReservation.agencyCommissionPercent
	*/
	String  PROJECT_RESERVATION_AGENCY_COMMISSION_PERCENT = PROJECT_RESERVATION_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ProjectReservation_agencyCommissionAmount
	* Hibernate value: ProjectReservation.agencyCommissionAmount
	*/
	String  PROJECT_RESERVATION_AGENCY_COMMISSION_AMOUNT = PROJECT_RESERVATION_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ProjectReservation_agencyRebate
	* Hibernate value: ProjectReservation.agencyRebate
	*/
	String  PROJECT_RESERVATION_AGENCY_REBATE = PROJECT_RESERVATION_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: ProjectReservation_company_id
	* Hibernate value: ProjectReservation.company.id
	*/
	String  PROJECT_RESERVATION_COMPANY_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: ProjectReservation_discountPercent
	* Hibernate value: ProjectReservation.discountPercent
	*/
	String  PROJECT_RESERVATION_DISCOUNT_PERCENT = PROJECT_RESERVATION_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: ProjectReservation_discountAmount
	* Hibernate value: ProjectReservation.discountAmount
	*/
	String  PROJECT_RESERVATION_DISCOUNT_AMOUNT = PROJECT_RESERVATION_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: ProjectReservation_bookingHolder
	* Hibernate value: ProjectReservation.bookingHolder
	*/
	String  PROJECT_RESERVATION_BOOKING_HOLDER = PROJECT_RESERVATION_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: ProjectReservation_tariff_id
	* Hibernate value: ProjectReservation.tariff.id
	*/
	String  PROJECT_RESERVATION_TARIFF_ID = PROJECT_RESERVATION_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: ProjectReservation_taxableBase
	* Hibernate value: ProjectReservation.taxableBase
	*/
	String  PROJECT_RESERVATION_TAXABLE_BASE = PROJECT_RESERVATION_ENTRY.getAliasNames()[18];

	/** 
	* Alias value: ProjectReservation_vatQuota
	* Hibernate value: ProjectReservation.vatQuota
	*/
	String  PROJECT_RESERVATION_VAT_QUOTA = PROJECT_RESERVATION_ENTRY.getAliasNames()[19];

	/** 
	* Alias value: ProjectReservation_otherTaxQuota
	* Hibernate value: ProjectReservation.otherTaxQuota
	*/
	String  PROJECT_RESERVATION_OTHER_TAX_QUOTA = PROJECT_RESERVATION_ENTRY.getAliasNames()[20];

	/** 
	* Alias value: ProjectReservation_total
	* Hibernate value: ProjectReservation.total
	*/
	String  PROJECT_RESERVATION_TOTAL = PROJECT_RESERVATION_ENTRY.getAliasNames()[21];

	/** 
	* Alias value: ProjectReservation_remarks
	* Hibernate value: ProjectReservation.remarks
	*/
	String  PROJECT_RESERVATION_REMARKS = PROJECT_RESERVATION_ENTRY.getAliasNames()[22];

	/** 
	* Alias value: ProjectReservation_status
	* Hibernate value: ProjectReservation.status
	*/
	String  PROJECT_RESERVATION_STATUS = PROJECT_RESERVATION_ENTRY.getAliasNames()[23];



	/** 
	* DAOConstantsEntry for ProjectReservationGuest entity.
	*/ 
	DAOConstantsEntry PROJECT_RESERVATION_GUEST_ENTRY = DAOConstants.getDAOConstant(ProjectReservationGuest.class);

	/** 
	* Alias value: ProjectReservationGuest_address
	* Hibernate value: ProjectReservationGuest.address
	*/
	String  PROJECT_RESERVATION_GUEST_ADDRESS = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectReservationGuest_city
	* Hibernate value: ProjectReservationGuest.city
	*/
	String  PROJECT_RESERVATION_GUEST_CITY = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectReservationGuest_country
	* Hibernate value: ProjectReservationGuest.country
	*/
	String  PROJECT_RESERVATION_GUEST_COUNTRY = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectReservationGuest_email
	* Hibernate value: ProjectReservationGuest.email
	*/
	String  PROJECT_RESERVATION_GUEST_EMAIL = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProjectReservationGuest_guestIndex
	* Hibernate value: ProjectReservationGuest.guestIndex
	*/
	String  PROJECT_RESERVATION_GUEST_GUEST_INDEX = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProjectReservationGuest_id
	* Hibernate value: ProjectReservationGuest.id
	*/
	String  PROJECT_RESERVATION_GUEST_ID = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProjectReservationGuest_name
	* Hibernate value: ProjectReservationGuest.name
	*/
	String  PROJECT_RESERVATION_GUEST_NAME = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProjectReservationGuest_phone
	* Hibernate value: ProjectReservationGuest.phone
	*/
	String  PROJECT_RESERVATION_GUEST_PHONE = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProjectReservationGuest_projectReservation_id
	* Hibernate value: ProjectReservationGuest.projectReservation.id
	*/
	String  PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProjectReservationGuest_province
	* Hibernate value: ProjectReservationGuest.province
	*/
	String  PROJECT_RESERVATION_GUEST_PROVINCE = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProjectReservationGuest_surname
	* Hibernate value: ProjectReservationGuest.surname
	*/
	String  PROJECT_RESERVATION_GUEST_SURNAME = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: ProjectReservationGuest_treatment
	* Hibernate value: ProjectReservationGuest.treatment
	*/
	String  PROJECT_RESERVATION_GUEST_TREATMENT = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: ProjectReservationGuest_zip
	* Hibernate value: ProjectReservationGuest.zip
	*/
	String  PROJECT_RESERVATION_GUEST_ZIP = PROJECT_RESERVATION_GUEST_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for ProjectReservationRoom entity.
	*/ 
	DAOConstantsEntry PROJECT_RESERVATION_ROOM_ENTRY = DAOConstants.getDAOConstant(ProjectReservationRoom.class);

	/** 
	* Alias value: ProjectReservationRoom_id
	* Hibernate value: ProjectReservationRoom.id
	*/
	String  PROJECT_RESERVATION_ROOM_ID = PROJECT_RESERVATION_ROOM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectReservationRoom_item_id
	* Hibernate value: ProjectReservationRoom.item.id
	*/
	String  PROJECT_RESERVATION_ROOM_ITEM_ID = PROJECT_RESERVATION_ROOM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectReservationRoom_projectReservation_id
	* Hibernate value: ProjectReservationRoom.projectReservation.id
	*/
	String  PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID = PROJECT_RESERVATION_ROOM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectReservationRoom_roomIndex
	* Hibernate value: ProjectReservationRoom.roomIndex
	*/
	String  PROJECT_RESERVATION_ROOM_ROOM_INDEX = PROJECT_RESERVATION_ROOM_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ProjectReservationRoomDetail entity.
	*/ 
	DAOConstantsEntry PROJECT_RESERVATION_ROOM_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProjectReservationRoomDetail.class);

	/** 
	* Alias value: ProjectReservationRoomDetail_assetActivity_id
	* Hibernate value: ProjectReservationRoomDetail.assetActivity.id
	*/
	String  PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ID = PROJECT_RESERVATION_ROOM_DETAIL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectReservationRoomDetail_id
	* Hibernate value: ProjectReservationRoomDetail.id
	*/
	String  PROJECT_RESERVATION_ROOM_DETAIL_ID = PROJECT_RESERVATION_ROOM_DETAIL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectReservationRoomDetail_projectReservationRoom_id
	* Hibernate value: ProjectReservationRoomDetail.projectReservationRoom.id
	*/
	String  PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID = PROJECT_RESERVATION_ROOM_DETAIL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ProjectReservationService entity.
	*/ 
	DAOConstantsEntry PROJECT_RESERVATION_SERVICE_ENTRY = DAOConstants.getDAOConstant(ProjectReservationService.class);

	/** 
	* Alias value: ProjectReservationService_description
	* Hibernate value: ProjectReservationService.description
	*/
	String  PROJECT_RESERVATION_SERVICE_DESCRIPTION = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProjectReservationService_effectiveDate
	* Hibernate value: ProjectReservationService.effectiveDate
	*/
	String  PROJECT_RESERVATION_SERVICE_EFFECTIVE_DATE = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProjectReservationService_id
	* Hibernate value: ProjectReservationService.id
	*/
	String  PROJECT_RESERVATION_SERVICE_ID = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProjectReservationService_item_id
	* Hibernate value: ProjectReservationService.item.id
	*/
	String  PROJECT_RESERVATION_SERVICE_ITEM_ID = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProjectReservationService_price
	* Hibernate value: ProjectReservationService.price
	*/
	String  PROJECT_RESERVATION_SERVICE_PRICE = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProjectReservationService_projectReservation_id
	* Hibernate value: ProjectReservationService.projectReservation.id
	*/
	String  PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProjectReservationService_quantity
	* Hibernate value: ProjectReservationService.quantity
	*/
	String  PROJECT_RESERVATION_SERVICE_QUANTITY = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProjectReservationService_serviceIndex
	* Hibernate value: ProjectReservationService.serviceIndex
	*/
	String  PROJECT_RESERVATION_SERVICE_SERVICE_INDEX = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProjectReservationService_taxableBase
	* Hibernate value: ProjectReservationService.taxableBase
	*/
	String  PROJECT_RESERVATION_SERVICE_TAXABLE_BASE = PROJECT_RESERVATION_SERVICE_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for Room entity.
	*/ 
	DAOConstantsEntry ROOM_ENTRY = DAOConstants.getDAOConstant(Room.class);

	/** 
	* Alias value: Room_asset_id
	* Hibernate value: Room.asset.id
	*/
	String  ROOM_ASSET_ID = ROOM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Room_hotel_id
	* Hibernate value: Room.hotel.id
	*/
	String  ROOM_HOTEL_ID = ROOM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Room_id
	* Hibernate value: Room.id
	*/
	String  ROOM_ID = ROOM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Room_item_id
	* Hibernate value: Room.item.id
	*/
	String  ROOM_ITEM_ID = ROOM_ENTRY.getAliasNames()[3];


}