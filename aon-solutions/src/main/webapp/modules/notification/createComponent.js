import { newComponent } from "../../services/utils.js";


const createDiv = (properties)=> newComponent({
  type: "div",
  ...properties
});

const createSpan = (properties)=> newComponent({
  type: "span",
  ...properties
});

export const createUl = (id) => newComponent({
  id,
  type: "ul",
  styles:{
    listStyle: "none",
    padding: 0,
    margin: 0
  }
});

export const createLi = (dataset) => newComponent({
    dataset,
    type: "li",
    styles:{
      width: "100%",
      position: "relative",
      transition: "background-color 1s",
      userSelect: "none"
    },
  });

export const createContent = (text) => createSpan({
  text,
  styles:{
    fontSize: "14px",
    fontFamily: "Times New Roman, Times, serif",
    wordWrap: "break-word"
  }
}); 

export const createTitle = (text) => createSpan({
  text,
  classes:["aonColorPrimary"]
}); 

export const createDivFooter = () => createDiv({
  styles:{
    display: "flex",
    marginTop: "10px",
    fontWeight: "800",
    fontSize: "10px"
  }
})

export const createDivFooter1 = (text) => createDiv({
  text,
  styles:{
    marginLeft: "auto",
  }
})

export const createAonNotification = (id) =>createDiv({
  id,
  styles:{
    margin: "auto",
    marginTop: "21px",
    width: "80%",
  }
})


