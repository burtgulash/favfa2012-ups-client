import java.util.StringTokenizer;


public class Response {
	private String data;
	private int type;
	private boolean valid = false;
	
	public Response(String msg) {
		if (msg == null)
			return;
		
		StringTokenizer izer = new StringTokenizer(msg);
		
		if (!izer.hasMoreTokens()) {
			valid = false;
			return;
		}
			
		String s = izer.nextToken();
		if ("PRIV_MSG".equals(s)) {
			data = msg.substring("PRIV_MSG ".length());
			type = 1;
		} else if ("ALL_MSG".equals(s)) {
			data = msg.substring("ALL_MSG ".length());
			type = 2;
		} else if ("USERS".equals(s)) {
			data = msg.substring("USERS ".length());
			type = 3;
		} else {
			valid = false;
			return;
		}
		
		valid = true;
	}
	
	boolean isPRIV_MSG() {
		return type == 1;
	}
	
	boolean isALL_MSG() {
		return type == 2;
	}
	
	boolean isUSERS() {
		return type == 3;
	}
	
	String getData() {
		return data;
	}
	
	boolean isValid() {
		return valid;
	}
}
