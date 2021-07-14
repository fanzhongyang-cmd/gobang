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
    private ResultSet rs=null;//�洢���ݿ��ѯ���
    private JLabel userCountLabel=new JLabel("�û��˺�:");
    private JTextField userCountTextField=new JTextField(5);
    private JLabel userNameLabel=new JLabel("�ǳ�");
    private JTextField userNameTextField=new JTextField(5);
    private JLabel userPasswordLabel1=new JLabel("��������:");
    private JTextField userPasswordTextField1=new JTextField(5);
    private JLabel userPasswordLabel2=new JLabel("�ظ�����:");
    private JTextField userPasswordTextField2=new JTextField(5);
    private JButton commitButton=new JButton("ע��");
    private JButton backButton=new JButton("����");

    SignPanel(){
        setSize(250,200);
        setResizable(false);
        //setLocation(0,0);
        setTitle("�û�ע��");
        JPanel jPanel1=new JPanel();//����label��textfield
        JPanel jPanel2=new JPanel();//����Button

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
            //��ȡ�������ʵ������
            MainFrame mainFrame = (MainFrame) getParent().getParent();
            String idText=userCountTextField.getText();
            String nameText=userNameTextField.getText();
            String passwordText1=userPasswordTextField1.getText();
            String passwordText2=userPasswordTextField2.getText();
//            if(passwordText1.equals(passwordText2)) {
//                JOptionPane.showMessageDialog(this, "������������ͬ��");
//                return;
//            }
            if (idText.trim().isEmpty()) {// �������û���κο���ʾ���ַ�
                JOptionPane.showMessageDialog(this, "�������û��˺�");
                return;
            }
            if (passwordText1 == null || passwordText1.isEmpty()) {// ���û�������κ�����
                JOptionPane.showMessageDialog(this, "�������û�����");
                return;
            }
            if (passwordText2 == null || passwordText2.isEmpty()) {// ���û�������κ�����
                JOptionPane.showMessageDialog(this, "���ظ��û�����");
                return;
            }

            //�������ݿ�
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
            //ִ��sql��䣬��ѯ���û���������
            String sql="insert into usercount values ('"+idText+"','"+passwordText1+"')";
            String sql1;
            if(nameText==""){
                sql1="insert into userinfo values('"+idText+"','"+idText+"','"+100+"','"+0+"')";//��ʼ��userinfo  rank��ֵΪ100 wincount Ϊ0
            }else {
                sql1 = "insert into userinfo values('" + idText + "','" + nameText + "','" + 100 + "','" + 0 + "')";//��ʼ��userinfo  rank��ֵΪ100 wincount Ϊ0
            }
            stmt = con.createStatement();
            stmt.execute(sql);
            stmt.execute(sql1);
            JOptionPane.showMessageDialog(this,"ע��ɹ�");
            dispose();
    }catch(SQLException ex){
            Logger.getLogger(SignPanel.class.getName()).log(
                    Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,"ע��ʧ��");
        }
    }
}
