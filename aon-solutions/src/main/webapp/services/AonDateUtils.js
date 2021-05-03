Date.prototype.isValid = function () {
  return this.getTime() === this.getTime();
};

Date.prototype.getWeekNumber = function(){
  let d = new Date(Date.UTC(this.getFullYear(), this.getMonth(), this.getDate()));
  let dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  let yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
  return Math.ceil((((d - yearStart) / 86400000) + 1)/7)
};

Date.prototype.getFirstDayOfWeek = function() {
  return (new Date(this.setDate(this.getDate() - this.getDay()+ (this.getDay() == 0 ? -6:1) )));
}

Date.prototype.getLastDayOfWeek = function() {
  return (new Date(this.setDate(this.getDate() - this.getDay() +7)));
}

Date.prototype.addDay = function(day) {
  return (new Date(this.setDate( this.getDate() + day)));
}

Date.prototype.addMonth = function(month) {
  return (new Date(this.setMonth( this.getMonth() + month)));
}

Date.prototype.addYear = function(year) {
  return (new Date(this.setFullYear( this.getFullYear() + year)));
}
