package com.github.fedorchuck.remote_logger;

import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.dal.model.UsersLogs;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.*;

import java.util.List;

/**
 * Logs messages for remote_logger-srv
 */
@MessageLogger(projectCode = "REMOTE_LOGGER_SRV_", length = 4)
@ValidIdRanges({
        @ValidIdRange(min = 0, max = 999),      // FATAL
        @ValidIdRange(min = 2000, max = 2999),  // CRITICAL
        @ValidIdRange(min = 3000, max = 3999),  // ERROR
        @ValidIdRange(min = 4000, max = 4999),  // WARN
        @ValidIdRange(min = 6000, max = 6999),  // INFO
        @ValidIdRange(min = 7000, max = 7999),  // DEBUG
        @ValidIdRange(min = 9000, max = 9999),  // TRACE
})
public interface RemoteLoggerSrvLogger extends BasicLogger {
    RemoteLoggerSrvLogger LOG = Logger.getMessageLogger(RemoteLoggerSrvLogger.class,
            "com.github.fedorchuck.remote_logger");

    @LogMessage(level = Logger.Level.FATAL)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 0,
            value = "Failed to listen on port: {0}")
    void fatalListening(int port, @Cause Throwable cause);

    //region ERROR
    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 2000, value = "It should never happen.")
    void errorItShouldNeverHappen(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 2100, value = "Exception in CS.")
    void errorExceptionInCS(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 3000, value = "Database error")
    void errorDatabaseError(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 3001, value = "Database error. Liquibase failed")
    void errorLiquibaseFailed(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 3002, value = "Database error")
    void errorDatabaseFailed(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 3003,
            value = "Environment variable {0} was not found in environment and gradle.properties ")
    void errorEnvironmentVariableNotFound(String varName);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 3004,
            value = "{0} has invalid format")
    void errorReadProperty(String name, @Cause DecodeException ex);

    //endregion ERROR

    //region WARN
    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4000, value = "Not implemented: {0}")
    void warnNotImplemented(String message);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4001,
            value = "Environment variable {0} is null or empty. Try to get value in gradle.properties ")
    void warnEnvironmentVariableNotFound(String varName);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4002, value = "It should never happen.")
    void warnItShouldNeverHappen(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4010, value = "Failed to create account: {0}")
    void warnFailedToCreateAccount(UsersLogger databaseEntry, @Cause Throwable cause);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4020, value = "Failed to create users data (logs): {0}")
    void warnFailedToCreateUsersDataEntry(UsersLogger val, @Cause Throwable cause);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4030,
            value = "Failed to get data. Input token: {0}")
    void warnFailedToGetAccountInfo(String accessToken, @Cause Throwable cause);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4031,
            value = "Failed to get data. Url: {0} StatusCode: {1}, StatusMessage: {2}, respBody: {3}")
    void warnFailedCreate(String absoluteURI, int code, String statusMessage, Buffer respBody);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4032,
            value = "Failed to get account. Url: {0} StatusCode: {1}, StatusMessage: {2}, respBody: {3}")
    void warnFailedToGetAccount(String absoluteURI, int code, String statusMessage, Buffer respBody);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4033,
            value = "Failed to update account. Url: {0} StatusCode: {1}, StatusMessage: {2}, respBody: {3}")
    void warnFailedUpdateAccount(String absoluteURI, int code, String statusMessage, Buffer respBody);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4034,
            value = "Failed to destroy account. Url: {0} StatusCode: {1}, StatusMessage: {2}, respBody: {3}")
    void warnFailedToDestroyAccount(String absoluteURI, int code, String statusMessage, Buffer respBody);

    @LogMessage(level = Logger.Level.WARN)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4040,
            value = "Failed to get data. Input token: {0}")
    void warnFailedToCreateMongoDocument(String token, @Cause Throwable cause);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4050, value = "Postgresql connection failed. ")
    void warnPostgresqlConnectionFail(@Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4051, value = "Mongo operation failed. Entry: {0}")
    void warnMongoConnectionFail(UsersLogs entry, @Cause Throwable throwable);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4052, value = "Mongo null result. Entry: {0}")
    void warnMongoNullResult(UsersLogs entry);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 4053, value = "Invalid size ({1}). Entry: {0} Record: {3}")
    void warnMongoInvalidSize(UsersLogs entry, int size, String record);

    //endregion WARN

    //region INFO
    @LogMessage(level = Logger.Level.INFO)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 6000, value = "Listening on port: {0}")
    void listening(int port);

    @LogMessage(level = Logger.Level.INFO)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 6010, value = "Liquibase updated. SCHEMA_VERSION: {0}")
    void liquibaseUpdated(String schemaVersion);

    @LogMessage(level = Logger.Level.INFO)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 6020,
            value = "Deployed verticle:  {0}")
    void infoDeployedVerticle(String s);

    @LogMessage(level = Logger.Level.INFO)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 6021,
            value = "Disabled verticle:  {0}")
    void infoDisabledVerticle(String s);

    //endregion INFO

    //region TRACE
    /*  database trace  */

    @LogMessage(level = Logger.Level.TRACE)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 9010,
            value = "Get entry from users_data: {0}")
    void traceUsersDataEntryFromDatabase(UsersLogger usersLogger);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 9011,
            value = "Created entry at users_data: {0}")
    void traceCreateUsersDataEntry(UsersLogger usersLogger);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 9012,
            value = "Read entry from users_data where account_id is: {0}")
    void traceReadByAccessTokenUsersData(String accountId);

    @LogMessage(level = Logger.Level.TRACE)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 9013,
            value = "Delete entry from users_data where account_id is: {0}")
    void traceDeleteByAccountIdUsersData(Long accountId);


    @LogMessage(level = Logger.Level.TRACE)
    @Message(format = Message.Format.MESSAGE_FORMAT, id = 9051,
            value = "Account: {0}")
    void traceGetAccount(UsersLogger account);

    //endregion TRACE
}