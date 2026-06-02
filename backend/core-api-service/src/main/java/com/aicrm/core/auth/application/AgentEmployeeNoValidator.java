package com.aicrm.core.auth.application;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class AgentEmployeeNoValidator {

    private static final Pattern DIGITS_16 = Pattern.compile("^\\d{16}$");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private AgentEmployeeNoValidator() {
    }

    public static String normalize(String employeeNo) {
        return employeeNo.trim();
    }

    public static void validate(String employeeNo) {
        if (!DIGITS_16.matcher(employeeNo).matches()) {
            throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_NO, "INVALID_EMPLOYEE_NO_FORMAT");
        }
        try {
            LocalDateTime.parse(employeeNo.substring(0, 14), DATE_TIME);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_NO, "INVALID_EMPLOYEE_NO_DATETIME");
        }
        int sequence = Integer.parseInt(employeeNo.substring(14, 16));
        if (sequence < 1 || sequence > 99) {
            throw new BusinessException(ErrorCode.INVALID_EMPLOYEE_NO, "INVALID_EMPLOYEE_NO_SEQUENCE");
        }
    }
}
