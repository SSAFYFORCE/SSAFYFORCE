package force.ssafy.domain.solvedProblem.batch;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.solvedProblem.controller.dto.response.SyncResultResponse;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;
import force.ssafy.domain.solvedProblem.service.SolvedProblemSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SolvedProblemSyncBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final SolvedProblemRepository solvedProblemRepository;
    private final MemberRepository memberRepository;
    private final SolvedProblemSyncService solvedProblemSyncService;


    /**
     * 일일 문제 동기화 JOB
     */
    @Bean
    public Job dailySolvedProblemSyncJob() {
        log.info("dailySolvedProblemSyncJob 생성");
        return new JobBuilder("dailySolvedProblemSyncJob", jobRepository)
                .start(solvedProblemSyncStep())
                .build();
    }

    /**
     * 문제 동기화 STEP
     */
    @Bean
    public Step solvedProblemSyncStep() {
        log.info("solvedProblemSyncStep 생성");
        return new StepBuilder("solvedProblemSyncStep", jobRepository)
                .<Member, SyncResult>chunk(10, platformTransactionManager)
                .reader(verifiedMemberReader())
                .processor(solvedProblemSyncProcessor())
                .writer(syncResultWriter())
                .faultTolerant()
                .skipLimit(3)
                .skip(Exception.class)
                .listener(new SyncStepListener())
                .build();
    }

    /**
     * 회원 Reader
     */
    @Bean
    public RepositoryItemReader<Member> verifiedMemberReader() {
        log.info("=== verifiedMemberReader 생성 ===");
        return new RepositoryItemReaderBuilder<Member>()
                .name("verifiedMemberReader")
                .repository(memberRepository)
                .methodName("findByVerified")
                .arguments(true)
                .pageSize(10)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }


    /**
     * 문제 동기화 Processor
     */
    @Bean
    public ItemProcessor<Member, SyncResult> solvedProblemSyncProcessor() {
        log.info("=== solvedProblemSyncProcessor 생성 ===");
        return member -> {
            try {
                log.info("회원 {} ({}) 동기화 시작", member.getName(), member.getSolvedAcId());
                SyncResultResponse response = solvedProblemSyncService.syncSolvedProblems(member.getSolvedAcId());

                log.info("회원 {} 동기화 완료 - 저장된 해결문제 {}건", member.getSolvedAcId(), response.resultCount());

                return SyncResult.builder()
                        .memberId(member.getId())
                        .memberName(member.getName())
                        .solvedAcId(member.getSolvedAcId())
                        .syncCount(response.resultCount())
                        .status("SUCCESS")
                        .syncTime(LocalDateTime.now())
                        .build();
            } catch (Exception e) {
                log.error("회원 {} 동기화 실패: {}", member.getSolvedAcId(), e.getMessage());
                return SyncResult.builder()
                        .memberId(member.getId())
                        .memberName(member.getName())
                        .solvedAcId(member.getSolvedAcId())
                        .syncCount(0)
                        .status("FAILED")
                        .errorMessage(e.getMessage())
                        .syncTime(LocalDateTime.now())
                        .build();
            }
        };
    }

    /**
     * 동기화 결과 처리 Writer
     */
    @Bean
    public ItemWriter<SyncResult> syncResultWriter() {
        log.info("=== syncResultWriter 생성 ===");
        return items -> {
            int successCount = 0;
            int failCount = 0;
            int totalSyncCount = 0;

            for (SyncResult result : items) {
                if ("SUCCESS".equals(result.status())) {
                    successCount++;
                    totalSyncCount += result.syncCount();
                }else{
                    failCount++;
                    log.error("❌ 동기화 실패 - 회원: {} ({}), 오류: {}",
                            result.memberName(), result.solvedAcId(), result.errorMessage());
                }
            }
            log.info("배치 청크 처리 완료! 성공 : {}명, 실패 : {}명, 총 동기화 : {}건",
                    successCount, failCount, totalSyncCount);
        };
    }
}
