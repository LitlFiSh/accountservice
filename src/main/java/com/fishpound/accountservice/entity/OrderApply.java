package com.fishpound.accountservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orderapply")
public class OrderApply {
    @Id
    @Column(name = "id")
    private String id;

    @NotEmpty(message = "申请部门不能为空")
    @Max(value = 16)
    @Column(name = "apply_department")
    private String applyDepartment;

    @NotEmpty(message = "申请人不能为空")
    @Max(value = 32, message = "申请人名称过长")
    @Column(name = "apply_user")
    private String applyUser;

    @NotEmpty(message = "采购经费代码不能为空")
    @Size(max = 6, min = 6, message = "采购经费代码长度只能为6位")
    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "apply_date")
    @DateTimeFormat(pattern = "yyyy年MM月dd日")
    private Date applyDate;

    @NotNull(message = "总金额不能为空")
    @Column(name = "total")
    private Double total;

    @JsonIgnore
    @Column(name = "dept_leader_sign")
    private boolean deptLeaderSign;

    @JsonIgnore
    @Column(name = "dept_leader_sign_date")
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private Date deptLeaderSignDate;

    @JsonIgnore
    @Column(name = "inst_leader_sign")
    private boolean instLeaderSign;

    @JsonIgnore
    @Column(name = "inst_leader_sign_date")
    private Date instLeaderSignDate;

    @NotNull(message = "申请单状态不能为空")
    @Column(name = "status")
    private Integer status;

    @Valid
    @NotNull(message = "申请设备列表不能为空")
    @OneToMany(targetEntity = OrderList.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "from_id", referencedColumnName = "id")
    @JsonManagedReference(value = "list_apply")
    private List<OrderList> orderLists;

    @NotEmpty(message = "申请单对应用户id不能为空")
    @Column(name = "uid")
    private String uid;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @JsonIgnore
    @Column(name = "file")
    private byte[] file;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Version
    @Column(name = "version")
    private Integer version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyDepartment() {
        return applyDepartment;
    }

    public void setApplyDepartment(String applyDepartment) {
        this.applyDepartment = applyDepartment;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isDeptLeaderSign() {
        return deptLeaderSign;
    }

    public void setDeptLeaderSign(boolean deptLeaderSign) {
        this.deptLeaderSign = deptLeaderSign;
    }

    public Date getDeptLeaderSignDate() {
        return deptLeaderSignDate;
    }

    public void setDeptLeaderSignDate(Date deptLeaderSignDate) {
        this.deptLeaderSignDate = deptLeaderSignDate;
    }

    public boolean isInstLeaderSign() {
        return instLeaderSign;
    }

    public void setInstLeaderSign(boolean instLeaderSign) {
        this.instLeaderSign = instLeaderSign;
    }

    public Date getInstLeaderSignDate() {
        return instLeaderSignDate;
    }

    public void setInstLeaderSignDate(Date instLeaderSignDate) {
        this.instLeaderSignDate = instLeaderSignDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrderList> getOrderLists() {
        return orderLists;
    }

    public void setOrderLists(List<OrderList> orderLists) {
        this.orderLists = orderLists;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    public void setWithdrawalReason(String withdrawalReason) {
        this.withdrawalReason = withdrawalReason;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

        public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
