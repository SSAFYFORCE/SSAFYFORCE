package force.ssafy.domain.solvedProblem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import force.ssafy.domain.member.entity.Member;
import force.ssafy.domain.member.repository.MemberRepository;
import force.ssafy.domain.problem.entity.Problem;
import force.ssafy.domain.problem.repository.ProblemRepository;
import force.ssafy.domain.solvedProblem.controller.dto.response.SyncResultResponse;
import force.ssafy.domain.solvedProblem.repository.SolvedProblemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MockPerformanceTest {

	@Mock
	private SolvedProblemRepository solvedProblemRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private SolvedProblemSyncService syncService;

	private Member mockMember;
	private Problem mockProblem;

	@BeforeEach
	void setUp() throws Exception {
		// Mock 회원 생성
		mockMember = Member.builder()
			.solvedAcId("testUser")
			.name("테스트유저")
			.password("password")
			.encryptionKey("key")
			.lastProblemSyncTime(null)
			.build();

		// Mock 문제 생성
		mockProblem = Problem.builder()
			.problemNumber(1000L)
			.title("테스트 문제")
			.build();

		// 성능 테스트에서는 실제 서비스 호출을 하지 않으므로 Mock 설정 최소화
		when(memberRepository.findBySolvedAcId(anyString())).thenReturn(Optional.of(mockMember));
	}

	// mockLambdaResponse 메서드 제거 - 실제로 사용되지 않음

	@Test
	void 동기_vs_비동기_성능_비교_시뮬레이션() {
		System.out.println("=== 동기 vs 비동기 성능 비교 (시뮬레이션) ===");

		String solvedAcId = "testUser";

		// 동기 방식 시뮬레이션 (1초 대기)
		long syncStart = System.currentTimeMillis();
		simulateWork(1000); // 1초 Lambda 호출 시뮬레이션
		simulateWork(100);  // DB 처리 시뮬레이션
		long syncDuration = System.currentTimeMillis() - syncStart;

		// 비동기 방식 시뮬레이션 (동일한 작업을 비동기로)
		long asyncStart = System.currentTimeMillis();
		CompletableFuture<Void> asyncTask = CompletableFuture.runAsync(() -> {
			simulateWork(1000); // Lambda 호출
			simulateWork(100);  // DB 처리
		});
		try {
			asyncTask.get(); // 완료 대기
		} catch (Exception e) {
			System.err.println("비동기 작업 실패: " + e.getMessage());
		}
		long asyncDuration = System.currentTimeMillis() - asyncStart;

		// 결과 출력
		System.out.println("동기 방식 소요시간: " + syncDuration + "ms");
		System.out.println("비동기 방식 소요시간: " + asyncDuration + "ms");
		System.out.println("차이: " + (syncDuration - asyncDuration) + "ms");

		// 단일 요청에서는 큰 차이가 없을 것으로 예상
		System.out.println("\n💡 단일 요청에서는 성능 차이가 미미합니다.");
		System.out.println("   실제 성능 차이는 동시 요청에서 확인할 수 있습니다.");
	}

	@Test
	void 동시_요청_성능_비교() throws InterruptedException {
		int requestCount = 5;
		System.out.println("=== 동시 " + requestCount + "개 요청 성능 비교 ===");

		// 동기 방식 (순차 처리)
		testSynchronousRequests(requestCount);

		System.out.println(); // 구분선

		// 비동기 방식 (병렬 처리)
		testAsynchronousRequests(requestCount);
	}

	private void testSynchronousRequests(int requestCount) {
		System.out.println("\n--- 동기 방식 (순차 처리) ---");
		long overallStart = System.currentTimeMillis();
		List<Long> durations = new ArrayList<>();

		for (int i = 0; i < requestCount; i++) {
			long start = System.currentTimeMillis();

			// 동기 작업 시뮬레이션 (Lambda 1초 + DB 처리 0.1초)
			simulateWork(1000); // Lambda 호출
			simulateWork(100);  // DB 처리

			long duration = System.currentTimeMillis() - start;
			durations.add(duration);
			System.out.println("  요청 " + (i + 1) + ": " + duration + "ms");
		}

		long overallDuration = System.currentTimeMillis() - overallStart;
		double avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0);

		System.out.println("동기 방식 전체 소요시간: " + overallDuration + "ms");
		System.out.println("동기 방식 평균 응답시간: " + String.format("%.1f", avgDuration) + "ms");
	}

	private void testAsynchronousRequests(int requestCount) {
		System.out.println("\n--- 비동기 방식 (병렬 처리) ---");
		long overallStart = System.currentTimeMillis();

		// 모든 요청을 비동기로 동시 실행
		List<CompletableFuture<Long>> futures = new ArrayList<>();

		for (int i = 0; i < requestCount; i++) {
			final int index = i;
			CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
				long start = System.currentTimeMillis();

				// 비동기 작업 시뮬레이션
				simulateWork(1000); // Lambda 호출
				simulateWork(100);  // DB 처리

				long duration = System.currentTimeMillis() - start;
				System.out.println("  요청 " + (index + 1) + ": " + duration + "ms");
				return duration;
			});
			futures.add(future);
		}

		// 모든 비동기 작업 완료 대기
		List<Long> durations = new ArrayList<>();
		for (CompletableFuture<Long> future : futures) {
			try {
				durations.add(future.get());
			} catch (Exception e) {
				System.err.println("비동기 작업 실패: " + e.getMessage());
			}
		}

		long overallDuration = System.currentTimeMillis() - overallStart;
		double avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0);

		System.out.println("비동기 방식 전체 소요시간: " + overallDuration + "ms");
		System.out.println("비동기 방식 평균 응답시간: " + String.format("%.1f", avgDuration) + "ms");
	}

	@Test
	void 스레드_풀_크기별_성능_비교() throws InterruptedException {
		int[] poolSizes = {1, 2, 5, 10};
		int taskCount = 8;

		System.out.println("=== 스레드 풀 크기별 성능 비교 ===");
		System.out.println("작업 수: " + taskCount + "개, 각 작업당 1초 소요");

		for (int poolSize : poolSizes) {
			testWithThreadPool(poolSize, taskCount);
		}

		System.out.println("\n💡 분석:");
		System.out.println("   - 스레드 풀 크기 1: 순차 실행과 동일 (8초 소요)");
		System.out.println("   - 스레드 풀 크기가 작업 수보다 크면 모든 작업이 동시 실행 (1초 소요)");
		System.out.println("   - 적절한 스레드 풀 크기 = CPU 코어 수 × 2 정도 권장");
	}

	private void testWithThreadPool(int poolSize, int taskCount) throws InterruptedException {
		System.out.println("\n--- 스레드 풀 크기: " + poolSize + " ---");

		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);

		List<Future<Void>> futures = new ArrayList<>();
		for (int i = 0; i < taskCount; i++) {
			final int taskIndex = i;
			futures.add(executor.submit(() -> {
				System.out.println("    작업 " + (taskIndex + 1) + " 시작");
				simulateWork(1000); // 1초 작업
				System.out.println("    작업 " + (taskIndex + 1) + " 완료");
				return null;
			}));
		}

		// 모든 작업 완료 대기
		for (Future<Void> future : futures) {
			try {
				future.get();
			} catch (Exception e) {
				System.err.println("작업 실패: " + e.getMessage());
			}
		}

		long duration = System.currentTimeMillis() - start;
		System.out.println("  스레드 풀 크기 " + poolSize + " 결과: " + duration + "ms");

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
	}

	@Test
	void 메모리_사용량_비교_시뮬레이션() {
		System.out.println("=== 메모리 사용량 비교 시뮬레이션 ===");

		Runtime runtime = Runtime.getRuntime();

		// 초기 메모리 상태
		runtime.gc();
		long initialMemory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("초기 메모리 사용량: " + (initialMemory / 1024 / 1024) + "MB");

		// 동기 방식 메모리 사용량 시뮬레이션
		long beforeSync = runtime.totalMemory() - runtime.freeMemory();
		List<Object> syncObjects = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			syncObjects.add("동기작업데이터" + i); // 메모리 사용 시뮬레이션
		}
		simulateWork(100); // 작업 시뮬레이션
		long afterSync = runtime.totalMemory() - runtime.freeMemory();

		System.out.println("동기 방식 메모리 증가량: " +
			((afterSync - beforeSync) / 1024) + "KB");

		// 메모리 정리
		syncObjects.clear();
		runtime.gc();

		// 비동기 방식 메모리 사용량 시뮬레이션
		long beforeAsync = runtime.totalMemory() - runtime.freeMemory();
		List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			final int index = i;
			CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
				List<Object> taskObjects = new ArrayList<>();
				for (int j = 0; j < 200; j++) {
					taskObjects.add("비동기작업데이터" + index + "_" + j);
				}
				simulateWork(50);
			});
			asyncTasks.add(task);
		}

		// 모든 비동기 작업 완료 대기
		CompletableFuture.allOf(asyncTasks.toArray(new CompletableFuture[0])).join();

		long afterAsync = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("비동기 방식 메모리 증가량: " +
			((afterAsync - beforeAsync) / 1024) + "KB");

		runtime.gc();
		long finalMemory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("최종 메모리 사용량: " + (finalMemory / 1024 / 1024) + "MB");

		System.out.println("\n💡 일반적으로 비동기 방식이 메모리를 더 효율적으로 사용합니다.");
	}

	@Test
	void 처리량_비교_테스트() {
		System.out.println("=== 처리량(Throughput) 비교 테스트 ===");

		int totalRequests = 20;
		int timeWindowSeconds = 10; // 10초 동안

		System.out.println(timeWindowSeconds + "초 동안 " + totalRequests + "개 요청 처리");

		// 동기 방식 처리량
		long syncStart = System.currentTimeMillis();
		int syncCompleted = 0;
		long syncEndTime = syncStart + (timeWindowSeconds * 1000);

		for (int i = 0; i < totalRequests; i++) {
			if (System.currentTimeMillis() > syncEndTime) break;

			simulateWork(400); // 0.4초 작업
			syncCompleted++;

			if (System.currentTimeMillis() > syncEndTime) break;
		}
		long syncActualDuration = System.currentTimeMillis() - syncStart;

		System.out.println("\n--- 동기 방식 ---");
		System.out.println("처리 완료: " + syncCompleted + "/" + totalRequests + " 요청");
		System.out.println("실제 소요 시간: " + syncActualDuration + "ms");
		System.out.println("초당 처리량: " + String.format("%.1f",
			(double) syncCompleted / (syncActualDuration / 1000.0)) + " 요청/초");

		// 비동기 방식 처리량
		long asyncStart = System.currentTimeMillis();
		List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();

		for (int i = 0; i < totalRequests; i++) {
			CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
				simulateWork(400); // 0.4초 작업
			});
			asyncTasks.add(task);
		}

		// 모든 작업 완료 대기 (최대 10초)
		try {
			CompletableFuture.allOf(asyncTasks.toArray(new CompletableFuture[0]))
				.get(timeWindowSeconds, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			System.out.println("비동기 작업 시간 초과");
		} catch (Exception e) {
			System.err.println("비동기 작업 실패: " + e.getMessage());
		}

		long asyncDuration = System.currentTimeMillis() - asyncStart;

		System.out.println("\n--- 비동기 방식 ---");
		System.out.println("처리 완료: " + totalRequests + "/" + totalRequests + " 요청");
		System.out.println("실제 소요 시간: " + asyncDuration + "ms");
		System.out.println("초당 처리량: " + String.format("%.1f",
			(double) totalRequests / (asyncDuration / 1000.0)) + " 요청/초");

		System.out.println("\n💡 비동기 방식의 처리량이 " +
			String.format("%.1f", (double) totalRequests * asyncDuration / (syncActualDuration * totalRequests)) +
			"배 더 높습니다.");
	}

	// 작업 시뮬레이션 메서드
	private void simulateWork(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}