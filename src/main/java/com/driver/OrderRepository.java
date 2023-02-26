package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<String,Order> orderDb = new HashMap<>();
    Map<String,String> orderPartnerDb = new HashMap<>();
    Map<String,DeliveryPartner> deliveryPartnerDb = new HashMap<>();
    Map<String, List<String>> deliveryPartnerListDb = new HashMap<>();

    public void addOrder(Order order){
        orderDb.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        deliveryPartnerDb.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){

        if(orderDb.containsKey(orderId) && deliveryPartnerDb.containsKey(partnerId)){
            List<String> listOfOrders = new ArrayList<>();
            if(deliveryPartnerListDb.containsKey(partnerId)){
                listOfOrders = deliveryPartnerListDb.get(partnerId);
            }
            listOfOrders.add(orderId);
            deliveryPartnerListDb.put(partnerId,listOfOrders);

            DeliveryPartner deliveryPartner = deliveryPartnerDb.get(partnerId);
            deliveryPartner.setNumberOfOrders(listOfOrders.size());

            orderPartnerDb.put(orderId,partnerId);
        }
    }

    public Order getOrderById(String orderId){
        return orderDb.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerDb.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return deliveryPartnerListDb.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return deliveryPartnerListDb.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for(String key: orderDb.keySet()){
            orders.add(key);
        }
        return orders;
    }

    public int getCountOfUnassignedOrders(){
        return orderDb.size()-orderPartnerDb.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId){

        List<String> orders = deliveryPartnerListDb.get(partnerId);
        int ans = 0;
        for(String orderId: orders){
            Order order = orderDb.get(orderId);
            if(order.getDeliveryTime()>time)
                ans++;
        }

        return ans;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId){
        int maxTime = 0;
        for(String order: deliveryPartnerListDb.get(partnerId)){
            int time = orderDb.get(order).getDeliveryTime();
            maxTime = Math.max(maxTime,time);
        }

        return maxTime;
    }

    public void deletePartnerById(String partnerId){
        if(deliveryPartnerDb.containsKey(partnerId))
            deliveryPartnerDb.remove(partnerId);

        if(deliveryPartnerListDb.containsKey(partnerId)){
            List<String> listOfOrders = deliveryPartnerListDb.get(partnerId);
            for(String orderId:listOfOrders)
                orderPartnerDb.remove(orderId);

            deliveryPartnerListDb.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId){
        if(orderDb.containsKey(orderId))
            orderDb.remove(orderId);

        if(orderPartnerDb.containsKey(orderId)){
            String partnerid = orderPartnerDb.get(orderId);

            for(String order: deliveryPartnerListDb.get(partnerid)){
                if(order.equals(orderId))
                    deliveryPartnerListDb.get(partnerid).remove(orderId);
            }

            orderPartnerDb.remove(orderId);
        }
    }
}
