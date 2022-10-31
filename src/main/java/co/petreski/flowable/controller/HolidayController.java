package co.petreski.flowable.controller;

import co.petreski.flowable.dto.HolidayRequest;
import co.petreski.flowable.dto.ProcessInstanceResponse;
import co.petreski.flowable.dto.TaskDetails;
import co.petreski.flowable.service.HolidayService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HolidayController {
    HolidayService holidayService;

    @PostMapping("/deploy")
    public void deployWorkflow() {
        holidayService.deployProcessDefinition();
    }

    @PostMapping("/holiday/apply")
    public ProcessInstanceResponse applyHoliday(@RequestBody HolidayRequest holidayRequest) {
        return holidayService.applyHoliday(holidayRequest);
    }

    @GetMapping("/manager/tasks")
    public List<TaskDetails> getTasks() {
        return holidayService.getManagerTasks();
    }

    @PostMapping("/manager/approve/tasks/{taskId}/{approved}")
    public void approveTask(@PathVariable("taskId") String taskId, @PathVariable("approved") Boolean approved) {
        holidayService.approveHoliday(taskId, approved);
    }

    @PostMapping("/user/accept/{taskId}")
    public void acceptHoliday(@PathVariable("taskId") String taskId) {
        holidayService.acceptHoliday(taskId);
    }

    @GetMapping("/user/{name}/tasks")
    public List<TaskDetails> getUserTasks(@PathVariable("name") String name) {
        return holidayService.getUserTasks(name);
    }

    @GetMapping("/process/{processId}")
    public void checkState(@PathVariable("processId") String processId) {
        holidayService.checkProcessHistory(processId);
    }
}