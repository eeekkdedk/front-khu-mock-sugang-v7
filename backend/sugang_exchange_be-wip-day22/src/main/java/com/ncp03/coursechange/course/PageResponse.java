package com.ncp03.coursechange.course;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "페이지 응답")
public record PageResponse<T>(
        @Schema(description = "현재 페이지 데이터") List<T> content,
        @Schema(description = "현재 페이지 번호 (0부터)", example = "0") int page,
        @Schema(description = "페이지 크기", example = "20") int size,
        @Schema(description = "전체 개수", example = "21") long totalElements,
        @Schema(description = "전체 페이지 수", example = "2") int totalPages,
        @Schema(description = "다음 페이지가 있는지", example = "true") boolean hasNext
) {
    public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.hasNext());
    }
}
