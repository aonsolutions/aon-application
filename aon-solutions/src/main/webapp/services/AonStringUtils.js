String.prototype.initCap = function () {
  return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
};

String.prototype.isEmpty = function () {
  return this.length === 0 || !this.trim();
};

//date isValid true or false
Date.prototype.isValid = function () {
  return this.getTime() === this.getTime();
};

//obj is empty
Object.prototype.isEmpty = function () {
  return Object.keys(this).length === 0;
};


//DETECT CHANGE IN OBJ, return (id, oldval, newval)
//ejm use obj.watch('c', handler )
if (!Object.prototype.watch)
  Object.prototype.watch = function (prop, handler) {
     let oldval = this[prop],
      newval = oldval,
      getter =  () =>  newval,
      setter =  (val) => {
        oldval = newval;
        return (newval = handler.call(this, prop, oldval, val));
      };
    if (delete this[prop]) {
      if (Object.defineProperty) {
        Object.defineProperty(this, prop, {
          get: getter,
          set: setter,
        });
      } else if ( Object.prototype.__defineGetter__ && Object.prototype.__defineSetter__) {
        Object.prototype.__defineGetter__.call(this, prop, getter);
        Object.prototype.__defineSetter__.call(this, prop, setter);
      }
    }
};
// object.unwatch
if (!Object.prototype.unwatch)
  Object.prototype.unwatch = function (prop) {
    var val = this[prop];
    delete this[prop];
    this[prop] = val;
};
