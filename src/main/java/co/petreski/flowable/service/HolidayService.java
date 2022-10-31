package co.petreski.flowable.service;

import co.petreski.flowable.dto.HolidayRequest;
import co.petreski.flowable.dto.ProcessInstanceResponse;
import co.petreski.flowable.dto.TaskDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class HolidayService {
    private static final String TASK_CANDIDATE_GROUP = "managers";
    private static final String PROCESS_DEFINITION_KEY = "holidayRequest";
    RuntimeService runtimeService;
    TaskService taskService;
    ProcessEngine processEngine;
    RepositoryService repositoryService;

    public void deployProcessDefinition() {
        Deployment deployment = repositoryService
                .createDeployment()
                .addClasspathResource("processes/holiday-request.bpmn20.xml")
                .deploy();
    }

    public ProcessInstanceResponse applyHoliday(HolidayRequest holidayRequest) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", holidayRequest.getEmpName());
        variables.put("noOfHolidays", holidayRequest.getNoOfHolidays());
        variables.put("description", holidayRequest.getRequestDescription());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, variables);
        return new ProcessInstanceResponse(processInstance.getId(), processInstance.isEnded());
    }

    public List<TaskDetails> getManagerTasks() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(TASK_CANDIDATE_GROUP).list();
        return getTaskDetails(tasks);
    }

    private List<TaskDetails> getTaskDetails(List<Task> tasks) {
        List<TaskDetails> taskDetails = new ArrayList<>();
        for (Task task : tasks) {
            Map<String, Object> processVariables = taskService.getVariables(task.getId());
            taskDetails.add(new TaskDetails(task.getId(), task.getName(), processVariables));
        }
        return taskDetails;
    }

    public void approveHoliday(String taskId, Boolean approved) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approved);
        taskService.complete(taskId, variables);
    }

    public void acceptHoliday(String taskId) {
        taskService.complete(taskId);
    }

    public List<TaskDetails> getUserTasks(String name) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned(name).list();
        return getTaskDetails(tasks);
    }

    public void checkProcessHistory(String processId) {
        HistoryService historyService = processEngine.getHistoryService();

        List<HistoricActivityInstance> activities = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processId)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();

        for (HistoricActivityInstance activity : activities) {
            log.info(activity.getActivityId() + " took " + activity.getDurationInMillis() + " milliseconds.");
        }

        log.info("\n \n \n \n");
    }
}
