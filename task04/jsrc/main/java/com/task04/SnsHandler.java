package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.ArrayList;
import java.util.List;

@LambdaHandler(lambdaName = "sns_handler",
        roleName = "sns_handler-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(targetTopic = "lambda_topic")
@DependsOn(
        name = "lambda_topic",
        resourceType = ResourceType.SNS_TOPIC
)

public class SnsHandler implements RequestHandler<SNSEvent, List<String>> {

    public List<String> handleRequest(SNSEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("EVENT TYPE: " + event.getClass().toString());
        var messagesFound = new ArrayList<String>();
        for (SNSEvent.SNSRecord record : event.getRecords()) {
            SNSEvent.SNS msg = record.getSNS();
            logger.log("sns message: " + msg.getMessage());
            messagesFound.add(msg.getMessage());
        }
        return messagesFound;
    }
}
