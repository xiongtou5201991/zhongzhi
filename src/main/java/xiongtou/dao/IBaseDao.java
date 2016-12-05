   package xiongtou.dao;

import java.util.List;
import java.util.Map;

import xiongtou.model.Pager;

   
   /**
    * 公共的DAO处理对象，这个对象中包含了Hibernate的所有基本操作和对SQL的操作 
    * @author apple
    *
    * @param <T>
    */
public interface IBaseDao<T> {
	
	public T add(T t);
	public void update(T t);
	public void delete(int id);
	public T load(int id);  
	
	
	/**
	 * 不分页列表对象
	 * @param hql查询对象的HQL
	 * @param args查询参数
	 * @return
	 */ 
	public List<T> list(String hql,Object[] args);
	public List<T> list(String hql,Object args);
	public List<T> list(String hql);
	/**
	 * 基于别名和参数的混合查询
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public List<T> list(String hql,Object[] args,Map<String,Object> alias);
	public List<T> listByAlias(String hql,Map<String,Object> alias);
	
	/**
	 * 分页列表对象
	 * @param hql查询对象的HQL
	 * @param args查询参数
	 * @return
	 */ 
	public Pager<T> find(String hql,Object[] args);
	public Pager<T> find(String hql,Object args);
	public Pager<T> find(String hql);
	/**
	 * 基于别名和参数的混合查询
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public Pager<T> find(String hql,Object[] args,Map<String,Object> alias);
	public Pager<T> findByAlias(String hql,Map<String,Object> alias);
	
	/**
	 * 根据HQL查询一组对象
	 * @param hql
	 * @param args
	 * @return
	 */
	public Object queryObject(String hql,Object[] args);
	public Object queryObject(String hql,Object args);
	public Object queryObject(String hql);
	public Object queryObject(String hql,Object[] args,Map<String,Object> alias);
	public Object queryObjectByAlias(String hql,Map<String,Object> alia);
	
	/**
	 * 根据HQL更新一组对象
	 * @param hql
	 * @param args
	 * @return
	 */
	public Object updateByHql(String hql,Object[] args);
	public Object updateByHql(String hql,Object args);
	public Object updateByHql(String hql);
	
	/**
	 * 根据SQL查询对象，不包含关联对象
	 * @param sql
	 * @param args
	 * @param clz查询的实体对象
	 * @param hasEntity该对象是否是一个hibernate所管理实体，如果不是就需要使用setResultTransform来查询
	 * @return
	 */
	public List<Object> listBySql(String sql,Object[] args,Class<Object> clz,boolean hasEntity);
	public List<Object> listBySql(String sql,Object args,Class<Object> clz,boolean hasEntity);
	public List<Object> listBySql(String sql,Class<Object> clz,boolean hasEntity);
	public List<Object> listBySql(String sql,Object[] args,Map<String,Object> alias,Class<Object> clz,boolean hasEntity);
	public List<Object> listByAliasSql(String sql,Map<String,Object> alias,Class<Object> clz,boolean hasEntity);
	
	/**
	 * 根据SQL查询对象，不包含关联对象(分页查询)
	 * @param sql
	 * @param args
	 * @param clz查询的实体对象
	 * @param hasEntity该对象是否是一个hibernate所管理实体，如果不是就需要使用setResultTransform来查询
	 * @return
	 */
	public Pager<Object> findBySql(String sql,Object[] args,Class<Object> clz,boolean hasEntity);
	public Pager<Object> findBySql(String sql,Object args,Class<Object> clz,boolean hasEntity);
	public Pager<Object> findBySql(String sql,Class<Object> clz,boolean hasEntity);
	public Pager<Object> findBySql(String sql,Object[] args,Map<String,Object> alias,Class<Object> clz,boolean hasEntity);
	public Pager<Object> findByAliasSql(String sql,Map<String,Object> alias,Class<Object> clz,boolean hasEntity);
}
