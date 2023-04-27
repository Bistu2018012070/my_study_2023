package com.zhangzq.controller;

import com.zhangzq.dao.DepartmentDao;
import com.zhangzq.dao.EmployeeDao;
import com.zhangzq.pojo.Department;
import com.zhangzq.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    DepartmentDao departmentDao;

    @Resource
    JdbcTemplate jdbcTemplate;
/////////////////////////////JDBC/////////////////////
    //查
    @GetMapping("/queryEmpList")
    public List<Map<String,Object>> queryEmpList(){
        String sql = "select * from employee";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
        return users;
    }
    //增
    @GetMapping("/add")
    public String addUser() {
        String sql = "insert into springcloud_study.employees(empId,lastName,pwd) values (4,'gpl','45678')";
        jdbcTemplate.update(sql);//自动提交事务
        return "添加成功";
    }

    //删
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        String sql = "delete from springboot.user where id=?";
        jdbcTemplate.update(sql, id);//自动提交事务
        return "删除成功";
    }

    //改
    @GetMapping("/change/{id}")
    public String updateUser(@PathVariable("id") int id) {
        String sql = "update springboot.user set name=?,pwd=? where id=" + id;
        Object[] objects = new Object[2];
        objects[0] = "zml";
        objects[1] = "1213353";
        jdbcTemplate.update(sql, objects);//自动提交事务
        return "修改成功";
    }

/////////////////////////////////////员工
    //Model 起到返回给前端的作用
    @RequestMapping("/emps")
    public String list(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("emps", employees);
        System.out.println(employees);
        return "emp/list";
    }



    @GetMapping("/addEmp")
    //添加一个Model返回到前端来传递参数
    public String toAddpage(Model model) {
        //查出所有部门数据
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments", departments);
        return "emp/add";
    }

    @PostMapping("/addEmp")
    public String addEmp(Employee employee) {
        //添加员工
        employeeDao.save(employee);
        //redirect重定向到emps
        return "redirect:/emps";
    }

    //到员工修改页面
    @GetMapping("/updateEmp/{id}")
    public String toUpdateEmp(@PathVariable("id") Integer id, Model model) {
        //查出选中的员工数据
        Employee employee = employeeDao.getEmployeeById(id);
        model.addAttribute("emp", employee);
        //查出所有部门数据
        Collection<Department> departments = departmentDao.getDepartments();
        model.addAttribute("departments", departments);
        return "emp/update";
    }

    @PostMapping("/updateEmp")
    public String updateEmp(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/emps";
    }

    //删除员工
    @GetMapping("/delemp/{id}")
    public String deleteEmp(@PathVariable("id") Integer id, Model model) {
        employeeDao.delete(id);
        return "redirect:/emps";
    }

}
