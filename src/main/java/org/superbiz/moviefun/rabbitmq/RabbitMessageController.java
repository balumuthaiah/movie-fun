package org.superbiz.moviefun.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RabbitMessageController {

    private final String queue;
    private final RabbitTemplate rabbitTemplate;

    public RabbitMessageController(RabbitTemplate rabbitTemplate, @Value("${rabbitmq.queue}") String queue) {
        this.queue = queue;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/rabbit")
    public Map<String, String> publishMessage() {
        rabbitTemplate.convertAndSend(queue, "This text message will trigger the consumer");

        Map<String, String> response = new HashMap<>();
        response.put("reponse", "This is an unrelated JSON response");
        return response;
    }
}
