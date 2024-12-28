package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestRestfulController {
    @RequestMapping(value = "/value", method = RequestMethod.GET)
    public String getAllUsers() {
        System.out.println("获取了所有用户信息-->/user-->get");
        return "success";
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String getUserById(@PathVariable ("id") int id) {
        System.out.println("根据id查询用户信息-->/user/"+id+"-->get");
        return "success";
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String adduser() {
        System.out.println("添加了用户信息");
        return "success";
    }

    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public String updateuser() {
        System.out.println("修改了用户信息");
        return "success";
    }
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public String deleteuser(@PathVariable ("id") int id) {
        System.out.println("删除了用户信息");
        return "success";
    }
}
