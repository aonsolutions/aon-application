
let elements;
let index;

export const getElements = () => {
  return elements;
}

export const setElements = (data) => {
  elements = data;
}

export const addElements = (data) => {
  if(elements) {
    data.forEach((item, i) => {
      elements.push(item);
    });
  } else elements = data;
}

export const getElement = (data) => {
  return elements[data];
}

export const getPreviousElement = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return elements[index];
}

export const getNextElement = () => {
  if(index === elements.length - 1) {
    return false;
  }
  index = index + 1;
  return elements[index];
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}
