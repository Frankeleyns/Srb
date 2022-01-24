package com.frankeleyn.common.exception;

import com.frankeleyn.common.result.R;
import com.frankeleyn.common.result.ResponseEnum;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Frankeleyn
 * @date 2022/1/22 9:03
 */
@Component
@RestControllerAdvice
public class UnifiedExceptionHandler {

    /**
     * 总通用异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        System.out.println("通用异常");
        return R.error();
    }

    /**
     * jdbc 框架中的特定异常
     * @param e
     * @return
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public R handleSQLException(BadSqlGrammarException e) {
        System.out.println("SQL 异常" + e.getMessage());
        return R.error(ResponseEnum.BAD_SQL_GRAMMAR_ERROR);
    }

    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public R handleBusinessException(BusinessException e){
        System.out.println("自定义异常" + e.getMessage());
        return R.error(e.getCode(), e.getMessage());
    }

}
