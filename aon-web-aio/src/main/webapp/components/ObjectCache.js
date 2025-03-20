
let objects;
let index;

export const initializeObjects = () => {
  objects = [];
}

export const getObjects = () => {
  return objects;
}

export const setObjects = (data) => {
  objects = data;
}

export const addObjects = (data) => {
  if(objects) {
    data.forEach((item, i) => {
      objects.push(item);
    });
  } else objects = data;
}

export const getObject = (data) => {
  return objects[data];
}

export const getPreviousObject = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return objects[index];
}

export const getNextObject = () => {
  if(index === objects.length - 1) {
    return false;
  }
  index = index + 1;
  return objects[index];
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}

export const removeObject = (data) => {
  let i = data || index;
  objects.splice(i, 1);  
}
