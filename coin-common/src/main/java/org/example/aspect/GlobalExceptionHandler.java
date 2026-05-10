package org.example.aspect;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import jakarta.servlet.http.HttpServletRequest;
import org.example.model.R;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value= MybatisPlusException.class)
    public R handlerMybatisPlusException(MybatisPlusException e) {
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(value = BindException.class)
    public R handlerBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return R.fail(fieldError.getField() + fieldError.getDefaultMessage());
            }
        }
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception e) {
        e.printStackTrace();
        return R.fail(e.getMessage());
    }
}
