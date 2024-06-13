
let counter = {};

export const getCounter = () => {
  return counter;
}

export const setCounter = (data) => {
  counter = data;
}

export const addCounter = (option, count) => {
  let c = counter[option.id];
  counter[option.id] = c ? c + count : count;
}

export const transferCounter = (from, to, count) => {
  addCounter(from, -count);
  addCounter(to, count); 
}

export const clearCounter = () => {
  counter = {};
} 

