import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection {
	static final int TIMEOUT = 1000;
	private String host;
	private int port;
	private String loginName;
	
	private Socket s;
	private PrintWriter w = null;
	private BufferedReader r = null;
	boolean connected = false;
	
	
	Connection(String host, int port, String loginName) {
		this.host = host;
		this.port = port;
		this.loginName = loginName;
		
		try {
			s = new Socket(host, port);
			s.setSoTimeout(TIMEOUT);
			w = new PrintWriter(s.getOutputStream(), true);
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			send("LOGIN " + loginName);
			connected = "OK".equals(recv());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void send(String s) {
		w.println(s.trim());
	}
	
	public String recv() throws IOException {
		return r.readLine().trim();
	}
	
	public String getHost() {
		return host;
	}

	int getPort() {
		return port;
	}

	String getLoginName() {
		return loginName;
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
