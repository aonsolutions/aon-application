let users;
let index;
let filter;

export const getFilter = () => {
  return filter;
}

export const setFilter = (data) => {
  filter = data;
}

export const getUsers = () => {
  return users;
}

export const setUsers = (data) => {
  users = data;
}

export const addUsers = (data) => {
  if(users) {
    data.forEach((item, i) => {
      users.push(item);
    });
  } else users = data;
}

export const getUser = (data) => {
  return users[data];
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

export const deleteUserCache = () => {
  users.splice(i, 1);
}

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}
