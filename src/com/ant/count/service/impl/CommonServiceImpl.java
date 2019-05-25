package com.ant.count.service.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ant.count.dao.inte.BaseDaoI;
import com.ant.count.pojo.Account;
import com.ant.count.pojo.SystemParam;
import com.ant.count.pojo.User;
import com.ant.count.service.inte.CommonService;

/**
 * @描述 公用接口实现<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-17
 */
@SuppressWarnings("unchecked")
@Service
public class CommonServiceImpl implements CommonService{
	
	@Autowired
	BaseDaoI dao;

	public User getUserByUserId(String id) {
		User user = dao.getById(User.class, Integer.parseInt(id));
		return user;
	}

	public Account getAccountByUserId(String id) {
		String hql = "from Account where userId=:id";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("id", Integer.parseInt(id));
		Account account = dao.findUnique(hql, params);
		return account;
	}

	public Map<String, Object> getUserInfoByUserId(String id) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("tu.id loginUserId,tu.userName,tu.userRole,tu.userAccount,tu.createTime loginUserCreateTime,ta.* ");
		sql.append("FROM t_user tu ");
		sql.append("LEFT JOIN t_account ta ON tu.id = ta.userId ");
		sql.append("WHERE ");
		sql.append("tu.id = :id ");
		sql.append("AND ta.state != :state ");
		sql.append("AND tu.isDel = :isDel ");
		sql.append("AND tu.userRole = :userRole ");

		Map<String,Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("isDel", 0);
		params.put("state", 2);
		params.put("userRole", 0);
		
		List<?> users = dao.findBySql(sql.toString(), params);
		if(users.size()==0){
			return null;
		}else{
			Map<String, Object> user = (Map<String, Object>) users.get(0);
			return user;
		}
	}
	
	public int countActiveUsers(String id){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(tu.id) FROM t_user tu ");
		sql.append("LEFT JOIN ");
		sql.append("t_account ta ON tu.id = ta.userId ");
		sql.append("WHERE ");
		sql.append("tu.referenceId = :referenceId ");
		sql.append("AND ta.state >:state ");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("referenceId", id);
		params.put("state", 0);
		BigInteger count = dao.countBySql(sql.toString(), params);
		return count.intValue();
	}
	
	public int getPackageNumByUserId(Integer userId){
		StringBuffer hql = new StringBuffer();
		hql.append("from Account ");
		hql.append("where userId=:userId");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		
		Account account= (Account) dao.findUnique(hql.toString(), params);
		if(account==null){
			return 0;
		}
		return account.getPackageNum();
	}

	public SystemParam getValByKey(String key) {
		String hql = "from SystemParam where keyName=:keyName";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("keyName", key);
		SystemParam sysParam = dao.findUnique(hql, params);
		return sysParam;
	}

	public String getValStrByKey(String key) {
		String hql = "from SystemParam where keyName=:keyName";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("keyName", key);
		SystemParam sysParam = dao.findUnique(hql, params);
		return sysParam.getVal();
	}
}
