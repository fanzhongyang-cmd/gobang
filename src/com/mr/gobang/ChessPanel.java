package com.mr.gobang;

//import com.mysql.cj.protocol.Resultset;



import sun.applet.Main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


/**
 * �������
 * 
 */
class point{
	int x_index;
	int y_index;
	point(int x,int y){
		this.x_index=x;
		this.y_index=y;
	}
}
public class ChessPanel extends javax.swing.JPanel {
	static ImageIcon WHITE_CHESS_ICON;// ��ɫ����ͼƬ
	static ImageIcon BLACK_CHESS_ICON;// ��ɫ����ͼƬ
	final static int OPRATION_REPENT = 0xEF; // ��������
	final static int OPRATION_NODE_REPENT = 0xCF; // ���ܻ�������
	final static int OPRATION_DRAW = 0xFE; // ��������
	final static int OPRATION_NODE_DRAW = 0xEE; // ���ܺ�������
	final static int OPRATION_START = 0xFd; // ��ʼ����
	final static int OPRATION_ALL_START = 0xEd; // ���ܿ�ʼ����
	final static int OPRATION_GIVEUP = 0xFc; // ��������
	final static int OPRATION_START_MACHINE = 0xBd; // ��ʼ�˻���ս����
	final static int WIN = 88; // ʤ������
	private boolean towardsStart = false;// �Է��Ƿ�ʼ
	private Image backImg;// ����ͼƬ
	protected JButton backButton;// ���尴ť
	private JToggleButton backplayToggleButton;// �طŰ�ť
	private JLabel bannerLabel;// ����ǩ
	private JButton giveupButton;// �����ǩ
	private GobangPanel gobangPanel1;// ����������
	private JButton heqiButton;// ���尴ť
	private JLabel jLabel5;// ��������ռλ��ǩ
	private JLabel jLabel6;// ��������ռλ��ǩ
	private JPanel jPanel1;// �·���ť���
	private JPanel jPanel2;// ����ҵ���Ϣ���
	private JPanel jPanel3;// �Ҳ�Է���Ϣ���
	private JPanel jPanel4;// �Ϸ�������
	protected JLabel leftInfoLabel;// ���ͷ��
	protected JLabel myChessColorLabel;// �ҵ�������ɫͼƬ
	protected JLabel rightInfoLabel;// �Ҳ�ͷ��
	protected JButton startButton;// ��ʼ��ť
	protected JLabel towardsChessColorLabel;// �Է�������ɫͼƬ
	int backIndex = 1;// ����ͼƬ����

	protected JButton musicButton;

	protected JButton backToLogin;
	/**
	 * �������Ĺ��췽��
	 */
	public ChessPanel() {
		WHITE_CHESS_ICON = new javax.swing.ImageIcon(getClass().getResource(
				"/res/whiteChess.png")); // ��ʼ���������ͼƬ
		BLACK_CHESS_ICON = new javax.swing.ImageIcon(getClass().getResource(
				"/res/blackChess.png")); // ��ʼ���������ͼƬ
		URL url = getClass().getResource("/res/bg/1.jpg");// ��ȡsrc·����ͼƬ
		backImg = new ImageIcon(url).getImage(); // ��ʼ������ͼƬ
		initComponents(); // ���ó�ʼ������ķ���

	}

	/**
	 * ��дpaintComponent���������Ʊ���ͼƬ
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// ���Ʊ���ͼƬ
		g.drawImage(backImg, 0, 0, getWidth(), getHeight(), null);
	}

	/**
	 * ����������ɫ�ķ������������ɫΪ��
	 * 
	 * @param color
	 *            - ָ����ɫ�����ͼƬ
	 */
	public void setChessColor(ImageIcon color) {
		myChessColorLabel.setIcon(color); // ���ñ����û������ͼ��
		if (color.equals(WHITE_CHESS_ICON)) { // �������Ϊ����
			gobangPanel1.setMyColor(GobangModel.WHITE_CHESSMAN);// ��ʹ�ð���
			towardsChessColorLabel.setIcon(BLACK_CHESS_ICON);// ����ʹ�ú���
		} else if (color.equals(BLACK_CHESS_ICON)) {// //�������Ϊ����
			gobangPanel1.setMyColor(GobangModel.BLACK_CHESSMAN);// ��ʹ�ú���
			towardsChessColorLabel.setIcon(WHITE_CHESS_ICON);// ����ʹ�ð���
		}
		revalidate();// �Զ���֤������������
	}

	/**
	 * �����ֻ�״̬�ķ���
	 * 
	 * @param turn
	 *            - �Ƿ�������Ȩ��
	 */
	public void setTurn(boolean turn) {
		if (turn) { // ����������Ȩ��
			myChessColorLabel.setVisible(true); // ��ʾ���
			towardsChessColorLabel.setVisible(false); // ���ضԼ����
		} else {// ����
			myChessColorLabel.setVisible(false); // �����Լ������
			towardsChessColorLabel.setVisible(true); // ��ʾ�Լҵ����
		}
	}

	/**
	 * �����ҵ������
	 */
	public synchronized void repentOperation() {
		// ��ȡ�������
		Deque<byte[][]> chessQueue = gobangPanel1.getChessQueue();
		if (chessQueue.isEmpty()) {// �����������ֵ
			return;
		}
		// ��ǰ�����ǰ��2��
		for (int i = 0; i < 2 && !chessQueue.isEmpty(); i++) {
			chessQueue.pop(); // �������岽��
		}
		if (chessQueue.size() < 1) {// ���������û���κ�ֵ��
			chessQueue.push(new byte[15][15]);// ��һ���µ����ݣ������̣����������
		}
		byte[][] pop = chessQueue.peek();// ��ȡ�����������ϵ���������
		GobangModel.getInstance().setChessmanArray(pop);// �������̵����Ӳ���
		repaint();// ���»��ƽ���
	}

	/**
	 * ��������
	 * 
	 * @param opration
	 *            -Ҫ���͵�����
	 */
	public void send(Object opration) {
		MainFrame mainFrame = (MainFrame) getRootPane().getParent();// ��ȡ���Ĵ������
		mainFrame.send(opration); // ��������

	}

	/**
	 * ���³�ʼ����Ϸ״̬
	 */
	void reInit() {
		gobangPanel1.oldRec();// ��¼��Ϸ����ǰ�����̼�¼
		backToLogin.setEnabled(true);
		startButton.setEnabled(true);// ��ʼ��ť����
		giveupButton.setEnabled(false);// ���䲻����
		heqiButton.setEnabled(false);// ���尴ť������
		backButton.setEnabled(false);// ���尴ť������
		gobangPanel1.setStart(false);// ������ϷΪδ��ʼ״̬
		setTowardsStart(false);// ���ƶԷ�Ϊδ��ʼ״̬
	}

	/**
	 * Ϊ˫����ҷ������ӵķ���
	 */
	private void fenqi() {
		MainFrame frame = (MainFrame) getRootPane().getParent(); // ��ȡ���������

		if (LoginPanel.isManMachineWar) {// ������˻���ս״̬
			frame.getChessPanel1().setChessColor(ChessPanel.WHITE_CHESS_ICON);// ��ҹ̶�ʹ�ð���
			frame.getChessPanel1().getGobangPanel1().setTurn(true);// �������
		} else {// �������Ҷ�ս״̬
			int myRank=frame.getUser().getRank();
			int towardsRank=frame.getTowardsUser().getRank();
			if (myRank < towardsRank) {
				frame.getChessPanel1().setChessColor(
						ChessPanel.WHITE_CHESS_ICON);// ��ʹ�ð���
				frame.getChessPanel1().getGobangPanel1().setTurn(true);// �ֵ���������
			} else if(myRank>towardsRank){
				frame.getChessPanel1().setChessColor(
						ChessPanel.BLACK_CHESS_ICON);// ��ʹ�ú���
				frame.getChessPanel1().getGobangPanel1().setTurn(false);// û�ֵ�������
			}else{//��rankֵ��ͬʱ �������
				double me= Math.random();
				double to=Math.random();
				if(me<to){
					frame.getChessPanel1().setChessColor(
							ChessPanel.WHITE_CHESS_ICON);// ��ʹ�ð���
					frame.getChessPanel1().getGobangPanel1().setTurn(true);// �ֵ���������
				}else{
					frame.getChessPanel1().setChessColor(
							ChessPanel.BLACK_CHESS_ICON);// ��ʹ�ú���
					frame.getChessPanel1().getGobangPanel1().setTurn(false);// û�ֵ�������
				}
			}
		}
	}

	/**
	 * ���� ������̵ķ���������ʹ��1��-1�ƶ�������̵����ӣ�ʹ��0�������
	 * 
	 * @param chessman
	 *            - ������̵����ӵ���ɫ����
	 */
	private void fillChessBoard(final byte chessman) {
		try {
			Runnable runnable = new Runnable() { // ���������Ķ����߳�
				/**
				 * �̵߳����巽��
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					byte[][] chessmanArray = GobangModel.getInstance()
							.getChessmanArray(); // ��ȡ��������
					for (int i = 0; i < chessmanArray.length; i += 2) {
						try {
							Thread.sleep(10); // �������ʱ��
						} catch (InterruptedException ex) {
							Logger.getLogger(ChessPanel.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// ʹ��ָ����ɫ��������������һ��
						Arrays.fill(chessmanArray[i], chessman);// ���ż����
						Arrays.fill(chessmanArray[(i + 1) % 15], chessman);// ���������
						GobangModel.getInstance().setChessmanArray(
								chessmanArray); // ���������ϵ�����
						gobangPanel1.paintImmediately(0, 0, getWidth(),
								getHeight()); // �����ػ�ָ�����������
					}
				}
			};
			// // ���¼�������ִ������
			if (SwingUtilities.isEventDispatchThread()) {// ����ǵ�ǰ������߳�
				runnable.run();// �߳�ֱ��ִ��
			} else {
				SwingUtilities.invokeAndWait(runnable);// ָ�ɸ��̵߳ȴ�ִ��
			}
		} catch (Exception ex) {
			Logger.getLogger(ChessPanel.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * ��ʼ���������ķ�����Ϊ�������ʵ����������ӽ���Ĳ��ֵ�Ч��
	 */
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel1.setOpaque(false);
		backButton = new javax.swing.JButton();
		heqiButton = new javax.swing.JButton();
		startButton = new javax.swing.JButton();
		giveupButton = new javax.swing.JButton();
		backplayToggleButton = new javax.swing.JToggleButton();
		//musicButton=new JRadioButton();
		musicButton=new JButton("������");

		jPanel2 = new javax.swing.JPanel();
		jPanel2.setOpaque(false);
		jLabel5 = new javax.swing.JLabel();
		leftInfoLabel = new javax.swing.JLabel();
		leftInfoLabel.setForeground(new Color(0, 255, 0));
		leftInfoLabel.setFont(new Font("����", Font.PLAIN, 22));
		myChessColorLabel = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jPanel3.setOpaque(false);
		jLabel6 = new javax.swing.JLabel();
		rightInfoLabel = new javax.swing.JLabel();
		rightInfoLabel.setForeground(Color.GREEN);
		rightInfoLabel.setFont(new Font("����", Font.PLAIN, 22));
		towardsChessColorLabel = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();
		jPanel4.setOpaque(false);
		bannerLabel = new javax.swing.JLabel();
		gobangPanel1 = new com.mr.gobang.GobangPanel();

		setLayout(new java.awt.BorderLayout());
		setOpaque(false);
		backToLogin=new JButton("���ص�¼����");
		backToLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backToLoginActionPerformed(e);
			}
		});
		jPanel1.add(backToLogin);
		backButton.setText("����");
		backButton.setEnabled(false);
		backButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});
		jPanel1.add(backButton);

		heqiButton.setText("����");
		heqiButton.setEnabled(false);
		heqiButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				heqiButtonActionPerformed(evt);
			}
		});
		jPanel1.add(heqiButton);

		startButton.setText("ƥ��");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});
		jPanel1.add(startButton);

		giveupButton.setText("����");
		giveupButton.setEnabled(false);
		giveupButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				giveupButtonActionPerformed(evt);
			}
		});
		jPanel1.add(giveupButton);

		backplayToggleButton.setText("��Ϸ�ط�");
		backplayToggleButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						backplayToggleButtonActionPerformed(evt);
					}
				});
		jPanel1.add(backplayToggleButton);

		add(jPanel1, java.awt.BorderLayout.PAGE_END);

		final JButton button = new JButton();
		button.addActionListener(new ButtonActionListener());
		button.setText("��������");
		jPanel1.add(button);

		//musicButton.setText("���ֿ�/��");
		musicButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				musicButtonActionperformed(e);
			}
		});
		jPanel1.add(musicButton);
		jPanel2.setPreferredSize(new java.awt.Dimension(110, 100));
		jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,
				50, 60));

		jLabel5.setPreferredSize(new java.awt.Dimension(42, 55));
		jPanel2.add(jLabel5);

		leftInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/res/infoPanelLeft.png")));
		leftInfoLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		leftInfoLabel
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jPanel2.add(leftInfoLabel);

		myChessColorLabel.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/res/whiteChess.png")));
		jPanel2.add(myChessColorLabel);

		add(jPanel2, java.awt.BorderLayout.LINE_START);

		jPanel3.setPreferredSize(new java.awt.Dimension(110, 100));
		jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER,
				50, 60));

		jLabel6.setPreferredSize(new java.awt.Dimension(42, 55));
		jPanel3.add(jLabel6);

		rightInfoLabel.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/res/infoPanel.png")));
		rightInfoLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		rightInfoLabel
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jPanel3.add(rightInfoLabel);

		towardsChessColorLabel.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/res/blackChess.png"))); // NOI18N
		jPanel3.add(towardsChessColorLabel);


		add(jPanel3, java.awt.BorderLayout.LINE_END);

		jPanel4.setLayout(new java.awt.BorderLayout());

		bannerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		bannerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/res/logo.png"))); // NOI18N
		bannerLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1,
				5, 1));
		bannerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		bannerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				bannerLabelMouseClicked(evt);
			}
		});
		jPanel4.add(bannerLabel, java.awt.BorderLayout.CENTER);

		add(jPanel4, java.awt.BorderLayout.PAGE_START);

		add(gobangPanel1, java.awt.BorderLayout.CENTER);

		javax.swing.GroupLayout gobangPanel1Layout = new javax.swing.GroupLayout(
				gobangPanel1);
		gobangPanel1Layout.setHorizontalGroup(gobangPanel1Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0,
						280, Short.MAX_VALUE));
		gobangPanel1Layout.setVerticalGroup(gobangPanel1Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0,
						248, Short.MAX_VALUE));
		gobangPanel1.setLayout(gobangPanel1Layout);
	}

	/**
	 * ��ʼ��ť���¼�������
	 * 
	 * @param evt
	 *            - �¼�����
	 */
	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (LoginPanel.isManMachineWar){//������˻���ս
			backToLogin.setEnabled(false);
			startButton.setEnabled(false);// ��ʼ��ť������
			gobangPanel1.setStart(true); // ������Ϸ�Ŀ�ʼ״̬Ϊ��ʼ
			gobangPanel1.setTowardsWin(false); // ���öԼ�ʤ��״̬
			gobangPanel1.setWin(false); // �����Լ�ʤ��״̬
			gobangPanel1.setDraw(false); // ���ú���״̬
			send(OPRATION_START_MACHINE);// ���Ϳ�ʼ�˻���սָ��
			giveupButton.setEnabled(false);// �˻���ս����ʹ�����䰴ť
			heqiButton.setEnabled(false);// �˻���ս����ʹ�ú��尴ť
			backButton.setEnabled(false);// �˻���ս����ʹ�û��尴ť

			fenqi(); // ����˫������

			fillChessBoard(gobangPanel1.getMyColor());// ʹ���Լ���������ɫ����
			fillChessBoard((byte) 0); // ʹ�ÿ���������
			byte[][] data = new byte[15][15]; // ����һ���յ����̲���
			GobangModel.getInstance().setChessmanArray(data);// ��������ʹ�ÿղ���
			return;
		}
		try{

			MainFrame mainFrame = (MainFrame) getRootPane().getParent();
			if(mainFrame.getSocket()!=null){//��������ŵ� ��ʾ���Ǻ�����ʼ���µ�һ��
				startButton.setEnabled(false);// ��ʼ��ť������
				gobangPanel1.setStart(true); // ������Ϸ�Ŀ�ʼ״̬Ϊ��ʼ
				gobangPanel1.setTowardsWin(false); // ���öԼ�ʤ��״̬
				gobangPanel1.setWin(false); // �����Լ�ʤ��״̬
				gobangPanel1.setDraw(false); // ���ú���״̬
				repaint();
				//���ͷ���ϵ�ready��Ϣ
				//-----------------------//
				send(OPRATION_START);// ���Ϳ�ʼָ��
				giveupButton.setEnabled(true);// ��Ҷ�ս��ʹ�����䰴ť
				heqiButton.setEnabled(true);// ��Ҷ�ս��ʹ�ú��尴ť
				backButton.setEnabled(true);// ��Ҷ�ս��ʹ�û��尴ť

				//fenqi(); // ����˫������

				fillChessBoard(gobangPanel1.getMyColor());// ʹ���Լ���������ɫ����
				fillChessBoard((byte) 0); // ʹ�ÿ���������
				byte[][] data = new byte[15][15]; // ����һ���յ����̲���
				GobangModel.getInstance().setChessmanArray(data);// ��������ʹ�ÿղ���
				return;
			}
			Connection con=null;
			Statement stmt=null;
			ResultSet rs=null;
			try{
				Class.forName("com.mysql.cj.jdbc.Driver");
				//JOptionPane.showMessageDialog(this,"�������سɹ�");

			}
			catch(Exception exw){
				//JOptionPane.showMessageDialog(this,"�������쳣");
				//JOptionPane.showMessageDialog(this,"��������ʧ��");
				//return;
				Logger.getLogger(ChessPanel.class.getName()).log(
						Level.SEVERE, null, exw);
			}
			try{
				con = DriverManager.getConnection("jdbc:mysql://39.106.59.135/gobang?useSSL=FALSE&serverTimezone=UTC","root", "fzy125..");
				//JOptionPane.showMessageDialog(this,"���������ӳɹ�");

			}
			catch(Exception exy){
				//System.out.println("���ݿ�����ʧ��");
				//JOptionPane.showMessageDialog(this,"����������ʧ��");
				//JOptionPane.showMessageDialog(this,"�������쳣");
				//return;
				Logger.getLogger(ChessPanel.class.getName()).log(
						Level.SEVERE, null, exy);

			}
			UserBean user =LoginPanel.user;
			String myId= user.getUserID();
			String myIp= user.getHost().getHostAddress();
			int myRank=user.getRank();
			stmt=con.createStatement();
			//ƥ��
			String sql2 = "SELECT\n" +
					"\t*\n" +
					"FROM\n" +
					"\tmatchqueue AS m1\n" +
					"WHERE\n" +
					"\t(m1.mark=1) and (m1.userrank-100 < '" + myRank + "') and ('" + myRank + "'-m1.userrank<100) and (m1.ip!='" + myIp + "')";//ƥ������
			try {
				rs= stmt.executeQuery(sql2);
			}catch(SQLException ex){
				Logger.getLogger(ChessPanel.class.getName()).log(
						Level.SEVERE, null, ex);
				try{
					sql2="select * from matchqueue where ip!='"+myIp+"'";
					rs=stmt.executeQuery(sql2);
				}catch (SQLException ex2){
					//ex2.printStackTrace();
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex2);
				}
			}


			rs.next();
			String ipText=rs.getString("ip"); // ��ȡ�Լ�IP��ַ
			int port=rs.getInt("port");
			String id=rs.getString("id");
			JOptionPane.showMessageDialog(this,ipText+"--"+port);

			InetAddress ip = InetAddress.getByName(ipText);// ��ȡ�ĵ�ַ����
			startButton.setEnabled(false);
			ReceiveThread.setFlag(true);//�޸ı�־λ��ʾ���߳��Ƿ���ƥ����߳�

			Socket socket = new Socket(ip, port); // ����Socket���ӶԼ�����

			if(!socket.isConnected()){//�������ʧ�� �ͷ���
				JOptionPane.showMessageDialog(this,"����ʧ�ܣ������� -00");
				startButton.setEnabled(true);
				return;
			}
			//�Ӷ����и��¶���
			String sql3="update matchqueue set mark='"+0+"'where ip='"+myIp+"' or ip='"+ip+"'";//�����Ҹ�ƥ�䵽�Ķ������ݵı�־ ��ֹ���������˸��ҽ�������
			stmt.execute(sql3);
			socket.setOOBInline(true); // ���ý������ݵĽ���
			mainFrame.setSocket(socket); // �����������Socket���Ӷ���
			mainFrame.send(user); // ���ͱ����û����󵽶Լ�����
			String sql4="select * from userinfo where userid ='"+id+"'";
			ResultSet rs4=stmt.executeQuery(sql4);
			rs4.next();
			MainFrame frame = (MainFrame) getRootPane().getParent(); // ��ȡ���������

			//���ö���  ƥ�䷢�����ö����Ǹ������ݿ����ʵ�ֵ�  ����ƥ����ܷ������send�Ķ����������
			UserBean towardsUser=new UserBean();
			towardsUser.setRank(rs4.getInt("userrank"));
			towardsUser.setName(rs4.getString("cname"));
			towardsUser.setWin_count(rs4.getInt("wincount"));
			towardsUser.setHost(ip);
			towardsUser.setUserID(id);
			frame.setTowardsUser(towardsUser);
			// ���ø�����ť�Ŀ���״̬
			//startButton.setText("��ʼ");
			startButton.setEnabled(false);// ��ʼ��ť������
			gobangPanel1.setStart(true); // ������Ϸ�Ŀ�ʼ״̬Ϊ��ʼ
			gobangPanel1.setTowardsWin(false); // ���öԼ�ʤ��״̬
			gobangPanel1.setWin(false); // �����Լ�ʤ��״̬
			gobangPanel1.setDraw(false); // ���ú���״̬

			send(OPRATION_START);// ���Ϳ�ʼָ��
			giveupButton.setEnabled(true);// ��Ҷ�ս��ʹ�����䰴ť
			heqiButton.setEnabled(true);// ��Ҷ�ս��ʹ�ú��尴ť
			backButton.setEnabled(true);// ��Ҷ�ս��ʹ�û��尴ť

			fenqi(); // ����˫������

			fillChessBoard(gobangPanel1.getMyColor());// ʹ���Լ���������ɫ����
			fillChessBoard((byte) 0); // ʹ�ÿ���������
			byte[][] data = new byte[15][15]; // ����һ���յ����̲���
			GobangModel.getInstance().setChessmanArray(data);// ��������ʹ�ÿղ���
		}catch (UnknownHostException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"ƥ��ʧ�ܣ������� -01");
			startButton.setEnabled(true);
		}catch (IOException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"ƥ��ʧ�ܣ������� -02");
			startButton.setEnabled(true);
		}catch (SQLException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"ƥ��ʧ�ܣ������� -03");
			startButton.setEnabled(true);

		}
	}

	public void receivedSideDo(Socket socket){
		try{
			MainFrame mainFrame = (MainFrame) getRootPane().getParent();
			socket.setOOBInline(true); // ���ý������ݵĽ���
			mainFrame.setSocket(socket); // �����������Socket���Ӷ���
			//startButton.setText("��ʼ");
			backToLogin.setEnabled(false);
			startButton.setEnabled(false);// ��ʼ��ť������
			gobangPanel1.setStart(true); // ������Ϸ�Ŀ�ʼ״̬Ϊ��ʼ
			gobangPanel1.setTowardsWin(false); // ���öԼ�ʤ��״̬
			gobangPanel1.setWin(false); // �����Լ�ʤ��״̬
			gobangPanel1.setDraw(false); // ���ú���״̬

			giveupButton.setEnabled(true);// ��Ҷ�ս��ʹ�����䰴ť
			heqiButton.setEnabled(true);// ��Ҷ�ս��ʹ�ú��尴ť
			backButton.setEnabled(true);// ��Ҷ�ս��ʹ�û��尴ť

			//fenqi(); // ����˫������

			fillChessBoard(gobangPanel1.getMyColor());// ʹ���Լ���������ɫ����
			fillChessBoard((byte) 0); // ʹ�ÿ���������
			byte[][] data = new byte[15][15]; // ����һ���յ����̲���
			GobangModel.getInstance().setChessmanArray(data);// ��������ʹ�ÿղ���
		}catch (Exception e){
			Logger.getLogger(ChessPanel.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	public void chessArrangement(){
		fenqi();
	}
	/**
	 * ���䰴ť���¼�������
	 * 
	 * @param evt
	 *            - ��ť���¼�����
	 */
	private void giveupButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// ���û���Լ����壬��ʾ�û��ȴ�
		if (!gobangPanel1.isTurn()) {
			JOptionPane.showMessageDialog(this, "û���������ء�", "��ȴ�...",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		send(OPRATION_GIVEUP);// ��������ָ��
		// ����һ���µ��߳�ʹ���䰴ť5�벻����
		new Thread() {
			@Override
			public void run() {
				try {
					giveupButton.setEnabled(false);// ������
					sleep(5000);
					giveupButton.setEnabled(true);// ����
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// �߳�����
	}

	/**
	 * ���尴ť���¼�������
	 * 
	 * @param evt
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// ���û���Լ�����
		if (!gobangPanel1.isTurn()) {
			JOptionPane.showMessageDialog(this, "û���������ء�", "��ȴ�...",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		send(OPRATION_REPENT);// ���ͻ�������
		new Thread() { // �����µ��̣߳�ʹ���尴ť����5��
			@Override
			public void run() {
				try {
					backButton.setEnabled(false);// ������
					sleep(5000);
					backButton.setEnabled(true);// ����
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// �����߳�
	}

	/**
	 * ���尴ť���¼�������
	 * 
	 * @param evt
	 *            - ��ť��action�¼�����
	 */
	private void heqiButtonActionPerformed(java.awt.event.ActionEvent evt) {
		send(OPRATION_DRAW);// ���ͺ���ָ��
		new Thread() { // �����µ��߳�ʹ���尴ť5�벻����
			public void run() {
				try {
					heqiButton.setEnabled(false);// ������
					sleep(5000);
					heqiButton.setEnabled(true);// ����
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// �����߳�
	}

	/**
	 * ���ͼƬ����굥���¼�������
	 * 
	 * @param evt
	 *            - ����¼�����
	 */
	private void bannerLabelMouseClicked(java.awt.event.MouseEvent evt) {
		try {
			// ����Desktop���browse���������̴ʵ���ҳ
			if (Desktop.isDesktopSupported()) {// �����ǰƽ̨֧�ִ���
				Desktop.getDesktop().browse(// ����Ĭ����������Ӵ˵�ַ
						new URL("http://www.cse.cqu.edu.cn/").toURI());
			} else {
				// �����Ի���
				JOptionPane.showMessageDialog(this, "��ǰϵͳ��֧�ָò���");
			}
		} catch (Exception ex) {
			Logger.getLogger(ChessPanel.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * ��Ϸ�طŰ�ť���¼�������
	 * 
	 * @param evt
	 *            - �¼�����
	 */
	private void backplayToggleButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (gobangPanel1.isStart()) {// �����Ϸ�����У���ʾ�û���Ϸ�������ڹۿ���Ϸ�ط�
			// �����Ի���
			JOptionPane.showMessageDialog(this, "������Ϸ�����󣬹ۿ���Ϸ�طš�");
			backplayToggleButton.setSelected(false);// ȡ����ťѡ��״̬
			return;
		}
		if (LoginPanel.isManMachineWar) {// ������˻���սģʽ
			// �����Ի���
			JOptionPane.showMessageDialog(this, "�˻�ģʽ�ݲ�֧�ֻطš�");
			backplayToggleButton.setSelected(false);// ȡ����ťѡ��״̬
			return;
		}
		if (!backplayToggleButton.isSelected()) {// �����ťû�б�ѡ��
			backplayToggleButton.setText("��Ϸ�ط�");// ���İ�ť��ʾ�ı�
		} else {
			backplayToggleButton.setText("��ֹ�ط�");// ���İ�ť��ʾ�ı�
			new Thread() { // �����µ��̲߳�����Ϸ��¼
				public void run() {// �߳����з���
					Object[] toArray = gobangPanel1.getOldRec();// ��ȡ���̼�¼
					if (toArray == null) {// ������������̼�¼
						// ������ʾ��
						JOptionPane.showMessageDialog(ChessPanel.this,
								"û����Ϸ��¼", "��Ϸ�ط�", JOptionPane.WARNING_MESSAGE);
						backplayToggleButton.setText("��Ϸ�ط�");// ���İ�ť��ʾ�ı�
						backplayToggleButton.setSelected(false);// ȡ����ťѡ��״̬
						return;// ���������߳�
					}
					// �������Ľ�����֣������Է�ʤ������ʤ���ˡ���սƽ��
					gobangPanel1.setTowardsWin(false);// �Է�ʤ��
					gobangPanel1.setWin(false);// ��ʤ����״̬
					gobangPanel1.setDraw(false);// ����״̬
					// ������û��ʼ��Ϸ�����һطŰ�ť��ѡ�еģ�����������̼�¼����
					for (int i = toArray.length - 1; !gobangPanel1.isStart()
							&& backplayToggleButton.isSelected() && i >= 0; i--) {
						try {
							Thread.sleep(1000); // �߳�����1��
						} catch (InterruptedException ex) {
							//��¼��־
							Logger.getLogger(ChessPanel.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// ������Ϸ��¼����ÿһ����Ϸ������
						GobangModel.getInstance().setChessmanArray((byte[][]) toArray[i]); 
						gobangPanel1.repaint(); // �ػ�����
					}
					backplayToggleButton.setSelected(false);// ȡ����ťѡ��״̬
					backplayToggleButton.setText("��Ϸ�ط�");// ���İ�ť����
				}
			}.start();// �����߳�
		}
	}

	/**
	 * ��������ͼƬ�İ�ť�¼�������
	 * 
	 * @author Li Zhong Wei
	 */
	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			backIndex = backIndex % 9 + 1; // ��ȡ9�ű���ͼƬ�������ĵ���
			URL url = getClass().getResource("/res/bg/" + backIndex + ".jpg");
			backImg = new ImageIcon(url).getImage(); // ��ʼ������ͼƬ
			repaint(); // ���»����������
		}
	}

	private synchronized void musicButtonActionperformed(ActionEvent e){
		BGM bgm=BGM.getInstance();

		if(musicButton.getText()=="������"){
			musicButton.setText("�ر�����");
			bgm.loop();
			return;
		}
		bgm.stop();
		musicButton.setText("������");

	}
	private void backToLoginActionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(this,"������Ա����Ŭ��~~~~~");
		return;
	}
//		MainFrame mainFrame = (MainFrame) getRootPane().getParent();// ��ȡ���Ĵ������
//
//		try{
//			if(ReceiveThread.chatSocketServer)
//		}
//		mainFrame.getLoginPanel1().setVisible(true);
//		DefaultTableModel model = (DefaultTableModel) mainFrame.userInfoTable
//				.getModel();// ��ȡ�û���Ϣ����
//		model.setRowCount(0);// ����û��б�
//		reInit();
//		mainFrame.setServerSocket(null);
//		mainFrame.startServer();
//		setTowardsStart(true);
//		repaint();
//		if(LoginPanel.isManMachineWar==true)
//			return;
//		//����ǵ�¼���ģ��൱��ע����ǰ�û�  ��Ҫ��matchqueue��ɾ���˼�¼
//		try {
//			Connection con = null;
//			Statement stmt = null;
//			try {
//				Class.forName("com.mysql.cj.jdbc.Driver");
//			} catch (Exception exw) {
//				Logger.getLogger(LoginPanel.class.getName()).log(
//						Level.SEVERE, null, exw);
//			}
//			try {
//				con = DriverManager.getConnection("jdbc:mysql://39.106.59.135/gobang?useSSL=FALSE&serverTimezone=UTC", "root", "fzy125..");
//			} catch (Exception exy) {
//				Logger.getLogger(LoginPanel.class.getName()).log(
//						Level.SEVERE, null, exy);
//
//			}
//			String sql = "delete from matchqueue where id='" + mainFrame.getUser().getUserID() + "'";
//			stmt = con.createStatement();
//			stmt.execute(sql);
//		}catch (Exception e1){
//			Logger.getLogger(ChessPanel.class.getName()).log(
//					Level.SEVERE, null, e1);
//		}
//	}
	/**
	 * �Է��Ƿ��ѿ�ʼ��Ϸ
	 * 
	 * @return
	 */
	public boolean isTowardsStart() {
		return towardsStart;
	}

	/**
	 * ���öԷ���ʼ״̬
	 * 
	 * @param towardsStart
	 */
	public void setTowardsStart(boolean towardsStart) {
		this.towardsStart = towardsStart;
	}

	/**
	 * ��ȡ�������
	 * 
	 * @return
	 */
	public GobangPanel getGobangPanel1() {
		return gobangPanel1;
	}
}

