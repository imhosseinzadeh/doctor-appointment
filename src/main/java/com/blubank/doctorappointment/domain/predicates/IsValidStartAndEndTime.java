package com.blubank.doctorappointment.domain.predicates;

import com.blubank.doctorappointment.domain.exception.InvalidStartAndEndTimeException;
import com.blubank.doctorappointment.domain.vo.OpenTime;


/**
 * Created by arash on 23.10.22.
 */

public class IsValidStartAndEndTime extends AbstractPredicate<OpenTime> {
    @Override
    public boolean test(OpenTime openTime) {
        return openTime.getTimeDuration().getStart().isBefore(openTime.getTimeDuration().getEnd());
    }

    @Override
    public void check(OpenTime openTime) {
        if (!test(openTime)) {
            throw new InvalidStartAndEndTimeException("is not valid start-end time");
        }
    }

}
