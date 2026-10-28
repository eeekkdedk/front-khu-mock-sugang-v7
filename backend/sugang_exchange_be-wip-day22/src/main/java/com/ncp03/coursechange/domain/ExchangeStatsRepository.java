package com.ncp03.coursechange.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/** 강의별 교환 현황 집계 (희망자 수·보유자 수). 개인 정보 없이 인원수만 공개한다. */
public interface ExchangeStatsRepository extends JpaRepository<ExchangeRequest, Long> {

    /** 이 분반을 희망하는 진행 중 요청의 학생 수. 결과 행: [offeringId, count] */
    @Query("""
            select p.targetOffering.id, count(distinct r.student.id)
            from ExchangePreference p join p.request r
            where r.status in :activeStatuses and p.targetOffering.id in :offeringIds
            group by p.targetOffering.id
            """)
    List<Object[]> countWantersByOfferingIds(@Param("offeringIds") Collection<Long> offeringIds,
                                             @Param("activeStatuses") Collection<RequestStatus> activeStatuses);

    /** 이 분반을 교환에 내놓은 진행 중 요청의 학생 수. 결과 행: [offeringId, count] */
    @Query("""
            select e.offering.id, count(distinct r.student.id)
            from ExchangeRequest r join r.offeredEnrollment e
            where r.status in :activeStatuses and e.offering.id in :offeringIds
            group by e.offering.id
            """)
    List<Object[]> countOfferersByOfferingIds(@Param("offeringIds") Collection<Long> offeringIds,
                                              @Param("activeStatuses") Collection<RequestStatus> activeStatuses);
}
