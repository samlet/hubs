package com.bluecc.prefabs.kafka;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Receiver {
    List<String> topics;
    public interface ReceiveCallback{
        void proc(ConsumerRecord<String, String> record);
    }
    Map<String, ReceiveCallback> callbacks= Maps.newConcurrentMap();
    public Receiver(String... topics){
        this.topics= Arrays.asList(topics);
    }

    public void register(byte[] callid, ReceiveCallback callback){
        String key=new String(callid, StandardCharsets.UTF_8);
        System.out.println("register callback for: "+key);
        this.callbacks.put(key, callback);
    }

    public Thread listen() {
        Thread thread = new Thread(() -> {
            Properties props = new Properties();
            props.setProperty("bootstrap.servers", "localhost:9092");
            props.setProperty("group.id", "hubs");
            props.setProperty("enable.auto.commit", "true");
            props.setProperty("auto.commit.interval.ms", "1000");
            props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
            // consumer.subscribe(Arrays.asList("foo", "bar"));
            // consumer.subscribe(Arrays.asList("mytopic"));
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    // record.headers()
                    String callid=null;
                    for (Header header : record.headers()) {
                        if(header.key().equals("callid")){
                            callid = new String(header.value(), StandardCharsets.UTF_8);
                            System.out.format("\t%s = %s\n", header.key(), callid);
                        }
                    }

                    // System.out.println("callbacks: "+callbacks.size());
                    // System.out.println(new String(callbacks.keySet().iterator().next(), StandardCharsets.UTF_8));
                    // ReceiveCallback c=callbacks.get(callid);
                    // c.proc(record);

                    if(callid!=null && callbacks.containsKey(callid)){
                        callbacks.get(callid).proc(record);
                    }else {
                        System.out.println("no callback.");
                        System.out.printf("offset = %d, key = %s, value = %s%n",
                                record.offset(), record.key(), record.value());
                    }
                }
            }
        });
        thread.start();
        return thread;
    }
}
