String.prototype.initCap = function () {
  return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
};

String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

//date isValid true or false
Date.prototype.isValid = function () {          
  return this.getTime() === this.getTime(); 
}; 