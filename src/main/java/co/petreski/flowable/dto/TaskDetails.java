package co.petreski.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class TaskDetails {
    String taskId;
    String taskName;
    Map<String, Object> taskData;
}
