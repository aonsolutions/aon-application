String.prototype.initCap = function (restLower) {
  return this.charAt(0).toUpperCase() + (restLower ? this.slice(1).toLowerCase() : this.slice(1));
};

String.prototype.isEmpty = function () {
  return this.length === 0 || !this.trim();
};
