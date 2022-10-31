#!/usr/bin/env just --justfile

# Start the app
run:
  ./mvnw spring-boot:run

# Create holiday request and start the process instance
holiday-apply:
  curl --location --request POST 'http://localhost:8080/holiday/apply' \
    --header 'Content-Type: application/json' \
    --data-raw '{"empName": "Vanja", "noOfHolidays": 1, "requestDescription": "Need to recharge"}'

# Get list of pending tasks for manager
manager-tasks:
  curl --location --request GET 'http://localhost:8080/manager/tasks'

# Reject or Approve specific task
approve-task task-id approve:
  curl --location --request POST 'http://localhost:8080/manager/approve/tasks/{{task-id}}/{{approve}}'

# Get all user tasks for specific user
user-tasks name:
  curl --location --request GET 'http://localhost:8080/user/{{name}}/tasks'

# User accept the specific task
user-accept task-id:
  curl --location --request POST 'http://localhost:8080/user/accept/{{task-id}}'

#
history process-id:
  curl --location --request GET 'http://localhost:8080/process/{{process-id}}'