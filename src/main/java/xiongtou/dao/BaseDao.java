/**
 * 
 */
package xiongtou.dao;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import xiongtou.model.Pager;
import xiongtou.model.SystemContext;

/**
 * @author apple
 *
 */
@SuppressWarnings("unchecked")
public class BaseDao<T> implements IBaseDao<T> {
	
	
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
    
	@Inject
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	protected Session getSession(){
		return sessionFactory.openSession();
	}
	
	
	private String initSort(String hql){
		String order=SystemContext.getOrder();
		String sort=SystemContext.getSort();
		if(sort!=null&&!"".equals(sort.trim())){
			hql+=" order by "+sort;
			if(!"desc".equals(order)){
				hql+=" asc";
			}
			else{
				hql+=" desc";
			}
		}
		return hql;
	}
	
	
	private void setAliasParameter(Query query,Map<String,Object> alias){
		if(alias!=null){
			Set<String> keys=alias.keySet();
			for(String key:keys){
				Object val =alias.get(key);
				if(val instanceof Collection){
					query.setParameterList(key, (Collection)val);
				}
				else{
					query.setParameter(key, val);
				}
			}
			
		}
	}
	private void setParameter(Query query,Object[] args){
		if(args!=null&&args.length>0){
			int index=0;
			for(Object arg:args){
				query.setParameter(index++, arg);
			}
		}
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#add(java.lang.Object)
	 */
	@Override
	public T add(T t) {
		getSession().save(t);
		return t;
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#update(java.lang.Object)
	 */
	@Override
	public void update(T t) {
		getSession().update(t);

	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#delete(int)
	 */
	@Override
	public void delete(int id) {
		getSession().delete(this.load(id));

	}

	
	
	private Class<T> clz;
	public Class<T> getClz(){
		if(clz==null){
			clz=((Class<T>)(((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments();
		}
	}
	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#load(int)
	 */
	@Override
	public T load(int id) {
		return (T)getSession().load(getClz(), id);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#list(java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<T> list(String hql, Object[] args) {
	
		return this.list(hql,args,null);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#list(java.lang.String, java.lang.Object)
	 */
	@Override
	public List<T> list(String hql, Object args) {
		return this.list(hql,new Object[]{args});
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#list(java.lang.String)
	 */
	@Override
	public List<T> list(String hql) {
		return this.list(hql, null);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#list(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		hql=initSort(hql);
		Query query=getSession().createQuery(hql);
	    setAliasParameter(query, alias);
		setParameter(query, args);
		return query.list();
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#list(java.lang.String, java.util.Map)
	 */
	@Override
	public List<T> listByAlias(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#find(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Pager<T> find(String hql, Object[] args) {
		return this.find(hql, args, null);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#find(java.lang.String, java.lang.Object)
	 */
	@Override
	public Pager<T> find(String hql, Object args) {
		return this.find(hql,new Object[]{args});
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#find(java.lang.String)
	 */
	@Override
	public Pager<T> find(String hql) {
		return this.find(hql, null);
	}

	
	private void setPagers(Query query,Pager pages){
		Integer pageSize=SystemContext.getPageSize();
		Integer pageOffset=SystemContext.getPageOffset();
		if(pageOffset==null||pageOffset<0)pageOffset=0;
		if(pageSize==null||pageSize<0)pageSize=15;
		pages.setOffset(pageOffset);
		pages.setSize(pageSize);
		query.setFirstResult(pageOffset).setMaxResults(pageSize);
	}
	
	private String getCountHql(String hql,boolean isHql){
		String endHql=hql.substring(hql.indexOf("from"));
		String c="select count(*) "+endHql;
		if(isHql){
		c.replaceAll("fetch", "");
		}
		return c;
	}
	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#find(java.lang.String, java.lang.Object[], java.util.Map)
	 */
	@Override
	public Pager<T> find(String hql, Object[] args, Map<String, Object> alias) {
		hql=initSort(hql);
		String cq=getCountHql(hql,true);
		cq=initSort(cq);
		Query cquery=getSession().createQuery(cq);
		Query query=getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setAliasParameter(cquery, alias);
		setParameter(query, args);
		setParameter(cquery, args);
		Pager<T> pages=new Pager<T>();
		setPagers(query,pages);
		List<T> datas=query.list();
		pages.setDatas(datas);
		long total=(Long)cquery.uniqueResult();
		pages.setTotal(total);
		return pages;
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#find(java.lang.String, java.util.Map)
	 */
	@Override
	public Pager<T> findByAlias(String hql, Map<String, Object> alias) {
		return this.find(hql, null, alias);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object queryObject(String hql, Object[] args) {
		
		
		return this.queryObject(hql, args, null);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#queryObject(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object queryObject(String hql, Object args) {
		return this.queryObject(hql, new Object[]{args});
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#queryObject(java.lang.String)
	 */
	@Override
	public Object queryObject(String hql) {
		return this.queryObject(hql, null);
	}
	
	@Override
	public Object queryObject(String hql, Object[] args,
			Map<String, Object> alias) {
		Query query=getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.uniqueResult();
	}

	@Override
	public Object queryObjectByAlias(String hql, Map<String, Object> alias) {
		return this.queryObject(hql, null, alias);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public Object updateByHql(String hql, Object[] args) {
		Query query=getSession().createQuery(hql);
		setParameter(query, args);
		query.executeUpdate();
		return null;
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#updateByHql(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object updateByHql(String hql, Object args) {
		return this.updateByHql(hql, new Object[]{args});
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#updateByHql(java.lang.String)
	 */
	@Override
	public Object updateByHql(String hql) {
		return this.updateByHql(hql, null);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#listBySql(java.lang.String, java.lang.Object[], java.lang.Class, boolean)
	 */
	@Override
	public List<Object> listBySql(String sql, Object[] args, Class<Object> clz,
			boolean hasEntity) {
		return this.listBySql(sql, args, null, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#listBySql(java.lang.String, java.lang.Object, java.lang.Class, boolean)
	 */
	@Override
	public List<Object> listBySql(String sql, Object args, Class<Object> clz,
			boolean hasEntity) {
		return this.listBySql(sql,new Object[]{args},clz,hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#listBySql(java.lang.String, java.lang.Class, boolean)
	 */
	@Override
	public List<Object> listBySql(String sql, Class<Object> clz, boolean hasEntity) {
		return this.listBySql(sql, null, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#listBySql(java.lang.String, java.lang.Object[], java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public List<Object> listBySql(String sql, Object[] args,
			Map<String, Object> alias, Class<Object> clz, boolean hasEntity) {
		sql=initSort(sql);
		SQLQuery sq=getSession().createSQLQuery(sql);
		setAliasParameter(sq, alias);
		setParameter(sq, args);
		if(hasEntity){
			sq.addEntity(clz);
		}
		else{
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		return sq.list();
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#listBySql(java.lang.String, java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public List<Object> listByAliasSql(String sql, Map<String, Object> alias,
			Class<Object> clz, boolean hasEntity) {
		return this.listBySql(sql, null, alias, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#findBySql(java.lang.String, java.lang.Object[], java.lang.Class, boolean)
	 */
	@Override
	public Pager<Object> findBySql(String sql, Object[] args, Class<Object> clz,
			boolean hasEntity) {
		return this.findBySql(sql, args, null, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#findBySql(java.lang.String, java.lang.Object, java.lang.Class, boolean)
	 */
	@Override
	public Pager<Object> findBySql(String sql, Object args, Class<Object> clz,
			boolean hasEntity) {
		// TODO Auto-generated method stub
		return this.findBySql(sql, new Object[]{args}, null, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#findBySql(java.lang.String, java.lang.Class, boolean)
	 */
	@Override
	public Pager<Object> findBySql(String sql, Class<Object> clz, boolean hasEntity) {
		return this.findBySql(sql, null, clz, hasEntity);
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#findBySql(java.lang.String, java.lang.Object[], java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public Pager<Object> findBySql(String sql, Object[] args,
			Map<String, Object> alias, Class<Object> clz, boolean hasEntity) {
		String cq=getCountHql(sql, false);
		cq=initSort(cq);
		sql=initSort(sql);
		SQLQuery sq=getSession().createSQLQuery(sql);
		SQLQuery cquery=getSession().createSQLQuery(cq);
		setAliasParameter(sq, alias);
		setAliasParameter(cquery, alias);
		setParameter(sq, args);
		setParameter(cquery, args);
		Pager<Object> pages=new Pager<Object>();
		setPagers(sq,pages);
		if(hasEntity){
			sq.addEntity(clz);
		}
		else{
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		List<Object> datas=sq.list();
		pages.setDatas(datas);
		long total=(Long)cquery.uniqueResult();
		pages.setTotal(total);
		return pages;
	}

	/* (non-Javadoc)
	 * @see xiongtou.dao.IBaseDao#findBySql(java.lang.String, java.util.Map, java.lang.Class, boolean)
	 */
	@Override
	public Pager<Object> findByAliasSql(String sql, Map<String, Object> alias,
			Class<Object> clz, boolean hasEntity) {
		
		return this.findBySql(sql, null, alias, clz, hasEntity);
	}

	

}
