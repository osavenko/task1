package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "sqs_handler",
        roleName = "sqs_handler-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 5)
public class SqsHandler implements RequestHandler<SQSEvent, Map<String, Object>> {

    public Map<String, Object> handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Event has " + event.getRecords().size() + " msg");
        event.getRecords().forEach(msg -> context.getLogger().log(msg.getBody()));
        return null;

    }
}
