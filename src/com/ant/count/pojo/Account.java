package com.ant.count.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ant.count.util.DateUtils;

@Entity
@Table(name="t_account")
@SuppressWarnings("unused")
public class Account {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private Integer userId;
	private Integer state;
	private double packageJ;
	private double packageD;
	private double packageZ;
	private String alipayNumber;
	private String webcatNumber;
	private String bankName;
	private String bankNumber;
	private Integer packageNum;
	private double profit;
	private String createTime;
	private String updateTime;
	private String activeTime = DateUtils.getCurrentDateStr();
	
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
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public double getPackageJ() {
		return packageJ;
	}
	public void setPackageJ(double packageJ) {
		this.packageJ = packageJ;
	}
	public double getPackageD() {
		return packageD;
	}
	public void setPackageD(double packageD) {
		this.packageD = packageD;
	}
	public double getPackageZ() {
		return packageZ;
	}
	public void setPackageZ(double packageZ) {
		this.packageZ = packageZ;
	}
	public String getAlipayNumber() {
		return alipayNumber;
	}
	public void setAlipayNumber(String alipayNumber) {
		this.alipayNumber = alipayNumber;
	}
	public String getWebcatNumber() {
		return webcatNumber;
	}
	public void setWebcatNumber(String webcatNumber) {
		this.webcatNumber = webcatNumber;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankNumber() {
		return bankNumber;
	}
	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}
	public Integer getPackageNum() {
		return packageNum;
	}
	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}
	@Override
	public String toString() {
		return "Account [id=" + id + ", userId=" + userId + ", state=" + state
				+ ", packageJ=" + packageJ + ", packageD=" + packageD
				+ ", packageZ=" + packageZ + ", alipayNumber=" + alipayNumber
				+ ", webcatNumber=" + webcatNumber + ", bankName=" + bankName
				+ ", bankNumber=" + bankNumber + ", packageNum=" + packageNum
				+ ", profit=" + profit + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + ", activeTime=" + activeTime
				+ "]";
	}
	
	
}
