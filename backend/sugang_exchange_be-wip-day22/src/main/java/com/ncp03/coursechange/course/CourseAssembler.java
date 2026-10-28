package com.ncp03.coursechange.course;

import com.ncp03.coursechange.domain.ClassTimeRepository;
import com.ncp03.coursechange.domain.CourseOffering;
import com.ncp03.coursechange.domain.EnrollmentRepository;
import com.ncp03.coursechange.domain.ExchangeStatsRepository;
import com.ncp03.coursechange.domain.RequestStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 분반 목록을 CourseResponse 목록으로 바꾼다.
 * 수업 시간·수강 인원·교환 현황을 분반 수와 상관없이 쿼리 4번으로 모아 온다 (N+1 방지).
 */
@Component
public class CourseAssembler {

    private static final List<RequestStatus> ACTIVE = List.of(RequestStatus.WAITING, RequestStatus.MATCHED);

    private final ClassTimeRepository classTimeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ExchangeStatsRepository exchangeStatsRepository;

    public CourseAssembler(ClassTimeRepository classTimeRepository,
                           EnrollmentRepository enrollmentRepository,
                           ExchangeStatsRepository exchangeStatsRepository) {
        this.classTimeRepository = classTimeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.exchangeStatsRepository = exchangeStatsRepository;
    }

    /** offerings의 course는 미리 fetch join 되어 있어야 한다. */
    public List<CourseResponse> toResponses(List<CourseOffering> offerings) {
        if (offerings.isEmpty()) {
            return List.of();
        }
        List<Long> ids = offerings.stream().map(CourseOffering::getId).toList();

        Map<Long, List<ScheduleResponse>> schedules = classTimeRepository.findByOfferingIds(ids).stream()
                .collect(Collectors.groupingBy(t -> t.getOffering().getId(),
                        Collectors.mapping(ScheduleResponse::from, Collectors.toList())));
        Map<Long, Long> enrolled = toCountMap(enrollmentRepository.countByOfferingIds(ids));
        Map<Long, Long> wanters = toCountMap(exchangeStatsRepository.countWantersByOfferingIds(ids, ACTIVE));
        Map<Long, Long> offerers = toCountMap(exchangeStatsRepository.countOfferersByOfferingIds(ids, ACTIVE));

        return offerings.stream()
                .map(o -> CourseResponse.of(o,
                        schedules.getOrDefault(o.getId(), List.of()),
                        enrolled.getOrDefault(o.getId(), 0L),
                        new ExchangeStatsResponse(wanters.getOrDefault(o.getId(), 0L),
                                offerers.getOrDefault(o.getId(), 0L))))
                .toList();
    }

    public CourseResponse toResponse(CourseOffering offering) {
        return toResponses(List.of(offering)).get(0);
    }

    private static Map<Long, Long> toCountMap(Collection<Object[]> rows) {
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
        }
        return map;
    }
}
