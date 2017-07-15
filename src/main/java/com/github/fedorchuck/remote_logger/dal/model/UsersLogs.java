package com.github.fedorchuck.remote_logger.dal.model;

import io.vertx.core.json.JsonObject;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersLogs {

    /**
     * Name of mongodb table
     */
    @NotNull
    private String collectionName;

    /**
     * Data for safe
     */
    @NotNull
    private JsonObject data;
}
