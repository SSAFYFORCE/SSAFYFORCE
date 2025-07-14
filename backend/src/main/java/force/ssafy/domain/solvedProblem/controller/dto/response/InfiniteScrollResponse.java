package force.ssafy.domain.solvedProblem.controller.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record InfiniteScrollResponse<T>(
        List<T> content,
        boolean hasNext,
        String nextCursor,
        int size,
        boolean isFirst) {
    public static <T> InfiniteScrollResponse<T> of(List<T> content, boolean hasNext, String nextCursor, int size,boolean isFirst) {
        return InfiniteScrollResponse.<T>builder()
                .content(content)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .size(size)
                .isFirst(isFirst)
                .build();
    }

    public static <T> InfiniteScrollResponse<T> first(List<T> content, boolean hasNext, String nextCursor, int size) {
        return of(content, hasNext, nextCursor, size, true);
    }

    public static <T> InfiniteScrollResponse<T> next(List<T> content, boolean hasNext, String nextCursor, int size) {
        return of(content, hasNext, nextCursor, size, false);
    }
}
