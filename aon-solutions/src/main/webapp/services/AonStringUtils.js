String.prototype.initCap = function (restLower) {
  return this.charAt(0).toUpperCase() + (restLower ? this.slice(1).toLowerCase() : this.slice(1));
};

String.prototype.isEmpty = function () {
  return this.length === 0 || !this.trim();
};

String.prototype.isNotEmpty = function () {
  return !this.isEmpty();
};

String.prototype.leftPad = function (char, length) {
  if (!char) char = " ";
  if (this.length < length) {
    let prefix = char.repeat( length - this.length );
    return (prefix + this);
  }
  return ""+this;
}

String.prototype.zeros = function (length) {
  return this.leftPad("0",length);
}
