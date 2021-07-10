package com.louisngatale.garagehub.data;

public class Requests {
    private String Customer;
    private String phone;
    private String service;

    public Requests() {
    }

    public Requests(String customer, String phone, String service) {
        Customer = customer;
        this.phone = phone;
        this.service = service;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
