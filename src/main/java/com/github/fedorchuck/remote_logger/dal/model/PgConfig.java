package com.github.fedorchuck.remote_logger.dal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PgConfig {
    @JsonProperty("PG_HOST")
    private String pgHost;
    @JsonProperty("PG_PORT")
    private String pgPort;
    @JsonProperty("PG_URL")
    private String pgUrl;
    @JsonProperty("PG_DRIVER")
    private String pgDriver;
    @JsonProperty("PG_DATABASE_NAME")
    private String pgDatabaseName;
    @JsonProperty("PG_USER")
    private String pgUser;
    @JsonProperty("PG_PASSWORD")
    private String pgPassword;

}
