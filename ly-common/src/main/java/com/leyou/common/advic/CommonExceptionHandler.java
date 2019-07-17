package com.leyou.common.advic;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.xml.ws.Response;

//默认拦截所有的controller,可以进行更改@ControllerAdvice(定义更改拦截)
@ControllerAdvice
public class CommonExceptionHandler {
    //拦截异常，可以写多个，对异常进行分类处理
    //在哪个微服务使用该类，需将该微服务坐标导入pom中
    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e){
        //缺点：1：状态码写死了，2：消息只返回一个字符串很简陋
        ExceptionEnum em=e.getExceptionEnum();
        return ResponseEntity.status(em.getCode()).body(new ExceptionResult(e.getExceptionEnum()));
    }
}
