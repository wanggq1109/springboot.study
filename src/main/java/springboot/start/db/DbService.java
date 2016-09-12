package springboot.start.db;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import springboot.start.db.company1.mapper.TUserMapper;
import springboot.start.db.company1.pojo.TUser;
import springboot.start.db.company1.pojo.TUserExample;
import springboot.start.db.company1.pojo.TUserExample.Criteria;
import springboot.start.db.company2.mapper.TSubjectMapper;
import springboot.start.db.company2.pojo.TSubject;
import springboot.start.db.company2.pojo.TSubjectExample;

@Service
public class DbService {
	@Autowired
	TUserMapper tUserMapper;
	@Autowired
	TSubjectMapper tSubjectMapper;
	/**查询第一个数据源的表
	 * 根据uid查询user
	 * @param uid
	 * @return
	 */
	public TUser selectUserById(int uid){
		TUser tUser = null;
		TUserExample example = new TUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUseridEqualTo(uid);
		List<TUser> users = tUserMapper.selectByExample(example);
		if(users!=null && users.size()>0){
			tUser = users.get(0);
		}
		return tUser;
	}
	/**
	 * 查询第二个数据源的表
	 * @param sid
	 * @return
	 */
	public TSubject selectSubjectById(int sid){
		TSubject subject = null;
		TSubjectExample example = new TSubjectExample();
		example.createCriteria().andSubidEqualTo(sid);
		List<TSubject> subjects = tSubjectMapper.selectByExample(example);
		if(subjects!=null && subjects.size()>0){
			subject = subjects.get(0);
		}
		return subject;
	}
}
