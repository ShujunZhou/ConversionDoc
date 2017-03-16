package cn.shu.service;

import cn.shu.helper.DatabaseHelper;
import cn.shu.model.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by shu on 2017/3/6.
 */
//提供用户数据服务
public class CustomerService {
    //每次获取Connection时，首先在ThreadLocal中寻找，若不存在，
    //则创建一个新的Connection,并将其放入ThreadLocal中。当Connection
    //使用完毕后，需要移除ThreadLocal中持有的Connection
    public List<Customer> getCustomerList() {
        String sql = "select * from customer";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }
    //根据关键字获取客户列表
    public List<Customer> getCustomerList(String keyword) {
        String sql = "select * from customer where name like %" + keyword + "%";
        return DatabaseHelper.queryEntityList(Customer.class, sql);
    }

    //获取客户
    public Customer getCustomer(long id) {
        String sql = "select * from customer where id = " + id;
        return DatabaseHelper.queryEntity(Customer.class, sql, id);
    }

    //创建客户
    public boolean createCustomer(Map<String, Object> fieldMap) {
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    //更新客户
    public boolean updateCustomer(long id, Map<String, Object> fieldMap) {
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

    //删除客户
    public boolean deleteCustomer(long id) {
        return DatabaseHelper.deleteEntity(Customer.class, id);
    }
}
