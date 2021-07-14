package com.mr.gobang;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * ��¼���
 * 
 */
public class LoginPanel extends javax.swing.JPanel {

	private Socket socket;// �ͻ����׽���
	private Connection con=null;//�������ݿ�
	//private Statement stmt=null;//ִ�����ݿ�sql���
	//private ResultSet rs=null;//�洢���ݿ��ѯ���
	int if_success = 0;//�ж��Ƿ��¼�ɹ�
	public static UserBean user;// ���ش������û�d
	private javax.swing.JButton signButton;// �رհ�ť
	private javax.swing.JTextField passwordTextField;// ���������
	private javax.swing.JLabel idLabel;// �û�����ǩ
	private javax.swing.JLabel passwordLabel;// �����ǩ
	private javax.swing.JButton loginButton;// ��½��ť
	private javax.swing.JTextField idTextField;// �û��������
	private javax.swing.JButton machineButton;// �˻���ս��ť
	public static boolean isManMachineWar=true;// �Ƿ�Ϊ�˻���ս
	public static boolean userExists=false;

	/**
	 * ���췽��
	 */
	public LoginPanel() {
		initComponents(); // ���ó�ʼ������ķ���
	}

	/**
	 * ��ʼ����¼����ķ���
	 */
	private void initComponents() {
		// ������������Լ����������΢������ڱ������ֵ�λ��
		java.awt.GridBagConstraints gridBagConstraints;
		idLabel = new javax.swing.JLabel();// ��ʼ���ǳƱ�ǩ
		idTextField = new javax.swing.JTextField();// ��ʼ���ǳ������
		passwordLabel = new javax.swing.JLabel();// ��ʼ�����Է�IP����ǩ
		passwordTextField = new javax.swing.JTextField();// ��ʼ��ip�����
		loginButton = new javax.swing.JButton();// ��ʼ����½��ť
		signButton = new javax.swing.JButton();// ��ʼ���رհ�ť
		machineButton = new javax.swing.JButton();// ��ʼ���˻���ս��ť
		//setForeground(java.awt.Color.blue);// ǰ��ɫΪ��ɫ
		setForeground(new Color(166,249,242));
		setOpaque(false);// ������͸����
		setLayout(new java.awt.GridBagLayout());// ʹ�����������

		idLabel.setFont(new Font("����", Font.ITALIC, 24));// �趨����
		idLabel.setForeground(new java.awt.Color(255, 255, 255));// �趨��ɫ
		idLabel.setText("�û�����");// �趨��������
		gridBagConstraints = new java.awt.GridBagConstraints();// ��ʼ����������Լ������
		gridBagConstraints.gridx = 0;// ���ڵ�һ��
		gridBagConstraints.gridy = 0;// ���ڵ�һ��
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;// �Ҷ���
		add(idLabel, gridBagConstraints);// �������ӵ�ָ��λ��

		gridBagConstraints = new java.awt.GridBagConstraints();// ��ʼ����������Լ������
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;// ˮƽ������С
		gridBagConstraints.ipady = -5;// ����������
		gridBagConstraints.gridwidth = 2;// һ��ռ����������ô��
		// ����࣬�������3����߼��0���ײ����3���ұ߼��0
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		add(idTextField, gridBagConstraints);// �������ӵ�ָ��λ��

		passwordLabel.setFont(new Font("����", Font.ITALIC, 24));
		passwordLabel.setForeground(java.awt.Color.white);
		passwordLabel.setText("�� �룺");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		add(passwordLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.ipady = -5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		add(passwordTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		add(machineButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		add(loginButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 55);
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		add(signButton, gridBagConstraints);

		addMouseListener(new java.awt.event.MouseAdapter() {// �������¼�
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);// ������λ�õ����
			}
		});

		machineButton.setText("�˻���ս");
		machineButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				machinButtonActionPerformed();
			}
		});

		loginButton.setText("��¼");
		loginButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginButtonActionPerformed(evt);
			}
		});
		signButton.setText("ע��");
		signButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				signButtonActionPerformed(evt);// �رհ�ť��������ر�
			}
		});
	}

	/**
	 * �رհ�ť��������ر�
	 * 
	 * @param evt
	 */
	private void signButtonActionPerformed(java.awt.event.ActionEvent evt) {

		SignPanel signPanel=new SignPanel();
		signPanel.setModal(true);
		Point p=this.getLocation();
		Dimension d=this.getSize();
		int x=p.x+(d.width-p.x)/2;
		int y=p.y+(d.height-p.y)/2;
		signPanel.setLocation(x,y);
		signPanel.setVisible(true);
		return;
	}

	/**
	 * ��������հ�λ��ʱ�����ķ���
	 * 
	 * @param evt
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt) {
		//JOptionPane.showMessageDialog(this, "��û��¼�أ����ĵ㣿");// ������ʾ��
		return;
	}

	/**
	 * ��¼��ť���¼�������
	 * 
	 * @param evt
	 *            - ��ť���¼�����
	 */
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try{
			 //��ȡ�������ʵ������
			MainFrame mainFrame = (MainFrame) getParent().getParent();
			String idText=idTextField.getText();
			String passwordText=passwordTextField.getText();
			if (idText.trim().isEmpty()) {// �������û���κο���ʾ���ַ�
				JOptionPane.showMessageDialog(this, "�������û��˺�");
				return;
			}
			if (passwordText == null || passwordText.isEmpty()) {// ���û�������κ�����
				JOptionPane.showMessageDialog(this, "�������û�����");
				return;
			}

			//�������ݿ�
			try{
				Class.forName("com.mysql.cj.jdbc.Driver");
			}
			catch(Exception exw){
				Logger.getLogger(LoginPanel.class.getName()).log(
						Level.SEVERE, null, exw);
			}
			try{
				con = DriverManager.getConnection("jdbc:mysql://39.106.59.135/gobang?useSSL=FALSE&serverTimezone=UTC","root", "fzy125..");
			}
			catch(Exception exy){
				Logger.getLogger(LoginPanel.class.getName()).log(
						Level.SEVERE, null, exy);

			}
			//ִ��sql��䣬��ѯ���û���������
			String sql1="select password from usercount where userid='"+idText+"'";
			String sql2="select * from userinfo where userid='"+idText+"'";
			Statement stmt1 = con.createStatement();
			Statement stmt2 = con.createStatement();

			ResultSet rs1=stmt1.executeQuery(sql1);
			ResultSet rs2=stmt2.executeQuery(sql2);

			//�ж��û����������Ƿ���ȷ
			while(rs1.next()){
				if(passwordText.equals(rs1.getString("password")))
					if_success = 1;
			}

			if(if_success==1){//��ȷ������ʼ����
				user = new UserBean(); // �����û�����
				Time time = new Time(System.currentTimeMillis()); // ��ȡ��ǰʱ�����
				rs2.next();
				String id=rs2.getString("userid");
				String name=rs2.getString("cname");
				int rank=rs2.getInt("userrank");
				int count=rs2.getInt("wincount");
				user.setUserID(id);
				user.setName(name); // ��ʼ���û��ǳ�
				user.setRank(rank);
				user.setWin_count(count);
				user.setHost(InetAddress.getLocalHost()); // ��ʼ���û�IP
				//user.setTime(time); // ��ʼ���û���¼ʱ��
				//socket.setOOBInline(true); // ���ý������ݵĽ���
				//mainFrame.setSocket(socket); // �����������Socket���Ӷ���
				mainFrame.setUser(user); // ��ӱ����û��������������
				//mainFrame.send(user); // ���ͱ����û����󵽶Լ�����
				isManMachineWar = false;// ��Ǵ˾�Ϊ��Ҷ�ս
				userExists=true;//��ʾ���û���½��
				setVisible(false); // ���ص�¼����
				String  sql="insert into matchqueue values('"+user.getUserID()+"','"+user.getHost().getHostAddress()+"','"+user.getPort()+"','"+user.getRank()+"','"+1+"')";//��¼�ɹ��ͽ���ǰ��ҵ���Ϣ���뵽����
				stmt1.execute(sql);
				mainFrame.getChessPanel1().startButton.setText("ƥ��");
			}
			else{
				JOptionPane.showMessageDialog(null,"�û������������");
			}
			rs1.close();
			rs2.close();
		}catch(SQLException ex){
			Logger.getLogger(LoginPanel.class.getName()).log(
					Level.SEVERE, null, ex);
		}catch (UnknownHostException ex){
			Logger.getLogger(LoginPanel.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * �˻���ս�¼�������
	 */
	private void machinButtonActionPerformed() {
		MainFrame mainFrame = (MainFrame) getParent().getParent();// ��ȡ�������ʵ������
//		String name = idTextField.getText(); // ��ȡ�û��ǳ�
//		if (name.trim().isEmpty()) {// ����û�û����������
//			JOptionPane.showMessageDialog(this, "�������ǳ�");// �����Ի���
//			return;
//		}
		try {
			socket = new Socket("127.0.0.1", 9527);// �������ӱ��ص��׽���
			if (socket.isConnected()) { // ������ӳɹ�
				user = new UserBean(); // �����û�����
				user.setName("���С¹"); // ��ʼ���û��ǳ�
				user.setHost(InetAddress.getLocalHost()); // ��ʼ���û�IP
				socket.setOOBInline(true); // ���ý������ݵĽ���
				mainFrame.setSocket(socket); // �����������Socket���Ӷ���
				UserBean machine = new UserBean();// ����AI������
				machine.setName("����");// Ϊ��������������
				machine.setHost(InetAddress.getLocalHost()); // ��ʼ��������IP
				//machine.setTime(new Time(0));// ����ʱ����Ϊһ���ϴ�ģ�ȷ��������ѡ����
				machine.setRank(0);
				mainFrame.setUser(user); // ����ҷ��ڴ������
				mainFrame.setTowardsUser(machine); // �������˷����������ұ�
				mainFrame.setSendButtonEnable(false);// �˻���սȡ��������Ϣ����
				isManMachineWar = true;// ��Ǵ˾�Ϊ�˻���ս

				setVisible(false); // ���ص�¼����
				mainFrame.getChessPanel1().startButton.setText("ƥ��");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����������ķ���
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // ��ȡ2D��ͼ������
		Composite composite = g2.getComposite(); // ���ݺϳ�ģʽ
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.8f)); // ���û�ͼʹ��͸���ϳɹ���
		g2.fillRect(0, 0, getWidth(), getHeight()); // ʹ�õ�ǰ��ɫ�����οռ�
		g2.setComposite(composite); // �ָ�ԭ�кϳ�ģʽ
		super.paintComponent(g2); // ִ�г����������Ʒ���
	}

	void setLinkIp(String ip) {
		passwordTextField.setText(ip);// IP�����д��ָ������
		passwordTextField.setEditable(false);// IP����򲻿ɱ༭
		idTextField.requestFocus();// ����������üǵý���
	}
}
