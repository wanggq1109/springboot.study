package springboot.start.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springboot.start.ApplicationStart;
import springboot.start.conf.MysqlDataSource1Config;
import springboot.start.conf.MysqlDataSource2Config;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={ApplicationStart.class,MysqlDataSource1Config.class,MysqlDataSource2Config.class})
public class DbServiceTest {
	@Autowired
	DbService dbService;
	
	@Test
	public void testSelectUserByUserId(){
		dbService.selectUserById(0);
	}
	@Test
	public void testSelectSubjectById(){
		dbService.selectSubjectById(0);
	}
}
