package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;

public class ProjectDAO {
	
	public static ProjectReservation getProjectReservation(AONContext ctx, Integer projectId){	
		return ctx.getDslContext()
				.select().from(PROJECT_RESERVATION).where(PROJECT_RESERVATION.PROJECT.eq(projectId))
				.limit(1).fetchInto(PROJECT_RESERVATION).stream().map(new FullProjectReservationFiller())
				.collect(Collectors.toCollection(LinkedList::new)).getFirst();
	}

	private static class FullProjectReservationFiller implements Function<ProjectReservationRecord, ProjectReservation> {
		
		@Override
		public ProjectReservation apply(ProjectReservationRecord r) {
			return new ProjectReservation().setAdvance(r.getAdvance())
					.setAdvanceInvoiced(r.getAdvanceInvoiced())
					.setAgency(r.getAgency())
					.setAgencyCommissionAmount(r.getAgencyCommissionAmount())
					.setAgencyCommissionPercent(r.getAgencyCommissionPercent())
					.setAgencyRebate(r.getAgencyRebate())
					.setBankTransaction(r.getBankTransaction())
					.setBookingHolder(r.getBookingHolder())
					.setCancellationDate(r.getCancellationDate())
					.setCancellationUser(r.getCancellationUser())
					.setCheckStatus(r.getCheckStatus())
					.setCode(r.getCode())
					.setComments(r.getComments())
					.setCompany(r.getCompany())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setCreditCardCvv(r.getCreditCardCvv())
					.setCreditCardExpirationMonth(r.getCreditCardExpirationMonth())
					.setCreditCardExpirationYear(r.getCreditCardExpirationYear())
					.setCreditCardHolder(r.getCreditCardHolder())
					.setCreditCardNumber(r.getCreditCardNumber())
					.setCrsCode(r.getCrsCode())
					.setDiscountAmount(r.getDiscountAmount())
					.setDiscountPercent(r.getDiscountPercent())		
					.setDomain(new Domain().setId(r.getDomain()))
					.setEarlyCheckOut(r.getEarlyCheckOut())
					.setEndDate(r.getEndDate())
					.setEndTime(r.getEndTime())
					.setHotel(r.getHotel())
					.setHotelReservation(r.getHotelReservation())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setOtherTaxQuota(r.getOtherTaxQuota())
					.setPenaltyDays(r.getPenaltyDays())
					.setPrepay(r.getPrepay())
					.setProject(r.getProject())
					.setRemarks(r.getRemarks())
					.setSeller(r.getSeller())
					.setSource(r.getSource())
					.setStartDate(r.getStartDate())
					.setStartTime(r.getStartTime())
					.setStatus(r.getStatus())
					.setTaxableBase(r.getTaxableBase())
					.setTotal(r.getTotal())
					.setVatQuota(r.getVatQuota());
		}

	}
}
