package de.predic8.microservices.scm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

/**
 * Über Spring-Kafka kann Stand 1.2 kein RebalanceListener eingeklinkt werden!
 * Daher ist diese Handarbeit noch notwendig! Kann sicher ändern!
 **/
@Component
public class KafkaListenerRunner implements Runnable {

    public static Logger logger = LoggerFactory.getLogger(KafkaListenerRunner.class);

    @Autowired
    private ArticlesStore store;

    private Gson gson = new Gson();

    public Map<String, Object> config() {

        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(GROUP_ID_CONFIG, "kasse");
        props.put(ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }

    public void run() {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config());

        consumer.subscribe(Arrays.asList("articles"), new OffsetBeginningRebalanceListener(consumer, "articles"));

        JsonParser parser = new JsonParser();

        Long startZeit = System.currentTimeMillis();

        try {

            System.out.println("Starting Listener!");

            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(1000);

                if (records.isEmpty())
                    continue;

                for (ConsumerRecord<String, String> cr : records) {

                    if (cr.key().startsWith("ENDE")) {
                        System.out.println("Zeit für 1.000.000 Nachrichten: " + ((System.currentTimeMillis() - startZeit)/1000.0) + " Sekunden.");
                        continue;
                    }

                    JsonObject json = parser.parse(cr.value()).getAsJsonObject();

                    String action = json.getAsJsonPrimitive("action").getAsString();

                    JsonObject object = json.getAsJsonObject("object");


                    // Für Performance-Test auskommentieren!
//                    System.out.println("----------------------------------------------------------------------------------");
//                    System.out.println("Offset: " + cr.offset());
//                    System.out.println("Key: "+ cr.key());
//                    System.out.println("Action: " + action);
//                    System.out.println("Object: " + object);

                    Article article = gson.fromJson(object, Article.class);

                    switch (action) {
                        case "update":
                        case "create":
                            article.setId(cr.key());
                            store.save(article);
                            break;
                        case "delete":
                            store.delete(cr.key());
                            break;

                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}



