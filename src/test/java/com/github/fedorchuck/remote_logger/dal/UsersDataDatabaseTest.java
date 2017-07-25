package com.github.fedorchuck.remote_logger.dal;

import com.github.fedorchuck.remote_logger.dal.model.UsersLogger;
import com.github.fedorchuck.remote_logger.serialization.MessageCodecs;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@RunWith(VertxUnitRunner.class)
public class UsersDataDatabaseTest {
    private Vertx vertx;
    private final String testToken = "test_token_2017.07.25+3";
    private final UsersLogger testUsersLogger = UsersLogger.builder()
            .accountId(123L)
            .collectionName("test_collection")
            .accessToken(testToken)
            .createdTs(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")))
            .build();

    @Before
    public void before(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();

        MessageCodecs.registerAllCodecs(vertx.eventBus());
        vertx.deployVerticle(UsersDataDatabase.class.getName(), it -> {
            if (it.failed()) {
                Runtime.getRuntime().halt(-1);
            }
        });
        Thread.sleep(5000);
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test   //Create user
    public void test1(TestContext context) {
        vertx.eventBus().send(
                UsersDataDatabase.Address.create.address(),
                testUsersLogger,
                context.asyncAssertSuccess(res -> {
                    UsersLogger createdLogger = (UsersLogger) res.body();
                    Assert.assertNotEquals(null, createdLogger.getAccountId());
                    Assert.assertNotEquals(java.util.Optional.of(0), createdLogger.getAccountId());
                    Assert.assertEquals(testUsersLogger, createdLogger);
                })
        );
    }

    @Test   //Read by accessToken
    public void test2(TestContext context) {
        vertx.eventBus().send(
                UsersDataDatabase.Address.readByAccessToken.address(),
                testToken,
                context.asyncAssertSuccess(res -> Assert.assertEquals(testUsersLogger, res.body()))
        );
    }

    @Test   //Delete user by token
    public void test5(TestContext context) {
        vertx.eventBus().send(
                UsersDataDatabase.Address.deleteByAccountId.address(),
                123L,
                context.asyncAssertSuccess(deleteRes -> {
                    Assert.assertEquals(testUsersLogger, deleteRes.body());
                })
        );
    }
}