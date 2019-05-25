package com.ant.count.service.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ant.count.dao.inte.BaseDaoI;
import com.ant.count.glob.AntSystemParams;
import com.ant.count.glob.GolbParams;
import com.ant.count.pojo.Account;
import com.ant.count.pojo.CapitalFlow;
import com.ant.count.pojo.GoldAward;
import com.ant.count.pojo.ManageProfitHistory;
import com.ant.count.pojo.PromotionAward;
import com.ant.count.pojo.StaticProfit;
import com.ant.count.pojo.User;
import com.ant.count.service.inte.AntTaskCountService;
import com.ant.count.service.inte.CommonService;
import com.ant.count.util.DateUtils;
import com.ant.count.util.MatchUtils;

/**
 * @描述 定时统计任务业务接口实现<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-29
 */
@Transactional
@Service
@SuppressWarnings("unchecked")
public class AntTaskCountServiceImpl implements AntTaskCountService {

	@Autowired
	BaseDaoI dao;
	
	@Autowired
	CommonService common;
	
	/**
	 * @描述 统计开始方法<br>
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	public void startCount(String date) {
		try {
			long startTime = System.currentTimeMillis();
			String tableDate = date.replace("-", "");
			
			String tableName = createTableName(tableDate);//表名
			
			craetTable(tableName);//创建表
			
			List<User> users = getUsers();//获取所有用户
			
			if(users.size()>0){//有用户
			
					matchAndSaveProfitDay(users,date,tableName);//计算并保存用户当天收益数据

					List<?> profitDays = getProfitDays(tableName);//获取用户当天收益数据;

					if(profitDays!=null){//如果有用户当天收益数据,计算机并更新
						
						dispatcherLevel1(profitDays,tableName);//计算机一级用户收益提成*衰减值

						dispatcherLevel2(profitDays,tableName);//计算机二级用户收益提成*衰减值
						
						dispatcherLevel3(profitDays,tableName);//计算机三级用户收益提成*衰减值
						
						profitDays = getProfitDays(tableName);//重新加载数据内容;
						
						dispatcherPackageProfit(profitDays,tableName,date);//分配静态收益
						
						dispatcherChildPackageProfit(profitDays,tableName,date);//分配下级用户静态收益
						
						updateUserProfit(profitDays,tableName);//修改用户收益数据
						
						updateManagerProfit(profitDays);//特殊账号收益提取
					}
					
					long endTime = System.currentTimeMillis();
					long time = endTime-startTime;
					System.out.println("---计算结束:"+(time/1000)+"秒");
			}
				
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}

	//TODO 1
	/**
	 * @描述 创建表名<br>
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	private String createTableName(String date){
		String tableName = GolbParams.TABLENAME+date;
		return tableName;
	}
	
	//TODO 2
	/**
	 * @描述 创建表<br>
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	private void craetTable(String tableName){
		
		/*检测是否表已存在*/
		BigInteger i = dao.countBySql("SELECT DISTINCT COUNT(t.TABLE_NAME) FROM information_schema.TABLES t WHERE t.TABLE_NAME='"+tableName+"'");
		if(i.intValue()>0){
			String dropSql = "DROP TABLE IF EXISTS "+tableName;
			dao.executeSql(dropSql);
			System.out.println("-------表【"+tableName+"】已存在，删除并重建");
		}
		StringBuffer createSql = new StringBuffer();
		createSql.append("CREATE TABLE `"+tableName+"` (");
		createSql.append("  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',");
		createSql.append("  `userId` int(11) NOT NULL COMMENT '用户ID',");
		createSql.append("  `parentId` int(11) NOT NULL COMMENT '推荐人ID',");
		createSql.append("  `signProfit` double(11,2) NOT NULL COMMENT '签到收益',");
		createSql.append("  `activeProfit` double(11,2) NOT NULL COMMENT '激活用户收益',");
		createSql.append("  `packageProfit` double(11,2) NOT NULL COMMENT '增值包收益*衰减值',");
		createSql.append("  `level_1_childPackageProfit` double(11,2) NOT NULL COMMENT '一级级用户收益提成*衰减值',");
		createSql.append("  `level_2_childPackageProfit` double(11,2) NOT NULL COMMENT '二级级用户收益提成*衰减值',");
		createSql.append("  `level_3_childPackageProfit` double(11,2) NOT NULL COMMENT '三级级用户收益提成*衰减值',");
		createSql.append("  `profit` double(11,2) NOT NULL COMMENT '一天的总收益',");
		createSql.append("  PRIMARY KEY (`id`)");
		createSql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8 ");
		dao.executeSql(createSql.toString());
		System.out.println("---建表:【"+tableName+"】");
	}
	
	//TODO 3
	/**
	 * @描述 获取所有会员用户<br>
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	private List<User> getUsers(){
		StringBuffer hql = new StringBuffer();
		hql.append("from User where isDel=:isDel ");
		hql.append("and (userRole=:userRole or userRole=:managerRole) ");
		hql.append("order by id asc");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isDel", 0);
		params.put("userRole", 0);
		params.put("managerRole", 3);
		List<User>users = dao.find(hql.toString(), params);
		System.out.println("---获取所有用户:"+users.size());
		return users;
	}	
	
	//TODO 4
	/**
	 * @描述 计算机增值包收益<br>
	 * @param packageNum
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-29
	 */
	private Double matchPackageProfit(Integer packageNum){
//		double packageProfit = MatchUtils.doCalculationB(String.valueOf(packageNum), "10", "(1+1+0.1*(n-1))*(n/2)*(y*0.01)");
		double packageProfit = MatchUtils.doCalculationB(String.valueOf(packageNum), "10", "(0.2+0.2+0.2*(n-1))*(n/2)*(y*0.01)");
		return packageProfit;
	}
	
	//TODO 5
	/**
	 * @描述 用户签到、激活用户奖励<br>
	 * @param userId 用户ID
	 * @param date 日期
	 * @param type 类型 0:签到，1:激活用户
	 * @return 
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-30
	 */
	private Double getReward(Integer userId,String date,Integer type){
		String startTime = date+" 00:00:00";
		String endTime = date+" 23:59:59";
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("IFNULL(SUM(tp.amount),0) amount ");
		sql.append("FROM t_profit tp ");
		sql.append("WHERE tp.userId = :userId ");
		sql.append("AND (tp.createDate ");
		sql.append("BETWEEN  :startTime AND :endTime )");
		sql.append("AND type=:type ");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("type", type);
		List<?> sumResult = dao.findBySql(sql.toString(), params);
		Map<String,Object> sumMap = (Map<String, Object>) sumResult.get(0);
		Double reward = Double.parseDouble(sumMap.get("amount").toString());
		return reward;
	}	
	
	//TODO 6
	/**
	 * @描述 收益衰减计算<br>
	 * @param userId 用户ID
	 * @param profit 增值包总收益
	 * @return 衰减计算后收益
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-30
	 */
	private Double matchProfit(Integer userId,Double profit){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("ta.activeTime ");
		sql.append("FROM ");
		sql.append("t_user tu ");
		sql.append("LEFT JOIN t_account ta ON tu.id = ta.userId ");
		sql.append("WHERE tu.referenceId = :id ");
		sql.append("AND ta.state != :state ");
		sql.append("ORDER BY ta.activeTime DESC");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("id", userId);
		params.put("state", 0);
		List<?> resutls = dao.findBySql(sql.toString(), params, 0, 1);
		int day = 0;
		String dateStr = null;
		if(resutls.size()!=0){
			Map<String,String> result = (Map<String, String>) resutls.get(0);
			dateStr = result.get("activeTime").toString();
		}else{
			Account account = common.getAccountByUserId(userId.toString());
			dateStr = account.getActiveTime();
		}
		day = DateUtils.daysBetween(dateStr);
		int attenuation = Integer.parseInt(common.getValStrByKey(AntSystemParams.ATTENUATION));
		
		if(day<=attenuation || attenuation==0){
			return profit;
		}else{
			if(day>attenuation&&day<=(attenuation*2)){
				profit = MatchUtils.multiply(MatchUtils.divide(profit, 3d),2d);
			}
			if(day>(attenuation*2)&&day<=(attenuation*3)){
				profit = MatchUtils.multiply(MatchUtils.divide(profit, 3d),1d);
			}
			
			if(day>(attenuation*3)){
				profit = 0d;
			}
		}
		return profit;
	}
	
	//TODO 7
	/**
	 * @描述 保存用户当天收益数据<br>
	 * @param userId 用户ID 
	 * @param parentId 推荐人ID
	 * @param signProfit 签到收益
	 * @param activeProfit 激活用户收益
	 * @param packageProfit 增值包收益*衰减值
	 * @param level_1_childPackageProfit 一级级用户收益提成*衰减值
	 * @param level_2_childPackageProfit 二级级用户收益提成*衰减值
	 * @param level_3_childPackageProfit 三级级用户收益提成*衰减值
	 * @param profit 一天总收益
	 * @param tableName 表名
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void saveProfitDay(Integer userId, Integer parentId, Double signProfit, Double activeProfit, Double packageProfit, Double level_1_childPackageProfit,Double level_2_childPackageProfit,Double level_3_childPackageProfit,Double profit, String tableName){
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO "+tableName+" ");
		sql.append("(userId,parentId,signProfit,activeProfit,packageProfit,level_1_childPackageProfit,level_2_childPackageProfit,level_3_childPackageProfit,profit) ");
		sql.append("VALUES(");
		sql.append(":userId,");
		sql.append(":parentId,");
		sql.append(":signProfit,");
		sql.append(":activeProfit,");
		sql.append(":packageProfit,");
		sql.append(":level_1_childPackageProfit,");
		sql.append(":level_2_childPackageProfit,");
		sql.append(":level_3_childPackageProfit,");
		sql.append(":profit ");
		sql.append(")");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("parentId", parentId);
		params.put("signProfit", signProfit);
		params.put("activeProfit", activeProfit);
		params.put("packageProfit", packageProfit);
		params.put("level_1_childPackageProfit", level_1_childPackageProfit);
		params.put("level_2_childPackageProfit", level_2_childPackageProfit);
		params.put("level_3_childPackageProfit", level_3_childPackageProfit);
		params.put("profit", profit);
		dao.executeSql(sql.toString(), params);
	}

	//TODO 8
	/**
	 * @描述 计算并保存用户当天收益数据<br>
	 * @param users
	 * @param date
	 * @param tableName
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-30
	 */
	private void matchAndSaveProfitDay(List<User> users,String date,String tableName) {
		for (User user : users) {
			Account account = common.getAccountByUserId(user.getId().toString());//获取账号信息
			if(account==null || account.getState()==0){
				continue;
			}
			
			Integer userId = user.getId(); //用户ID
			Integer parentId = user.getReferenceId();//推荐人ID
			Double signProfit = getReward(userId,date,0);//签到奖励
			Double activeProfit = getReward(userId,date,1);//激活用户奖励
			Double allPackageProfit = matchPackageProfit(account.getPackageNum());//增值包收益
			Double packageProfit = matchProfit(userId,allPackageProfit);//增值包收益*衰减值
			Double level_1_childPackageProfit = 0d;
			Double level_2_childPackageProfit = 0d;
			Double level_3_childPackageProfit = 0d;
			Double profit = 0d;//一天的总收益
			
			if(account.getState()==2){
				allPackageProfit = 0d;
			}
			
			saveProfitDay(userId, parentId, signProfit, activeProfit, packageProfit, level_1_childPackageProfit,level_2_childPackageProfit,level_3_childPackageProfit,profit,tableName);//保存用户当天收益数据
		}
		System.out.println("---计算并保存用户当天收益数据");
	}	

	//TODO 9
	/**
	 * @描述 获取用户当天收益数据<br>
	 * @param tableName
	 * @return
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-6-30
	 */
	private List<?> getProfitDays(String tableName){
		StringBuffer sql = new StringBuffer();
		sql.append("select * from "+tableName);
		List<?> profitDays = dao.findBySql(sql.toString());
		if(profitDays.size()==0){
			return null;
		}
		System.out.println("---获取用户当天收益数据");
		return profitDays;
	}
	
	//TODO 10
	/**
	 * @描述 一级用户收益计算<br>
	 * @param obj
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void dispatcherLevel1(List<?> profitDays,String tableName){
		Double level_1 = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_PROFIT_LEVEL_1));
		
		Integer firstCount = Integer.parseInt(common.getValStrByKey(AntSystemParams.FIRST_LEVEL_COUNT));
		
		for(Object obj:profitDays){
			Map<String,Object> profitDay = (Map<String, Object>) obj;
			Integer profitId = Integer.parseInt(profitDay.get("id").toString());
			Integer userId = Integer.parseInt(profitDay.get("userId").toString());
			Double level_1_childPackageProfit = Double.parseDouble(profitDay.get("level_1_childPackageProfit").toString());
			
			StringBuffer getLevel1Sql = new StringBuffer();
			getLevel1Sql.append("SELECT");
			getLevel1Sql.append(" * FROM "+tableName+" tp ");
			getLevel1Sql.append("WHERE tp.parentId = :parentId ");
			
			Map<String,Object> getLevel1SqlParams = new HashMap<String, Object>();
			getLevel1SqlParams.put("parentId", userId);
			
			List<?> childProfits = dao.findBySql(getLevel1Sql.toString(), getLevel1SqlParams);
			if(childProfits!=null&&childProfits.size()>=firstCount){
				for (Object ot : childProfits) {
					Map<String,Object> childProfitDay = (Map<String, Object>) ot;
					
					/*小推大计算机*/
					Integer childUserId = Integer.parseInt(childProfitDay.get("userId").toString());
					int selfPackageNum = common.getPackageNumByUserId(userId);
					int childPackageNum = common.getPackageNumByUserId(childUserId);
					Double packageProfit = 0d;
					if(selfPackageNum>childPackageNum){//推荐人增值包数量多
						packageProfit = Double.parseDouble(childProfitDay.get("packageProfit").toString());
					}else{//一级会员增值包数量多
						packageProfit = Double.parseDouble(profitDay.get("packageProfit").toString());
					}
					Double award = MatchUtils.multiply(packageProfit, level_1);
					level_1_childPackageProfit = MatchUtils.add(level_1_childPackageProfit, award);
				}
				
				level_1_childPackageProfit = matchProfit(userId, level_1_childPackageProfit);//一级收益最终衰减计算
				
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("UPDATE ");
				updateSql.append(tableName+" ");
				updateSql.append("SET level_1_childPackageProfit = :level_1_childPackageProfit ");
				updateSql.append(" WHERE id = :id ");
				
				Map<String,Object> updateSqlParams = new HashMap<String, Object>();
				updateSqlParams.put("level_1_childPackageProfit", level_1_childPackageProfit);
				updateSqlParams.put("id", profitId);
				
				dao.executeSql(updateSql.toString(), updateSqlParams);
			}
			
		}
		System.out.println("---计算机一级用户收益提成*衰减值");
	}
	
	//TODO 11
	/**
	 * @描述 二级用户收益计算<br>
	 * @param obj
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void dispatcherLevel2(List<?> profitDays,String tableName){
		Double level_2 = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_PROFIT_LEVEL_2));
		
		Integer secondCount = Integer.parseInt(common.getValStrByKey(AntSystemParams.SECOND_LEVEL_COUNT));
		
		for(Object obj:profitDays){
			Map<String,Object> profitDay = (Map<String, Object>) obj;
			Integer profitId = Integer.parseInt(profitDay.get("id").toString());
			Integer userId = Integer.parseInt(profitDay.get("userId").toString());
			Double level_2_childPackageProfit = Double.parseDouble(profitDay.get("level_2_childPackageProfit").toString());
			
			StringBuffer getLevel2Sql = new StringBuffer();
			getLevel2Sql.append("SELECT ");
			getLevel2Sql.append("tp.* ");
			getLevel2Sql.append("FROM "+tableName+" tp ");
			getLevel2Sql.append("INNER JOIN ");
			getLevel2Sql.append("(");
			getLevel2Sql.append("SELECT * FROM "+tableName+" ");
			getLevel2Sql.append(" WHERE parentId = :parentId ");
			getLevel2Sql.append(") u  ");
			getLevel2Sql.append("ON tp.parentId = u.userId ");
			
			Map<String,Object> getLevel1SqlParams = new HashMap<String, Object>();
			getLevel1SqlParams.put("parentId", userId);
			
			List<?> childProfits = dao.findBySql(getLevel2Sql.toString(), getLevel1SqlParams);
			if(childProfits!=null&&childProfits.size()>=secondCount){
				for (Object ot : childProfits) {
					Map<String,Object> childProfitDay = (Map<String, Object>) ot;
					Double packageProfit = Double.parseDouble(childProfitDay.get("packageProfit").toString());//获取总收益
					Double award = MatchUtils.multiply(packageProfit, level_2);
					level_2_childPackageProfit = MatchUtils.add(level_2_childPackageProfit, award);
				}
				
				level_2_childPackageProfit = matchProfit(userId, level_2_childPackageProfit);//二级收益最终衰减计算
				
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("UPDATE ");
				updateSql.append(tableName+" ");
				updateSql.append("SET level_2_childPackageProfit = :level_2_childPackageProfit ");
				updateSql.append(" WHERE id = :id ");
				
				Map<String,Object> updateSqlParams = new HashMap<String, Object>();
				updateSqlParams.put("level_2_childPackageProfit", level_2_childPackageProfit);
				updateSqlParams.put("id", profitId);
				
				dao.executeSql(updateSql.toString(), updateSqlParams);
			}
			
		}
		System.out.println("---计算机二级用户收益提成*衰减值");
	}
	
	//TODO 12
	/**
	 * @描述 三级用户收益计算<br>
	 * @param obj
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void dispatcherLevel3(List<?> profitDays,String tableName){
		Double level_3 = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_PROFIT_LEVEL_3));
		
		Integer thirdCount = Integer.parseInt(common.getValStrByKey(AntSystemParams.THIRD_LEVEL_COUNT));
		
		for(Object obj:profitDays){
			Map<String,Object> profitDay = (Map<String, Object>) obj;
			Integer profitId = Integer.parseInt(profitDay.get("id").toString());
			Integer userId = Integer.parseInt(profitDay.get("userId").toString());
			Double level_3_childPackageProfit = Double.parseDouble(profitDay.get("level_3_childPackageProfit").toString());
			
			StringBuffer getLevel3Sql = new StringBuffer();
			getLevel3Sql.append("SELECT ");
			getLevel3Sql.append("t.* ");
			getLevel3Sql.append("FROM "+tableName+ " t ");
			getLevel3Sql.append("INNER JOIN (");
			getLevel3Sql.append("SELECT ");
			getLevel3Sql.append("tp.* ");
			getLevel3Sql.append("FROM "+tableName+" tp ");
			getLevel3Sql.append("INNER JOIN ");
			getLevel3Sql.append("(");
			getLevel3Sql.append("SELECT * FROM "+tableName+" ");
			getLevel3Sql.append(" WHERE parentId = :parentId ");
			getLevel3Sql.append(") u  ");
			getLevel3Sql.append("ON tp.parentId = u.userId ");
			getLevel3Sql.append(") p ON t.parentId = p.userId ");
			
			Map<String,Object> getLevel1SqlParams = new HashMap<String, Object>();
			getLevel1SqlParams.put("parentId", userId);
			
			List<?> childProfits = dao.findBySql(getLevel3Sql.toString(), getLevel1SqlParams);
			if(childProfits!=null&&childProfits.size()>=thirdCount){
				for (Object ot : childProfits) {
					Map<String,Object> childProfitDay = (Map<String, Object>) ot;
					Double packageProfit = Double.parseDouble(childProfitDay.get("packageProfit").toString());//获取总收益
					Double award = MatchUtils.multiply(packageProfit, level_3);
					level_3_childPackageProfit = MatchUtils.add(level_3_childPackageProfit, award);
				}
				
				level_3_childPackageProfit = matchProfit(userId, level_3_childPackageProfit);//三级收益最终衰减计算
				
				StringBuffer updateSql = new StringBuffer();
				updateSql.append("UPDATE ");
				updateSql.append(tableName+" ");
				updateSql.append("SET level_3_childPackageProfit = :level_3_childPackageProfit ");
				updateSql.append(" WHERE id = :id ");
				
				Map<String,Object> updateSqlParams = new HashMap<String, Object>();
				updateSqlParams.put("level_3_childPackageProfit", level_3_childPackageProfit);
				updateSqlParams.put("id", profitId);
				
				dao.executeSql(updateSql.toString(), updateSqlParams);
			}
			
		}
		System.out.println("---计算机三级用户收益提成*衰减值");
	}

	//TODO 13
	/**
	 * @描述 分配静态收益(每天增值包收益20%存入Z钱包；80%存入J钱包。)<br>
	 * @param profitDays 数据集合
	 * @param tableName 表名
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void dispatcherPackageProfit(List<?> profitDays, String tableName,String date) {
		String createDate = date+" 23:00:00";
		for (Object object : profitDays) {
			Map<String,Object> profitInfo = (Map<String, Object>) object;

			Integer userId = Integer.parseInt(profitInfo.get("userId").toString());
			int packageNum = common.getPackageNumByUserId(userId);
			
			Double profit = Double.parseDouble(profitInfo.get("packageProfit").toString());
			Double toJVal = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_SELFE_PROFIT_TO_J));
			Double toZVal = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_SELFE_PROFIT_TO_Z));
			Double toJ = MatchUtils.multiply(profit, toJVal);
			Double toZ = MatchUtils.multiply(profit, toZVal);
			
			/*修改用户J钱包和Z钱包*/
			Account account = common.getAccountByUserId(String.valueOf(userId));
			account.setPackageJ(MatchUtils.add(account.getPackageJ(), toJ));
			account.setPackageZ(MatchUtils.add(account.getPackageZ(), toZ));
			dao.update(account);
			
			/*记录J钱包静态收益*/
			StaticProfit staticProfit = new StaticProfit();
			staticProfit.setAmount(toJ);
			staticProfit.setCreateDate(createDate);
			staticProfit.setPackageNum(packageNum);
			staticProfit.setTotal(profit);
			staticProfit.setUserId(userId);
			dao.save(staticProfit);
			
			/*记录J钱包金币流水*/
			CapitalFlow cap = new CapitalFlow();
			cap.setAmount(toJ);
			cap.setCreateDateTime(createDate);
			cap.setPackageType(0);
			cap.setUserId(userId);
			cap.setType(0);
			dao.save(cap);
			
			/*记录Z钱包静态收益*/
			GoldAward award = new GoldAward();
			award.setAmount(toZ);
			award.setAmountType(2);
			award.setCreateDateTime(createDate);
			award.setProfitType(0);
			award.setUserId(userId);
			dao.save(award);
			
			/*记录Z钱包金币流水*/
			CapitalFlow flow = new CapitalFlow();
			flow.setAmount(toZ);
			flow.setCreateDateTime(createDate);
			flow.setPackageType(2);
			flow.setUserId(userId);
			flow.setType(0);
			dao.save(flow);
			
		}
		System.out.println("---分配静态收益");
	}
	
	//TODO 14
	/**
	 * @描述 分配下级用户动态收益(其中30%存入Z钱包，70%存入D钱包)<br>
	 * @param profitDays 数据集合
	 * @param tableName 表名
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void dispatcherChildPackageProfit(List<?> profitDays,String tableName,String date) {
		String createDate = date+" 23:00:00";
		for (Object object : profitDays) {
			Map<String,Object> profitInfo = (Map<String, Object>) object;

			Integer userId = Integer.parseInt(profitInfo.get("userId").toString());
			
			Double level_1 = Double.parseDouble(profitInfo.get("level_1_childPackageProfit").toString());
			Double level_2 = Double.parseDouble(profitInfo.get("level_2_childPackageProfit").toString());
			Double level_3 = Double.parseDouble(profitInfo.get("level_3_childPackageProfit").toString());
			
			Double[] levels = new Double[]{level_1,level_2,level_3};
			
			Double toDVal = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_CHILD_PROFIT_TO_D));
			Double toZVal = Double.parseDouble(common.getValStrByKey(AntSystemParams.PACKAGE_CHILD_PROFIT_TO_Z));
			
			Double amountD = 0d;
			Double amountZ = 0d;
			
			for (int i = 0; i < levels.length; i++) {
				Double profit = levels[i];
				Double toD = MatchUtils.multiply(profit, toDVal);
				Double toZ = MatchUtils.multiply(profit, toZVal);
				
				/*记录D钱包推广奖励*/
				PromotionAward promotionAward = new PromotionAward();
				promotionAward.setAmount(toD);
				promotionAward.setCreateDate(createDate);
				promotionAward.setTeamLevel(i);
				promotionAward.setTotal(profit);
				promotionAward.setUserId(userId);
				dao.save(promotionAward);
				
				/*记录D钱包金币流水*/
				CapitalFlow cap = new CapitalFlow();
				cap.setAmount(toD);
				cap.setCreateDateTime(createDate);
				cap.setPackageType(1);
				cap.setUserId(userId);
				cap.setType(1);
				dao.save(cap);
				
				/*记录Z钱包动态收益*/
				GoldAward award = new GoldAward();
				award.setAmount(toZ);
				award.setAmountType(2);
				award.setCreateDateTime(createDate);
				award.setProfitType(1);
				award.setUserId(userId);
				dao.save(award);
				
				/*记录Z钱包金币流水*/
				CapitalFlow flow = new CapitalFlow();
				flow.setAmount(toZ);
				flow.setCreateDateTime(createDate);
				flow.setPackageType(2);
				flow.setUserId(userId);
				flow.setType(1);
				dao.save(flow);
				
				amountD = MatchUtils.add(amountD, toD);
				amountZ = MatchUtils.add(amountZ, toZ);
			}
			
			/*修改用户J钱包和Z钱包*/
			Account account = common.getAccountByUserId(String.valueOf(userId));
			account.setPackageD(MatchUtils.add(account.getPackageD(), amountD));
			account.setPackageZ(MatchUtils.add(account.getPackageZ(), amountZ));
			dao.update(account);
		}
		System.out.println("---分配下级用户静态收益");
		
	}

	//TODO 15
	/**
	 * @描述 修改用户收益数据<br>
	 * @param profitDays 数据集合
	 * @param tableName 表名
	 * @author 陈之晶
	 * @版本 v1.0.0
	 * @日期 2017-7-3
	 */
	private void updateUserProfit(List<?> profitDays, String tableName) {
		for (Object object : profitDays) {
			Map<String,Object> profitInfo = (Map<String, Object>) object;
			Integer profitId = Integer.parseInt(profitInfo.get("id").toString());
			Integer userId = Integer.parseInt(profitInfo.get("userId").toString());
			Double profit = Double.parseDouble(profitInfo.get("profit").toString());
			Double packageProfit = Double.parseDouble(profitInfo.get("packageProfit").toString());
			Double level_1 = Double.parseDouble(profitInfo.get("level_1_childPackageProfit").toString());
			Double level_2 = Double.parseDouble(profitInfo.get("level_2_childPackageProfit").toString());
			Double level_3 = Double.parseDouble(profitInfo.get("level_3_childPackageProfit").toString());
			Double signProfit = Double.parseDouble(profitInfo.get("signProfit").toString());
			Double activeProfit = Double.parseDouble(profitInfo.get("activeProfit").toString());
			
			profit = MatchUtils.add(MatchUtils.add(signProfit, activeProfit),MatchUtils.add(packageProfit, MatchUtils.add(level_1, MatchUtils.add(level_2, level_3))));
			StringBuffer updateSql = new StringBuffer();
			updateSql.append("UPDATE ");
			updateSql.append(tableName+" ");
			updateSql.append("SET profit = :profit ");
			updateSql.append(" WHERE id = :id ");
			
			Map<String,Object> updateSqlParams = new HashMap<String, Object>();
			updateSqlParams.put("profit", profit);
			updateSqlParams.put("id", profitId);
			dao.executeSql(updateSql.toString(), updateSqlParams);
			
			Account account = common.getAccountByUserId(String.valueOf(userId));
			account.setProfit(packageProfit);
			dao.update(account);
			
		}
		System.out.println("---修改用户收益数据");
	}
	
	private void updateManagerProfit(List<?> profitDays){//TODO 特殊账号收益提取
		String hql = "from User where userRole = :userRole";
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("userRole", 3);
		User user = dao.findUnique(hql, params);
		if(user!=null){
			String createDate = DateUtils.getCurrentTimeStr();
			Double profit = 0d;
			for (Object object : profitDays) {
				Map<String,Object> profitInfo = (Map<String, Object>) object;
				Integer userId = Integer.parseInt(profitInfo.get("userId").toString());
				if(userId!=user.getId()){
					Double userProfit = Double.parseDouble(profitInfo.get("packageProfit").toString());
					Double profitPercentage = Double.parseDouble(common.getValStrByKey(AntSystemParams.MANAGER_PROFIT_PERCENTAGE));
					profit = MatchUtils.add(profit, MatchUtils.multiply(userProfit, profitPercentage));
				}
			}
			
			ManageProfitHistory history = new ManageProfitHistory();
			history.setCreateDate(createDate);
			history.setProfit(profit);
			history.setUserId(user.getId());
			history.setType(0);
			dao.save(history);
		}
		System.out.println("---特殊账号收益提取");
	}

}
