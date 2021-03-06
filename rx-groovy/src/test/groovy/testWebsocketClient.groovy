import io.vertx.groovy.core.buffer.Buffer
import io.vertx.groovy.core.http.HttpClient
import io.vertx.groovy.core.http.HttpServer

HttpServer server = vertx.createHttpServer(port: 8080);
server.websocketStream().handler({ ws ->
  ws.write(Buffer.buffer("some_content"));
  ws.close();
});
server.listen({ ar ->
  HttpClient client = vertx.createHttpClient();
  client.websocket(8080, "localhost", "/the_uri") { ws ->
    def content = Buffer.buffer();
    def observable = ws.toObservable();
    observable.forEach(content.&appendBuffer, { err -> test.fail() } , { ->
      server.close();
      test.assertEquals("some_content", content.toString("UTF-8"));
      test.testComplete();
    });
  };
});
test.await();
