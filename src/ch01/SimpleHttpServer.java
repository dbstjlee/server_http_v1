package ch01;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Exchanger;

import com.sun.net.httpserver.*;

public class SimpleHttpServer {

	public static void main(String[] args) {

		// 8080 <-- https, 80 <-- http (포트번호 생략 가능하다)
		try {
			// 포트 번호 8080으로 HTTP 서버 생성하는 코드임.
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

			// 서버에 대한 설정 필요
			// 프로토콜 정의(경로, 핸들러 처리)
			// 핸들러 처리를 정적 클래스로 사용
			httpServer.createContext("/test", new MyTestHandler());
			// '/test' 가 오면 new MyTestHandler()를 시작하라는 명령
			httpServer.createContext("/hello", new HelloHandler());

			// 서버 시작
			httpServer.start();
			System.out.println(">> My Http Server started on port 8080 <<");
			// 주소창에 던지는 건 GET 방식
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end of main

	// 클래스로 정의
	// http://localhost:8080/test <-- 주소 설계
	static class MyTestHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			// 사용자의 요청 방식(METHOD), GET, POST 인지 알아야 우리가 동작시킬 수 있다.
			String method = exchange.getRequestMethod();
			System.out.println("method : " + method);

			// 사용자가 던진 값과 비교(대,소문자 상관 X)
			if ("GET".equalsIgnoreCase(method)) {
				// GET 이라면 여기 동작
				// System.out.println("여기는 Get 방식으로 호출됨");
				// GET -> path: /test 라고 들어오면 어떤 응답 처리를 내려주면 된다.
				handleGetRequest(exchange);

			} else if ("POST".equalsIgnoreCase(method)) {
				// POST 요청 시 여기 동작
				// System.out.println("여기는 Post 방식으로 호출됨");
				handlePostRequest(exchange);
				// 사용자 DB 받아서 보내기
				// 약속: 주소창에는 get 방식을 쓸 수 밖에 없다.
				// get 방식은 body가 없다.
				// post 방식은 body가 있다. (많은 데이터를 받아야 하기 때문에 )
			} else {
				// 지원하지 않는 메서드에 대한 응답
				String respnose = "Unsupported Methdo : " + method;
				exchange.sendResponseHeaders(405, respnose.length()); // Method Not Allowed

				// 예시
				// new OutputStreamWriter(exchange.getResponseBody());

				OutputStream os = exchange.getResponseBody();
				os.write(respnose.getBytes());
				os.flush();
				os.close();
			}
		}

		// Get 요청 시 동작 만들기
		// HttpExchange exchange = 그대로 받아서 전달하면 됨.
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

			// String response = "hello GET~~"; // 응답 메시지

			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes()); // 응답 본문 전송
			os.close();
			// write는 바이트 기반
			// response은 String 인데 getBytes로 변환해서 한 바이트씩 전송
		}

		// Post 요청시 동작 만들기
		private void handlePostRequest(HttpExchange exchange) throws IOException {
			// POST 요청은 HTTP 메세지에 바디 영역이 존재한다.
			String response = """
						<!DOCTYPE html>
						<html lang=ko>
							<head></head>
							<body>
								<h1 style="background-color:red"> Hello path by /test </h1>
							</body>
						</html>
					""";

			// HTTP 응답 메세지 헤더 설정
			// sendResponseHeaders => 헤더 부분을 정리
			exchange.setAttribute("Content-Type", "text/html; charset = UTF-8");
			exchange.sendResponseHeaders(200, response.length());

			// getResponseBody
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}// end of MyTestHandler

	static class HelloHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			String method = exchange.getRequestMethod();
			System.out.println("hello method : " + method);
		}
	}// end of HelloHandler
}
