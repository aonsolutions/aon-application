package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public abstract class EnterpriseITStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static interface Visitor  {
		void up2DateEnterprise();
		void updatedEnterprise();
		void unknownError(String message);
		void credentialsNotFound();
		void itNotExist(ItNotExist itNotExist);
	}



	public static class CredentialsNotFound extends EnterpriseITStatus{
		@Override
		public void visit(Visitor visitor) {
			visitor.credentialsNotFound();
		}
	}	
	
	public static class AndEmployeeITStatus extends EnterpriseITStatus {
		
		private AndEmployeeITStatus next;
		
		@Override
		public void visit(Visitor visitor) {
			if ( next != null ) {
				next.visit(visitor);
			}
		}
		
		public AndEmployeeITStatus and(AndEmployeeITStatus status) {

			AndEmployeeITStatus last = this;
			while ( last.next != null ) 
				last = last.next;
			
			last.next = status;
			
			return this;
		}
	}
	
	public static class Up2Date extends AndEmployeeITStatus{
		@Override	
		public void visit(Visitor visitor) {
			visitor.up2DateEnterprise();
			super.visit(visitor);
		}
	}		
	
	public static class UpdatedEnterprise extends AndEmployeeITStatus{
		@Override	
		public void visit(Visitor visitor) {
			visitor.updatedEnterprise();
			super.visit(visitor);
		}
	}		
	
	public static class ItNotExist extends AndEmployeeITStatus{
		
		Date date;
		String naf;
		String ccc;
		String name;
		Byte part; //0 baja , 1 confirmacion, 2 alta
		Byte confirmOrder;
		Integer idPart;
		
		
//		Double base;
//		Integer quoteDays;
//		Byte contracTypeLeave;
//		Byte contractType; // 0 = FIJO_DISCONTINUO_Y_TIEMPO_PARCIAL, 1 = RESTO_Y_AUTONOMOS;
		
		public Date getDate() {
			return date;
		}
		
		public String getNaf() {
			return naf;
		}

		public String getCcc() {
			return ccc;
		}
		
		public String getName() {
			return name;
		}
		
		public Byte getPart() {
			return part;
		}
		
		public Byte getConfirmOrder() {
			return confirmOrder;
		}

		public Optional<Integer> getIdPart() {
			return Optional.ofNullable(idPart);
		}
		
		public ItNotExist setDate(Date date) {
			this.date = date;
			return this;
		}
		
		public ItNotExist setNaf(String naf) {
			this.naf = naf;
			return this;
		}
		
		public ItNotExist setCcc(String ccc) {
			this.ccc = ccc;
			return this;
		}
		
		public ItNotExist setName(String name) {
			this.name = name;
			return this;
		}
		
		public ItNotExist setPart(Byte part) {
			this.part = part;
			return this;
		}
		
		public ItNotExist setConfirmOrder(Byte confirmOrder) {
			this.confirmOrder = confirmOrder;
			return this;
		}
		
		public ItNotExist setIdPart(Integer id) {
			this.idPart = id;
			return this;
		}
		
//		public Double getBase() {
//			return base;
//		}
//		
//		public Integer getQuoteDays() {
//			return quoteDays;
//		}
//		
//		public Byte getContractTypeLeave() {
//			return contracTypeLeave;
//		}
//		
//		public Byte getContractType() {
//			return contractType;
//		}
	
//		public ItBajaNotExist setBase(Double base) {
//			this.base = base;
//			return this;
//		}
//		
//		public ItBajaNotExist setQuoteDays(Integer quoteDays) {
//			this.quoteDays = quoteDays;
//			return this;
//		}
//		
//		public ItBajaNotExist setContractTypeLeave(Byte contracTypeLeave) {
//			this.contracTypeLeave = contracTypeLeave;
//			return this;
//		}
//		
//		public ItBajaNotExist getContractType(Byte contracType) {
//			this.contractType = contracType;
//			return this;
//		}
		
		@Override
		public void visit(Visitor visitor) {
			visitor.itNotExist(this);
			super.visit(visitor);
		}
		
		protected void super_visit( Visitor visitor) {
			super.visit(visitor);
		}
	}		

	public static class UnknownError extends EnterpriseITStatus{
		
		private String message;
		
		public String getMessage() {
			return message;
		}
		
		public UnknownError setMessage(String message) {
			this.message = message;
			return this;
		}
		

		@Override
		public void visit(Visitor visitor) {
			visitor.unknownError(message);
		}
	}	

	public abstract void visit(Visitor visitor);
	
	
	public static void ifSistemaREDEnabled(EnterpriseITStatus enterpriseStatus, Runnable enable,
			Runnable disabled) {
		enterpriseStatus.visit(new Visitor() {
	
			@Override
			public void up2DateEnterprise() {
				enable.run();
			}
				
			@Override
			public void updatedEnterprise() {
				enable.run();
			}
			
			@Override
			public void unknownError(String message) {
				disabled.run();
			}
			
			@Override
			public void credentialsNotFound() {
				disabled.run();
			}
			
			@Override
			public void itNotExist(ItNotExist itNotExist) {
				enable.run();
			}
		});
	}

	public static void ifSistemaREDError(EnterpriseITStatus enterpriseStatus, Runnable onError,
			Runnable onSuccess) {
		enterpriseStatus.visit(new Visitor() {
	
			@Override
			public void up2DateEnterprise() {
				onSuccess.run();
			}
	
			@Override
			public void updatedEnterprise() {
				onSuccess.run();
			}
			
			@Override
			public void unknownError(String message) {
				onError.run();
			}
			
			@Override
			public void credentialsNotFound() {
				onError.run();
			}
			
			@Override
			public void itNotExist(ItNotExist itNotExist) {
				onError.run();
			}
		});
	}

	public static <T extends EnterpriseITStatus> T trace( T status ) {
		status.visit(new Visitor() {
			
			@Override
			public void up2DateEnterprise() {
				System.out.println("up2Date Enterprise");
			}
			
			@Override
			public void updatedEnterprise() {
				System.out.println("updatedEnterprise");
			}
			
			@Override
			public void unknownError(String message) {
				System.out.println("unknownError");
			}			
			
			@Override
			public void credentialsNotFound() {
				System.out.println("CredentialsNotFound");
			}
			
			@Override
			public void itNotExist(ItNotExist itNotExist) {
				System.out.println("ItNotExist "+ itNotExist.name +" "+ itNotExist.naf + "[" + itNotExist.date + "]");
			}
		});
		return status;
	}

	public static <T extends EnterpriseITStatus> T isUp2Date( T status ) {
		status.visit(new Visitor() {
			
			@Override
			public void up2DateEnterprise() {}

			@Override
			public void updatedEnterprise() {}
			
			@Override
			public void unknownError(String message) {
				throw new OutOfDateException();
			}
			
			@Override
			public void credentialsNotFound() {
				throw new OutOfDateException();
			}

			@Override
			public void itNotExist(ItNotExist itNotExist) {
				throw new OutOfDateException();
			}
		});
		return status;
	}
	
	public static <T extends EnterpriseITStatus> T updated( T status ) {
		status.visit(new Visitor() {
			
			@Override
			public void up2DateEnterprise() {}
			
			@Override
			public void updatedEnterprise() {}
			
			@Override
			public void unknownError(String message) {
				throw new OutOfDateException();
			}
			
			@Override
			public void credentialsNotFound() {
				throw new OutOfDateException();
			}

			@Override
			public void itNotExist(ItNotExist itNotExist) {
				throw new OutOfDateException();
			}
		});
		return status;
	}
}
