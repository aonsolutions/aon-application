let users;
let index;

export const getUsers = () => {
  return users;
}

export const setUsers = (data) => {
  users = data;
}

export const addInvoices = (data) => {
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

export const getIndex = () => {
  return index;
}

export const setIndex = (data) => {
  index = data;
}
