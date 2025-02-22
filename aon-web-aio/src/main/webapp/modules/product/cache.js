let list;
let index;
let filter;

export const getFilter = () => {
  return filter;
}

export const setFilter = (data) => {
  filter = data;
}

export const getList = () => {
  return list;
}

export const setList = (data) => {
  list = data;
}

export const addList = (data) => {
  if(list) {
    data.forEach((item, i) => {
      list.push(item);
    });
  } else list = data;
}

export const getObject = (data) => {
  return list[data];
}

export const getPreviousUser = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return users[index];
}

export const getNextUser = () => {
  if(index === users.length - 1) {
    return false;
  }
  index = index + 1;
  return users[index];
}

export const updateUser = (user) => {
  users[index] = user;
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}
