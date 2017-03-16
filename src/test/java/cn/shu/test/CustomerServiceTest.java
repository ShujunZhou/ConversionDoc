package cn.shu.test;

import cn.shu.dealwithdata.ParseData;
import cn.shu.helper.DatabaseHelper;
import cn.shu.model.Customer;
import cn.shu.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shu on 2017/3/6.
 */
//单元测试
public class CustomerServiceTest {
    private final CustomerService customerService;

    public CustomerServiceTest() {
        customerService = new CustomerService();
    }

    @Before
    public void init() {
        //初始化数据库
//        String filePath = "sql/customer_init.sql";
//        DatabaseHelper.executeSqlFile(filePath);
    }

    @Test //数据解析
    public void getCustomerListTest() throws Exception {
        List<Customer> customers = customerService.getCustomerList();
        ParseData.newWordDoc(Customer.class, customers, "customer");
    }

    @Test
    public void getCustomerTest() throws Exception {
        Customer customer = customerService.getCustomer(1);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "LiBai");
        map.put("contact", "li");
        map.put("email", "21@foxmail.com");
        boolean result = customerService.createCustomer(map);
        Assert.assertTrue(result);
    }

    @Test
    public void updateCustomerTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "json");
        long id = 1;
        boolean result = customerService.updateCustomer(id, map);
        Assert.assertTrue(result);
    }

    @Test
    public void deleteCustomerTest() throws Exception {
        long id = 1;
        boolean result = customerService.deleteCustomer(id);
        Assert.assertTrue(result);
    }
}
