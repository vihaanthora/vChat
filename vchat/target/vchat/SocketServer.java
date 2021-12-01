import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
	ServerSocket ss = null;
	HashMap<String, DataOutputStream> userData = new HashMap<String, DataOutputStream>();
	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	
	public void startServer() {
		try {
			ss = new ServerSocket(6666);
			System.out.println("Server started and ready to accept connections...");
			while (true) {
				final Socket s = ss.accept();
				new Thread(new Runnable() {
					public void run() {
						DataOutputStream dout = null;
						DataInputStream dis = null;
						String userName = "";
						try {
							dis = new DataInputStream(s.getInputStream());
							dout = new DataOutputStream(s.getOutputStream());
							userName = dis.readUTF();
							userData.put(userName, dout);
							String date = format.format(new Date());
							System.out.println("Client added - " + userName);
							broadcast(" [" + date + "] - "+userName + " Joined ! ");
							while (true) {
								String message = dis.readUTF();
								date = format.format(new Date());
								broadcast(" [" + date + "] - "+userName +" : "+ message);
							} // while
						} catch (Exception e) {
							try {
								dout.close();
								dis.close();
								userData.remove(userName);
								s.close();
								System.out.println("Client left - " + userName);
							} catch (Exception e2) {
								System.out.println("Could not close client socket " + e.getMessage());
							}
						} // catch

					}// run
				}).start();
			} // while

		} catch (Exception e) {
			e.printStackTrace();
		}
	}// acceptConnection

	public void broadcast(String message) {

		for (Map.Entry<String, DataOutputStream> user : userData.entrySet()) {
			try {
				DataOutputStream dout = user.getValue();
				dout.writeUTF(message);
				dout.flush();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static void main(String[] args) {
		SocketServer ss = new SocketServer();
		ss.startServer();

	}// main

}// class
