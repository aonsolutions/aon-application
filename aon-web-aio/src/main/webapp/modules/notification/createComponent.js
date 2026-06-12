import { CSS, TAG } from "../../environments/environments.js";
import { createDiv, createSpan, newComponent } from "../../services/utilsComponents.js";

const createUl = (id) => newComponent({
  id,
  type: TAG.UL,
  classes: [CSS.AON_UL],
  styles:{
    listStyle: "none",
    padding: 0,
    paddingTop: "5px",
    margin: 0
  }
});

const createLi = (dataset) => newComponent({
    dataset,
    type: TAG.LI,
    styles:{
      width: "100%",
      position: "relative",
      transition: "background-color 1s",
      userSelect: "none"
    },
});

const createContent = (text) => createSpan({
    text,
    styles:{
      fontSize: "14px",
      wordWrap: "break-word"
    }
}); 

const createTitle = (text) => createSpan({
  text,
  classes:[CSS.AON_COLOR_PRIMARY]
}); 

const createDivFooter = () => createDiv({
  styles:{
    display: "flex",
    marginTop: "10px",
    fontWeight: "800",
    fontSize: "10px"
  }
})

const createDivFooter1 = (text) => createDiv({
  text,
  styles:{
    marginLeft: "auto",
  }
})

const createAonNotification = (id) =>createDiv({
  id,
  styles:{
    margin: "auto",
    width: "80%",
  }
});

const createButtonClose = () => newComponent({
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

const createSpanFloat = () => createSpan({
  id: "aonNotificationFloatSpan",
  styles:{
    position: "fixed",
    right: "6%",
    bottom: "80px"
  }
});

const createBadge = (id) => createSpan({
  id,
  styles:{
    position: "absolute", 
    right: "8px",
    top: "5px",
    padding: "4px",
    borderRadius: "50%",
    background: "rgb(220, 77, 48)",
    color: "white",
    fontSize: "10px",
    fontWeight: 800,
  }
});


export const NotificationCreateComponent = {
  createUl,
  createLi,
  createContent,
  createTitle,
  createDivFooter,
  createDivFooter1,
  createAonNotification,
  createButtonClose,
  createSpanFloat,
  createBadge
}