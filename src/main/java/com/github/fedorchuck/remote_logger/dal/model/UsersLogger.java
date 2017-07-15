package com.github.fedorchuck.remote_logger.dal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersLogger {

    /**
     * Assigned record id
     */
    private Long id;

    /**
     * RL account id
     */
    @NotNull
    private Long accountId;

    /**
     * Name of mongodb table
     */
    @NotNull
    private String collectionName;

    /**
     * Access token for REST-API
     */
    @NotNull
    private String accessToken;

    /**
     * When connection was created
     */
    private LocalDateTime createdTs;

}
