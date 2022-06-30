import { Domain } from "../Domain.js";
import { Project } from "../project/Project.js";
import { TaskHolder } from "../project/TaskHolder.js";
import { Registry } from "../registry/Registry.js";

export class DailyTracking {
  id;
  domain;
  task_holder;
  tracking_date;
  tracking_duration;
  job_type;
  registry;
  project;
  activity_type;
  comments;
  task;
  cost;

  constructor(dailyTracking) {
    if (dailyTracking) {
      this.setDailyTracking(dailyTracking);
    } else {
      this.id = undefined;
      this.domain = new Domain();
      this.task_holder = new TaskHolder();
      this.tracking_date = undefined;
      this.tracking_duration = undefined;
      this.job_type = undefined;
      this.registry = new Registry();
      this.project = new Project();
      this.activity_type = undefined;
      this.comments = undefined;
      this.task = undefined;
      this.cost = undefined;
    }
  }

  setDailyTracking(dailyTracking) {
    if (dailyTracking) {
      this.setId(dailyTracking.id || undefined);
      this.setDomain(new Domain(dailyTracking.domain));
      this.setTaskHolder(new TaskHolder(dailyTracking.task_holder));
      this.setTrackingDate(dailyTracking.tracking_date || undefined);
      this.setTrackingDuration(dailyTracking.tracking_duration || undefined);
      this.setJobType(dailyTracking.job_type || undefined);
      this.setRegistry(new Registry(dailyTracking.registry));
      this.setProject(new Project(dailyTracking.project));
      this.setActivityType(dailyTracking.activity_type || undefined);
      this.setComments(dailyTracking.comments || undefined);
      this.setTask(dailyTracking.task || undefined);
      this.setCost(dailyTracking.cost || undefined);
    }
  }

  setId(id) {
    this.id = id;
    return this;
  }
  
  setDomain(domain) {
    this.domain = domain;
    return this;
  }

  setTaskHolder(taskHolder) {
    this.task_holder = taskHolder;
    return this;
  }

  setTrackingDate(trackingDate) {
    this.tracking_date = trackingDate;
    return this;
  }

  setTrackingDuration(trackingDuration) {
    this.tracking_duration = trackingDuration;
    return this;
  }

  setJobType(jobType) {
    this.job_type = jobType;
    return this;
  }

  setRegistry(registry) {
    this.registry = registry;
    return this;
  }

  setProject(project) {
    this.project = project;
    return this;
  }

  setActivityType(activityType) {
    this.activity_type = activityType;
    return this;
  }

  setComments(comments) {
    this.comments = comments;
    return this;
  }

  setTask(task) {
    this.task = task;
    return this;
  }

  setCost(cost) {
    this.cost = cost;
    return this;
  }

  getId() {
    return this.id;
  }

  getDomain() {
    return this.domain;
  }

  getTaskHolder() {
    return this.task_holder;
  }

  getTrackingDate() {
    return this.tracking_date;
  }

  getTrackingDuration() {
    return this.tracking_duration;
  }

  getJobType() {
    return this.job_type;
  }

  getRegistry() {
    return this.registry;
  }

  getProject() {
    return this.project;
  }

  getActivityType() {
    return this.activity_type;
  }

  getComments() {
    return this.comments;
  }

  getTask() {
    return this.task;
  }

  getCost() {
    return this.cost;
  }
}
