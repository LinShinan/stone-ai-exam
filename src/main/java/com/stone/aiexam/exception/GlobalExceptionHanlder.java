package com.stone.aiexam.exception;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.common.ResultCode;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Spring Boot 3.5 使用的 Spring Framework 6.1 修改了 ControllerAdviceBean 构造方法，
 * Knife4j 扫描 @RestControllerAdvice 时会触发 NoSuchMethodError 导致接口列表为空，
 * 加 @Hidden 跳过 Knife4j 扫描即可规避。
 */
@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHanlder {

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("异常信息:",e);
        return Result.fail(ResultCode.FAIL);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleSizeExceeded(MaxUploadSizeExceededException e) {
        log.warn("上传文件超过大小限制");
        return Result.fail("上传文件大小不能超过5MB");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}",e.getMessage());
        return Result.fail(e.getMessage());
    }
}