package controller;

import dao.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojo.Employee;

import java.util.Collection;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @RequestMapping(value = "/employee", method = RequestMethod.GET)
    public String showEmployee(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("allEmployee", employees);
        return "employee_list";
    }

    @RequestMapping(value = "/to/add", method = RequestMethod.GET)
    public String toAddEmployee(Model model) {
        Collection<Employee> employees = employeeDao.getAll();
        model.addAttribute("allEmployee", employees);
        return "employee_add";
    }

    @RequestMapping(value = "/employee1", method = RequestMethod.POST)
    public String addEmployee(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/employee";
    }

    @RequestMapping(value = "/employee/{id}", method = RequestMethod.GET)
    public String updateEmployeePage(@PathVariable int id, Model model ) {
        Employee employee = employeeDao.get(id);
        model.addAttribute("employee", employee);
        return "employee_update";
    }

    @RequestMapping(value = "/employee", method = RequestMethod.PUT)
    public String updateEmployee(Employee employee) {
        employeeDao.save(employee);
        return "redirect:/employee";
    }

}
