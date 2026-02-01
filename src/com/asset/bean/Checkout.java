package com.asset.bean;

import java.util.Date;

public class Checkout {

    private int checkoutID;
    private String assetTag;
    private String employeeID;
    private Date checkoutDate;
    private Date dueDate;
    private Date returnDate;
    private String status;
    
	public int getCheckoutID() {
		return checkoutID;
	}
	public void setCheckoutID(int checkoutID) {
		this.checkoutID = checkoutID;
	}
	public String getAssetTag() {
		return assetTag;
	}
	public void setAssetTag(String assetTag) {
		this.assetTag = assetTag;
	}
	public String getEmployeeID() {
		return employeeID;
	}
	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}
	public Date getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Date getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
