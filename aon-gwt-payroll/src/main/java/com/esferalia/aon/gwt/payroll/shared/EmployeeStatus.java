package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;


public abstract class EmployeeStatus implements Serializable {
	
	
	public static interface Visitor  {
		void up2Date();
		void invalidData();
		void endDateNotFound();
		void employeeNotFound();
		void occupationNotFound();
		void saltraCredentialsNotFound();
		void mismatchedCCC(MismatchedCCC status);
		void mismatchedStartDate(MismatchedStartDate status);
		void mismatchedOccupation(MismatchedOccupation status);
		void mismatchedContractType(MismatchedContractType status);
		void mismatchedPartialFactor(MismatchedPartialFactor status);		
		void mismatchedQuoteGroup(MismatchedQuoteGroup status);		
	}



	public static class InvalidData extends EmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.invalidData();
		}
	}
	public static class EmployeeNotFound extends EmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.employeeNotFound();
		}
	}
	
	public static class CredentialsNotFound extends EmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.saltraCredentialsNotFound();
		}
	}	
	
	public static class AndEmployeeStatus extends EmployeeStatus {
		
		private AndEmployeeStatus next;
		
		@Override
		public void visit(Visitor visitor) {
//			for (AndEmployeeStatus status = next; status != null; status = status.next) {
//				status.visit(visitor);
//			}
			if ( next != null ) {
				next.visit(visitor);
			}
		}
		
		public AndEmployeeStatus and(AndEmployeeStatus status) {

			AndEmployeeStatus last = this;
			while ( last.next != null ) 
				last = last.next;
			
			last.next = status;
			
			return this;
		}
	}
	
	public static class Up2Date extends AndEmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.up2Date();
			super.visit(visitor);
		}
	}	
	
	public static class EndDateNotFound extends AndEmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.endDateNotFound();
			super.visit(visitor);
		}
	}
	
	public static class OccupationNotFound extends AndEmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.occupationNotFound();
			super.visit(visitor);
		}
	}	
	
	public static class MismatchedStartDate extends AndEmployeeStatus {
		
		private Date ssStartDate;
		private Date aonStartDate;

		
		public Date getAonStartDate() {
			return aonStartDate;
		}

		public Date getSsStartDate() {
			return ssStartDate;
		}

		public MismatchedStartDate setSsStartDate(Date ssStartDate) {
			this.ssStartDate = ssStartDate;
			return this;
		}
		
		public MismatchedStartDate setAonStartDate(Date aonStartDate) {
			this.aonStartDate = aonStartDate;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedStartDate(this);
			super.visit(visitor);
		}
	}
	

	public static class MismatchedContractType extends AndEmployeeStatus {
		
		private String ssContractType;
		private String aonContractType;

		
		public String getAonContractType() {
			return aonContractType;
		}

		public String getSsContractType() {
			return ssContractType;
		}

		public MismatchedContractType setSsContractType(String ssContractType) {
			this.ssContractType = ssContractType;
			return this;
		}
		
		public MismatchedContractType setAonContractType(String aonContractType) {
			this.aonContractType = aonContractType;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedContractType(this);
			super.visit(visitor);
		}
	}
	
	public static class MismatchedCCC extends AndEmployeeStatus {
		
		private String ssCCC;
		private String aonCCC;

		
		public String getCCC() {
			return aonCCC;
		}

		public String getSsCCC() {
			return ssCCC;
		}

		public MismatchedCCC setSsCCC(String ssCCC) {
			this.ssCCC = ssCCC;
			return this;
		}
		
		public MismatchedCCC setAonCCC(String aonCCC) {
			this.aonCCC = aonCCC;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedCCC(this);
			super.visit(visitor);
		}
	}
	
	public static class MismatchedOccupation extends AndEmployeeStatus {
		
		private String ssOccupation;
		private String aonOccupation;

		
		public String getOccupation() {
			return aonOccupation;
		}

		public String getSsOccupation() {
			return ssOccupation;
		}

		public MismatchedOccupation setSsOccupation(String ssOccupation) {
			this.ssOccupation = ssOccupation;
			return this;
		}
		
		public MismatchedOccupation setAonOccupation(String aonOccupation) {
			this.aonOccupation = aonOccupation;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedOccupation(this);
			super.visit(visitor);
		}
	}
		
	public static class MismatchedPartialFactor extends AndEmployeeStatus {
		
		private String ssPartialFactor;
		private String aonPartialFactor;

		
		public String getPartialFactor() {
			return aonPartialFactor;
		}

		public String getSsPartialFactor() {
			return ssPartialFactor;
		}

		public MismatchedPartialFactor setSsPartialFactor(String ssPartialFactor) {
			this.ssPartialFactor = ssPartialFactor;
			return this;
		}
		
		public MismatchedPartialFactor setAonPartialFactor(String aonPartialFactor) {
			this.aonPartialFactor = aonPartialFactor;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedPartialFactor(this);
			super.visit(visitor);
		}
	}
	
	public static class MismatchedQuoteGroup extends AndEmployeeStatus {
		
		private String ssQuoteGroup;
		private String aonQuoteGroup;

		
		public String getQuoteGroup() {
			return aonQuoteGroup;
		}

		public String getSsQuoteGroup() {
			return ssQuoteGroup;
		}

		public MismatchedQuoteGroup setSsQuoteGroup(String ssQuoteGroup) {
			this.ssQuoteGroup = ssQuoteGroup;
			return this;
		}
		
		public MismatchedQuoteGroup setAonQuoteGroup(String aonQuoteGroup) {
			this.aonQuoteGroup = aonQuoteGroup;
			return this;

		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.mismatchedQuoteGroup(this);
			super.visit(visitor);
		}
	}
	
	public abstract void visit(Visitor visitor);
	

}
