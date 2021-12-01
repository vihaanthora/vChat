import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;

public class SocketClientUI extends JFrame implements Runnable, ActionListener {
	private static final long serialVersionUID = 1L;
	JLabel lblIP = new JLabel();
	JTextField txtIP = new JTextField();
	JLabel lblPort = new JLabel();
	JTextField txtPort = new JTextField();
	JLabel lblUID = new JLabel();
	JTextField txtUID = new JTextField();
	JButton btnConnect = new JButton();
	JLabel lblChatRoom = new JLabel();
	JPanel paneConn = new JPanel(null);
	JTextArea txtChat = new JTextArea();
	JLabel lblMsg = new JLabel();
	JTextField txtMsg = new JTextField();
	JScrollPane scrollChat = new JScrollPane(txtChat);

	Socket s = null;
	DataOutputStream dout = null;

	public void connectSocket(String ip, int port, String name) {

		try {
			s = new Socket(ip, port);
			dout = new DataOutputStream(s.getOutputStream());
			dout.writeUTF(name);
			dout.flush();
			txtMsg.setEnabled(true);
			txtMsg.requestFocus();
			Thread t = new Thread(this);
			t.start();

		} catch (Exception e) {
			System.out.println("Connection refused.. please try again");
			JOptionPane.showMessageDialog(this, "Please enter correct IP and Port number");
			txtUID.setEnabled(true);
			btnConnect.setEnabled(true);
		}

	}

	public void sendMessage(String message) {
		try {
			dout.writeUTF(message);
			dout.flush();
			txtMsg.setText(null);
			txtMsg.requestFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// sendMesssage

	public void run() {
		try {
			DataInputStream dis = new DataInputStream(s.getInputStream());
			while (true) {
				String str = dis.readUTF();
				txtChat.append(str + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initUI() {

		lblIP.setBounds(10, 20, 100, 25);
		lblIP.setText("IP Address :");
		paneConn.add(lblIP);

		txtIP.setBounds(10, 40, 200, 25);
		txtIP.setText("192.168.120.161");
		paneConn.add(txtIP);

		lblPort.setBounds(220, 20, 100, 25);
		lblPort.setText("Port Number :");
		paneConn.add(lblPort);

		txtPort.setBounds(220, 40, 90, 25);
		txtPort.setText("6666");
		paneConn.add(txtPort);

		lblUID.setBounds(320, 20, 100, 25);
		lblUID.setText("User ID :");
		paneConn.add(lblUID);

		txtUID.setActionCommand("connect");
		txtUID.addActionListener(this);
		txtUID.setBounds(320, 40, 90, 25);
		paneConn.add(txtUID);

		btnConnect.setActionCommand("connect");
		btnConnect.addActionListener(this);
		btnConnect.setBounds(420, 40, 100, 25);
		btnConnect.setText("Connect");
		paneConn.add(btnConnect);

		paneConn.setBounds(25, 10, 530, 85);
		paneConn.setBorder(new TitledBorder("Connection Details"));
		this.add(paneConn);

		lblChatRoom.setBounds(25, 95, 100, 25);
		lblChatRoom.setText("Chat Room - ");
		this.add(lblChatRoom);

		txtChat.setEditable(false);
		scrollChat.setBounds(25, 125, 530, 200);
		scrollChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollChat);

		lblMsg.setBounds(25, 330, 100, 25);
		lblMsg.setText("Your Message :");
		this.add(lblMsg);

		txtMsg.setActionCommand("enter");
		txtMsg.setBounds(25, 360, 530, 25);
		txtMsg.addActionListener(this);
		txtMsg.setEnabled(false);
		this.add(txtMsg);

		this.setSize(600, 440);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setLocation(400, 100);
		this.setVisible(true);
		txtUID.requestFocus();
	}

	public void actionPerformed(ActionEvent evt) {
		String command = evt.getActionCommand();
		if (command.equals("connect")) {
			String ip = txtIP.getText().trim();
			int port = Integer.parseInt(txtPort.getText().trim());
			String name = txtUID.getText().trim();
			if (name.equals("")) {
				JOptionPane.showMessageDialog(this, "Enter a user id to proceed");
			} else {
				txtUID.setEnabled(false);
				btnConnect.setEnabled(false);

				connectSocket(ip, port, name);
			}

		} else if (command.equals("enter")) {
			this.sendMessage(txtMsg.getText());
		}

	}

	public static void main(String[] args) {
		SocketClientUI sc = new SocketClientUI();
		sc.initUI();

	}// main
}// SocketClient
