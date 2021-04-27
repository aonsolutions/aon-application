import { newComponent } from "../../services/utils.js";

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

export const createContent = (text) => newComponent({
    text,
    type: "span",
    styles:{
      fontSize: "14px",
      fontFamily: "Times New Roman, Times, serif",
      wordWrap: "break-word"
    }
});

export const createTitle = (text) => newComponent({
  text,
  type: "span",
  classes:["aonColorPrimary"]
});

export const createDivFooter = () =>  newComponent({
  type: "div",
  styles:{
    display: "flex",
    marginTop: "10px",
    fontWeight: "800",
    fontSize: "10px"
  }
});

export const createDivFooter1 = (text) =>  newComponent({
  text,
  type: "div",
  styles:{
    marginLeft: "auto",
  }
});

export const createAonNotification = (id) =>  newComponent({
  id,
  type: "div",
  styles:{
    margin: "auto",
    marginTop: "21px",
    width: "80%",
  }
});

export const createUl = (id) =>  newComponent({
  id,
  type: "ul",
  styles:{
    listStyle: "none",
    padding: 0,
    margin: 0
  }
});




