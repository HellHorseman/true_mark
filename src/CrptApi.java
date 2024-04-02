import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final int requestLimit;
    private final long timeInterval;
    private int requestCount;
    private long lastRequestTime;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        this.timeInterval = timeUnit.toMillis(1);
        this.requestCount = 0;
        this.lastRequestTime = System.currentTimeMillis();
    }

    public synchronized void makeRequest() throws InterruptedException {
        long currentTime = System.currentTimeMillis();

        // Проверяем, прошло ли достаточно времени с момента последнего запроса
        if (currentTime - lastRequestTime >= timeInterval) {
            // Если прошло достаточно времени, сбрасываем счетчик запросов и обновляем время последнего запроса
            requestCount = 0;
            lastRequestTime = currentTime;
        }

        // Проверяем, достигнуто ли ограничение на количество запросов
        if (requestCount >= requestLimit) {
            // Если достигнуто ограничение, ждем до окончания текущего интервала времени
            long sleepTime = timeInterval - (currentTime - lastRequestTime);
            Thread.sleep(sleepTime);
            // После ожидания сбрасываем счетчик запросов и обновляем время последнего запроса
            requestCount = 0;
            lastRequestTime = System.currentTimeMillis();
        }

        // Выполняем запрос
        requestCount++;

    }


    public class DocumentCreation {
        public static void main(String[] args) {
            try {
                // Создание URL для отправки POST-запроса
                URL url = new URL("https://ismp.crpt.ru/api/v3/lk/documents/create");

                // Создание объекта HttpURLConnection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Установка метода запроса на POST
                connection.setRequestMethod("POST");

                // Включение возможности записи данных в тело запроса
                connection.setDoOutput(true);

                // Установка заголовков запроса
                connection.setRequestProperty("Content-Type", "application/json");

                // Создание JSON-строки с данными документа и подписью
                String documentData = "{\"description\":" +
                        "{ \"participantInn\": \"string\" }, \"doc_id\": \"string\", \"doc_status\": \"string\"," +
                        "\"doc_type\": \"LP_INTRODUCE_GOODS\", 109 \"importRequest\": true," +
                        "\"owner_inn\": \"string\", \"participant_inn\": \"string\", \"producer_inn\":" +
                        "\"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\"," +
                        "\"products\": [ { \"certificate_document\": \"string\"," +
                        "\"certificate_document_date\": \"2020-01-23\"," +
                        "\"certificate_document_number\": \"string\", \"owner_inn\": \"string\"," +
                        "\"producer_inn\": \"string\", \"production_date\": \"2020-01-23\"," +
                        "\"tnved_code\": \"string\", \"uit_code\": \"string\", \"uitu_code\": \"string\" } ]," +
                        "\"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";

                // Получение потока для записи данных в тело запроса
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(documentData.getBytes());
                outputStream.flush();
                outputStream.close();

                // Получение ответа от сервера
                int responseCode = connection.getResponseCode();

                // Чтение ответа сервера
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Вывод ответа сервера
                System.out.println("Response Code: " + responseCode);
                System.out.println("Response Body: " + response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
