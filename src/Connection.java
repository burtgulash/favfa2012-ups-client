import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection {
	String host;
	int port;
	Socket s;
	PrintWriter w = null;
	BufferedReader r = null;
	boolean connected = false;
	
	Connection(String host, int port) {
		this.host = host;
		this.port = port;
		
		try {
			s = new Socket(host, port);
			w = new PrintWriter(s.getOutputStream(), true);
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		connected = true;
	}
	
	void disconnect() {
		if (connected) {
			w.close();
			try {
				r.close();
				s.close();
			} catch (IOException e) {
				System.err.println("Disconnect: closing failed");
			}
			
			// TODO refresh users
			// TODO refresh connection info
		}
	}
}
