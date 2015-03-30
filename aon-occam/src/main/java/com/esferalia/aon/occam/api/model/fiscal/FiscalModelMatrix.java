package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalModelMatrix implements Serializable {

	private static final long serialVersionUID = -3239003535147628145L;
	
	private LinkedList<FiscalModelMatrixRow> rows;
	
	public static enum FiscalStatus implements Serializable{
		 MISSING
		,PENDING
		,FINISHED
	}

	public static enum FiscalMatrixPeriod implements Serializable {
		MONTHLY {
			@Override
			public int getNumberOfPeriod() {
				return 12;
			}
		},
		QUATERLY {
			@Override
			public int getNumberOfPeriod() {
				return 4;
			}
		},
		YEARLY {
			@Override
			public int getNumberOfPeriod() {
				return 1;
			}
		};

		public abstract int getNumberOfPeriod();

		public static FiscalMatrixPeriod get(Period period) {
			if (period.isMonthPeriod()) return MONTHLY;
			if (period.isQuarterPeriod()) return QUATERLY;
			return YEARLY;
		}
	}

	public static class FiscalModelMatrixRow implements Serializable {

		private static final long serialVersionUID = 413993542639618705L;
		
		private FiscalModelType model;
		private int year;
		private FiscalMatrixPeriod period;
		private Administration administration;
		private int domainId;
		private String domainName;
		private String document;
		private String name;
		private FiscalStatus[] statuses;

		public FiscalModelType getModel() {
			return model;
		}

		public FiscalModelMatrixRow setModel(FiscalModelType model) {
			this.model = model;
			return this;
		}

		public int getYear() {
			return year;
		}

		public FiscalModelMatrixRow setYear(int year) {
			this.year = year;
			return this;
		}

		public FiscalMatrixPeriod getPeriod() {
			return period;
		}

		public FiscalModelMatrixRow setPeriod(FiscalMatrixPeriod period) {
			this.period = period;
			setStatuses(new FiscalStatus[this.period.getNumberOfPeriod()]);
			for (int x = 0; x < getStatuses().length; x++ ) {
				getStatuses()[x] = FiscalStatus.MISSING;	
			}
			return this;
		}

		public FiscalModelMatrixRow setPeriod(Period period) {
			if (period.isMonthPeriod()) {
				setPeriod(FiscalMatrixPeriod.MONTHLY);
			} else if (period.isQuarterPeriod()) {
				setPeriod(FiscalMatrixPeriod.QUATERLY);
			} else {
				setPeriod(FiscalMatrixPeriod.YEARLY);
			}
			return this;
		}

		public Administration getAdministration() {
			return administration;
		}

		public FiscalModelMatrixRow setAdministration(Administration administration) {
			this.administration = administration;
			return this;
		}

		public int getDomainId() {
			return domainId;
		}

		public FiscalModelMatrixRow setDomainId(int domainId) {
			this.domainId = domainId;
			return this;
		}

		public String getDomainName() {
			return domainName;
		}

		public FiscalModelMatrixRow setDomainName(String domainName) {
			this.domainName = domainName;
			return this;
		}

		public String getDocument() {
			return document;
		}

		public FiscalModelMatrixRow setDocument(String document) {
			this.document = document;
			return this;
		}

		public String getName() {
			return name;
		}

		public FiscalModelMatrixRow setName(String name) {
			this.name = name;
			return this;
		}
		
		public FiscalStatus[] getStatuses() {
			return statuses;
		}
		public FiscalModelMatrixRow setStatuses(FiscalStatus[] statuses) {
			this.statuses = statuses;
			return this;
		}

		public boolean isMonthly() {
			return period == FiscalMatrixPeriod.MONTHLY;
		}

		public boolean isQuaterly() {
			return period == FiscalMatrixPeriod.QUATERLY;
		}

		public boolean isYearly() {
			return period == FiscalMatrixPeriod.YEARLY;
		}

		public void setStatus(Period per, FiscalStatus status) {
			int i = per.isMonthPeriod()?per.ordinal()
					:per.isQuarterPeriod()
						?(per.ordinal()-12)
						:0;
			getStatuses()[i] = status;
		}
	}

	public static class FiscalModelMatrixItem implements Serializable{
		
		private static final long serialVersionUID = -3871892696016731326L;
		
		private FiscalModelType model;
		private int year;
		private Period period;
		private Administration administration;
		private FiscalStatus status;
		private int domainId;
		private String domainName;
		private String document;
		private String name;
		
		
		public FiscalModelType getModel() {
			return model;
		}
		public FiscalModelMatrixItem setModel(FiscalModelType model) {
			this.model = model;
			return this;
		}
		public int getYear() {
			return year;
		}
		public FiscalModelMatrixItem setYear(int year) {
			this.year = year;
			return this;
		}
		public Period getPeriod() {
			return period;
		}
		public FiscalModelMatrixItem setPeriod(Period period) {
			this.period = period;
			return this;
		}
		public Administration getAdministration() {
			return administration;
		}
		public FiscalModelMatrixItem setAdministration(Administration administration) {
			this.administration = administration;
			return this;
		}
		public FiscalStatus getStatus() {
			return status;
		}
		public FiscalModelMatrixItem setStatus(FiscalStatus status) {
			this.status = status;
			return this;
		}
		public int getDomainId() {
			return domainId;
		}
		public FiscalModelMatrixItem setDomainId(int domainId) {
			this.domainId = domainId;
			return this;
		}
		public String getDomainName() {
			return domainName;
		}
		public FiscalModelMatrixItem setDomainName(String domainName) {
			this.domainName = domainName;
			return this;
		}
		public String getDocument() {
			return document;
		}
		public FiscalModelMatrixItem setDocument(String document) {
			this.document = document;
			return this;
		}
		public String getName() {
			return name;
		}
		public FiscalModelMatrixItem setName(String name) {
			this.name = name;
			return this;
		}
		
		@Override
		public String toString() {
			return model
				+ "\t - " +year
				+ " - " +period
				+ "\t - " +administration
				+ " - " +status
				+ " - " +domainId
				+ " - " +domainName
				+ " - " +document
				+ " - " +name;
		}
		
	}
	
	public FiscalModelMatrix add(FiscalModelMatrixItem item) {
		if (rows == null) {
			rows = new LinkedList<FiscalModelMatrix.FiscalModelMatrixRow>();
		}
		FiscalModelMatrixRow row = null; 
		for (FiscalModelMatrixRow r : rows) {
			if (r.getModel() == item.getModel() && r.getPeriod() == FiscalMatrixPeriod.get(item.getPeriod()) && r.getAdministration() == item.getAdministration() && r.getDomainId() == item.getDomainId()) {
				if (r.getYear() == 0) {
					r.setYear( item.getYear());
				}
				if (AonStringUtils.isBlank( r.getDocument())) {
					r.setDocument( item.getDocument());
					r.setName( item.getName());
				}
				if ( r.getYear() == item.getYear() && AonStringUtils.equals( r.getDocument(),item.getDocument())) {
					row = r;
					break;
				}
			}
		}
		if (row == null) {
			row = new FiscalModelMatrixRow().setModel(item.getModel())
					.setYear(item.getYear()).setPeriod(item.getPeriod())
					.setAdministration(item.getAdministration())
					.setDomainId(item.getDomainId())
					.setDomainName(item.getDomainName())
					.setDocument(item.getDocument())
					.setName(item.getName())
					.setPeriod(item.getPeriod());
			rows.add(row);
		}
		row.setStatus(item.getPeriod(),item.getStatus());
		return this;
	}
	
	public LinkedList<FiscalModelMatrixRow> getRows() {
		return rows;
	}
}