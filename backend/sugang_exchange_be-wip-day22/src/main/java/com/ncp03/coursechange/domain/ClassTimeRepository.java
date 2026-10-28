package com.ncp03.coursechange.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ClassTimeRepository extends JpaRepository<ClassTime, Long> {

    @Query("""
            select t from ClassTime t
            where t.offering.id in :offeringIds
            order by t.offering.id, t.weekday, t.startTime
            """)
    List<ClassTime> findByOfferingIds(@Param("offeringIds") Collection<Long> offeringIds);
}
