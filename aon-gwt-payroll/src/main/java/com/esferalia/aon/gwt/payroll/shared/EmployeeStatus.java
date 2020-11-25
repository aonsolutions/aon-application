package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;


public abstract class EmployeeStatus implements Serializable {
	
	
	public static interface Visitor  {
		void up2Date();
		void forbidden();
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
	
	public static class Forbidden extends EmployeeStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.forbidden();
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

	public static void ifSistemaREDEnabled(EmployeeStatus employeeStatus, Runnable saltraEnable, Runnable saltraDisabled) {
		employeeStatus.visit(new Visitor() {
	
			@Override
			public void up2Date() {
				saltraEnable.run();
			}
			
			@Override
			public void forbidden() {
				saltraDisabled.run();
			}
	
			@Override
			public void invalidData() {
				saltraEnable.run();
			}
	
			@Override
			public void endDateNotFound() {
				saltraEnable.run();
			}
	
			@Override
			public void employeeNotFound() {
				saltraEnable.run();
			}
	
			@Override
			public void occupationNotFound() {
				saltraEnable.run();
			}
	
			@Override
			public void saltraCredentialsNotFound() {
				saltraDisabled.run();
			}
	
			@Override
			public void mismatchedStartDate(MismatchedStartDate status) {
				saltraEnable.run();
			}
	
			@Override
			public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				saltraEnable.run();
			}
	
			@Override
			public void mismatchedOccupation(MismatchedOccupation status) {
				saltraEnable.run();
			}
	
			@Override
			public void mismatchedContractType(MismatchedContractType status) {
				saltraEnable.run();
			}
	
			@Override
			public void mismatchedCCC(MismatchedCCC status) {
				saltraEnable.run();
			}
	
			@Override
			public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				saltraEnable.run();
	
			}
	
		});
	}

	public static void ifSistemaREDError(EmployeeStatus employeeStatus, Runnable onError, Runnable onSuccess) {
		employeeStatus.visit(new Visitor() {
	
			@Override
			public void up2Date() {
				onSuccess.run();
			}
			
			@Override
			public void forbidden() {
				onError.run();
			}
	
			@Override
			public void invalidData() {
				onError.run();
			}
	
			@Override
			public void endDateNotFound() {
				onError.run();
			}
	
			@Override
			public void employeeNotFound() {
				onError.run();
			}
	
			@Override
			public void occupationNotFound() {
				onError.run();
			}
	
			@Override
			public void saltraCredentialsNotFound() {
				onError.run();
			}
	
			@Override
			public void mismatchedStartDate(MismatchedStartDate status) {
				onError.run();
			}
	
			@Override
			public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				onError.run();
			}
	
			@Override
			public void mismatchedOccupation(MismatchedOccupation status) {
				onError.run();
			}
	
			@Override
			public void mismatchedContractType(MismatchedContractType status) {
				onError.run();
			}
	
			@Override
			public void mismatchedCCC(MismatchedCCC status) {
				onError.run();
			}
	
			@Override
			public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				onError.run();
	
			}
	
		});
	}

	public static void trace(AndEmployeeStatus employeeStatus) {
		employeeStatus.visit(new Visitor() {
			
			@Override
			public void up2Date() {
				System.out.println("up2Date");					
			}
			
			@Override
			public void forbidden() {
				System.out.println("forbidden");					
			}
			
			@Override
			public void saltraCredentialsNotFound() {
				System.out.println("saltraCredentialsNotFound");
			}
			
	
			@Override
			public void invalidData() {
				System.out.println("invalidData");
			}
			
			@Override
			public void employeeNotFound() {
				System.out.println("employeeNotFound");
			}
	
			@Override
			public void endDateNotFound() {
				System.out.println("endDateNotFound");
			}
	
			@Override
			public void mismatchedCCC(MismatchedCCC status) {
				System.out.println("mismatchedCCC");
			}
	
			@Override
			public void mismatchedStartDate(MismatchedStartDate status) {
				System.out.println("mismatchedStartDate");
			}
	
			@Override
			public void mismatchedContractType(MismatchedContractType status) {
				System.out.println("mismatchedContractType");
			}
	
			@Override
			public void occupationNotFound() {
				System.out.println("occupationNotFound");
			}
	
			@Override
			public void mismatchedOccupation(MismatchedOccupation status) {
				System.out.println("mismatchedOccupation");				
			}
	
			@Override
			public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				System.out.println("mismatchedPartialFactor");	
			}
			
			@Override
			public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				System.out.println("mismatchedQuoteGroup");	
			}			
		});
	}

	public static AndEmployeeStatus isUp2Date(AndEmployeeStatus employeeStatus) {
		employeeStatus.visit( new Visitor() {
	
			@Override
			public void up2Date() {
			}
			
			@Override
			public void forbidden() {
				throw new OutOfDateException();
			}
	
			@Override
			public void invalidData() {
				throw new OutOfDateException();
			}
	
			@Override
			public void endDateNotFound() {
				throw new OutOfDateException();
			}
	
			@Override
			public void employeeNotFound() {
				throw new OutOfDateException();
			}
	
			@Override
			public void saltraCredentialsNotFound() {
				throw new OutOfDateException();
			}
	
			@Override
			public void mismatchedCCC(MismatchedCCC status) {
				throw new OutOfDateException();
			}
	
			@Override
			public void mismatchedStartDate(MismatchedStartDate status) {
				throw new OutOfDateException();
			}
	
			@Override
			public void mismatchedContractType(MismatchedContractType status) {
				throw new OutOfDateException();
			}
	
			@Override
			public void occupationNotFound() {
				throw new OutOfDateException();				
			}
	
			@Override
			public void mismatchedOccupation(MismatchedOccupation status) {
				throw new OutOfDateException();
			}
	
			@Override
			public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				throw new OutOfDateException();
			}
			
			@Override
			public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				throw new OutOfDateException();
			}
			
		});
		return employeeStatus;
	}
	

}
