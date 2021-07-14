package com.mr.gobang;

import jdk.nashorn.internal.scripts.JO;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * ���������߳�
 *
 */
public class ReceiveThread extends Thread {
	public static  ServerSocket chatSocketServer;// �������׽���
	MainFrame frame;// ��������
	private String host;// ���ֵ�ַ��Ϣ
	AI ai;
	private static boolean flag=false;

	public static void setFlag(boolean flag1){
		flag=flag1;
	}
	public static boolean getFlag(){
		return flag;
	}
	/**
	 * �̵߳����巽��
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {

				frame.serverSocket = chatSocketServer.accept(); // ����Socket���ӣ��ȴ������߳�new socket(��Ӧ��port)
				Socket serverSocket = frame.serverSocket;// ��ȡ���ӳɹ���socket
				host = serverSocket.getInetAddress().getHostName(); // ��ȡ�Է�������Ϣ
				String ip = serverSocket.getInetAddress().getHostAddress(); // ��ȡ�Է�IP��ַ
				serverSocket.setOOBInline(true); // ���ý������ݵĽ���
				InputStream is = serverSocket.getInputStream(); // ��ȡ����������
				ObjectInputStream objis = new ObjectInputStream(is);// ��������������
				if (ip.equals("127.0.0.1")) {// ����Ǳ������ӣ�����������AIģʽ
					System.out.println("����Ǳ������ӣ�����������AIģʽ");
					ai = new AI(frame);// ������AI
					while (frame.isVisible()) {
						Object messageObj = objis.readObject(); // �Ӷ�����������ȡJava����
						if (messageObj instanceof byte[][]) { // �����ȡ�����ֽ��������
							if (!frame.getChessPanel1().getGobangPanel1()
									.isWin()) {// ������û�л��ʤ��
								ai.chess();// ����������
							}
						} else if (messageObj instanceof Integer) {// ��������Ͷ���
							ai.oprationHandler(messageObj);// �������Ľ��պʹ�����
						} else if (messageObj instanceof UserBean) {// ������û�ʵ�����
							UserBean user = (UserBean) messageObj;
							frame.setTowardsUser(user); // ���öԼ���Ϣ
						}
					}
				} else {
					if(flag==false){
						int link = JOptionPane.showConfirmDialog(frame, "�յ�" + host
								+ "�����������Ƿ���ܣ�"); // ѯ���Ƿ��������

						if (link == JOptionPane.YES_OPTION) { // �����������
							frame.getChessPanel1().receivedSideDo(new Socket(ip,9527));//���ܷ�������Ϸ��������

						}
					}else{
						frame.send(ChessPanel.OPRATION_START);//����Ƿ��ͷ����յ��������ӵ������ֻ��Ҫ���Ϳ�ʼָ��
					}
					while (frame.isVisible()) {// ��������ǿɼ���
						serverSocket.sendUrgentData(255); // ���ͽ�������,��֤�����Ƿ�ͨ
						Object messageObj = objis.readObject(); // �Ӷ�����������ȡJava����
						if (messageObj instanceof String) { // �����ȡ�Ķ�����String����
							String name = frame.getTowardsUser().getName();// ��ȡ�Լ��ǳ�
							frame.appendMessage(name + "��" + messageObj); // ���ַ�����Ϣ��ӵ�ͨѶ���
						} else if (messageObj instanceof byte[][]) { // �����ȡ�����ֽ�������󣬼���������
							frame.getChessPanel1().getGobangPanel1()
									.pustChessQueue((byte[][]) messageObj);// ���������ݱ�����Ϸ��ֶ����У�������ͻط�ʹ��
							byte[][] newArray=(byte[][]) messageObj;
							for(int i=0;i<GobangModel.getInstance().getChessmanArray().length;i++){//����֮ǰ���������յ����������ҵ����в�ͬ�ĵ�
								for(int j=0;j<GobangModel.getInstance().getChessmanArray().length;j++){
									if(GobangModel.getInstance().getChessmanArray()[i][j]==(newArray[i][j])){
										continue;
									}
									GobangPanel.difPoint=new point(i,j);
									break;
								}
							}
							GobangModel.getInstance().setChessmanArray(
									(byte[][]) messageObj);// �������������Ϊ����ģ������
							frame.getChessPanel1().getGobangPanel1()
									.setTurn(true);// �������Ȩ��
							frame.getChessPanel1().getGobangPanel1()
									.zhengliBoard(); // ��������
							GobangPanel.playVoice();
							frame.getChessPanel1().backButton.setEnabled(true);// ���尴ť����
						} else if (messageObj instanceof Integer) {// ��������ζ���
							oprationHandler(messageObj);// �������Ľ��պʹ�����
						} else if (messageObj instanceof UserBean) {// ������û�ʵ�����
							UserBean user = (UserBean) messageObj;
							frame.setTowardsUser(user); // ���öԼ���Ϣ
							frame.getChessPanel1().chessArrangement();//�����շ��յ�user�� setTowardsUser ֮���ٷ��� ��Ȼ��������ָ���쳣
						}
					}
				}
			} catch (SocketException ex) {
				
				Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE,
						null, ex);// ��¼��־
				JOptionPane.showMessageDialog(frame, "�����ж�");// �����Ի���
				frame.getChessPanel1().reInit();// ���̳�ʼ��
				frame.getChessPanel1().startButton.setText("ƥ��");
				DefaultTableModel model = (DefaultTableModel) frame.userInfoTable
						.getModel();// ��ȡ�û���Ϣ����
				model.setRowCount(0);// ����û��б�
				frame.setSocket(null);
				frame.userInfoTable.revalidate();
				//frame.getGlassPane().setVisible(true);// �ָ���½����
			} catch (Exception ex) {
				Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE,
						null, ex);// ��¼��־
			}
		}
	}

	/**
	 * ���췽��
	 * 
	 * @param chatSocketServer
	 *            - Socket������
	 * @param outer
	 *            - ���������
	 */
	public ReceiveThread(ServerSocket chatSocketServer, MainFrame outer) {
		super();
		this.frame = outer;
		this.chatSocketServer = chatSocketServer;
	}

	/**
	 * ����Զ������ķ���
	 * 
	 * @param messageObj
	 *            - �������
	 */
	private void oprationHandler(Object messageObj) {
		int code = (Integer) messageObj; // ��ȡ�������
		String towards = frame.getTowardsUser().getName();// ��ȡ�Լ��ǳ�
		int option;
		switch (code) {
		case ChessPanel.OPRATION_REPENT: // ����ǻ�������
			System.out.println("�������");
			// ѯ������Ƿ�ͬ��Է�����
			option = JOptionPane.showConfirmDialog(frame,
					towards + "Ҫ���壬�Ƿ�ͬ�⣿", "�����ˣ����ߴ��ˣ����һ��壡����",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			// �����������ӻ�����Ϣ
			frame.appendMessage("�Է��������.......");
			if (option == JOptionPane.YES_OPTION) { // ���ͬ�����
				frame.send(ChessPanel.OPRATION_NODE_REPENT);// ����ͬ��������Ϣ
				frame.getChessPanel1().repentOperation();// ִ�б��صĻ������
				frame.appendMessage("���ܶԷ��Ļ�������");// ��ӻ�����Ϣ���������
				frame.send(frame.getUser().getName() + "���ܻ�������");
			} else { // �����ͬ�����
				// ��Ӳ�ͬ��������Ϣ���������
				frame.send(frame.getUser().getName() + "�ܾ���������");
				frame.appendMessage("�ܾ��˶Է��Ļ�������");
			}
			break;
		case ChessPanel.OPRATION_NODE_REPENT: // �����ͬ���������
			System.out.println("ͬ���������");
			frame.getChessPanel1().repentOperation(); // ִ�б��صĻ������
			frame.appendMessage("����ɹ�"); // �ѻ���ɹ���Ϣ��ӵ��������
			break;
		case ChessPanel.OPRATION_NODE_DRAW: // �����ͬ���������
			System.out.println("ͬ���������");
			// ���ú���״̬Ϊtrue
			frame.getChessPanel1().getGobangPanel1().setDraw(true);
			frame.getChessPanel1().reInit(); // ��ʼ����Ϸ״̬����
			frame.appendMessage("��սƽ�֡�"); // ��������Ϣ��ӵ��������
			break;
		case ChessPanel.OPRATION_DRAW: // ����Ǻ�������
			System.out.println("�������");
			// ѯ������Ƿ�ͬ�����
			option = JOptionPane.showConfirmDialog(frame, towards
					+ "������壬�Ƿ�ͬ�⣿", "��磬����ɣ�����", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			frame.appendMessage("�Է��������......."); // �����Ϣ���������
			if (option == JOptionPane.YES_OPTION) { // ���ͬ�����
				frame.send(ChessPanel.OPRATION_NODE_DRAW);// ���ͽ��ܺ������Ϣ
				// ���ú���״̬Ϊtrue
				frame.getChessPanel1().getGobangPanel1().setDraw(true);
				frame.getChessPanel1().reInit(); // ��ʼ����Ϸ״̬����
				frame.appendMessage("���ܶԷ��ĺ�������"); // �����Ϣ���������
				frame.send(frame.getUser().getName() + "���ܺ�������");
			} else { // �����ͬ�����
				// ���;ܾ���Ϣ
				frame.send(frame.getUser().getName() + "�ܾ���������");
				frame.appendMessage("�ܾ��˶Է��ĺ�������");
			}
			break;
		case ChessPanel.OPRATION_GIVEUP: // ����ǶԷ����������
			System.out.println("�Է�����");
			// ѯ������Ƿ�ͬ��Է�����
			option = JOptionPane.showConfirmDialog(frame, towards
					+ "�������䣬�Ƿ�ͬ�⣿", "�Է�����", JOptionPane.YES_NO_OPTION);
			frame.appendMessage("�Է���������.......");
			if (option == JOptionPane.YES_OPTION) { // ���ͬ��Է�����
				frame.send(ChessPanel.WIN);// ����ʤ����Ϣ
				// ����ʤ��״̬Ϊtrue
				frame.getChessPanel1().getGobangPanel1().setWin(true);
				frame.getChessPanel1().reInit(); // ��ʼ����Ϸ��״̬����
				frame.appendMessage("���ܶԷ�����������");
			} else {
				frame.send(frame.getUser().getName() + "�ܾ���������");
				frame.appendMessage("�ܾ��˶Է�����������");
			}
			break;
		case ChessPanel.OPRATION_START: // ����ǿ�ʼ��Ϸ������
			System.out.println("����ʼ");
			// ����Լ��Ѿ�ִ����Ϸ��ʼ����
			if (frame.getChessPanel1().getGobangPanel1().isStart()) {
				frame.send((int) ChessPanel.OPRATION_ALL_START); // ����ȫ����ʼ����
				frame.getChessPanel1().setTowardsStart(true); // ���öԼ���Ϸ��ʼ״̬Ϊtrue
			}
			break;

		case ChessPanel.OPRATION_ALL_START: // ����ǻ�Ӧ��ʼ����
			System.out.println("��Ӧ��ʼ����");
			frame.getChessPanel1().setTowardsStart(true); // ���öԼ�Ϊ��ʼ״̬
			break;
		case ChessPanel.WIN: // �����ʤ�����������
			System.out.println("�Է�ʤ��");
			// ���öԼ�ʤ��״̬Ϊtrue
			frame.getChessPanel1().getGobangPanel1().setTowardsWin(true);
			frame.getChessPanel1().reInit(); // ��ʼ����Ϸ״̬����
			break;
		default:
			System.out.println("δ֪�������룺" + code);
		}
	}
}
