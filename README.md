# Microservices Tutorial: Eventsourcing with Apache Kafka

This project contains 3 maven projects for a [Microservices tutorial on Youtube(German)](https://www.youtube.com/watch?v=QjJqc5ALpKs).

Clone the repository and open the projects *kasse* and *historie* in your IDE. See the video for an explanation.

# Running the Demo

## Running a Console Consumer

./bin/kafka-console-consumer.sh --bootstrap-server "localhost:9092" --topic "articles" --from-beginning


## Creating an Article

curl -X POST http://localhost:8081/articles/ -d '{ "name":"Bananas", "price":1.99 }' -H "Content-Type: application/json"

curl http://localhost:8081/articles/

curl http://localhost:8082/articles/

curl -X POST http://localhost:8082/articles/ -d '{ "name":"Pineapple", "price":1.99 }' -H "Content-Type: application/json"

curl http://localhost:8082/articles/

curl http://localhost:8082/articles/

curl -X PUT http://localhost:8082/articles/af097bf7-5107-44e1-b445-8bca37332fe0 -d '{ "name":"Pineapple", "price": 0.99 }' -H "Content-Type: application/json"

curl http://localhost:8082/articles/

curl http://localhost:8082/articles/

curl -X DELETE http://localhost:8082/articles/af097bf7-5107-44e1-b445-8bca37332fe0

curl http://localhost:8082/articles/

curl http://localhost:8082/articles/





