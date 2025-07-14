package force.ssafy.domain.solvedProblem.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolvedProblemSyncScheduler {
    private final JobLauncher jobLauncher;
    private final Job dailySolvedProblemSyncJob;

    /**
     * 매일 00시 01분에 자동 실행
     * cron = "초 분 시 일 월 요일"
     * 0 1 0 * * * = 매일 00시 01분 00초
     */
    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Seoul")
    public void runDailySyncJob(){
        LocalDateTime startTime = LocalDateTime.now();
        String formattedTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try{
            // Job 실행 시마다 고유한 파라미터 생성 (중복 실행 방지)
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("data", formattedTime)
                    .addString("trigger","SCHEDULED")
                    .addLong("timestamp",System.currentTimeMillis())
                    .toJobParameters();

            // Job 실행
            JobExecution execution = jobLauncher.run(dailySolvedProblemSyncJob, jobParameters);
            LocalDateTime endTime = LocalDateTime.now();
            long jobDuration = Duration.between(startTime, endTime).toMillis();
            log.info("✅ === 배치 작업 완료 ({}) ===", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log.info("📈 실행 결과 - 상태: {}, 소요시간: {}분", execution.getStatus(), jobDuration);
            // 실행 통계 로깅
            execution.getStepExecutions().forEach(stepExecution -> {
                log.info("📊 Step [{}] - 읽기: {}건, 처리: {}건, 스킵: {}건, 실패: {}건",
                        stepExecution.getStepName(),
                        stepExecution.getReadCount(),
                        stepExecution.getWriteCount(),
                        stepExecution.getSkipCount(),
                        stepExecution.getFailureExceptions().size());
            });
        }catch(Exception e){
            LocalDateTime errorTime = LocalDateTime.now();
            log.error("❌ === 배치 작업 실행 중 오류 발생 ({}) ===",
                    errorTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), e);

            // 배치 실패 시 알람 필요할 것 같음.
        }
    }
}