package com.esferalia.aon.payroll.enumeration;

public enum DismissalType {
	
	
	 
	 UNFAIR {
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitUnfair(this);
		}
	 },
	 OBJECTIVE{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitObjective(this);
		}
	 },
	 WORK_END{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitWorkEnd(this);
		}
	 },
	 TEMP_END{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitTempEnd(this);
		}
	 },
	 RETIREMENT{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitRetirement(this);
		}
	 },
	 DEFINITE_END{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitDefiniteEnd(this);
		}
	 },
	 CONDITIONS_CHANGE{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitVoluntaryEnd(this);
		}
	 },
	 NOT_PASS_TRIAL_PERIOD{
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitNotPassTrialPeriod(this);
		}
	 },
	 DEATH_OF_EMPLOYEE  {
		 @Override
		 public<T> T accept(Visitor<T> visitor) {
			return visitor.visitDeathOfEmployee(this);
		}
	 },	
	;
	 
	 
	public static interface Visitor<T> {
		T visitUnfair(DismissalType type);
		T visitObjective(DismissalType type);
		T visitWorkEnd(DismissalType type);
		T visitTempEnd(DismissalType type);
		T visitRetirement(DismissalType type);
		T visitDefiniteEnd(DismissalType type);
		T visitVoluntaryEnd(DismissalType type);
		T visitNotPassTrialPeriod(DismissalType type);
		T visitDeathOfEmployee(DismissalType type);
	}
	
	public abstract <T> T accept(Visitor<T> visitor );
	
	public static DismissalType getByOrdinal(int ordinal){
		if ( ordinal < 0 )
			return null;
		DismissalType values [] = DismissalType.values();
		if ( ordinal >= values.length)
			return null;
		return values[ordinal];
	}
	
	
}
