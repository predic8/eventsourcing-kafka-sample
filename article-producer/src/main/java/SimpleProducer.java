import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.math.BigDecimal;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class SimpleProducer {

    static Gson gson = new Gson();

    public static void main(String[] args) throws InterruptedException {


        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        props.put(RETRIES_CONFIG, 0);
        props.put(BATCH_SIZE_CONFIG, 16000);
        props.put(LINGER_MS_CONFIG, 100);
        props.put(BUFFER_MEMORY_CONFIG, 33554432);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<>(props);

        long t1 = System.currentTimeMillis();

        int i = 0;
        for (; i < 1000000; i++) {

            Article article = new Article();
            String id = "" +(int) (Math.random() * 1000000);
            article.setId(id);
            article.setName("Article-" + i);
            article.setPrice(new BigDecimal(Math.random() * 1000));

            producer.send(new ProducerRecord<String, String>("articles", id, createWrapper(article)));

        }
        producer.send(new ProducerRecord<String, String>("articles", "ENDE", "ENDE"));
        System.out.println("fertig " + i + " Nachrichten in " + (System.currentTimeMillis() - t1 + " ms"));

        producer.close();
    }

    private static String createWrapper(Article article) {
        JsonObject cmd = new JsonObject();
        cmd.addProperty("action", "create");
        cmd.add("object", gson.toJsonTree(article));
        return cmd.toString();
    }
}
