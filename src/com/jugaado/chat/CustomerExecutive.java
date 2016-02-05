package com.jugaado.chat;


public class CustomerExecutive implements Comparable<CustomerExecutive>{
	
	/**
	 * XMPP chat user id
	 */
	private String username;
	
	/**
	 * Count of active chats with customers
	 */
	private int customersCount;
	
	/**
	 * Executive's current availability status
	 */
	private String status;
	
	public CustomerExecutive(String username) {
		this.username = username;
		this.customersCount = 0;
		this.status = "unavailable";
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getCustomersCount() {
		return customersCount;
	}
	
	public void setCustomersCount(int customersCount) {
		this.customersCount = customersCount;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Sort the executive based on their availability and the
	 * count of active customers being handled 
	 */
	@Override
	public int compareTo(CustomerExecutive o) {
		if(this.status.equals("available") && o.getStatus().equals("available")){
			if(this.customersCount<o.getCustomersCount()){
				return -1;
			}
			else if(this.customersCount==o.getCustomersCount()){
				return 0;
			}
			else{
				return 1;
			}
		}else if(this.status.equals("available") && o.getStatus().equals("unavailable")){
			return -1;
		}
		else {
			return 1;
		}
	}
	
	@Override
	public String toString() {
		return "CustomerExecutive [username=" + username + ", customersCount="
				+ customersCount + ", status=" + status + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof CustomerExecutive){
			return (this.username.equals(((CustomerExecutive)obj).getUsername()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
	
}
