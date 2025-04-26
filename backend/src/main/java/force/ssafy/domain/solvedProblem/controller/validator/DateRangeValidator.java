package force.ssafy.domain.solvedProblem.controller.validator;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateRangeValidator {
    public void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if((startDate==null && endDate!=null)|| (startDate!=null && endDate==null)) {
            throw new IllegalArgumentException("시작 날짜와 종료 날짜는 둘 다 제공하거나 둘 다 생략해야 합니다.");
        }
        if((startDate!=null && endDate!=null) && (startDate.isAfter(endDate))) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }
    }
}
