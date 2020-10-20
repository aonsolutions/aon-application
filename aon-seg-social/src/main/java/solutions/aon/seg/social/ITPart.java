package solutions.aon.seg.social;

import java.util.Date;

import solutions.aon.seg.social.Employee.Visitor;

public class ITPart {

	private Date receptionDate;
	private String naf;
	private Date workLeaveDate;
	private Date workRestartDate;
	private Date partDate;
	private Integer partNum;
	private String partType;
	private Boolean canceled;
	private Boolean wrong;
	
	
	private ITPart() {}
	public void accept(Visitor visitor) {
		if(receptionDate != null) visitor.visitReceptionDate(receptionDate);
		if(naf != null) visitor.visitNaf(naf);
		if(workLeaveDate != null) visitor.visitWorkLeaveDate(workLeaveDate);
		if(workRestartDate != null) visitor.visitWorkRestartDate(workRestartDate);
		if(partDate != null) visitor.visitPartDate(partDate);
		if(partNum != null) visitor.visitPartNum(partNum);
		if(partType != null) visitor.visitPartType(partType);
		if(canceled != null) visitor.visitCanceled(canceled);
		if(wrong != null) visitor.visitWrong(wrong);
	}
	
	public static interface Visitor{
		void visitReceptionDate(Date receptionDate);
		void visitNaf(String naf);
		void visitWorkLeaveDate(Date workLeaveDate);
		void visitWorkRestartDate(Date workRestartDate);
		void visitPartDate(Date partDate);
		void visitPartNum(Integer partNum);
		void visitPartType(String partType);
		void visitCanceled(Boolean canceled);
		void visitWrong(Boolean wrong);
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('{');
		accept(new Visitor() {
			
			@Override
			public void visitWrong(Boolean wrong) {
				stringBuffer.append(String.format(" wrong : \"%s\" ", wrong));
			}
			
			@Override
			public void visitWorkRestartDate(Date workRestartDate) {
				stringBuffer.append(String.format(" workRestartDate : \"%s\" ", workRestartDate));
			}
			
			@Override
			public void visitWorkLeaveDate(Date workLeaveDate) {
				stringBuffer.append(String.format(" workLeaveDate : \"%s\" ", workLeaveDate));
			}
			
			@Override
			public void visitReceptionDate(Date receptionDate) {
				stringBuffer.append(String.format(" receptionDate : \"%s\" ", receptionDate));
			}
			
			@Override
			public void visitPartType(String partType) {
				stringBuffer.append(String.format(" partType : \"%s\" ", partType));
			}
			
			@Override
			public void visitPartNum(Integer partNum) {
				stringBuffer.append(String.format(" partNum : \"%s\" ", partNum));
			}
			
			@Override
			public void visitPartDate(Date partDate) {
				stringBuffer.append(String.format(" partDate : \"%s\" ", partDate));
			}
			
			@Override
			public void visitNaf(String naf) {
				stringBuffer.append(String.format(" naf : \"%s\" ", naf));
			}
			
			@Override
			public void visitCanceled(Boolean canceled) {
				stringBuffer.append(String.format(" canceled : \"%s\" ", canceled));
			}
		});
		stringBuffer.append('}');
		return stringBuffer.toString();
	}
	
	
	
	public static class ITPartBuilder {
		
		private Date receptionDate;
		private String naf;
		private Date workLeaveDate;
		private Date workRestartDate;
		private Date partDate;
		private Integer partNum;
		private String partType;
		private Boolean canceled;
		private Boolean wrong;
		
		private void EmployeeBuilder() {}
		
		public ITPartBuilder setReceptionDate(Date receptionDate) {
			this.receptionDate = receptionDate;
			return this;
		}
		
		public ITPartBuilder setNaf(String naf) {
			if(naf != null && !naf.trim().equals("")) this.naf = naf;
			else this.naf =null; 
			return this;
		}
		
		public ITPartBuilder setWorkLeaveDate(Date workLeaveDate) {
			this.workLeaveDate = workLeaveDate;
			return this;
		}
		
		public ITPartBuilder setWorkRestartDate(Date workRestartDate) {
			this.workRestartDate = workRestartDate;
			return this;
		}
		
		public ITPartBuilder setPartDate(Date partDate) {
			this.partDate = partDate;
			return this;
		}
		
		public ITPartBuilder setPartNum(Integer partNum) {
			this.partNum = partNum;
			return this;
		}
		
		public ITPartBuilder setPartType(String partType) {
			if(partType != null && !partType.trim().equals("")) this.partType = partType;
			else this.partType =null; 
			return this;
		}
		
		public ITPartBuilder setCanceled(Boolean canceled) {
			this.canceled = canceled;
			return this;
		}
		
		public ITPartBuilder setWrong(Boolean wrong) {
			this.wrong = wrong;
			return this;
		}
		
		public ITPart build() {
			ITPart it = new ITPart();
			
			it.receptionDate = this.receptionDate;
			it.naf = this.naf;
			it.workLeaveDate = this.workLeaveDate;
			it.workRestartDate = this.workRestartDate;
			it.partDate = this.partDate;
			it.partNum = this.partNum;
			it.partType = this.partType;
			it.canceled = this.canceled;
			it.wrong = this.wrong;
			
			return it;
		}
		
	}
}
