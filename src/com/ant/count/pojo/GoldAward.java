package com.ant.count.pojo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @描述 奖励金币<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-19
 */
@Entity
@Table(name="t_gold_award")
public class GoldAward implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private Integer userId;
	private String createDateTime;
	private Double amount;
	private Integer amountType;
	private Integer childUserId;
	private Integer profitType;
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
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Integer getAmountType() {
		return amountType;
	}
	public void setAmountType(Integer amountType) {
		this.amountType = amountType;
	}
	public Integer getChildUserId() {
		return childUserId;
	}
	public void setChildUserId(Integer childUserId) {
		this.childUserId = childUserId;
	}
	public Integer getProfitType() {
		return profitType;
	}
	public void setProfitType(Integer profitType) {
		this.profitType = profitType;
	}
	
}
