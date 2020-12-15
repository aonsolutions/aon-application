package solutions.aon.seg.social.objects;

import java.util.Date;

import solutions.aon.seg.social.objects.Employee.Visitor;

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
	
	public Date getReceptionDate() {return receptionDate;}
	public String getNaf() {return naf;}
	public Date getWorkLeaveDate() {return workLeaveDate;}
	public String getPartType() {return partType;}
	
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
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((canceled == null) ? 0 : canceled.hashCode());
		result = prime * result + ((naf == null) ? 0 : naf.hashCode());
		result = prime * result + ((partDate == null) ? 0 : partDate.hashCode());
		result = prime * result + ((partNum == null) ? 0 : partNum.hashCode());
		result = prime * result + ((partType == null) ? 0 : partType.hashCode());
		result = prime * result + ((receptionDate == null) ? 0 : receptionDate.hashCode());
		result = prime * result + ((workLeaveDate == null) ? 0 : workLeaveDate.hashCode());
		result = prime * result + ((workRestartDate == null) ? 0 : workRestartDate.hashCode());
		result = prime * result + ((wrong == null) ? 0 : wrong.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ITPart other = (ITPart) obj;
		if (canceled == null) {
			if (other.canceled != null)
				return false;
		} else if (!canceled.equals(other.canceled))
			return false;
		if (naf == null) {
			if (other.naf != null)
				return false;
		} else if (!naf.equals(other.naf))
			return false;
		if (partDate == null) {
			if (other.partDate != null)
				return false;
		} else if (!partDate.equals(other.partDate))
			return false;
		if (partNum == null) {
			if (other.partNum != null)
				return false;
		} else if (!partNum.equals(other.partNum))
			return false;
		if (partType == null) {
			if (other.partType != null)
				return false;
		} else if (!partType.equals(other.partType))
			return false;
		if (receptionDate == null) {
			if (other.receptionDate != null)
				return false;
		} else if (!receptionDate.equals(other.receptionDate))
			return false;
		if (workLeaveDate == null) {
			if (other.workLeaveDate != null)
				return false;
		} else if (!workLeaveDate.equals(other.workLeaveDate))
			return false;
		if (workRestartDate == null) {
			if (other.workRestartDate != null)
				return false;
		} else if (!workRestartDate.equals(other.workRestartDate))
			return false;
		if (wrong == null) {
			if (other.wrong != null)
				return false;
		} else if (!wrong.equals(other.wrong))
			return false;
		return true;
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

