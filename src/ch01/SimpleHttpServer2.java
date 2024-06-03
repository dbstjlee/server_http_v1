package ch01;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer2 {

	public static void main(String[] args) {

		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
			httpServer.createContext("/test", new MyTestHandler());
			httpServer.createContext("/hello", new HelloHandler());

			// 서버 시작
			httpServer.start();
			System.out.println("Server start");
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end of main

	static class MyTestHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String method = exchange.getRequestMethod(); // 요청 방식
			System.out.println("method :" + method);

			if ("GET".equalsIgnoreCase(method)) {
				handleGetRequest(exchange);
			} else if ("POST".equalsIgnoreCase(method)) {
				handlePostRequest(exchange);
			} else {
				String response = "Unsupported Method : " + method;
				exchange.sendResponseHeaders(405, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.flush();
				os.close();
			}
		}

		// Get 요청 시
		private void handleGetRequest(HttpExchange exchange) throws IOException {
			String response = """
						<!DOCTYPE html>
						<html lang=ko>
							<head></head>
							<body>
								<h1 style="background-color:red"> Hello path by /test </h1>
							</body>
						</html>
					""";
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}

		// Post 요청 시
		private void handlePostRequest(HttpExchange exchange) throws IOException {
			String response = """
						<!DOCTYPE html>
						<html lang=ko>
							<head></head>
							<body>
								<h1 style="background-color:red"> Hello path by /test </h1>
							</body>
						</html>
					""";
			exchange.setAttribute("Content-Type", "text/html; charset = UTF-8");
			exchange.sendResponseHeaders(200, response.length());
		}
	} // end of MyTestHandler

	static class HelloHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String method = exchange.getRequestMethod();
			System.out.println("hello method : " + method);
		}
	} // end of HelloHandler
}