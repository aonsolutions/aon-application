package solutions.aon.seg.social.object;

import java.util.Date;

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
	private String ccc;
	private Integer numberHealth;
	private String typeProcess;
	private String entity;
	private String situation;

	private String nameEmployee;
	private String ipf;
	private String directionEmployee;
	private String occupation;

	private String nameEnterprise;
	private String directionEnterprise;
	
	private String collegiateNumber;
	private String  cias;
	private String  contingency;
	private String  causeRestart;
	private Date    dateConfirmation;
	private Integer durationDays;
	private Date    dateAcc;
	private Date    dateBjPrev;
	private Date    dateBjInit;
	private String  typeAcc;
	private String  typeAssist;
	private Date    dateNextMedical;
	private Boolean relapse;
	
	private Float  	baseCtz;
	private Integer daysCtz;
	private Float   hoursCtzExtr;
	private Float   sumBCtz;
	private Integer daysSumCtz;
	private Float   hoursCrzOther;
	private String  gpCtz;
	private String  catProf;
	private String  typeCto;
	private Integer  lack;

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
	public String getCcc() {return ccc;}
	public Date getWorkLeaveDate() {return workLeaveDate;}
	public String getPartType() {return partType;}
	public Integer getNumberHealth() {return numberHealth;}
	public String getTypeProcess() {return typeProcess;}
	public String getEntity() {return entity;}
	public String getSituation() {return situation;}

	public String getNameEmployee() {return nameEmployee;}
	public String getIpf() {return ipf;}
	public String getDirectionEmployee() {return directionEmployee;}
	public String getOccupation() {return occupation;}

	public String getNameEnterprise() {return nameEnterprise;}
	public String getDirectionEnterprise() {return directionEnterprise;}
	public Date getPartDate() {
		return partDate;
	}
	public Integer getPartNum() {
		return partNum;
	}
	public String getCollegiateNumber() {
		return collegiateNumber;
	}
	public String getCias() {
		return cias;
	}
	public String getContingency() {
		return contingency;
	}
	public Date getDateConfirmation() {
		return dateConfirmation;
	}
	public Integer getDurationDays() {
		return durationDays;
	}
	public Date getDateAcc() {
		return dateAcc;
	}
	public Date getDateBjPrev() {
		return dateBjPrev;
	}
	public Date getDateBjInit() {
		return dateBjInit;
	}
	public Date getDateNextMedical() {
		return dateNextMedical;
	}
	public String getCauseRestart() {
		return causeRestart;
	}
	public String getTypeAcc() {
		return typeAcc;
	}
	public String getTypeAssist() {
		return typeAssist;
	}
	public Date getWorkRestartDate() {
		return workRestartDate;
	}
	public Boolean getCanceled() {
		return canceled;
	}
	public Boolean getWrong() {
		return wrong;
	}
	public Boolean getRelapse() {
		return relapse;
	}
	public Float getBaseCtz() {
		return baseCtz;
	}
	public Integer getDaysCtz() {
		return daysCtz;
	}
	public Float getHoursCtzExtr() {
		return hoursCtzExtr;
	}
	public Float getSumBCtz() {
		return sumBCtz;
	}
	public Integer getDaysSumCtz() {
		return daysSumCtz;
	}
	public Float getHoursCrzOther() {
		return hoursCrzOther;
	}
	public String getGpCtz() {
		return gpCtz;
	}
	public String getCatProf() {
		return catProf;
	}
	public String getTypeCto() {
		return typeCto;
	}
	public Integer getLack() {
		return lack;
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
		private String ccc;
		private Integer numberHealth;
		private String typeProcess;
		private String entity;
		private String situation;

		private String nameEmployee;
		private String ipf;
		private String directionEmployee;
		private String occupation;

		private String nameEnterprise;
		private String directionEnterprise;

		private String collegiateNumber;
		private String  cias;
		private String  contingency;
		private String  causeRestart;
		private Date    dateConfirmation;
		private Boolean relapse;
		private Integer durationDays;
		private Date    dateAcc;
		private Date    dateBjPrev;
		private Date    dateBjInit;
		private String  typeAcc;
		private String  typeAssist;
		private Date    dateNextMedical;

		private Float  	baseCtz;
		private Integer daysCtz;
		private Float   hoursCtzExtr;
		private Float   sumBCtz;
		private Integer daysSumCtz;
		private Float   hoursCrzOther;
		private String  gpCtz;
		private String  catProf;
		private String  typeCto;
		private Integer  lack;

		public ITPartBuilder setBaseCtz(Float baseCtz) {
			this.baseCtz = baseCtz;
			return this;
		}

		public ITPartBuilder setDaysCtz(Integer daysCtz) {
			this.daysCtz = daysCtz;
			return this;
		}

		public ITPartBuilder setHoursCtzExtr(Float hoursCtzExtr) {
			this.hoursCtzExtr = hoursCtzExtr;
			return this;
		}

		public ITPartBuilder setSumBCtz(Float sumBCtz) {
			this.sumBCtz = sumBCtz;
			return this;
		}

		public ITPartBuilder setDaysSumCtz(Integer daysSumCtz) {
			this.daysSumCtz = daysSumCtz;
			return this;
		}

		public ITPartBuilder setHoursCrzOther(Float hoursCrzOther) {
			this.hoursCrzOther = hoursCrzOther;
			return this;
		}

		public ITPartBuilder setGpCtz(String gpCtz) {
			this.gpCtz = gpCtz;
			return this;
		}

		public ITPartBuilder setCatProf(String catProf) {
			this.catProf = catProf;
			return this;
		}

		public ITPartBuilder setTypeCto(String typeCto) {
			this.typeCto = typeCto;
			return this;
		}

		public ITPartBuilder setLack(Integer lack) {
			this.lack = lack;
			return this;
		}
		
		public ITPartBuilder setCias(String cias) {
			this.cias = cias;
			return this;
		}

		public ITPartBuilder setCauseRestart(String causeRestart) {
			this.causeRestart = causeRestart;
			return this;
		}
		
		public ITPartBuilder  setCollegiateNumber(String collegiateNumber) {
			this.collegiateNumber = collegiateNumber;
			return this;
		}

		public ITPartBuilder  setContingency(String contingency) {
			this.contingency = contingency;
			return this;
		}

		public ITPartBuilder  setDateConfirmation(Date dateConfirmation) {
			this.dateConfirmation = dateConfirmation;
			return this;
		}
		
		public ITPartBuilder  setRelapse(Boolean relapse) {
			this.relapse = relapse;
			return this;
		}
		
		public ITPartBuilder  setDurationDays(Integer durationDays) {
			this.durationDays = durationDays;
			return this;
		}

		public ITPartBuilder  setDateAcc(Date dateAcc) {
			this.dateAcc = dateAcc;
			return this;
		}

		public ITPartBuilder  setDateBjPrev(Date dateBjPrev) {
			this.dateBjPrev = dateBjPrev;
			return this;
		}

		public ITPartBuilder  setDateBjInit(Date dateBjInit) {
			this.dateBjInit = dateBjInit;
			return this;
		}

		public ITPartBuilder setTypeAcc(String typeAcc) {
			this.typeAcc = typeAcc;
			return this;
		}

		public ITPartBuilder setTypeAssist(String typeAssist) {
			this.typeAssist = typeAssist;
			return this;
		}

		public ITPartBuilder setDateNextMedical(Date dateNextMedical) {
			this.dateNextMedical = dateNextMedical;
			return this;
		}
		
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

		public ITPartBuilder setCcc(String d) {
			this.ccc = d;
			return this;
		}

		public ITPartBuilder setTypeProcess(String d) {
			this.typeProcess = d;
			return this;
		}

		public ITPartBuilder setEntity(String d) {
			this.entity = d;
			return this;
		}
		
		public ITPartBuilder setSituation(String d) {
			this.situation = d;
			return this;
		}
		
		public ITPartBuilder setNumberHealth(Integer d) {
			this.numberHealth = d;
			return this;
		}
		
		public ITPartBuilder setNameEmployee(String d) {
			this.nameEmployee = d;
			return this;
		}

		public ITPartBuilder setIpf(String d) {
			this.ipf = d;
			return this;
		}

		public ITPartBuilder setDirectionEmployee(String d) {
			this.directionEmployee = d;
			return this;
		}

		public ITPartBuilder setOccupation(String d) {
			this.occupation = d;
			return this;
		}

		public ITPartBuilder setDirectionEnterprise(String d) {
			this.directionEnterprise = d;
			return this;
		}

		public ITPartBuilder setNameEnterprise(String d) {
			this.nameEnterprise = d;
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
			it.ccc = this.ccc;
			it.numberHealth = this.numberHealth;
			it.typeProcess = this.typeProcess;
			it.entity = this.entity;
			it.situation = this.situation;
			it.nameEmployee = this.nameEmployee;
			it.ipf = this.ipf;
			it.directionEmployee = this.directionEmployee;
			it.occupation = this.occupation;
			it.directionEnterprise = this.directionEnterprise;
			it.nameEnterprise = this.nameEnterprise;
			
			it.collegiateNumber = this.collegiateNumber;
			it.cias = this.cias;
			it.contingency = this.contingency;
			it.causeRestart = this.causeRestart;
			it.dateConfirmation = this.dateConfirmation;
			it.relapse = this.relapse;
			it.durationDays = this.durationDays;
			it.dateAcc = this.dateAcc;
			it.dateBjPrev = this.dateBjPrev;
			it.dateBjInit = this.dateBjInit;
			it.typeAcc = this.typeAcc;
			it.typeAssist = this.typeAssist;
			it.dateNextMedical  = this.dateNextMedical;

			it.baseCtz = this.baseCtz;
			it.daysCtz = this.daysCtz;
			it.hoursCtzExtr = this.hoursCtzExtr;
			it.sumBCtz = this.sumBCtz;
			it.daysSumCtz = this.daysSumCtz;
			it.hoursCrzOther = this.hoursCrzOther;
			it.gpCtz = this.gpCtz;
			it.catProf = this.catProf;
			it.typeCto = this.typeCto;
			it.lack = this.lack;
			return it;
		}
		
	}

}

