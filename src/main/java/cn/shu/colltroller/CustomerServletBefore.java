package cn.shu.colltroller;

import cn.shu.model.Customer;
import cn.shu.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by shu on 2017/3/12.
 */
@WebServlet("/customerB")
public class CustomerServletBefore extends HttpServlet {

    private CustomerService service;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Customer> customerList = service.getCustomerList();

        req.setAttribute("customerList", customerList);
        req.getRequestDispatcher("/WEB-INF/view/customer").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void init() throws ServletException {
        service = new CustomerService();
    }
}
