package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;

public abstract class EnterpriseITStatus implements Serializable {


	public static interface Visitor  {
		void up2DateEnterprise();
		void updatedEnterprise();
		void unknownError(String message);
		void credentialsNotFound();
		void itNotExist(ItNotExist itNotExist);
		void onFinish();
		void unknownErrorAnd(String message);
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
			if ( next != null ) 
				next.visit(visitor);
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
	
	public static class onFinish extends AndEmployeeITStatus{
		@Override	
		public void visit(Visitor visitor) {
			visitor.onFinish();
			super.visit(visitor);
		}
	}		
	
    public static class unknownErrorAnd extends AndEmployeeITStatus{
        private String message;
        
        public String getMessage() {
            return message;
        }
        
        public unknownErrorAnd setMessage(String message) {
            this.message = message;
            return this;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.unknownErrorAnd(message);
            super.visit(visitor);
        }
     }       
	    
	
	
	public static class ItNotExist extends AndEmployeeITStatus{
		
		EmployeeIT employeeIT;
		EmployeeITPart employeeITPart;

		public ItNotExist setEmployeeIT(EmployeeIT employeeIT) {
			this.employeeIT = employeeIT;
			return this;
		}
		
		public ItNotExist setEmployeeITPart(EmployeeITPart part) {
			this.employeeITPart = part;
			return this;
		}
		
		public Date getDate() {
			return getEmployeeITPart().getDate();
		}
		
		public Date getStartDate() {
			return getEmployeeIT().getStartDate();
		}
		
		public String getNaf() {
			return getEmployeeIT().getNss();
		}

		public String getCcc() {
			return getEmployeeIT().getCcc();
		}
		
		public String getName() {
			Optional<String> tmp = getEmployeeIT().getName();
			return tmp.isPresent() ? tmp.get() : "";
		}
		
		public ContractLeaveType getType() {
			return getEmployeeIT().getType();
		}
		
		public Byte getPart() {
			return getEmployeeITPart().getType().value();
		}
		
		public Optional<Integer> getIdPart() {
			return Optional.ofNullable(getEmployeeITPart().getId());
		}
		
		public EmployeeIT getEmployeeIT() {
			return employeeIT;
		}
		
		public EmployeeITPart getEmployeeITPart() {
			return employeeITPart;
		}
		
		public Optional<Byte> getConfirmOrder() {
			return getEmployeeITPart().getConfirmOrder();
		}
	
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
			
			@Override
			public void onFinish() {
				enable.run();
			}

            @Override
            public void unknownErrorAnd(String message) {
                disabled.run();
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

			@Override public void onFinish() {}

            @Override
            public void unknownErrorAnd(String message) {
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
				System.out.println("ItNotExist "+ itNotExist.getName() +" "+ itNotExist.getNaf() + "[" + itNotExist.getDate() + "]");
			}

			@Override public void onFinish() {}

            @Override
            public void unknownErrorAnd(String message) {
                System.out.println("unknownErrorAnd");
            }
		});
		return status;
	}

	public static <T extends EnterpriseITStatus> T isUp2Date( T status ) {
		status.visit(new Visitor() {
			
			@Override public void up2DateEnterprise() {}

			@Override public void updatedEnterprise() {}
			
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

			@Override public void onFinish() {}

            @Override
            public void unknownErrorAnd(String message) {
                throw new OutOfDateException();
            }
		});
		return status;
	}
	
	public static <T extends EnterpriseITStatus> T updated( T status ) {
		status.visit(new Visitor() {
			
			@Override public void up2DateEnterprise() {}
			
			@Override public void updatedEnterprise() {}
			
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
			@Override public void onFinish() {}

            @Override
            public void unknownErrorAnd(String message) {
                throw new OutOfDateException();
            }
		});
		return status;
	}
}
