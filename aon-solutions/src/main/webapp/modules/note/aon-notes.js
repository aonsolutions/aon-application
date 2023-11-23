import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import {
  COLORS,
  CONSTANT,
  CSS,
  EVENT,
  MATERIAL_ICONS,
  MSG,
  TAG,
} from "../../environments/environments.js";

import Apps, { NOTES } from "../../services/app.js";
import { AON_NOTES } from "../../environments/aonTag.js";
import {
  getNotes,
  saveNote,
  deleteNote,
  getNoteCount,
} from "../../services/noteService.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { Note } from "../../models/note/Note.js";
import { AonTextArea } from "../../components/aon-textarea.js";
import { setStyles } from "../../services/utilsComponents.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { AonNewDialog } from "../../components/aon-new-dialog.js";

export class AonNotes extends AonElement {
  NOTES_OPT;
  NOTES;
  notesContainer;

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  build() {
    this.createApplication(AON_NOTES, "", new AonApplication());
    this.buildToolbar();
    this.buildSidenav();

    this.waitForElementToExist("aon-notesSidenavNotesMain").then(
      (noteSidenavOpt) => {
        noteSidenavOpt.click();

        // Hide sideNav
        if (this.isMobile() || window.innerWidth < 730) {
          this.waitForElementToExist(
            "aon-notesToolbarHeaderTitleSectionMenuIconButton"
          ).then((collapseMenuBtn) => {
            collapseMenuBtn.click();
          });
        }
      }
    );
  }

  buildToolbar() {
    this.getApplication().addToolbarOption2(
      {
        name: "Nueva Nota",
        icon: MATERIAL_ICONS.ADD,
        id: MATERIAL_ICONS.ADD,
      },
      () => {
        const titleInputId = this.addNote();
        this.waitForElementToExist(
          `${titleInputId}`
        ).then((titleInput) => {
          titleInput.focus();
        });
      }
    );
  }

  buildSidenav() {
    if (this.isMobile()) {
      this.getApplication().addMobileSidenavHeader(Apps.NOTES);
    }
    this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) =>
      this.selectOption(e.detail)
    );

    this.buildUtilitiesOptions();
  }

  async buildUtilitiesOptions() {
    let count = await getNoteCount();
    this.NOTES_OPT = {
      id: CONSTANT.NOTES.initCap() + "Main",
      name: MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")",
      icon: MATERIAL_ICONS.NOTES,
      app: NOTES,
    };

    let options = {
      id: CONSTANT.NOTES.initCap(),
      name: MSG.NOTES.toUpperCase(),
      app: NOTES,
      options: [this.NOTES_OPT],
    };

    this.getApplication().addSidenavOptions3(options);
  }

  selectOption(option) {
    switch (option.id) {
      case this.NOTES_OPT.id:
        this.aonNotes();
        break;
      default:
        this.aonNotes();
        break;
    }
  }

  async aonNotes() {
    this.notesContainer = this.createElement(TAG.DIV);
    this.notesContainer.id = "notesContainer";
    this.notesContainer.className = CSS.NOTE_CONTAINER;
    this.getApplication().setContent(this.notesContainer);

    await getNotes().then((notes) => {
      this.NOTES = notes;

      let emptyNotes = this.createElement(TAG.DIV);
      emptyNotes.id = "notesEmpty";
      emptyNotes.className = CSS.EMPTY_NOTE;
      emptyNotes.innerHTML = "No existen notas disponibles";
      this.notesContainer.appendChild(emptyNotes);
      emptyNotes.style.display = "none";


      if (!this.NOTES || this.NOTES.length === 0) {
        emptyNotes.style.display = "block";
      } else {
        this.NOTES.forEach((noteJson) => {
          let note = new Note(noteJson);
          this.addNote(note);
        });
      }
    });
  }

  addNote(note) {
    try{
      document.getElementById("notesEmpty").style.display = 'none';
    } catch(e){}

    if (!note) {
      note = new Note();
      note.setId(Math.floor((Math.random() - 1) * 100));
    }

    let noteCard = this.createElement(TAG.DIV);
    noteCard.id = note.getId() + "Card";
    noteCard.className = CSS.NOTE_CARD;

    let noteCardTitleDiv = this.createElement(TAG.DIV);
    noteCardTitleDiv.className = CSS.NOTE_TITLE_CARD;
    noteCard.appendChild(noteCardTitleDiv);

    let noteCardTitle = this.createElement(TAG.INPUT);
    noteCardTitle.id = note.getId() + "Title";
    noteCardTitle.type = "text";
    noteCardTitle.maxlength = 60;
    noteCardTitle.placeholder = MSG.TITLE;
    noteCardTitle.className = CSS.NOTE_TITLE;
    noteCardTitle.value = note.getSubject() ? note.getSubject() : "";
    noteCardTitleDiv.appendChild(noteCardTitle);

    this.createOptions(noteCardTitleDiv, note);

    let noteCardBodyDiv = this.createElement(TAG.DIV);
    noteCardBodyDiv.className = CSS.NOTE_BODY_CONTENT;
    noteCard.appendChild(noteCardBodyDiv);

    let textAreaBody = this.createElement("textarea");
    textAreaBody.placeholder = MSG.WRITE_A_NOTE;
    textAreaBody.rows = "4";
    textAreaBody.className = CSS.NOTE_BODY;
    textAreaBody.value = note.getNote() ? note.getNote() : "";
    noteCardBodyDiv.appendChild(textAreaBody);

    let noteCardDate = this.createElement(TAG.INPUT);
    noteCardDate.type = "date";
    noteCardDate.id = note.getId() + "Date";
    noteCardDate.className = CSS.NOTE_DATE;
    noteCardDate.value = note.getDate();

    if(note.getDate()){
      let date = new Date();
      let dateNote = new Date(note.getDate())
      if(dateNote.getTime() < date.getTime()) noteCardDate.style.color = "red";
      else noteCardDate.style.color = "#787885";
    }

    noteCardBodyDiv.appendChild(noteCardDate);

    // EVENTS
    noteCardTitle.addEventListener("change", async ({ target }) => {
      note.setSubject(target.value);
      await this.saveNote(note, aonDialogMenu, noteCard, noteCardTitleDiv, noteCardDate);
    });

    textAreaBody.addEventListener("change", async ({ target }) => {
      note.setNote(target.value);
      await this.saveNote(note, aonDialogMenu, noteCard, noteCardTitleDiv, noteCardDate);
    });

    noteCardDate.addEventListener("focusout", async ({ target }) => {
      note.setDate(target.value);
      
      if(note.getDate()){
        let date = new Date();
        let dateNote = new Date(note.getDate())
        if(dateNote.getTime() < date.getTime()) noteCardDate.style.color = "red";
        else noteCardDate.style.color = "#787885";
      }

      await this.saveNote(note, aonDialogMenu, noteCard, noteCardTitleDiv, noteCardDate);
    });

    // noteCardDate.addEventListener("change", async ({ target }) => {
    //   note.setDate(target.value);
      
    //   if(note.getDate()){
    //     let date = new Date();
    //     let dateNote = new Date(note.getDate())
    //     if(dateNote.getTime() < date.getTime()) noteCardDate.style.color = "red";
    //     else noteCardDate.style.color = "#787885";
    //   }

    //   await this.saveNote(note, aonDialogMenu, noteCard, noteCardTitleDiv, noteCardDate);
    // });

    let aonDialogMenu = new AonDialogMenu();
    aonDialogMenu.id = note.getId()+ "Options";
    noteCard.appendChild(aonDialogMenu);

    this.notesContainer.prepend(noteCard);

    return note.getId() + "Title";
  }

  async saveNote(note, aonDialogMenu, noteCard, noteCardTitleDiv, noteCardDate){
      const { id } = await saveNote(note);
      const count = await getNoteCount();
      document.getElementById("aon-notesToolbarHeaderTitleSectionOption").innerText = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
      document.getElementById("aon-notesSidenavNotesMain").title = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
      document.getElementById("aon-notesSidenavNotesMain").childNodes[1].innerText = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
      document.getElementById("aon-notesSidenavNotesMain").childNodes[1].title = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
      note.setId(id);
      aonDialogMenu.id = note.getId()+ "Options";
      noteCard.id = note.getId()+ "Card";
      noteCardDate.id = note.getId()+ "Date";
      this.createOptions(noteCardTitleDiv, note);
  }

  createOptions(noteCardTitleDiv, note) {
    if(noteCardTitleDiv.children.length > 1){
      noteCardTitleDiv.removeChild(noteCardTitleDiv.children[1]);
    }
    let noteCardOptions = this.addTitleButton(
      MSG.OPTIONS,
      MATERIAL_ICONS.MORE_VERT,
      () => this.openOptions(noteCardOptions, note)
    );
    noteCardOptions.id = note.getId() + "OptionsButton";
    noteCardTitleDiv.appendChild(noteCardOptions);
    
  }

  waitForElementToExist(selector) {
    return new Promise((resolve) => {
      if (document.getElementById(selector)) {
        return resolve(document.getElementById(selector));
      }

      const observer = new MutationObserver(() => {
        if (document.getElementById(selector)) {
          resolve(document.getElementById(selector));
          observer.disconnect();
        }
      });

      observer.observe(document.body, {
        subtree: true,
        childList: true,
      });
    });
  }

  addTitleButton(name, icon, fn) {
    let id = this.TITLE_SECTION2 + name + "Button";
    let aib = new AonIconButton();
    aib.id = id;
    aib.icon = icon;
    aib.title = name;
    aib.addEventListener(EVENT.CLICK, fn);
    aib.style.position = "relative";
    let span = this.createElement(TAG.SPAN);
    span.id = id + "Span";
    span.appendChild(aib);

    return span;
  }

  openOptions(optionsBtn, note) {
    let top = optionsBtn.getBoundingClientRect().top + 20;
    const left = optionsBtn.getBoundingClientRect().left;

    let d = document.getElementById(`${note.getId()}Options`);

    let moreActions = [
      {
        id: MATERIAL_ICONS.NOTIFICATION_ADD,
        icon: MATERIAL_ICONS.NOTIFICATION_ADD,
        name: MSG.REMINDER,
        permission: true,
        backgroundColor: Apps.NOTES.color,
        fn: () => {
          const date = document.getElementById(note.getId() + "Date");
          date.showPicker();
        },
      },
      {
        name: MSG.DELETE,
        icon: MATERIAL_ICONS.DELETE,
        id: MATERIAL_ICONS.DELETE,
        permission: true,
        backgroundColor: Apps.NOTES.color,
        fn: async () => {
          let aonDeleteDialog = new AonNewDialog("¿Desea eliminar este elemento?");
          aonDeleteDialog.id = note.getId();
          this.getApplication().appendChild(aonDeleteDialog);

          aonDeleteDialog.createAcceptButton(async () => {
            await deleteNote(note);
            this.getApplication().removeChild(aonDeleteDialog);
            const child = document.getElementById(note.getId() + "Card");
            this.notesContainer.removeChild(child);
            if(this.notesContainer.childNodes.length === 1){
              this.waitForElementToExist(
                "notesEmpty"
              ).then((emptyNote) => {
                emptyNote.style.display = "block";
              });
            }
            const count = await getNoteCount();
            document.getElementById("aon-notesToolbarHeaderTitleSectionOption").innerText = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
            document.getElementById("aon-notesSidenavNotesMain").title = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
            document.getElementById("aon-notesSidenavNotesMain").childNodes[1].innerText = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
            document.getElementById("aon-notesSidenavNotesMain").childNodes[1].title = MSG.NOTES + " (" + count.total_expired + "/" + count.total + ")";
          });

          aonDeleteDialog.createCancelButton(() => {this.getApplication().removeChild(aonDeleteDialog);});

          this.waitForElementToExist(
            `${note.getId()}AcceptButton`
          ).then((acceptButton) => {
            acceptButton.focus();
          });
        },
      },
    ];

    d.setMenuOptions(moreActions, top, left);
    d.open();
  }
}

if (!window.customElements.get(TAG.AON_NOTES)) {
  window.customElements.define(TAG.AON_NOTES, AonNotes);
}
