package com.ant.count.service.inte;

import java.util.Map;

import com.ant.count.pojo.Account;
import com.ant.count.pojo.SystemParam;
import com.ant.count.pojo.User;

/**
 * @描述 公用接口<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-17
 */
public interface CommonService {

	/**
	 * @描述 根据ID获取用户信息<br>
	 * @param id 用户ID
	 * @return 用户信息
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public User getUserByUserId(String id);
	
	/**
	 * 
	 * @描述 根据用户ID获取账号信息<br>
	 * @param id 用户ID
	 * @return 账号信息
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public Account getAccountByUserId(String id);
	
	/**
	 * @描述 根据ID获取用户的所有信息<br>
	 * @param id 用户ID
	 * @return 用户信息及账号信息
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public Map<String,Object> getUserInfoByUserId(String id);

	/**
	 * @描述 根据推荐人ID查询已激活人数<br>
	 * @param id 推荐人ID
	 * @return 人数
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public int countActiveUsers(String id);

	/**
	 * @描述 根据用户ID查询用户增值包数量<br>
	 * @param userId 用户ID
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	public int getPackageNumByUserId(Integer userId);
	
	/**
	 * @描述 根据Key值获取系统参数的值<br>
	 * @param key 值
	 * @return 值
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public SystemParam getValByKey(String key);
	
	/**
	 * @描述 根据Key值获取系统参数的值<br>
	 * @param key 值
	 * @return 值
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-19
	 */
	public String getValStrByKey(String key);
}
