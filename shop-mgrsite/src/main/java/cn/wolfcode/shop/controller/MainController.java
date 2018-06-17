package cn.wolfcode.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

    @RequestMapping(value = "/main",method = RequestMethod.GET)
    public String main(){
        return "main";
    }

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String root(){
        return "forward:/main";
    }

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "index";
    }



}
