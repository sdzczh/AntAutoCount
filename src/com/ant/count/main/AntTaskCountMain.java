package com.ant.count.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ant.count.service.inte.AntTaskCountService;
import com.ant.count.util.DateUtils;

/**
 * @描述 定时任务主类<br>
 * @author 陈之晶
 * @版本 v1.0.0
 * @日期 2017-6-29
 */
@Component("task")  
public class AntTaskCountMain {
	
	@Autowired
	AntTaskCountService service;
	
//	@Scheduled(fixedDelay=300000)
	@Scheduled(cron="0 0 23 * * ?")
	public void start(){
		System.out.println("------------------------------------------当前时间:"+DateUtils.getCurrentTimeStr());
		String date = DateUtils.getCurrentDateStr();
		String[] dates = new String []{date};
		for (String dateStr : dates) {
			System.out.println("---计算开始:"+dateStr);
			service.startCount(dateStr);
		}
	}
}
