let tasks = [];
let index = 0;
export const getTasks = () => tasks;

export const addTasks = (data) => {
    if(tasks.length) {
        data.forEach((item, i) => tasks.push(item));
    } else 
    tasks = data;
}

export const getTask = (data) => tasks[data];

// export const getPreviousTask = () => index === 0 ? false : tasks[index - 1];

// export const getNextTask = () => index === tasks.length - 1 ?  false :  tasks[index + 1];


export const getPreviousTask = () =>{
    if(index == 0)
      index = tasks.length - 1;
    else 
      index--;
    console.log(index);
    return tasks[index];
 }

export const getNextTask =() =>{
    if(index == tasks.length - 1)
      index = 0
    else 
      index++;
    console.log(index);
    return tasks[index];
}

export const setIndexTask = (data) => index = data;

