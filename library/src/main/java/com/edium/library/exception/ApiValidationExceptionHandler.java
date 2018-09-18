package com.edium.library.exception;

import com.edium.library.payload.ApiErrorsView;
import com.edium.library.payload.ApiFieldError;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ControllerAdvice
public class ApiValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        BindingResult bindingResult = ex
                .getBindingResult();

        List<ApiFieldError> apiFieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiFieldError(
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage())
                )
                .collect(toList());

        apiFieldErrors.addAll(bindingResult
                .getGlobalErrors()
                .stream()
                .map(globalError -> new ApiFieldError(
                        "",
                        globalError.getCode(),
                        "",
                        globalError.getDefaultMessage())
                )
                .collect(toList()));

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name(),
                getPath(request), apiFieldErrors);

        return handleExceptionInternal(ex, apiErrorsView, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers,
                                                         final HttpStatus status, final WebRequest request) {
        BindingResult bindingResult = ex
                .getBindingResult();

        List<ApiFieldError> apiFieldErrors = bindingResult
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiFieldError(
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage())
                )
                .collect(toList());

        apiFieldErrors.addAll(bindingResult
                .getGlobalErrors()
                .stream()
                .map(globalError -> new ApiFieldError(
                        "",
                        globalError.getCode(),
                        "",
                        globalError.getDefaultMessage())
                )
                .collect(toList()));

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name(),
                getPath(request), apiFieldErrors);

        return handleExceptionInternal(ex, apiErrorsView, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers,
                                                        final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, error, getPath(request));

        return handleExceptionInternal(ex, apiErrorsView, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
                                                                     final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = ex.getRequestPartName() + " part is missing";

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, error, getPath(request));

        return handleExceptionInternal(ex, apiErrorsView, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = ex.getParameterName() + " parameter is missing";

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, error, getPath(request));

        return handleExceptionInternal(ex, apiErrorsView, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, error, getPath(request));

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<ApiFieldError> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new ApiFieldError(violation.getRootBeanClass().getName(), violation.getPropertyPath().toString(),
                    violation.getInvalidValue(), violation.getMessage()));
        }

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.name(),
                getPath(request), errors);

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 404
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, error, getPath(request));

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 405
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());

        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, builder.toString(), getPath(request));

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 415
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        ApiErrorsView apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, builder.substring(0, builder.length() - 2), getPath(request));

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 500
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);

        ApiErrorsView apiErrorsView;
        if (ex instanceof ResourceNotFoundException || ex instanceof BadRequestException || ex instanceof ResourceExistException) {
            apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, ex.getMessage(), getPath(request));
        } else {
            apiErrorsView = new ApiErrorsView(HttpStatus.BAD_REQUEST, "error occurred", getPath(request));
        }

        return new ResponseEntity<>(apiErrorsView, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "";
    }
}