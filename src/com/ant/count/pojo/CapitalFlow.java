package com.ant.count.pojo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @描述 金币流水<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-19
 */
@Entity
@Table(name="t_capital_flow")
public class CapitalFlow implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private Integer userId;
	private String createDateTime;
	private Integer packageType;
	private double amount;
	private Integer type;
	private Integer buyUserId;
	private Integer activeUserId;
	private double fee;
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Integer getPackageType() {
		return packageType;
	}
	public void setPackageType(Integer packageType) {
		this.packageType = packageType;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getBuyUserId() {
		return buyUserId;
	}
	public void setBuyUserId(Integer buyUserId) {
		this.buyUserId = buyUserId;
	}
	public Integer getActiveUserId() {
		return activeUserId;
	}
	public void setActiveUserId(Integer activeUserId) {
		this.activeUserId = activeUserId;
	}
}
