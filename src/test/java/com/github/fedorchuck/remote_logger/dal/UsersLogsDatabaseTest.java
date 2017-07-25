package com.github.fedorchuck.remote_logger.dal;

import com.github.fedorchuck.remote_logger.dal.model.UsersLogs;
import com.github.fedorchuck.remote_logger.serialization.MessageCodecs;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@RunWith(VertxUnitRunner.class)
public class UsersLogsDatabaseTest {
    private Vertx vertx;
    private final UsersLogs testUsersLogs = UsersLogs.builder()
            .collectionName("test_collection_name_2017.07.25+3")
            .data(new JsonObject("{ \"test\":\"some test data; bla bla; 2017.07.25\" }"))
            .build();

    @Before
    public void before(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();

        MessageCodecs.registerAllCodecs(vertx.eventBus());
        vertx.deployVerticle(UsersLogsDatabase.class.getName(), it -> {
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

    @Test   //Create
    public void test1(TestContext context) {
        vertx.eventBus().send(
                UsersLogsDatabase.Address.create.address(),
                testUsersLogs,
                context.asyncAssertSuccess(createResult -> {

                    String recordId = (String) createResult.body();
                    Assert.assertNotEquals(null, recordId);
                    Assert.assertNotEquals("", recordId);

        vertx.eventBus().send(
                UsersLogsDatabase.Address.read.address(),
                testUsersLogs,
                context.asyncAssertSuccess(readResult -> {

                    String result = (String) readResult.body();
                    JsonObject jo = new JsonObject(result);
                    Assert.assertEquals(recordId, jo.getValue("_id"));

//        vertx.eventBus().send(
//                UsersLogsDatabase.Address.remove.address(),
//                testUsersLogs,
//                context.asyncAssertSuccess(removeResult -> {
//
//                    String result = (String) readResult.body();
//                    JsonObject jo = new JsonObject(result);
//                    Assert.assertEquals(recordId, jo.getValue("_id"));
//
//
//        vertx.eventBus().send(
//                UsersLogsDatabase.Address.read.address(),
//                testUsersLogs,
//                context.asyncAssertSuccess(readResult -> {
////not found
//                    String result = (String) readResult.body();
//                    JsonObject jo = new JsonObject(result);
//                    Assert.assertEquals(recordId, jo.getValue("_id"));
//
//
//        vertx.eventBus().send(
//                UsersLogsDatabase.Address.remove.address(),
//                testUsersLogs,
//                context.asyncAssertSuccess(readResult -> {
////delete
//                    String result = (String) readResult.body();
//                    JsonObject jo = new JsonObject(result);
//                    Assert.assertEquals(recordId, jo.getValue("_id"));
//
//                })
//        );
//
//                })
//        );
//
//                })
//        );

                })
        );

                })
        );
    }

}