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
 * 登录面板
 * 
 */
public class LoginPanel extends javax.swing.JPanel {

	private Socket socket;// 客户端套接字
	private Connection con=null;//连接数据库
	//private Statement stmt=null;//执行数据库sql语句
	//private ResultSet rs=null;//存储数据库查询结果
	int if_success = 0;//判断是否登录成功
	public static UserBean user;// 本地创建的用户d
	private javax.swing.JButton signButton;// 关闭按钮
	private javax.swing.JTextField passwordTextField;// 密码输入框
	private javax.swing.JLabel idLabel;// 用户名标签
	private javax.swing.JLabel passwordLabel;// 密码标签
	private javax.swing.JButton loginButton;// 登陆按钮
	private javax.swing.JTextField idTextField;// 用户名输入框
	private javax.swing.JButton machineButton;// 人机对战按钮
	public static boolean isManMachineWar=true;// 是否为人机对战
	public static boolean userExists=false;

	/**
	 * 构造方法
	 */
	public LoginPanel() {
		initComponents(); // 调用初始化界面的方法
	}

	/**
	 * 初始化登录界面的方法
	 */
	private void initComponents() {
		// 创建表格包布局约束对象，用于微调组件在表格包布局的位置
		java.awt.GridBagConstraints gridBagConstraints;
		idLabel = new javax.swing.JLabel();// 初始化昵称标签
		idTextField = new javax.swing.JTextField();// 初始化昵称输入框
		passwordLabel = new javax.swing.JLabel();// 初始化“对方IP”标签
		passwordTextField = new javax.swing.JTextField();// 初始化ip输入框
		loginButton = new javax.swing.JButton();// 初始化登陆按钮
		signButton = new javax.swing.JButton();// 初始化关闭按钮
		machineButton = new javax.swing.JButton();// 初始化人机对战按钮
		//setForeground(java.awt.Color.blue);// 前景色为灰色
		setForeground(new Color(166,249,242));
		setOpaque(false);// 可以是透明的
		setLayout(new java.awt.GridBagLayout());// 使用网格包布局

		idLabel.setFont(new Font("隶书", Font.ITALIC, 24));// 设定字体
		idLabel.setForeground(new java.awt.Color(255, 255, 255));// 设定颜色
		idLabel.setText("用户名：");// 设定文字内容
		gridBagConstraints = new java.awt.GridBagConstraints();// 初始化表格包布局约束对象
		gridBagConstraints.gridx = 0;// 处于第一行
		gridBagConstraints.gridy = 0;// 处于第一列
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;// 右对齐
		add(idLabel, gridBagConstraints);// 将组件添加到指定位置

		gridBagConstraints = new java.awt.GridBagConstraints();// 初始化表格包布局约束对象
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;// 水平调整大小
		gridBagConstraints.ipady = -5;// 上下填充距离
		gridBagConstraints.gridwidth = 2;// 一行占两个格子那么宽
		// 间距类，顶部间距3，左边间距0，底部间距3，右边间距0
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		add(idTextField, gridBagConstraints);// 将组件添加到指定位置

		passwordLabel.setFont(new Font("隶书", Font.ITALIC, 24));
		passwordLabel.setForeground(java.awt.Color.white);
		passwordLabel.setText("密 码：");
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

		addMouseListener(new java.awt.event.MouseAdapter() {// 添加鼠标事件
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);// 在其他位置点鼠标
			}
		});

		machineButton.setText("人机对战");
		machineButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				machinButtonActionPerformed();
			}
		});

		loginButton.setText("登录");
		loginButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginButtonActionPerformed(evt);
			}
		});
		signButton.setText("注册");
		signButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				signButtonActionPerformed(evt);// 关闭按钮触发程序关闭
			}
		});
	}

	/**
	 * 关闭按钮触发程序关闭
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
	 * 当鼠标点击空白位置时触发的方法
	 * 
	 * @param evt
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt) {
		//JOptionPane.showMessageDialog(this, "还没登录呢，往哪点？");// 弹出提示框
		return;
	}

	/**
	 * 登录按钮的事件处理方法
	 * 
	 * @param evt
	 *            - 按钮的事件对象
	 */
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try{
			 //获取主窗体的实例对象
			MainFrame mainFrame = (MainFrame) getParent().getParent();
			String idText=idTextField.getText();
			String passwordText=passwordTextField.getText();
			if (idText.trim().isEmpty()) {// 如果名称没有任何可显示的字符
				JOptionPane.showMessageDialog(this, "请输入用户账号");
				return;
			}
			if (passwordText == null || passwordText.isEmpty()) {// 如果没有输入任何密码
				JOptionPane.showMessageDialog(this, "请输入用户密码");
				return;
			}

			//连接数据库
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
			//执行sql语句，查询出用户名和密码
			String sql1="select password from usercount where userid='"+idText+"'";
			String sql2="select * from userinfo where userid='"+idText+"'";
			Statement stmt1 = con.createStatement();
			Statement stmt2 = con.createStatement();

			ResultSet rs1=stmt1.executeQuery(sql1);
			ResultSet rs2=stmt2.executeQuery(sql2);

			//判断用户名和密码是否正确
			while(rs1.next()){
				if(passwordText.equals(rs1.getString("password")))
					if_success = 1;
			}

			if(if_success==1){//正确则进入初始界面
				user = new UserBean(); // 创建用户对象
				Time time = new Time(System.currentTimeMillis()); // 获取当前时间对象
				rs2.next();
				String id=rs2.getString("userid");
				String name=rs2.getString("cname");
				int rank=rs2.getInt("userrank");
				int count=rs2.getInt("wincount");
				user.setUserID(id);
				user.setName(name); // 初始化用户昵称
				user.setRank(rank);
				user.setWin_count(count);
				user.setHost(InetAddress.getLocalHost()); // 初始化用户IP
				//user.setTime(time); // 初始化用户登录时间
				//socket.setOOBInline(true); // 启用紧急数据的接收
				//mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
				mainFrame.setUser(user); // 添加本地用户对象到主窗体对象
				//mainFrame.send(user); // 发送本地用户对象到对家主机
				isManMachineWar = false;// 标记此局为玩家对战
				userExists=true;//表示有用户登陆过
				setVisible(false); // 隐藏登录窗体
				String  sql="insert into matchqueue values('"+user.getUserID()+"','"+user.getHost().getHostAddress()+"','"+user.getPort()+"','"+user.getRank()+"','"+1+"')";//登录成功就将当前玩家的信息加入到队列
				stmt1.execute(sql);
				mainFrame.getChessPanel1().startButton.setText("匹配");
			}
			else{
				JOptionPane.showMessageDialog(null,"用户名或密码错误");
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
	 * 人机对战事件处理方法
	 */
	private void machinButtonActionPerformed() {
		MainFrame mainFrame = (MainFrame) getParent().getParent();// 获取主窗体的实例对象
//		String name = idTextField.getText(); // 获取用户昵称
//		if (name.trim().isEmpty()) {// 如果用户没有输入名称
//			JOptionPane.showMessageDialog(this, "请输入昵称");// 弹出对话框
//			return;
//		}
		try {
			socket = new Socket("127.0.0.1", 9527);// 创建连接本地的套接字
			if (socket.isConnected()) { // 如果连接成功
				user = new UserBean(); // 创建用户对象
				user.setName("你的小鹿"); // 初始化用户昵称
				user.setHost(InetAddress.getLocalHost()); // 初始化用户IP
				socket.setOOBInline(true); // 启用紧急数据的接收
				mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
				UserBean machine = new UserBean();// 创建AI机器人
				machine.setName("憨憨");// 为机器人设置名称
				machine.setHost(InetAddress.getLocalHost()); // 初始化机器人IP
				//machine.setTime(new Time(0));// 设置时间设为一个较大的，确保机器人选黑子
				machine.setRank(0);
				mainFrame.setUser(user); // 将玩家放在窗体左边
				mainFrame.setTowardsUser(machine); // 将机器人放在主窗体右边
				mainFrame.setSendButtonEnable(false);// 人机对战取消发送消息功能
				isManMachineWar = true;// 标记此局为人机对战

				setVisible(false); // 隐藏登录窗体
				mainFrame.getChessPanel1().startButton.setText("匹配");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制组件界面的方法
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // 获取2D绘图上下文
		Composite composite = g2.getComposite(); // 备份合成模式
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.8f)); // 设置绘图使用透明合成规则
		g2.fillRect(0, 0, getWidth(), getHeight()); // 使用当前颜色填充矩形空间
		g2.setComposite(composite); // 恢复原有合成模式
		super.paintComponent(g2); // 执行超类的组件绘制方法
	}

	void setLinkIp(String ip) {
		passwordTextField.setText(ip);// IP输入框写入指定内容
		passwordTextField.setEditable(false);// IP输入框不可编辑
		idTextField.requestFocus();// 名称输入框获得记得焦点
	}
}
