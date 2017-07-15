package com.github.fedorchuck.remote_logger;

import com.github.fedorchuck.remote_logger.dal.LiquibaseVerticle;
import com.github.fedorchuck.remote_logger.dal.UsersDataDatabase;
import com.github.fedorchuck.remote_logger.dal.UsersLogsDatabase;
import com.github.fedorchuck.remote_logger.serialization.MessageCodecs;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;

/**
 * @author <a href="http://vl-fedorchuck.rhcloud.com/">Volodymyr Fedorchuk</a>.
 */
@SuppressWarnings("unused")
public class Main extends AbstractVerticle {
    private static RemoteLoggerSrvLogger LOG = RemoteLoggerSrvLogger.LOG;

    @Override
    public void start() throws Exception {
        super.start();

        MessageCodecs.registerAllCodecs(vertx.eventBus());

        vertx.deployVerticle(LiquibaseVerticle.class.getName(), (AsyncResult<String> res) -> {
            if (res.failed()) {
                printErrorMessage(res, "LiquibaseVerticle");
                Runtime.getRuntime().halt(-1);
                return;
            }
            //region setup database
            LOG.infoDeployedVerticle("LiquibaseVerticle");

            vertx.deployVerticle(UsersDataDatabase.class.getName(), it1 -> {
                if (it1.failed()) {
                    printErrorMessage(it1, "UsersDataDatabase");
                    Runtime.getRuntime().halt(-1);
                } else {
                    LOG.infoDeployedVerticle("UsersDataDatabase");
                }
            });
            vertx.deployVerticle(UsersLogsDatabase.class.getName(), it1 -> {
                if (it1.failed()) {
                    printErrorMessage(it1, "UsersLogsDatabase");
                    Runtime.getRuntime().halt(-1);
                } else {
                    LOG.infoDeployedVerticle("UsersLogsDatabase");
                }
            });

            //endregion setup database


            //region setup logic
//            vertx.deployVerticle(MarketplaceToTextback.class.getName(), it ->{
//                if (it.failed()) {
//                    printErrorMessage(it, "MarketplaceToTextback");
//                    Runtime.getRuntime().halt(-1);
//                } else {
//                    LOG.infoDeployedVerticle("MarketplaceToTextback");
//                }
//            });
            //endregion setup logic

            //region setup web server
            vertx.deployVerticle(WebServerVerticle.class.getName(), it -> {
                if (it.failed()) {
                    printErrorMessage(it, "WebServerVerticle");
                    Runtime.getRuntime().halt(-1);
                } else {
                    LOG.infoDeployedVerticle("WebServerVerticle");
                }
            });
            //endregion setup web server

        });

    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private void printErrorMessage(AsyncResult<String> stringAsyncResult, String failedDeployVerticleName) {
        System.out.println(
                "Failed to deploy : " + failedDeployVerticleName +
                        "\n\t result:  " + stringAsyncResult.result() +
                        "\n\t cause: " + stringAsyncResult.cause() +
                        "\n\t stackTrace: "
        );
        stringAsyncResult.cause().printStackTrace();
    }
}
