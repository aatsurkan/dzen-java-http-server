/**
 * Since Java 1.6, there's a built-in HTTP server included with the JDK. 
 * http://docs.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html
 *
 * Created by aleksey on 13.04.16.
 * 
 */

package myserver;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class java_http_server {

	public static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		// fill filestorage :)
		createInfoFile();

		HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/", new RootHandler());
		server.createContext("/get1", new Get1Handler());

		// basic auth
		HttpContext hc0 = server.createContext("/get0", new Get0Handler());
		hc0.setAuthenticator(new BasicAuthenticator("get") {
			@Override
			public boolean checkCredentials(String user, String pwd) {
				return user.equals("admin") && pwd.equals("Password");
			}
		});

		server.setExecutor(null); // creates a default executor
		server.start();

		System.out.println("Server started at port " + PORT + "\nPress any key to stop...");
		System.in.read();
		server.stop(0);
		System.out.println("Server stoped");
	}

	static class RootHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/html; charset=utf-8");
			t.sendResponseHeaders(200, 0);

			OutputStream os = t.getResponseBody();

			String body = "<!DOCTYPE html>\n";
			body = body + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
			body = body + "<head>\n<meta charset=\"UTF-8\"/>\n<title>Index [admin:Password]</title>\n";
			body = body + "<style>\na{text-decoration:none}\na:hover{color:#fe2e2e;}\n</style>\n";
			body = body + "</head>\n<body>\n";
			body = body + "<p><b>My Library</b></p>\n";
			body = body + "<ol>\n";
			body = body + "<li><a href=\"/get0\">Source code of this Server :)</a>  <tt>[txt]</tt></li>\n";
			body = body + "<li><a href=\"/get1\">Some info about this server</a> <tt>[txt]</tt></li>\n";
   			body = body + "</ol>\n";
	   		body = body + "<tt>Page created at <span id=\"currdate\"></span>. Please refresh to take effect ;)</tt>\n";
   			body = body + "<script>document.getElementById(\"currdate\").innerHTML = Date();</script>\n"; 
   			body = body + "</body>\n</html>";

			os.write(body.getBytes());
			os.close();
		}
	}

	static class Get0Handler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			Headers h = t.getResponseHeaders();
			//h.add("Content-Type", "application/pdf");
			h.add("Content-Type", "text/plain");

			File file = new File ("filestorage/java_http_server.java");
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			// ok, we are ready to send the response.
			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
		}
	}

	static class Get1Handler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/plain");

			File file = new File ("filestorage/server_info.txt");
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
		}
	}

	static void createInfoFile() {
		// the name of the file to open.
    	String fileName = "filestorage/server_info.txt";

    	try {
			// assume default encoding.
			FileWriter fw = new FileWriter(fileName);

			// always wrap FileWriter in BufferedWriter.
			BufferedWriter bw = new BufferedWriter(fw);

			// add current datetime stamp
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date));

			bw.write("Hello from MyServer!");
			bw.newLine();
			bw.write("Server started " + date + " at http://" + InetAddress.getLocalHost().getHostAddress() + ":" + PORT);

			// always close files.
			bw.close();
		}
		catch(IOException ex) {
			System.out.println( "Error writing to file '" + fileName + "'");
			// or we could just do this:
			// ex.printStackTrace();
		}
	}
}

