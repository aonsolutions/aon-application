
let packages;
let index;

export const initializePackages = () => {
  packages = [];
}

export const getPackages = () => {
  return packages;
}

export const setPackages = (data) => {
  packages = data;
}

export const addPackages = (data) => {
  if(packages) {
    data.forEach((item, i) => {
      packages.push(item);
    });
  } else packages = data;
}

export const getPackage = (data) => {
  return packages[data];
}

export const getPreviousPackage = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return packages[index];
}

export const getNextPackage = () => {
  if(index === packages.length - 1) {
    return false;
  }
  index = index + 1;
  return packages[index];
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}

export const removePackage = (data) => {
  let i = data || index;
  packages.splice(i, 1);  
}
