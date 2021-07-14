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
 * 下棋面板
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
	static ImageIcon WHITE_CHESS_ICON;// 白色棋子图片
	static ImageIcon BLACK_CHESS_ICON;// 黑色棋子图片
	final static int OPRATION_REPENT = 0xEF; // 悔棋命令
	final static int OPRATION_NODE_REPENT = 0xCF; // 接受悔棋命令
	final static int OPRATION_DRAW = 0xFE; // 和棋命令
	final static int OPRATION_NODE_DRAW = 0xEE; // 接受和棋命令
	final static int OPRATION_START = 0xFd; // 开始命令
	final static int OPRATION_ALL_START = 0xEd; // 接受开始命令
	final static int OPRATION_GIVEUP = 0xFc; // 认输命令
	final static int OPRATION_START_MACHINE = 0xBd; // 开始人机对战命令
	final static int WIN = 88; // 胜利代码
	private boolean towardsStart = false;// 对方是否开始
	private Image backImg;// 背景图片
	protected JButton backButton;// 悔棋按钮
	private JToggleButton backplayToggleButton;// 回放按钮
	private JLabel bannerLabel;// 广告标签
	private JButton giveupButton;// 认输标签
	private GobangPanel gobangPanel1;// 棋盘面板对象
	private JButton heqiButton;// 和棋按钮
	private JLabel jLabel5;// 调整间距的占位标签
	private JLabel jLabel6;// 调整间距的占位标签
	private JPanel jPanel1;// 下方按钮面板
	private JPanel jPanel2;// 左侧我的信息面板
	private JPanel jPanel3;// 右侧对方信息面板
	private JPanel jPanel4;// 上方广告面板
	protected JLabel leftInfoLabel;// 左侧头像
	protected JLabel myChessColorLabel;// 我的棋子颜色图片
	protected JLabel rightInfoLabel;// 右侧头像
	protected JButton startButton;// 开始按钮
	protected JLabel towardsChessColorLabel;// 对方棋子颜色图片
	int backIndex = 1;// 背景图片索引

	protected JButton musicButton;

	protected JButton backToLogin;
	/**
	 * 下棋面板的构造方法
	 */
	public ChessPanel() {
		WHITE_CHESS_ICON = new javax.swing.ImageIcon(getClass().getResource(
				"/res/whiteChess.png")); // 初始化白棋棋盒图片
		BLACK_CHESS_ICON = new javax.swing.ImageIcon(getClass().getResource(
				"/res/blackChess.png")); // 初始化黑棋棋盒图片
		URL url = getClass().getResource("/res/bg/1.jpg");// 获取src路径下图片
		backImg = new ImageIcon(url).getImage(); // 初始化背景图片
		initComponents(); // 调用初始化界面的方法

	}

	/**
	 * 重写paintComponent方法，绘制背景图片
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// 绘制背景图片
		g.drawImage(backImg, 0, 0, getWidth(), getHeight(), null);
	}

	/**
	 * 设置棋子颜色的方法，以棋盒颜色为主
	 * 
	 * @param color
	 *            - 指定颜色的棋盒图片
	 */
	public void setChessColor(ImageIcon color) {
		myChessColorLabel.setIcon(color); // 设置本地用户的棋盒图标
		if (color.equals(WHITE_CHESS_ICON)) { // 如果参数为白棋
			gobangPanel1.setMyColor(GobangModel.WHITE_CHESSMAN);// 我使用白棋
			towardsChessColorLabel.setIcon(BLACK_CHESS_ICON);// 对手使用黑棋
		} else if (color.equals(BLACK_CHESS_ICON)) {// //如果参数为黑棋
			gobangPanel1.setMyColor(GobangModel.BLACK_CHESSMAN);// 我使用黑棋
			towardsChessColorLabel.setIcon(WHITE_CHESS_ICON);// 对手使用白棋
		}
		revalidate();// 自动验证面板上所有组件
	}

	/**
	 * 设置轮回状态的方法
	 * 
	 * @param turn
	 *            - 是否获得走棋权利
	 */
	public void setTurn(boolean turn) {
		if (turn) { // 如果获得走棋权利
			myChessColorLabel.setVisible(true); // 显示棋盒
			towardsChessColorLabel.setVisible(false); // 隐藏对家棋盒
		} else {// 否则
			myChessColorLabel.setVisible(false); // 隐藏自己的棋盒
			towardsChessColorLabel.setVisible(true); // 显示对家的棋盒
		}
	}

	/**
	 * 悔棋的业务处理方法
	 */
	public synchronized void repentOperation() {
		// 获取下棋队列
		Deque<byte[][]> chessQueue = gobangPanel1.getChessQueue();
		if (chessQueue.isEmpty()) {// 如果队列里有值
			return;
		}
		// 当前棋局往前退2局
		for (int i = 0; i < 2 && !chessQueue.isEmpty(); i++) {
			chessQueue.pop(); // 废弃走棋步骤
		}
		if (chessQueue.size() < 1) {// 如果队列中没有任何值了
			chessQueue.push(new byte[15][15]);// 将一个新的数据（空棋盘）放入队列中
		}
		byte[][] pop = chessQueue.peek();// 获取队列中棋盘上的所有数据
		GobangModel.getInstance().setChessmanArray(pop);// 更新棋盘的棋子布局
		repaint();// 重新绘制界面
	}

	/**
	 * 发送数据
	 * 
	 * @param opration
	 *            -要发送的数据
	 */
	public void send(Object opration) {
		MainFrame mainFrame = (MainFrame) getRootPane().getParent();// 获取最顶层的窗体对象
		mainFrame.send(opration); // 发送命令

	}

	/**
	 * 重新初始化游戏状态
	 */
	void reInit() {
		gobangPanel1.oldRec();// 记录游戏结束前的棋盘记录
		backToLogin.setEnabled(true);
		startButton.setEnabled(true);// 开始按钮可用
		giveupButton.setEnabled(false);// 认输不可用
		heqiButton.setEnabled(false);// 和棋按钮不可用
		backButton.setEnabled(false);// 悔棋按钮不可用
		gobangPanel1.setStart(false);// 设制游戏为未开始状态
		setTowardsStart(false);// 设制对方为未开始状态
	}

	/**
	 * 为双方玩家分配棋子的方法
	 */
	private void fenqi() {
		MainFrame frame = (MainFrame) getRootPane().getParent(); // 获取主窗体对象

		if (LoginPanel.isManMachineWar) {// 如果是人机对战状态
			frame.getChessPanel1().setChessColor(ChessPanel.WHITE_CHESS_ICON);// 玩家固定使用白子
			frame.getChessPanel1().getGobangPanel1().setTurn(true);// 玩家先下
		} else {// 如果是玩家对战状态
			int myRank=frame.getUser().getRank();
			int towardsRank=frame.getTowardsUser().getRank();
			if (myRank < towardsRank) {
				frame.getChessPanel1().setChessColor(
						ChessPanel.WHITE_CHESS_ICON);// 我使用白棋
				frame.getChessPanel1().getGobangPanel1().setTurn(true);// 轮到我先下棋
			} else if(myRank>towardsRank){
				frame.getChessPanel1().setChessColor(
						ChessPanel.BLACK_CHESS_ICON);// 我使用黑棋
				frame.getChessPanel1().getGobangPanel1().setTurn(false);// 没轮到我下棋
			}else{//当rank值相同时 随机分配
				double me= Math.random();
				double to=Math.random();
				if(me<to){
					frame.getChessPanel1().setChessColor(
							ChessPanel.WHITE_CHESS_ICON);// 我使用白棋
					frame.getChessPanel1().getGobangPanel1().setTurn(true);// 轮到我先下棋
				}else{
					frame.getChessPanel1().setChessColor(
							ChessPanel.BLACK_CHESS_ICON);// 我使用黑棋
					frame.getChessPanel1().getGobangPanel1().setTurn(false);// 没轮到我下棋
				}
			}
		}
	}

	/**
	 * 清屏 填充棋盘的方法。可以使用1或-1制定填充棋盘的棋子，使用0清除棋盘
	 * 
	 * @param chessman
	 *            - 填充棋盘的棋子的颜色代码
	 */
	private void fillChessBoard(final byte chessman) {
		try {
			Runnable runnable = new Runnable() { // 创建清屏的动画线程
				/**
				 * 线程的主体方法
				 * 
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					byte[][] chessmanArray = GobangModel.getInstance()
							.getChessmanArray(); // 获取棋盘数组
					for (int i = 0; i < chessmanArray.length; i += 2) {
						try {
							Thread.sleep(10); // 动画间隔时间
						} catch (InterruptedException ex) {
							Logger.getLogger(ChessPanel.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// 使用指定颜色的棋子填充数组的一列
						Arrays.fill(chessmanArray[i], chessman);// 填充偶数列
						Arrays.fill(chessmanArray[(i + 1) % 15], chessman);// 填充奇数列
						GobangModel.getInstance().setChessmanArray(
								chessmanArray); // 更新棋盘上的棋子
						gobangPanel1.paintImmediately(0, 0, getWidth(),
								getHeight()); // 立即重绘指定区域的棋盘
					}
				}
			};
			// // 在事件队列中执行清屏
			if (SwingUtilities.isEventDispatchThread()) {// 如果是当前窗体的线程
				runnable.run();// 线程直接执行
			} else {
				SwingUtilities.invokeAndWait(runnable);// 指派该线程等待执行
			}
		} catch (Exception ex) {
			Logger.getLogger(ChessPanel.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * 初始化程序界面的方法，为组件进行实例化，并添加界面的布局等效果
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
		musicButton=new JButton("打开音乐");

		jPanel2 = new javax.swing.JPanel();
		jPanel2.setOpaque(false);
		jLabel5 = new javax.swing.JLabel();
		leftInfoLabel = new javax.swing.JLabel();
		leftInfoLabel.setForeground(new Color(0, 255, 0));
		leftInfoLabel.setFont(new Font("隶书", Font.PLAIN, 22));
		myChessColorLabel = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		jPanel3.setOpaque(false);
		jLabel6 = new javax.swing.JLabel();
		rightInfoLabel = new javax.swing.JLabel();
		rightInfoLabel.setForeground(Color.GREEN);
		rightInfoLabel.setFont(new Font("隶书", Font.PLAIN, 22));
		towardsChessColorLabel = new javax.swing.JLabel();
		jPanel4 = new javax.swing.JPanel();
		jPanel4.setOpaque(false);
		bannerLabel = new javax.swing.JLabel();
		gobangPanel1 = new com.mr.gobang.GobangPanel();

		setLayout(new java.awt.BorderLayout());
		setOpaque(false);
		backToLogin=new JButton("返回登录界面");
		backToLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backToLoginActionPerformed(e);
			}
		});
		jPanel1.add(backToLogin);
		backButton.setText("悔棋");
		backButton.setEnabled(false);
		backButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				backButtonActionPerformed(evt);
			}
		});
		jPanel1.add(backButton);

		heqiButton.setText("和棋");
		heqiButton.setEnabled(false);
		heqiButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				heqiButtonActionPerformed(evt);
			}
		});
		jPanel1.add(heqiButton);

		startButton.setText("匹配");
		startButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});
		jPanel1.add(startButton);

		giveupButton.setText("认输");
		giveupButton.setEnabled(false);
		giveupButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				giveupButtonActionPerformed(evt);
			}
		});
		jPanel1.add(giveupButton);

		backplayToggleButton.setText("游戏回放");
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
		button.setText("更换背景");
		jPanel1.add(button);

		//musicButton.setText("音乐开/关");
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
	 * 开始按钮的事件处理方法
	 * 
	 * @param evt
	 *            - 事件对象
	 */
	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (LoginPanel.isManMachineWar){//如果是人机对战
			backToLogin.setEnabled(false);
			startButton.setEnabled(false);// 开始按钮不可用
			gobangPanel1.setStart(true); // 设置游戏的开始状态为开始
			gobangPanel1.setTowardsWin(false); // 设置对家胜利状态
			gobangPanel1.setWin(false); // 设置自己胜利状态
			gobangPanel1.setDraw(false); // 设置和棋状态
			send(OPRATION_START_MACHINE);// 发送开始人机对战指令
			giveupButton.setEnabled(false);// 人机对战不可使用认输按钮
			heqiButton.setEnabled(false);// 人机对战不可使用和棋按钮
			backButton.setEnabled(false);// 人机对战不可使用悔棋按钮

			fenqi(); // 分配双方棋子

			fillChessBoard(gobangPanel1.getMyColor());// 使用自己的棋子颜色清屏
			fillChessBoard((byte) 0); // 使用空棋子清屏
			byte[][] data = new byte[15][15]; // 创建一个空的棋盘布局
			GobangModel.getInstance().setChessmanArray(data);// 设置棋盘使用空布局
			return;
		}
		try{

			MainFrame mainFrame = (MainFrame) getRootPane().getParent();
			if(mainFrame.getSocket()!=null){//如果连接着的 表示这是后续开始的新的一局
				startButton.setEnabled(false);// 开始按钮不可用
				gobangPanel1.setStart(true); // 设置游戏的开始状态为开始
				gobangPanel1.setTowardsWin(false); // 设置对家胜利状态
				gobangPanel1.setWin(false); // 设置自己胜利状态
				gobangPanel1.setDraw(false); // 设置和棋状态
				repaint();
				//添加头像上的ready信息
				//-----------------------//
				send(OPRATION_START);// 发送开始指令
				giveupButton.setEnabled(true);// 玩家对战可使用认输按钮
				heqiButton.setEnabled(true);// 玩家对战可使用和棋按钮
				backButton.setEnabled(true);// 玩家对战可使用悔棋按钮

				//fenqi(); // 分配双方棋子

				fillChessBoard(gobangPanel1.getMyColor());// 使用自己的棋子颜色清屏
				fillChessBoard((byte) 0); // 使用空棋子清屏
				byte[][] data = new byte[15][15]; // 创建一个空的棋盘布局
				GobangModel.getInstance().setChessmanArray(data);// 设置棋盘使用空布局
				return;
			}
			Connection con=null;
			Statement stmt=null;
			ResultSet rs=null;
			try{
				Class.forName("com.mysql.cj.jdbc.Driver");
				//JOptionPane.showMessageDialog(this,"驱动加载成功");

			}
			catch(Exception exw){
				//JOptionPane.showMessageDialog(this,"服务器异常");
				//JOptionPane.showMessageDialog(this,"驱动加载失败");
				//return;
				Logger.getLogger(ChessPanel.class.getName()).log(
						Level.SEVERE, null, exw);
			}
			try{
				con = DriverManager.getConnection("jdbc:mysql://39.106.59.135/gobang?useSSL=FALSE&serverTimezone=UTC","root", "fzy125..");
				//JOptionPane.showMessageDialog(this,"服务器连接成功");

			}
			catch(Exception exy){
				//System.out.println("数据库连接失败");
				//JOptionPane.showMessageDialog(this,"服务器连接失败");
				//JOptionPane.showMessageDialog(this,"服务器异常");
				//return;
				Logger.getLogger(ChessPanel.class.getName()).log(
						Level.SEVERE, null, exy);

			}
			UserBean user =LoginPanel.user;
			String myId= user.getUserID();
			String myIp= user.getHost().getHostAddress();
			int myRank=user.getRank();
			stmt=con.createStatement();
			//匹配
			String sql2 = "SELECT\n" +
					"\t*\n" +
					"FROM\n" +
					"\tmatchqueue AS m1\n" +
					"WHERE\n" +
					"\t(m1.mark=1) and (m1.userrank-100 < '" + myRank + "') and ('" + myRank + "'-m1.userrank<100) and (m1.ip!='" + myIp + "')";//匹配条件
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
			String ipText=rs.getString("ip"); // 获取对家IP地址
			int port=rs.getInt("port");
			String id=rs.getString("id");
			JOptionPane.showMessageDialog(this,ipText+"--"+port);

			InetAddress ip = InetAddress.getByName(ipText);// 获取改地址对象
			startButton.setEnabled(false);
			ReceiveThread.setFlag(true);//修改标志位表示本线程是发起匹配的线程

			Socket socket = new Socket(ip, port); // 创建Socket连接对家主机

			if(!socket.isConnected()){//如果连接失败 就返回
				JOptionPane.showMessageDialog(this,"连接失败，请重试 -00");
				startButton.setEnabled(true);
				return;
			}
			//从队列中更新对象
			String sql3="update matchqueue set mark='"+0+"'where ip='"+myIp+"' or ip='"+ip+"'";//更新我跟匹配到的对手数据的标志 防止后续再有人跟我建立连接
			stmt.execute(sql3);
			socket.setOOBInline(true); // 启用紧急数据的接收
			mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
			mainFrame.send(user); // 发送本地用户对象到对家主机
			String sql4="select * from userinfo where userid ='"+id+"'";
			ResultSet rs4=stmt.executeQuery(sql4);
			rs4.next();
			MainFrame frame = (MainFrame) getRootPane().getParent(); // 获取主窗体对象

			//设置对手  匹配发起方设置对手是根据数据库访问实现的  而被匹配接受方则根据send的对象进行设置
			UserBean towardsUser=new UserBean();
			towardsUser.setRank(rs4.getInt("userrank"));
			towardsUser.setName(rs4.getString("cname"));
			towardsUser.setWin_count(rs4.getInt("wincount"));
			towardsUser.setHost(ip);
			towardsUser.setUserID(id);
			frame.setTowardsUser(towardsUser);
			// 设置各个按钮的可用状态
			//startButton.setText("开始");
			startButton.setEnabled(false);// 开始按钮不可用
			gobangPanel1.setStart(true); // 设置游戏的开始状态为开始
			gobangPanel1.setTowardsWin(false); // 设置对家胜利状态
			gobangPanel1.setWin(false); // 设置自己胜利状态
			gobangPanel1.setDraw(false); // 设置和棋状态

			send(OPRATION_START);// 发送开始指令
			giveupButton.setEnabled(true);// 玩家对战可使用认输按钮
			heqiButton.setEnabled(true);// 玩家对战可使用和棋按钮
			backButton.setEnabled(true);// 玩家对战可使用悔棋按钮

			fenqi(); // 分配双方棋子

			fillChessBoard(gobangPanel1.getMyColor());// 使用自己的棋子颜色清屏
			fillChessBoard((byte) 0); // 使用空棋子清屏
			byte[][] data = new byte[15][15]; // 创建一个空的棋盘布局
			GobangModel.getInstance().setChessmanArray(data);// 设置棋盘使用空布局
		}catch (UnknownHostException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"匹配失败，请重试 -01");
			startButton.setEnabled(true);
		}catch (IOException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"匹配失败，请重试 -02");
			startButton.setEnabled(true);
		}catch (SQLException ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,"匹配失败，请重试 -03");
			startButton.setEnabled(true);

		}
	}

	public void receivedSideDo(Socket socket){
		try{
			MainFrame mainFrame = (MainFrame) getRootPane().getParent();
			socket.setOOBInline(true); // 启用紧急数据的接收
			mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
			//startButton.setText("开始");
			backToLogin.setEnabled(false);
			startButton.setEnabled(false);// 开始按钮不可用
			gobangPanel1.setStart(true); // 设置游戏的开始状态为开始
			gobangPanel1.setTowardsWin(false); // 设置对家胜利状态
			gobangPanel1.setWin(false); // 设置自己胜利状态
			gobangPanel1.setDraw(false); // 设置和棋状态

			giveupButton.setEnabled(true);// 玩家对战可使用认输按钮
			heqiButton.setEnabled(true);// 玩家对战可使用和棋按钮
			backButton.setEnabled(true);// 玩家对战可使用悔棋按钮

			//fenqi(); // 分配双方棋子

			fillChessBoard(gobangPanel1.getMyColor());// 使用自己的棋子颜色清屏
			fillChessBoard((byte) 0); // 使用空棋子清屏
			byte[][] data = new byte[15][15]; // 创建一个空的棋盘布局
			GobangModel.getInstance().setChessmanArray(data);// 设置棋盘使用空布局
		}catch (Exception e){
			Logger.getLogger(ChessPanel.class.getName()).log(
					Level.SEVERE, null, e);
		}
	}

	public void chessArrangement(){
		fenqi();
	}
	/**
	 * 认输按钮的事件处理方法
	 * 
	 * @param evt
	 *            - 按钮的事件对象
	 */
	private void giveupButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// 如果没到自己走棋，提示用户等待
		if (!gobangPanel1.isTurn()) {
			JOptionPane.showMessageDialog(this, "没到你走棋呢。", "请等待...",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		send(OPRATION_GIVEUP);// 发送认输指令
		// 启动一个新的线程使认输按钮5秒不可用
		new Thread() {
			@Override
			public void run() {
				try {
					giveupButton.setEnabled(false);// 不可用
					sleep(5000);
					giveupButton.setEnabled(true);// 可用
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// 线程启动
	}

	/**
	 * 悔棋按钮的事件处理方法
	 * 
	 * @param evt
	 */
	private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// 如果没到自己走棋
		if (!gobangPanel1.isTurn()) {
			JOptionPane.showMessageDialog(this, "没到你走棋呢。", "请等待...",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		send(OPRATION_REPENT);// 发送悔棋命令
		new Thread() { // 开启新的线程，使悔棋按钮禁用5秒
			@Override
			public void run() {
				try {
					backButton.setEnabled(false);// 不可用
					sleep(5000);
					backButton.setEnabled(true);// 可用
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// 开启线程
	}

	/**
	 * 和棋按钮的事件处理方法
	 * 
	 * @param evt
	 *            - 按钮的action事件对象
	 */
	private void heqiButtonActionPerformed(java.awt.event.ActionEvent evt) {
		send(OPRATION_DRAW);// 发送和棋指令
		new Thread() { // 开启新的线程使和棋按钮5秒不可用
			public void run() {
				try {
					heqiButton.setEnabled(false);// 不可用
					sleep(5000);
					heqiButton.setEnabled(true);// 可用
				} catch (InterruptedException ex) {
					Logger.getLogger(ChessPanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}.start();// 开启线程
	}

	/**
	 * 广告图片的鼠标单击事件处理方法
	 * 
	 * @param evt
	 *            - 鼠标事件对象
	 */
	private void bannerLabelMouseClicked(java.awt.event.MouseEvent evt) {
		try {
			// 调用Desktop类的browse方法浏览编程词典首页
			if (Desktop.isDesktopSupported()) {// 如果当前平台支持此类
				Desktop.getDesktop().browse(// 启动默认浏览器连接此地址
						new URL("http://www.cse.cqu.edu.cn/").toURI());
			} else {
				// 弹出对话框
				JOptionPane.showMessageDialog(this, "当前系统不支持该操作");
			}
		} catch (Exception ex) {
			Logger.getLogger(ChessPanel.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * 游戏回放按钮的事件处理方法
	 * 
	 * @param evt
	 *            - 事件对象
	 */
	private void backplayToggleButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (gobangPanel1.isStart()) {// 如果游戏进行中，提示用户游戏结束后在观看游戏回放
			// 弹出对话框
			JOptionPane.showMessageDialog(this, "请在游戏结束后，观看游戏回放。");
			backplayToggleButton.setSelected(false);// 取消按钮选中状态
			return;
		}
		if (LoginPanel.isManMachineWar) {// 如果是人机对战模式
			// 弹出对话框
			JOptionPane.showMessageDialog(this, "人机模式暂不支持回放。");
			backplayToggleButton.setSelected(false);// 取消按钮选中状态
			return;
		}
		if (!backplayToggleButton.isSelected()) {// 如果按钮没有被选中
			backplayToggleButton.setText("游戏回放");// 更改按钮显示文本
		} else {
			backplayToggleButton.setText("终止回放");// 更改按钮显示文本
			new Thread() { // 开启新的线程播放游戏记录
				public void run() {// 线程运行方法
					Object[] toArray = gobangPanel1.getOldRec();// 获取棋盘记录
					if (toArray == null) {// 如果不存在棋盘记录
						// 弹出提示框
						JOptionPane.showMessageDialog(ChessPanel.this,
								"没有游戏记录", "游戏回放", JOptionPane.WARNING_MESSAGE);
						backplayToggleButton.setText("游戏回放");// 更改按钮显示文本
						backplayToggleButton.setSelected(false);// 取消按钮选中状态
						return;// 方法结束线程
					}
					// 清除界面的结局文字，包括对方胜利、你胜利了、此战平局
					gobangPanel1.setTowardsWin(false);// 对方胜利
					gobangPanel1.setWin(false);// 我胜利的状态
					gobangPanel1.setDraw(false);// 和棋状态
					// 如果玩家没开始游戏，并且回放按钮是选中的，反序遍历棋盘记录数组
					for (int i = toArray.length - 1; !gobangPanel1.isStart()
							&& backplayToggleButton.isSelected() && i >= 0; i--) {
						try {
							Thread.sleep(1000); // 线程休眠1秒
						} catch (InterruptedException ex) {
							//记录日志
							Logger.getLogger(ChessPanel.class.getName()).log(
									Level.SEVERE, null, ex);
						}
						// 根据游戏记录跟换每一布游戏的棋谱
						GobangModel.getInstance().setChessmanArray((byte[][]) toArray[i]); 
						gobangPanel1.repaint(); // 重绘棋盘
					}
					backplayToggleButton.setSelected(false);// 取消按钮选中状态
					backplayToggleButton.setText("游戏回放");// 更改按钮文字
				}
			}.start();// 开启线程
		}
	}

	/**
	 * 更换背景图片的按钮事件监听器
	 * 
	 * @author Li Zhong Wei
	 */
	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			backIndex = backIndex % 9 + 1; // 获取9张背景图片的索引的递增
			URL url = getClass().getResource("/res/bg/" + backIndex + ".jpg");
			backImg = new ImageIcon(url).getImage(); // 初始化棋盘图片
			repaint(); // 重新绘制下棋面板
		}
	}

	private synchronized void musicButtonActionperformed(ActionEvent e){
		BGM bgm=BGM.getInstance();

		if(musicButton.getText()=="打开音乐"){
			musicButton.setText("关闭音乐");
			bgm.loop();
			return;
		}
		bgm.stop();
		musicButton.setText("打开音乐");

	}
	private void backToLoginActionPerformed(ActionEvent e){
		JOptionPane.showMessageDialog(this,"开发人员正在努力~~~~~");
		return;
	}
//		MainFrame mainFrame = (MainFrame) getRootPane().getParent();// 获取最顶层的窗体对象
//
//		try{
//			if(ReceiveThread.chatSocketServer)
//		}
//		mainFrame.getLoginPanel1().setVisible(true);
//		DefaultTableModel model = (DefaultTableModel) mainFrame.userInfoTable
//				.getModel();// 获取用户信息数据
//		model.setRowCount(0);// 清空用户列表
//		reInit();
//		mainFrame.setServerSocket(null);
//		mainFrame.startServer();
//		setTowardsStart(true);
//		repaint();
//		if(LoginPanel.isManMachineWar==true)
//			return;
//		//如果是登录过的，相当于注销当前用户  需要从matchqueue中删除此记录
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
	 * 对方是否已开始游戏
	 * 
	 * @return
	 */
	public boolean isTowardsStart() {
		return towardsStart;
	}

	/**
	 * 设置对方开始状态
	 * 
	 * @param towardsStart
	 */
	public void setTowardsStart(boolean towardsStart) {
		this.towardsStart = towardsStart;
	}

	/**
	 * 获取棋盘面板
	 * 
	 * @return
	 */
	public GobangPanel getGobangPanel1() {
		return gobangPanel1;
	}
}

