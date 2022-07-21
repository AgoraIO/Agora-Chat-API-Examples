package com.easemob.agora.config;

import com.easemob.agora.exception.*;
import com.easemob.agora.model.ResCode;
import com.easemob.agora.model.ResponseParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentExcetpion(IllegalArgumentException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundExcetpion(ASNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_NOT_FOUND.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetChatUserNameException.class)
    public ResponseEntity<Object> handleGetChatUserIdExcetpion(ASGetChatUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASRegisterChatUserNameException.class)
    public ResponseEntity<Object> handleRegisterChatUserIdExcetpion(ASRegisterChatUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetChatUserIdException.class)
    public ResponseEntity<Object> handleGetChatUserIdExcetpion(ASGetChatUserIdException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASPasswordErrorException.class)
    public ResponseEntity<Object> handlePasswordErrorException(ASPasswordErrorException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASResourceLimitedException.class)
    public ResponseEntity<Object> handleResourceLimitedException(ASResourceLimitedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REACH_LIMIT.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASDuplicateUniquePropertyExistsException.class)
    public ResponseEntity<Object> handleDuplicateUniquePropertyExistsException(ASDuplicateUniquePropertyExistsException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_ALREADY_EXISTS.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(error.getDefaultMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }
}
