let tasks = [];
let index = 0;
export const getTasks = () => tasks;

export const addTasks = (data) => {
    if(tasks.length) 
      data.forEach((item) => tasks.push(item));
    else 
      tasks = data;
}

export const getTask = (data) => tasks[data];

export const getPreviousTask = () =>{
    if(index == 0)
      index = tasks.length - 1;
    else 
      index--;
    return tasks[index];
 }

export const getNextTask =() =>{
    if(index == tasks.length - 1)
      index = 0
    else 
      index++;
    return tasks[index];
}

export const setIndexTask = (data) => index = data;

