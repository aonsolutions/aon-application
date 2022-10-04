let taskHolders;
let index;
let filter;

const getFilter = () =>  filter;
const setFilter = (data) =>  filter = data;

const getIndex = () =>  index;
const setIndex = (data) =>  index = data;

const getTaskHolders = () =>  taskHolders;
const setTaskHolders = (data) => taskHolders = data;
const getTaskHolder = (data) =>  taskHolders[data];

const addTaskHolder = (data) => {
  if(taskHolders) {
    data.forEach((item, i) => {
      taskHolders.push(item);
    });
  } else {
    taskHolders = data;
  }
}

const getPreviousTaskHolder = () => {
  if(index === 0) {
    return false;
  }
  index = index - 1;
  return taskHolders[index];
}

const getNextTaskHolder = () => {
  if(index === taskHolders.length - 1) {
    return false;
  }
  index = index + 1;
  return taskHolders[index];
}

const updateTaskHolder = (taskHolder) => {
  taskHolders[index] = taskHolder;
}

export const TaskHolderCache = {
  getIndex,
  setIndex,
  getFilter,
  setFilter,
  updateTaskHolder,
  getNextTaskHolder,
  getPreviousTaskHolder,
  getTaskHolders,
  getTaskHolder,
  addTaskHolder,
  setTaskHolders
}