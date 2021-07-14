package com.mr.gobang;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Time;

/**
 * 用户类
 */
public class UserBean implements Serializable {
	private String userID;
	private int rank;
	private int win_count;
	protected String name ;// 用户昵称
	protected InetAddress host;// 用户IP地址

	protected int port=9527;


	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setUserID(String id){
		this.userID=id;
	}
	public String getUserID(){
		return userID;
	}
	public void setRank(int rank){
		this.rank=rank;
	}
	public int getRank(){
		return this.rank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return getName();
	}

	public int getWin_count() {
		return win_count;
	}
	public void setWin_count(int count){
		this.win_count=count;
	}
	public void winCountAdd(){
		win_count++;
	}
}
