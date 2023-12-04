import { AonElement } from "../../components/AonElement.js";
import { AonApplication } from "../../components/aon-application.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import Apps, { NOTES } from "../../services/app.js";
import { AON_NOTES } from "../../environments/aonTag.js";
import { getNotes, saveNote, deleteNote, getNoteCount, getNoteTagsCount, getNoteTags, saveNoteTag, deleteNoteTag } from "../../services/noteService.js";
import { AonIconButton } from "../../components/aon-icon-button.js";
import { Note } from "../../models/note/Note.js";
import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { AonNewDialog } from "../../components/aon-new-dialog.js";
import { TAG_TYPE } from "../messenger/MessengerEnums.js";
import { sortBy } from "../../services/utils.js";
import { AonNewInput } from "../../components/aon-new-input.js";

export class AonNotes extends AonElement {
  NOTES;
  NOTES_TAG;

  notesContainer;
  filter;

  constructor() {
    super();
    this.filter = {};
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  async build() {
    this.createApplication(AON_NOTES, "", new AonApplication());
    this.buildToolbar();
    
    await this.buildSidenav();
    await this.buildTagSidenav();

    this.waitForElementToExist("aon-notesSidenavNotesOpen").then(
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

  // ----------------------------- TOOLBAR

  buildToolbar() {
    this.getApplication().addToolbarOption2(
      {
        name: "Nueva Nota",
        icon: MATERIAL_ICONS.ADD,
        id: MATERIAL_ICONS.ADD,
      },
      () => {
        const titleInputId = this.addNote();
        this.waitForElementToExist(`${titleInputId}`).then((titleInput) => {
          titleInput.focus();
        });
      }
    );
  }

  // ----------------------------- SIDENAV

  async buildSidenav() {
    if (this.isMobile()) {
      this.getApplication().addMobileSidenavHeader(Apps.NOTES);
    }
    this.getApplication().addEventListener(EVENT.SELECT_OPTION, (e) =>
      this.selectOption(e.detail)
    );

    await this.buildUtilitiesOptions();
  }

  async selectOption(option) {
    switch (option.id) {
      case "NotesOpen":
        this.filter.active = true;
        this.filter.expired = false;
        this.filter.archived = false;
        await this.aonNotes();
        this.addColorSelectionSidenav();
        this.addColorTagSelectionSidenav();
        break;
      case "NotesExpired":
        this.filter.active = false;
        this.filter.expired = true;
        this.filter.archived = false;
        await this.aonNotes();
        this.addColorSelectionSidenav();
        this.addColorTagSelectionSidenav();
        break;
      case "NotesArchived":
        this.filter.active = false;
        this.filter.expired = false;
        this.filter.archived = true;
        await this.aonNotes();
        this.addColorSelectionSidenav();
        this.addColorTagSelectionSidenav();
        break;
      default:
        break;
    }
  }

  // ----------------------------- SIDENAV (NOTES)

  async buildUtilitiesOptions() {
    let count = await getNoteCount();
    let notesOpts = [];
    notesOpts.push(
      {
        id: CONSTANT.NOTES.initCap() + "Open",
        name: `Activas (${count.total - count.total_expired})`,
        icon: MATERIAL_ICONS.NOTES,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      },
      {
        id: CONSTANT.NOTES.initCap() + "Expired",
        name: `Expiradas (${count.total_expired})`,
        icon: MATERIAL_ICONS.SCHEDULE,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      },
      {
        id: CONSTANT.NOTES.initCap() + "Archived",
        name: `Archivadas (${count.archive})`,
        icon: MATERIAL_ICONS.ALL_INBOX,
        app: NOTES,
        fn: () => {
          // Filter selectOption method
        },
      }
    );

    let options = {
      id: CONSTANT.NOTES.initCap(),
      name: MSG.NOTES.toUpperCase(),
      app: NOTES,
    };

    this.getApplication().addSidenavOptions2(options, notesOpts);
  }

  // ----------------------------- SIDENAV (TAG)

  async buildTagSidenav() {
    this.getApplication().addSidenavOptions2(
      {
        id: TAG_TYPE.NOTE_LABEL,
        name: MSG.TAG,
        app: Apps.NOTES,
      },
      [],
      () => this.dialogTag({})
    );

    await this.loadTags();
  }

  async loadTags() {
    const type = TAG_TYPE.NOTE_LABEL;
    await this.getTags();

    this.clearElementById(this.getApplication().SIDENAV + type + "List");

    const tagCount = await getNoteTagsCount();
    
    this.NOTES_TAG.forEach((item) => {
      let tagId = item.id;
      let option = {
        id: item.id,
        name: `${item.name} (${tagCount[tagId] || '0'})`,
        icon: MATERIAL_ICONS.LABEL,
        actions: [
          {
            id: "Delete",
            icon: MATERIAL_ICONS.DELETE,
            action: () => {
              if(item.reference){
                this.deleteDialog(item);
              } else {
                this.deleteTag(item);
              }
            },
          },
          {
            id: "Edit",
            icon: MATERIAL_ICONS.EDIT,
            action: () => this.dialogTag(item),
          },
        ],
        fn: async () => {
          // Filter notes by tag
          if(this.filter.tag && this.filter.tag === item.id){
            this.filter.tag = undefined;
          } else {
            this.filter.tag = item.id;
          }
          await this.aonNotes();
          this.addColorSelectionSidenav();
          this.addColorTagSelectionSidenav();
        },
      };

      this.getApplication().addSidenavOptionsListValue(
        {
          id: type,
          name: MSG.TYPE.toUpperCase(),
          app: NOTES,
        },
        option
      );
    });
  }

  async getTags() {
    this.NOTES_TAG = await getNoteTags();
    this.NOTES_TAG = sortBy(this.NOTES_TAG, "name", "asc");
  }

  deleteTag(tag) {
    let aonDeleteTagDialog = new AonNewDialog("Eliminar etiqueta");
    aonDeleteTagDialog.id = tag.id;
    this.getApplication().appendChild(aonDeleteTagDialog);

    aonDeleteTagDialog.createMessage(
      "¿Realmente desea eliminar la etiqueta '" + tag.name + "'?"
    );

    aonDeleteTagDialog.createAcceptButton(async () => {
      await deleteNoteTag(tag);
      this.getApplication().removeChild(aonDeleteTagDialog);
      if(this.filter.tag && this.filter.tag === tag.id){
        this.filter.tag = undefined;
      }
      await this.loadTags();
      this.addColorTagSelectionSidenav();
      this.aonNotes();
      
    });

    aonDeleteTagDialog.createCancelButton(() => {
      this.getApplication().removeChild(aonDeleteTagDialog);
    });

    this.waitForElementToExist(`${tag.id}AcceptButton`).then((acceptButton) => {
      acceptButton.focus();
    });
  }

  deleteDialog(tag) {
    let aonDeleteTagDialog = new AonNewDialog("Etiqueta referenciada");
    aonDeleteTagDialog.id = tag.id;
    this.getApplication().appendChild(aonDeleteTagDialog);

    aonDeleteTagDialog.createMessage(
      `La etiqueta '${tag.name}' no puede ser eliminada, ya que existe al menos una nota con esta etiqueta asignada.`
    );

    aonDeleteTagDialog.createCancelButton(() => {
      this.getApplication().removeChild(aonDeleteTagDialog);
    });

    this.waitForElementToExist(`${tag.id}CancelButton`).then((cancelButton) => {
      cancelButton.focus();
    });
  }

  dialogTag(tag) {
    let aonCreateUpdateTagDialog = new AonNewDialog("Etiqueta");
    aonCreateUpdateTagDialog.id = "createUpdateTagDialog";
    this.getApplication().appendChild(aonCreateUpdateTagDialog);

    // Create content
    let content = this.createElement(TAG.DIV);
    content.style.display = "flex";
    content.style.flexDirection = "column";
    content.style.gap = ".5rem";

    // Create name input
    let nameInput = this.createAonElement(new AonNewInput(), 'nameInput', 'Nombre');
    nameInput.type = 'text';
    if (tag.name) nameInput.value = tag.name;
    content.appendChild(nameInput);

    // Create color input
    let colorDiv = this.createElement(TAG.DIV);
    colorDiv.classList.add('clr-field');
    colorDiv.style.height = "50px";
    if (tag.color) colorDiv.style.color = tag.color;
    else colorDiv.style.color = "#fff8b8";

    let colorBtn = this.createElement(TAG.BUTTON);
    colorBtn.style.width = "100%";
    colorBtn.style.height = "100%";
    colorBtn.style.borderRadius = "5px";
    colorDiv.appendChild(colorBtn);
    
    let colorInput = this.createElement(TAG.INPUT);
    colorInput.type = 'text';
    colorInput.classList.add('coloris');
    colorInput.classList.add('instance3');
    colorInput.style.width = "100%";
    colorInput.style.height = "100%";
    colorInput.style.cursor = "pointer";
    colorInput.style.borderRadius = "5px";
    colorDiv.appendChild(colorInput);

    Coloris({
      el: '.coloris',
      swatches: [
        '#faafa8',
        '#f39f76',
        '#fff8b8',
        '#e2f6d3',
        '#b4ddd3',
        '#d4e4ed',
        '#aeccdc',
        '#d3bfdb',
        '#f6e2dd',
        '#e9e3d4',
        '#efeff1'
      ]
    });

    Coloris.setInstance('.instance3', {
      theme: 'polaroid',
      swatchesOnly: true
    });
    content.appendChild(colorDiv);

    // Add dialog body
    aonCreateUpdateTagDialog.createBody(content);

    aonCreateUpdateTagDialog.createAcceptButton(async () => {
      tag.name = nameInput.value;
      tag.color = colorInput.value;
      tag.type = 14;
      await saveNoteTag(tag);
      this.getApplication().removeChild(aonCreateUpdateTagDialog);
      await this.loadTags();
      this.addColorTagSelectionSidenav();
    });

    aonCreateUpdateTagDialog.createCancelButton(() => {
      this.getApplication().removeChild(aonCreateUpdateTagDialog);
    });

    this.waitForElementToExist(`colorInputLabel`).then((colorInputLabel) => {
      colorInputLabel.firstElementChild.style.padding = ".5rem";
      colorInputLabel.firstElementChild.style.backgroundColor = "white";
    });
  }

  // ----------------------------- NOTES

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

      this.filterNotes();

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

  filterNotes() {
    let currentDate = new Date().getTime();
    if (this.filter.active) {
      this.NOTES = this.NOTES.filter(
        (note) => currentDate <= new Date(note.date).getTime()
      );
    } else if (this.filter.expired) {
      this.NOTES = this.NOTES.filter(
        (note) => currentDate > new Date(note.date).getTime()
      );
    } else if (this.filter.archived) {
      this.NOTES = this.NOTES.filter((note) => note.archive === true);
    }

    if(this.filter.tag){
      this.NOTES = this.NOTES.filter((note) => note.tag && note.tag.id === this.filter.tag);
    }
  }

  addNote(note) {
    try {
      document.getElementById("notesEmpty").style.display = "none";
    } catch (e) {}

    if (!note) {
      note = new Note();
      note.setId(Math.floor((Math.random() - 1) * 100));
    }

    let noteCard = this.createElement(TAG.DIV);
    noteCard.id = note.getId() + "Card";
    noteCard.className = CSS.NOTE_CARD;

    // Title
    let noteCardTitleDiv = this.createElement(TAG.DIV);
    noteCardTitleDiv.className = CSS.NOTE_TITLE_CARD;
    if(note.tag && note.tag.color){
      noteCardTitleDiv.style.backgroundColor = note.tag.color;
    } else {
      noteCardTitleDiv.style.backgroundColor = "#fff8b8";
    }
    noteCard.appendChild(noteCardTitleDiv);

    let noteCardTitle = this.createElement(TAG.INPUT);
    noteCardTitle.id = note.getId() + "Title";
    noteCardTitle.type = "text";
    noteCardTitle.maxlength = 60;
    noteCardTitle.placeholder = MSG.TITLE;
    noteCardTitle.className = CSS.NOTE_TITLE;
    noteCardTitle.value = note.getSubject() ? note.getSubject() : "";
    noteCardTitleDiv.appendChild(noteCardTitle);

     // Body
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
    if(new Date(note.getDate()).getTime() !== new Date("9999-01-01").getTime())
      noteCardDate.value = note.getDate();

    // Bottom
    let bottomNoteDiv = this.createElement(TAG.DIV);
    bottomNoteDiv.style.width = "100%";
    bottomNoteDiv.style.display = "flex";
    bottomNoteDiv.style.justifyContent = "space-between";
    noteCardBodyDiv.appendChild(bottomNoteDiv);

    let buttonsNoteDiv = this.createElement(TAG.DIV);
    buttonsNoteDiv.style.display = "flex";
    buttonsNoteDiv.style.visibility = "hidden";
    bottomNoteDiv.appendChild(buttonsNoteDiv);

    // Delete
    let deleteButton = this.createNoteButton(
      MATERIAL_ICONS.DELETE,
      "Eliminar nota",
      () => {
        let aonDeleteDialog = new AonNewDialog();
        aonDeleteDialog.id = note.getId();
        this.getApplication().appendChild(aonDeleteDialog);

        aonDeleteDialog.createMessage("¿Desea eliminar este elemento?");

        aonDeleteDialog.createAcceptButton(async () => {
          await deleteNote(note);
          this.getApplication().removeChild(aonDeleteDialog);
          await this.updateSideNavCount();
          await this.loadTags();
          this.addColorTagSelectionSidenav();
          this.aonNotes();
        });

        aonDeleteDialog.createCancelButton(() => {
          this.getApplication().removeChild(aonDeleteDialog);
        });

        this.waitForElementToExist(`${note.getId()}AcceptButton`).then(
          (acceptButton) => {
            acceptButton.focus();
          }
        );
      }
    )
    buttonsNoteDiv.appendChild(deleteButton);

    let alarmButton = this.createNoteButton(
      MATERIAL_ICONS.NOTIFICATION_ADD,
      "Fecha",
      () => {
        const date = document.getElementById(note.getId() + "Date");
        date.showPicker();
      }
    );
    buttonsNoteDiv.appendChild(alarmButton);

    let archiveButton = this.createNoteButton(
      note.getArchive() ? MATERIAL_ICONS.OUTBOX : MATERIAL_ICONS.MOVE_TO_INBOX,
      note.getArchive() ? "Desarchivar" : "Archivar",
      async () => {
        note.setArchive(!note.getArchive());
        await this.saveNote(note);
      }
    );
    buttonsNoteDiv.appendChild(archiveButton);

    let tagButton = this.createNoteButton(
      MATERIAL_ICONS.LABEL,
      "Etiqueta",
      () => {
        let aonTagDialog = new AonNewDialog("Asignar etiqueta");
        aonTagDialog.id = note.getId() + "Tag";
        this.getApplication().appendChild(aonTagDialog);

        // Create name input
        let tagInputDiv = this.createElement(TAG.DIV);
        tagInputDiv.style.height = "50px";
        tagInputDiv.style.border = "1px solid #d2d2d6";
        tagInputDiv.style.borderRadius = "3px";
        tagInputDiv.style.position = "relative";

        let selectInputDic = this.createElement("select");
        selectInputDic.style.width = "100%";
        selectInputDic.style.height = "100%";
        selectInputDic.style.background = "none";
        selectInputDic.style.border = "none";
        selectInputDic.style.paddingLeft = "1rem";
        tagInputDiv.appendChild(selectInputDic);

        let optionSelectDic = this.createElement("option");
        optionSelectDic.innerText = "Sin etiqueta";
        optionSelectDic.value = -1;
        selectInputDic.appendChild(optionSelectDic);

        this.NOTES_TAG.forEach(tag => {
          let optionSelectDic = this.createElement("option");
          optionSelectDic.innerText = tag.name;
          optionSelectDic.value = tag.id;
          selectInputDic.appendChild(optionSelectDic);
        });

        let tagSpantDic = this.createElement(TAG.SPAN);
        tagSpantDic.innerText = "Etiqueta";
        tagSpantDic.style.top = "-8px";
        tagSpantDic.style.padding = "0 5px";
        tagSpantDic.style.background = "white";
        tagSpantDic.style.position = "absolute";
        tagSpantDic.style.left = "10px";
        tagSpantDic.style.fontSize = "12px";
        tagInputDiv.appendChild(tagSpantDic);
        
        aonTagDialog.createBody(tagInputDiv);

        aonTagDialog.createAcceptButton(async () => {
          this.getApplication().removeChild(aonTagDialog);
          if(selectInputDic.value === "-1") note.tag = {};
          else note.tag.id = selectInputDic.value;
          await this.saveNote(note);
          await this.loadTags();
          this.addColorTagSelectionSidenav();
        });

        aonTagDialog.createCancelButton(() => {
          this.getApplication().removeChild(aonTagDialog);
        });

        this.waitForElementToExist(`${note.getId()}TagAcceptButton`).then(
          (acceptButton) => {
            acceptButton.focus();
          }
        );
      }
    );
    buttonsNoteDiv.appendChild(tagButton);

    // Check if its expired
    if (note.getDate()) {
      let date = new Date();
      let dateNote = new Date(note.getDate());
      if (dateNote.getTime() < date.getTime()) noteCardDate.style.color = "#e1444c";
      else noteCardDate.style.color = "#787885";
    }
    bottomNoteDiv.appendChild(noteCardDate);

    // EVENTS
    noteCard.addEventListener(
      "mouseover",
      (event) => {
        if(note.tag && note.tag.color){
          noteCardBodyDiv.style.backgroundColor = note.tag.color;
          textAreaBody.style.backgroundColor = note.tag.color;
          noteCardDate.style.backgroundColor = note.tag.color;
        }
        else {
          noteCardBodyDiv.style.backgroundColor = "#fff8b8";
          textAreaBody.style.backgroundColor = "#fff8b8";
          noteCardDate.style.backgroundColor = "#fff8b8";
        }

        noteCardBodyDiv.style.color = "#202124";
        textAreaBody.style.color = "#202124";

        buttonsNoteDiv.style.visibility = "visible";
      },
      false,
    );

    noteCard.addEventListener(
      "mouseout",
      (event) => {
        noteCardBodyDiv.style.backgroundColor = "white";
        textAreaBody.style.backgroundColor = "white";
        noteCardDate.style.backgroundColor = "white";
        buttonsNoteDiv.style.visibility = "hidden";
      },
      false,
    );

    noteCardTitle.addEventListener("change", async ({ target }) => {
      note.setSubject(target.value);
      await this.saveNote(note);
    });

    textAreaBody.addEventListener("change", async ({ target }) => {
      note.setNote(target.value);
      await this.saveNote(note);
    });

    noteCardDate.addEventListener("focusout", async ({ target }) => {
      note.setDate(target.value);

      if (note.getDate()) {
        let date = new Date();
        let dateNote = new Date(note.getDate());
        if (dateNote.getTime() < date.getTime())
          noteCardDate.style.color = "#e1444c";
        else noteCardDate.style.color = "#787885";
      }

      await this.saveNote(note);
    });

    let aonDialogMenu = new AonDialogMenu();
    aonDialogMenu.id = note.getId() + "Options";
    noteCard.appendChild(aonDialogMenu);

    this.notesContainer.prepend(noteCard);

    return note.getId() + "Title";
  }

  createNoteButton(icon, title, fn){
    let button = new AonIconButton();
    button.noHover = true;
    button.icon = icon;
    button.title = title;
    
    let innerButton = button.getButton();
    innerButton.style.height = "30px";
    innerButton.style.minWidth = "30px";
    innerButton.style.width = "30px";

    let innerIcon = button.getIcon();
    innerIcon.style.fontSize = "1.3rem";
    
    button.addEventListener(EVENT.CLICK, (ev)=>{
      ev.stopPropagation();
      fn();
    });

    return button;
  }

  async saveNote(note) {
    const { id } = await saveNote(note);
    await this.updateSideNavCount();
    this.aonNotes();
  }

  // ----------------------------- SIDENAV (COUNTS)

  async updateSideNavCount(){
    const count = await getNoteCount();
    const countTag = await getNoteTagsCount();
   
    document.getElementById("aon-notesSidenavNotesOpen").childNodes[1].innerText =
      `Activas (${count.total - count.total_expired})`;

    document.getElementById("aon-notesSidenavNotesExpired").childNodes[1].innerText =
      `Expiradas (${count.total_expired})`;
    
    document.getElementById("aon-notesSidenavNotesArchived").childNodes[1].innerText =
      `Archivadas (${count.archive})`;

    if(countTag){
      Object.keys(countTag).forEach(key => {
        document.getElementById("aon-notesSidenav" + key).childNodes[1].innerText =
          this.updateTagLabel(document.getElementById("aon-notesSidenav" + key).childNodes[1].innerText, countTag[key]);
      });
    }
  }

  updateTagLabel(originalString, newText){
    // Use a regular expression to find and replace text inside parentheses
    var regex = /\((.*?)\)/g;
    var replacedString = originalString.replace(regex, '(' + newText + ')');
    return replacedString;
  }

  // ----------------------------- SIDENAV (COLORS)

  addColorSelectionSidenav(){
    if(this.filter.active){
      this.waitForElementToExist(`aon-notesSidenavNotesOpen`).then((typeLi) => {
        typeLi.style.fontWeight = "bold";
        typeLi.style.color = "white";
        typeLi.style.backgroundColor = "rgb(255, 192, 0)";
      });
      this.waitForElementToExist(`aon-notesSidenavNotesOpenicon`).then((typeIcon) => {
        typeIcon.style.color = "white";
      });
    } else if(this.filter.expired){
      this.waitForElementToExist(`aon-notesSidenavNotesExpired`).then((typeLi) => {
        typeLi.style.fontWeight = "bold";
        typeLi.style.color = "white";
        typeLi.style.backgroundColor = "rgb(255, 192, 0)";
      });
      this.waitForElementToExist(`aon-notesSidenavNotesExpiredicon`).then((typeIcon) => {
        typeIcon.style.color = "white";
      });
    } else if(this.filter.archived){
      this.waitForElementToExist(`aon-notesSidenavNotesArchived`).then((typeLi) => {
        typeLi.style.fontWeight = "bold";
        typeLi.style.color = "white";
        typeLi.style.backgroundColor = "rgb(255, 192, 0)";
      });
      this.waitForElementToExist(`aon-notesSidenavNotesArchivedicon`).then((typeIcon) => {
        typeIcon.style.color = "white";
      });
    }
  }

  addColorTagSelectionSidenav(){
    this.NOTES_TAG.forEach(tag => {
      if(this.filter.tag && this.filter.tag === tag.id){
        this.getApplication().addBackgroundSidenav(tag.id, this.NOTES_TAG.filter(note => note.id === tag.id).map(note => note.color));
        this.waitForElementToExist(`aon-notesSidenav${tag.id}icon`).then((tagIcon) => {
          tagIcon.style.color = "white";
        });

        this.waitForElementToExist(tag.id).then((tagLi) => {
          tagLi.style.fontWeight = "bold";
          tagLi.style.color = "white";
        });
      } else {
        this.getApplication().removeBackgroundSidenav(tag.id,this.NOTES_TAG.filter(note => note.id === tag.id).map(note => note.color));
        this.waitForElementToExist(`aon-notesSidenav${tag.id}icon`).then((tagIcon) => {
          tagIcon.style.color = this.NOTES_TAG.filter(note => note.id === tag.id).map(note => note.color);
        });
      }
    });
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
}

if (!window.customElements.get(TAG.AON_NOTES)) {
  window.customElements.define(TAG.AON_NOTES, AonNotes);
}
