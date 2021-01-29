String.prototype.initCap = function () {
  return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
};

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

Date.prototype.isValid = function () { 
              
  // If the date object is invalid it 
  // will return 'NaN' on getTime()  
  // and NaN is never equal to itself. 
  return this.getTime() === this.getTime(); 
}; 