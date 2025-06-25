package force.ssafy.domain.solvedProblem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch/solved-problems")
@RequiredArgsConstructor
public class SolvedProblemBatchController {

    private final JobLauncher jobLauncher;
    private final Job dailySolvedProblemSyncJob;
    private final JobExplorer jobExplorer;

    /**
     * 배치 수동 실행 API
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> runBatchManually() {
        LocalDateTime startTime = LocalDateTime.now();
        String formattedTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            log.info("🔧 수동 배치 실행 요청 - 시작시간: {}", formattedTime);

            // 실행중인 Job이 있는지 확인
            Set<JobExecution> runningExecutions = jobExplorer.findRunningJobExecutions("dailySolvedProblemSyncJob");
            if (!runningExecutions.isEmpty()) {
                log.warn("⚠️ 이미 실행중인 배치가 있습니다.");
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "이미 실행중인 배치가 있습니다.",
                        "runningExecutions", runningExecutions.size()
                ));
            }

            // Job 파라미터 생성
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startTime", formattedTime)
                    .addString("trigger", "MANUAL") // 수동 실행 표시
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            JobExecution jobExecution = jobLauncher.run(dailySolvedProblemSyncJob, jobParameters);

            LocalDateTime endTime = LocalDateTime.now();
            long durationSeconds = java.time.Duration.between(startTime, endTime).toSeconds();

            log.info("✅ 수동 배치 실행 완료 - 소요시간: {}초", durationSeconds);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "배치가 성공적으로 실행되었습니다.");
            response.put("jobExecutionId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", formattedTime);
            response.put("endTime", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            response.put("durationSeconds", durationSeconds);

            // Step 실행 결과 추가
            Map<String, Object> stepResults = new HashMap<>();
            jobExecution.getStepExecutions().forEach(stepExecution -> {
                Map<String, Object> stepInfo = new HashMap<>();
                stepInfo.put("readCount", stepExecution.getReadCount());
                stepInfo.put("writeCount", stepExecution.getWriteCount());
                stepInfo.put("skipCount", stepExecution.getSkipCount());
                stepInfo.put("status", stepExecution.getStatus().toString());
                stepResults.put(stepExecution.getStepName(), stepInfo);
            });
            response.put("stepResults", stepResults);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 수동 배치 실행 실패", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "배치 실행 중 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("startTime", formattedTime);
            errorResponse.put("error", e.getClass().getSimpleName());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 배치 실행 상태 조회 API
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBatchStatus() {
        try {
            Set<JobExecution> runningExecutions = jobExplorer.findRunningJobExecutions("dailySolvedProblemSyncJob");

            Map<String, Object> response = new HashMap<>();
            response.put("isRunning", !runningExecutions.isEmpty());
            response.put("runningCount", runningExecutions.size());

            if (!runningExecutions.isEmpty()) {
                JobExecution latestExecution = runningExecutions.iterator().next();
                response.put("currentJobId", latestExecution.getId());
                response.put("currentStatus", latestExecution.getStatus().toString());
                response.put("startTime", latestExecution.getStartTime());

                // 현재 실행중인 Step 정보
                latestExecution.getStepExecutions().forEach(stepExecution -> {
                    if (stepExecution.getStatus().isRunning()) {
                        Map<String, Object> currentStep = new HashMap<>();
                        currentStep.put("stepName", stepExecution.getStepName());
                        currentStep.put("readCount", stepExecution.getReadCount());
                        currentStep.put("writeCount", stepExecution.getWriteCount());
                        response.put("currentStep", currentStep);
                    }
                });
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("배치 상태 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "상태 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    /**
     * 최근 배치 실행 이력 조회 API
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getBatchHistory(@RequestParam(defaultValue = "10") int limit) {
        try {
            // 최근 실행 이력 조회 (limit 개수만큼)
            var jobInstances = jobExplorer.findJobInstancesByJobName("dailySolvedProblemSyncJob", 0, limit);

            var history = jobInstances.stream().map(jobInstance -> {
                var executions = jobExplorer.getJobExecutions(jobInstance);
                var latestExecution = executions.stream()
                        .max((e1, e2) -> e1.getCreateTime().compareTo(e2.getCreateTime()))
                        .orElse(null);

                if (latestExecution != null) {
                    Map<String, Object> executionInfo = new HashMap<>();
                    executionInfo.put("jobExecutionId", latestExecution.getId());
                    executionInfo.put("status", latestExecution.getStatus().toString());
                    executionInfo.put("startTime", latestExecution.getStartTime());
                    executionInfo.put("endTime", latestExecution.getEndTime());
                    executionInfo.put("exitCode", latestExecution.getExitStatus().getExitCode());

                    // 파라미터에서 트리거 타입 확인
                    String trigger = latestExecution.getJobParameters().getString("trigger");
                    executionInfo.put("trigger", trigger != null ? trigger : "SCHEDULED");

                    // Step 실행 통계 추가
                    Map<String, Object> stepStats = new HashMap<>();
                    latestExecution.getStepExecutions().forEach(stepExecution -> {
                        Map<String, Object> stepInfo = new HashMap<>();
                        stepInfo.put("readCount", stepExecution.getReadCount());
                        stepInfo.put("writeCount", stepExecution.getWriteCount());
                        stepInfo.put("skipCount", stepExecution.getSkipCount());
                        stepStats.put(stepExecution.getStepName(), stepInfo);
                    });
                    executionInfo.put("stepStats", stepStats);

                    return executionInfo;
                }
                return null;
            }).filter(info -> info != null).toList();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "history", history,
                    "totalCount", history.size()
            ));

        } catch (Exception e) {
            log.error("배치 이력 조회 실패", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "이력 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}