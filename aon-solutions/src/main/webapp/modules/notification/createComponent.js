import { TAG } from "../../environments/environments.js";
import { newComponent } from "../../services/utils.js";


const createDiv = (properties)=> newComponent({
  type: TAG.DIV,
  ...properties
});

const createSpan = (properties)=> newComponent({
  type: TAG.SPAN,
  ...properties
});

export const createUl = (id) => newComponent({
  id,
  type: TAG.UL,
  styles:{
    listStyle: "none",
    padding: 0,
    margin: 0
  }
});

export const createLi = (dataset) => newComponent({
    dataset,
    type: TAG.LI,
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
});

export const createButtonClose = () => newComponent({
  type: TAG.LABEL,
  text: "×",
  styles:{
    float: "right",
    marginTop: "-23px",
    marginRight: "-19px",
    cursor: "pointer",
    padding: "10px",
  },
});


export const createSpanFloat = () => newComponent({
  type: TAG.SPAN,
  id: "aonNotificationFloatSpan",
  styles:{
    position: "fixed",
    right: "20px",
    bottom: "70px"
  }
});

export const createForm  = (id="form") => newComponent({
  type:TAG.FORM,
  id,
  action: "#"
});