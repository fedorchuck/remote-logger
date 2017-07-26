package com.github.fedorchuck.remote_logger.commont.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExceptionInfo {
    private String message;
    private List<String> causeMessages;
    private List<ExceptionInfo> suppressed;
    private String stackTrace;
}
