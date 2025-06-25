package force.ssafy.domain.solvedProblem.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class SyncStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("🚀 Step 시작: {}", stepExecution.getStepName());
        stepExecution.getExecutionContext().put("step.start.time", System.currentTimeMillis());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long startTime = stepExecution.getExecutionContext().getLong("step.start.time", 0L);
        long durationMs = System.currentTimeMillis() - startTime;

        log.info("✅ Step 완료: {} - 읽기: {}건, 처리: {}건, 스킵: {}건, 소요시간 {}초",
                stepExecution.getStepName(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getSkipCount(),
                durationMs / 1000.0);
        // 성공률 계산 및 로깅
        if (stepExecution.getReadCount() > 0) {
            double successRate = (double) stepExecution.getWriteCount() / stepExecution.getReadCount() * 100;
            double skipRate = (double) stepExecution.getSkipCount() / stepExecution.getReadCount() * 100;
            log.info("📊 성공률: {:.1f}%, 스킵률: {:.1f}%", successRate, skipRate);

            // 스킵률이 20% 이상이면 경고
            if (skipRate >= 20.0) {
                log.warn("⚠️ 스킵률이 {}%로 높습니다!", skipRate);
                return new ExitStatus("COMPLETED_WITH_HIGH_SKIP_RATE",
                        String.format("스킵률: %.1f%%", skipRate));
            }
        }

        // 오류가 있었다면 로깅
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            log.error("❌ Step 실행 중 {}개의 오류 발생", stepExecution.getFailureExceptions().size());
            stepExecution.getFailureExceptions().forEach(ex ->
                    log.error("   - {}: {}", ex.getClass().getSimpleName(), ex.getMessage())
            );
        }
        return stepExecution.getExitStatus();
    }
}
