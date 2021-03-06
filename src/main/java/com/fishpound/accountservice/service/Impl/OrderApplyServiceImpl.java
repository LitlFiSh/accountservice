package com.fishpound.accountservice.service.Impl;

import com.fishpound.accountservice.entity.Department;
import com.fishpound.accountservice.entity.OrderApply;
import com.fishpound.accountservice.entity.OrderList;
import com.fishpound.accountservice.repository.DepartmentRepository;
import com.fishpound.accountservice.repository.OrderApplyRepository;
import com.fishpound.accountservice.repository.OrderListRepository;
import com.fishpound.accountservice.service.OrderApplyService;
import com.fishpound.accountservice.service.tools.PageTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderApplyServiceImpl implements OrderApplyService {
    @Autowired
    OrderApplyRepository orderApplyRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    OrderListRepository orderListRepository;

    /**
     * 新增申请单
     * @param orderApply
     */
    @Override
    public String addOrder(OrderApply orderApply) {
        String idPrefix = "", id;
        //id 生成策略：年份(4) 部门编号(2) 部门该年第 n 份申请(2)
//        System.out.println(orderApply.getApplyDepartment());
        Department department = departmentRepository.findByDeptName(orderApply.getApplyDepartment());
//        System.out.println(department.getId());
        Calendar now = Calendar.getInstance();
        idPrefix = "" + now.get(Calendar.YEAR) + department.getId();
        id = idPrefix + new DecimalFormat("00").format(orderApplyRepository.countByIdStartsWith(idPrefix) + 1);
//        System.out.println(id);
        orderApply.setId(id);
        List<OrderList> orderLists = orderApply.getOrderLists();
        int no = 1;
        for(OrderList orderList : orderLists){
            orderList.setId(id + new DecimalFormat("00").format(no++));
            orderList.setStatus(1);
            orderList.setOrderApply(orderApply);
        }
        orderApply.setOrderLists(orderLists);
        orderApply.setStatus(1);
        orderApply.setCreateTime(new Date());
        orderApply.setUpdateTime(new Date());
        orderApplyRepository.save(orderApply);
        return id;
    }

    /**
     * 更新申请单
     * @param orderApply
     */
    @Override
    public void updateOrder(OrderApply orderApply) {
        List<OrderList> lists = orderApply.getOrderLists();
        long n = orderListRepository.countAllByOrOrderApply_Id(orderApply.getId());
        //更新之前先检查设备列表是否有新增加（设备列表 OrderList.id 为空）
        //  如有则设置 id 为 申请单id(8) + 设备列表序号(2)
        for(int i = 0; i < lists.size(); i++){
            OrderList orderList = lists.get(i);
            if(orderList.getId() == null || "".equals(orderList.getId())){
                orderList.setId(orderApply.getId() + new DecimalFormat("00").format(n++));
                orderList.setOrderApply(orderApply);
            }
        }
        orderApply.setUpdateTime(new Date());
        orderApplyRepository.save(orderApply);
    }

    /**
     * 删除申请单
     * 删除：将申请单状态设置为已删除（-1）
     * @param orderApply
     */
    @Override
    public void deleteOrder(OrderApply orderApply) {
//        orderApplyRepository.deleteById(id);
        //获取该申请单包含的申请列表，并将申请列表的状态设置为已删除（-1）
        List<OrderList> orderLists = orderApply.getOrderLists();
//        for(OrderList orderList : orderLists){
//            orderList.setStatus(-1);
//        }
        orderApply.setStatus(-1);
        orderApplyRepository.save(orderApply);
    }

    /**
     * 查找编号为 id 的申请单
     * @param id
     * @return
     */
    @Override
    public OrderApply findOne(String id) {
        OrderApply orderApply = orderApplyRepository.getById(id);
//        List<OrderList> lists = new ArrayList<>();
//        for(OrderList orderList : orderApply.getOrderLists()){
//            if(orderList.getStatus() == 1){
//                lists.add(orderList);
//            }
//        }
//        orderApply.setOrderLists(lists);
        return orderApply;
    }

    /**
     * 根据传入参数查找申请单
     * @param param
     * @return
     */
    @Override
    public Page<OrderApply> findInCondition(Map<String, Object> param, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findInCondition(param.get("id").toString(),
                param.get("department").toString(),
                param.get("startDate").toString(),
                param.get("endDate").toString(),
                param.get("user").toString(),
                param.get("fundCode").toString(),
                Integer.valueOf(param.get("status").toString()),
                pageTools.sortSingle());
    }

    /**
     * 查询某部门某月的所有申请单
     * @param department
     * @param date
     * @param page
     * @return
     */
    @Override
    public Page<OrderApply> findByDepartmentAndMonth(String department, Date date, Integer page) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        try {
            Date last = format.parse(year + "-" + (month - 1) + "-" + "31");
            Date next = format.parse(year + "-" + (month + 1) + "-" + "00");
            PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
            return orderApplyRepository.findAllByApplyDepartmentAndApplyDateBetweenAndStatusNot(
                    department, last, next, -1, pageTools.sortSingle());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Page<OrderApply> findByUser(String id, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findAllByUidAndStatusNot(id, -1, pageTools.sortSingle());
    }

    /**
     * 通过传入参数查找用户申请单
     * @param param
     * @param page
     * @return
     */
    @Override
    public Page<OrderApply> findByUserInCondition(Map<String, Object> param, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        Integer status = (Integer) param.get("status");
        if(status == 10){
            return orderApplyRepository.findUserOrders1(param.get("uid").toString(),
                    param.get("id").toString(),
                    param.get("department").toString(),
                    param.get("startDate").toString(),
                    param.get("endDate").toString(),
                    param.get("user").toString(),
                    param.get("fundCode").toString(),
                    -1,
                    pageTools.sortSingle());
        } else {
            return orderApplyRepository.findUserOrders2(param.get("uid").toString(),
                    param.get("id").toString(),
                    param.get("department").toString(),
                    param.get("startDate").toString(),
                    param.get("endDate").toString(),
                    param.get("user").toString(),
                    param.get("fundCode").toString(),
                    status,
                    pageTools.sortSingle());
        }
    }

    /**
     * 查询月份为 month 的所有申请单
     * @param date
     * @return
     */
    @Override
    public Page<OrderApply> findByMonth(Date date, Integer page) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        try {
            Date last = format.parse(year + "-" + (month - 1) + "-" + "31");
            Date next = format.parse(year + "-" + (month + 1) + "-" + "00");
            PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
            return orderApplyRepository.findAllByApplyDateBetweenAndStatusNot(last, next, -1, pageTools.sortSingle());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询对应部门的申请单
     * @param department 部门名称
     * @return
     */
    @Override
    public Page<OrderApply> findByDepartment(String department, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findAlllByApplyDepartmentAndStatusNot(department, -1, pageTools.sortSingle());
    }

    @Override
    public Page<OrderApply> findByDepartmentAndStatus(String department, Integer status, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findAllByApplyDepartmentAndStatus(department, status, pageTools.sortSingle());
    }

    @Override
    public Page<OrderApply> findByStatus(Integer status, Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findAllByStatus(status, pageTools.sortSingle());
    }

    /**
     * 查找所有已删除申请单
     * @param page
     * @return
     */
    @Override
    public Page<OrderApply> findDeleted(Integer page) {
        PageTools pageTools = new PageTools("id", Sort.Direction.DESC, page);
        return orderApplyRepository.findAllByStatus(-1, pageTools.sortSingle());
    }

    /**
     * 上传签名后的申请单扫描文件
     * @param id 对应申请单 id
     * @param data 文件流
     */
    @Override
    public void uploadFile(String id, byte[] data) {
        OrderApply orderApply = orderApplyRepository.getById(id);
        orderApply.setFile(data);
        orderApplyRepository.save(orderApply);
    }
}
