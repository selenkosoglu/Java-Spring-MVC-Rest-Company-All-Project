package com.works.controllers.adminpanel;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/orders")
public class OrdersController {


    @GetMapping("/list")
    public String ordersList() {
        return "adminpanel/orders/orderslist";
    }
}