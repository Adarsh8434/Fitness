package com.fitness.activityservice.config;



import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;


@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.queue.name}")
    private String queue;
    @Value("${rabbitmq.routing.key}")
    private String routing;


    @Bean
    public Queue activityQueue(){
      return new Queue(queue,true);
  }
@Bean
public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
}
@Bean
public Binding activiyuBinding (Queue activityQueue,DirectExchange activityExchange){
       return BindingBuilder.bind(activityQueue).to(activityExchange).with(routing);
}
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
