import { AonDateUtils } from "../../modules/utils/AonDateUtils.js";
import { Domain } from "../Domain.js";

export class Note {
  id;
  domain;
  subject;
  date;
  owner;
  note;
  archive;
  pinUp;
  noteTag;
  color;
  archiveDate;
  creationDate;
  modificationDate;

  constructor(note) {
    if(note) {
      this.id               = note.id || undefined;
      this.domain           = note.domain || new Domain().getId();
      this.subject          = note.subject || "";
      this.note             = note.note || "";
      this.owner            = note.owner || undefined;
      this.date             = note.date && !note.date.includes("0001-01-01") ? AonDateUtils.formatDateOrigin(note.date) : null;
      this.archive          = note.archive || false;
      this.pinUp            = note.pinUp || false;
      this.noteTag          = note.noteTag || "";
      this.color            = note.color || "";
      this.archiveDate      = note.archiveDate && !note.archiveDate.includes("0001-01-01") ? AonDateUtils.formatDateOrigin(note.archiveDate) : null;
      this.creationDate     = note.creationDate && !note.creationDate.includes("0001-01-01") ? AonDateUtils.formatDateOrigin(note.creationDate) : null;
      this.modificationDate = note.modificationDate && !note.modificationDate.includes("0001-01-01") ? AonDateUtils.formatDateOrigin(note.modificationDate) : null;
    } else {
      this.id               = undefined;
      this.domain           = new Domain().getId(); 
      this.subject          = "";
      this.note             = undefined;
      this.owner            = undefined;
      this.date             = null;
      this.archive          = false;
      this.pinUp            = false;
      this.noteTag          = undefined;
      this.color            = undefined;
      this.archiveDate      = null;
      this.creationDate     = null;
      this.modificationDate = null;
    }
  }

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getSubject() {
    return this.subject;
  }

  setSubject(subject) {
    this.subject = subject;
  }

  getDate() {
    return this.date;
  }

  setDate(date) {
    this.date = date;
  }

  getOwner() {
    return this.owner;
  }

  setOwner(owner) {
    this.owner = owner;
  }

  getNote() {
    return this.note;
  }

  setNote(note) {
    this.note = note;
  }

  getDomain() {
    return this.domain;
  }

  setDomain(domain) {
    this.domain = domain;
  }

  getArchive() {
    return this.archive;
  }

  setArchive(archive) {
    this.archive = archive;
  }

  getPinUp() {
    return this.pinUp;
  }

  setPinUp(pinUp) {
    this.pinUp = pinUp;
  }

  getNoteTag() {
    return this.noteTag;
  }

  setNoteTag(noteTag) {
    this.noteTag = noteTag;
  }

  getColor() {
    return this.color;
  }

  setColor(color) {
    this.color = color;
  }

  getArchiveDate() {
    return this.archiveDate;
  }

  setArchiveDate(archiveDate) {
    this.archiveDate = archiveDate;
  }

  getModificationDate() {
    return this.modificationDate;
  }

  setModificationDate(modificationDate) {
    this.modificationDate = modificationDate;
  }

  getCreationDate() {
    return this.creationDate;
  }

  setCreationDate(creationDate) {
    this.creationDate = creationDate;
  }

}

