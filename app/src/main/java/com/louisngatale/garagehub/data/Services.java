package com.louisngatale.garagehub.data;

public class Services {
    private String service, price;

    public Services() {
    }

    public Services(Object service, Object price) {
        this.service = (String) service;
        this.price = (String) price;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
