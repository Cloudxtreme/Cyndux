import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Player {
	private String username;
	private File file;
	private Properties playerProp = new Properties();
	//private Properties ship = new Properties();
	PrintWriter out;

	public Player(String n, PrintWriter o) {
		username = n;
		out = o;
		file = new File("./users/" + this.username + ".properties");
	}

	public String load() {
		if (!file.exists()) {
			return "NOTEXIST";
		} else {
			try {
				FileInputStream input = new FileInputStream(file);
				playerProp.load(input);
				input.close();
				return "OK";
			} catch (IOException e) {
				e.printStackTrace();
				return "FAIL";
			}
		}
	}

	public boolean authenticate(String password) {
		if (!playerProp.getProperty("password").equals(password)) {
			return false;
		} else {
			return true;
		}
	}

	public String getLocation() {
		return playerProp.getProperty("location");
	}

	public void setLocation(int loc) {
		try {
			FileOutputStream output = new FileOutputStream("./users/" + username + ".properties");
			playerProp.setProperty("location", Integer.toString(loc));
			playerProp.store(output, null);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String register(String password) {
		if (file.exists()) {
			return "ALREADYEXIST";
		} else {
			try {
				file.createNewFile();
				FileInputStream input = new FileInputStream("./server.properties");
				Properties prop = new Properties();
				prop.load(input);
				FileOutputStream output = new FileOutputStream(file);
				playerProp.setProperty("money", prop.getProperty("starting.money"));
				playerProp.setProperty("password", password);
				playerProp.setProperty("location", "");
				playerProp.setProperty("ship", prop.getProperty("starting.ship"));
				playerProp.store(output, null);
				output.flush();
				output.close();
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				return "FAIL";
			}
		}
	}

	public String getUsername() {
		return this.username;
	}

	public void send(Object o) {
		out.println(o);
	}
}

