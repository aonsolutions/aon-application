package com.esferalia.aon.pms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.person.Person;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.entity.master.ProjectReservationGuestDB;
import com.esferalia.aon.pms.enumeration.ReservationGuestType;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

@Entity
@Table(name="project_reservation_guest")
public class ProjectReservationGuest extends ProjectReservationGuestDB implements ISQLConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static String SELECT_GUEST_TYPE =
			"SELECT IF(PR.hotel != ?, 1, 2) AS " + GUEST_TYPE +  
			" FROM project_reservation AS PR, project_reservation_guest AS PRG" +
			" WHERE PRG.domain = ?" +
			" AND PRG.person = ?" +
			" AND PRG.project_reservation = PR.project" +
			" AND PR.start_date < ?" + 
			" ORDER BY " + GUEST_TYPE + " DESC";

	private ReservationGuestType guestType;

	public ProjectReservationGuest() {
		setDocumentType(DocumentType.NIF);
		setDocumentCountry(Country.ES);
	}

	@Transient
	public ReservationGuestType getGuestType() {
		if (guestType == null) {
			guestType = obtainGuestType();
		}
		return guestType;
	}
	public void setGuestType(ReservationGuestType guestType) {
		this.guestType = guestType;
	}

	@Transient
	public ReservationGuestType obtainGuestType() {
		if (getPerson() != null && getPerson().getId() != null) {
			Connection connection = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(getProjectReservation().getDomain()));
				stmt = connection.prepareStatement(SELECT_GUEST_TYPE, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				SQLUtils.setInt(stmt, 1, getProjectReservation().getHotelReservation().getId());
				SQLUtils.setInt(stmt, 2, getProjectReservation().getDomain());
				SQLUtils.setInt(stmt, 3, getPerson().getId());
				SQLUtils.setDate(stmt, 4, getProjectReservation().getStartDate());
				rs = stmt.executeQuery();
				return (rs.next()) ? ReservationGuestType.values()[rs.getInt(GUEST_TYPE)] : ReservationGuestType.NEW_IN_CHAIN;
			} catch (Throwable ex) {
				return null;
			} finally {
				SQLUtils.closeQuietly(rs);
				SQLUtils.closeQuietly(stmt);
				SQLUtils.closeQuietly(connection);
			}
		}
		return null;
	}

	@Transient
	public boolean isGuestNewInChain() {
		return getGuestType() == ReservationGuestType.NEW_IN_CHAIN;
	}

	@Transient
	public boolean isGuestNewInHotel() {
		return getGuestType() == ReservationGuestType.NEW_IN_HOTEL;
	}

	@Transient
	public boolean isGuestPreviouslyStayedInHotel() {
		return getGuestType() == ReservationGuestType.PREVIOUSLY_STAYED_IN_HOTEL;
	}

	@Transient
	public String getFullName() {
    	String fullName = StringUtils.isEmpty(getTreatment()) ? "" : getTreatment() + " ";
    	fullName += StringUtils.isEmpty(getName()) ? "" : getName() + " ";
    	fullName += StringUtils.isEmpty(getSurname()) ? "" : getSurname() + " ";
    	fullName += StringUtils.isEmpty(getSurname2()) ? "" : getSurname2() + " ";
    	return fullName;
	}

    @Transient
	public String getFullAddress() {
    	String fullAddress = StringUtils.isEmpty(getAddress()) ? "" : getAddress() + " ";
    	fullAddress += StringUtils.isEmpty(getNumber()) ? "" : getNumber() + " ";
    	fullAddress += StringUtils.isEmpty(getAddress2()) ? "" : getAddress2() + " ";
    	fullAddress += StringUtils.isEmpty(getZip()) ? "" : getZip() + " - ";
    	fullAddress += StringUtils.isEmpty(getCity()) ? "" : getCity() + " ";
    	fullAddress += StringUtils.isEmpty(getProvince()) ? "" : "(" + getProvince() + ") ";
    	fullAddress += StringUtils.isEmpty(getCountry()) ? "" : getCountry();
    	return fullAddress;
	}
	
	@Transient
	public boolean isValidDocument() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValid();
	}

	@Transient
	public boolean isDocumentValidable() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getDocument());
		registryDocument.setType(getDocumentType());
		registryDocument.setCountry(getDocumentCountry());
		return registryDocument.isValidable();
	}

	@Override
	public void setPerson(Person person) {
		super.setPerson(person);
		setGuestType(null);
	}

}