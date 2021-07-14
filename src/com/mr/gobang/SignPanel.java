package com.mr.gobang;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignPanel extends JDialog {
    private Connection con=null;
    private Statement stmt=null;
    private ResultSet rs=null;//存储数据库查询结果
    private JLabel userCountLabel=new JLabel("用户账号:");
    private JTextField userCountTextField=new JTextField(5);
    private JLabel userNameLabel=new JLabel("昵称");
    private JTextField userNameTextField=new JTextField(5);
    private JLabel userPasswordLabel1=new JLabel("输入密码:");
    private JTextField userPasswordTextField1=new JTextField(5);
    private JLabel userPasswordLabel2=new JLabel("重复密码:");
    private JTextField userPasswordTextField2=new JTextField(5);
    private JButton commitButton=new JButton("注册");
    private JButton backButton=new JButton("返回");

    SignPanel(){
        setSize(250,200);
        setResizable(false);
        //setLocation(0,0);
        setTitle("用户注册");
        JPanel jPanel1=new JPanel();//放置label和textfield
        JPanel jPanel2=new JPanel();//放置Button

        jPanel1.setLayout(new GridLayout(4,2));
        jPanel1.add(userCountLabel);
        jPanel1.add(userCountTextField);
        jPanel1.add(userNameLabel);
        jPanel1.add(userNameTextField);
        jPanel1.add(userPasswordLabel1);
        jPanel1.add(userPasswordTextField1);
        jPanel1.add(userPasswordLabel2);
        jPanel1.add(userPasswordTextField2);

        jPanel2.setLayout(new GridLayout(1,2));
        jPanel2.add(commitButton);
        jPanel2.add(backButton);

        add(jPanel1,BorderLayout.NORTH);
        add(jPanel2,BorderLayout.SOUTH);


        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commitButtonActionPerformed();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void commitButtonActionPerformed(){
        try{
            //获取主窗体的实例对象
            MainFrame mainFrame = (MainFrame) getParent().getParent();
            String idText=userCountTextField.getText();
            String nameText=userNameTextField.getText();
            String passwordText1=userPasswordTextField1.getText();
            String passwordText2=userPasswordTextField2.getText();
//            if(passwordText1.equals(passwordText2)) {
//                JOptionPane.showMessageDialog(this, "两次密码需相同！");
//                return;
//            }
            if (idText.trim().isEmpty()) {// 如果名称没有任何可显示的字符
                JOptionPane.showMessageDialog(this, "请输入用户账号");
                return;
            }
            if (passwordText1 == null || passwordText1.isEmpty()) {// 如果没有输入任何密码
                JOptionPane.showMessageDialog(this, "请输入用户密码");
                return;
            }
            if (passwordText2 == null || passwordText2.isEmpty()) {// 如果没有输入任何密码
                JOptionPane.showMessageDialog(this, "请重复用户密码");
                return;
            }

            //连接数据库
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            catch(Exception exw){
                Logger.getLogger(SignPanel.class.getName()).log(
                        Level.SEVERE, null, exw);
            }
            try{
                con = DriverManager.getConnection("jdbc:mysql://39.106.59.135/gobang?useSSL=FALSE&serverTimezone=UTC","root", "fzy125..");
            }
            catch(Exception exy){
                Logger.getLogger(SignPanel.class.getName()).log(
                        Level.SEVERE, null, exy);
            }
            //执行sql语句，查询出用户名和密码
            String sql="insert into usercount values ('"+idText+"','"+passwordText1+"')";
            String sql1;
            if(nameText==""){
                sql1="insert into userinfo values('"+idText+"','"+idText+"','"+100+"','"+0+"')";//初始化userinfo  rank初值为100 wincount 为0
            }else {
                sql1 = "insert into userinfo values('" + idText + "','" + nameText + "','" + 100 + "','" + 0 + "')";//初始化userinfo  rank初值为100 wincount 为0
            }
            stmt = con.createStatement();
            stmt.execute(sql);
            stmt.execute(sql1);
            JOptionPane.showMessageDialog(this,"注册成功");
            dispose();
    }catch(SQLException ex){
            Logger.getLogger(SignPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,"注册失败");
        }
    }
}
