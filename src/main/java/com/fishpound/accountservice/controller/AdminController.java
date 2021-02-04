package com.fishpound.accountservice.controller;

import com.fishpound.accountservice.entity.*;
import com.fishpound.accountservice.result.JsonResult;
import com.fishpound.accountservice.result.ResultTool;
import com.fishpound.accountservice.result.ResultUser;
import com.fishpound.accountservice.service.DepartmentService;
import com.fishpound.accountservice.service.OrderApplyService;
import com.fishpound.accountservice.service.RoleService;
import com.fishpound.accountservice.service.UserInfoService;
import com.fishpound.accountservice.service.tools.FileTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    RoleService roleService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    OrderApplyService orderApplyService;

    /**
     * 根据用户ID获取用户信息
     * @param uid 用户ID
     * @return
     */
    @GetMapping("/user")
    public JsonResult getUser(@RequestParam(value = "uid") String uid){
        UserInfo userInfo = userInfoService.findById(uid);
        ResultUser user = new ResultUser(userInfo.getId(),
                userInfo.getUsername(),
                userInfo.getDepartment().getDeptName(),
                userInfo.getAccount().getRole().getRoleDescription());
        return ResultTool.success(user);
    }

    /**
     * 添加用户
     * @param resultUser
     * @return
     */
    @PostMapping("/user")
    public JsonResult addUser(@Validated @RequestBody ResultUser resultUser){
        UserInfo userInfo = createUser(resultUser, true);
        if(userInfo != null){
            userInfoService.save(userInfo);
            return ResultTool.success();
        } else{
            return ResultTool.fail("用户ID已存在");
        }
    }

    /**
     * 更新用户信息
     * @param resultUser
     * @return
     */
    @PutMapping("/user")
    public JsonResult updateUser(@Validated @RequestBody ResultUser resultUser){
        UserInfo userInfo = createUser(resultUser, false);
        userInfoService.save(userInfo);
        return ResultTool.success();
    }

    /**
     * 获取除当前用户外的所有用户
     * @param request
     * @param page
     * @return
     */
    @GetMapping("/users")
    public JsonResult getAllUser(HttpServletRequest request,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page)
    {
        return ResultTool.success(userInfoService.findAllExcept(request.getAttribute("user").toString(), page));
    }

    /**
     * 批量导入用户，通过传递下载的模板文件
     * @param file
     * @return
     */
    @PostMapping("/user/batchAdd")
    public JsonResult batchAdduser(@RequestParam(value = "file") MultipartFile file){
        List<Map> userList = FileTools.importExcel(file);
        return ResultTool.success(userInfoService.batchAddUser(userList));
    }

    /**
     * 获取所有已删除的申请单（status = -1）
     * @param page
     * @return
     */
    @GetMapping("/order/deleted")
    public JsonResult getDeletedOrder(@RequestParam(value = "page", defaultValue = "1") Integer page){
        return ResultTool.success(orderApplyService.findDeleted(page));
    }

    /**
     * 还原一个已删除的申请单
     * @param id 申请单id
     * @return
     */
    @PutMapping("/order/reduct/{id}")
    public JsonResult reductOrder(@PathVariable(value = "id") String id){
        OrderApply orderApply = orderApplyService.findOne(id);
        orderApply.setStatus(1);
        orderApplyService.updateOrder(orderApply);
        return ResultTool.success();
    }

    /**
     * 下载用户导入模板文件
     * @param response
     */
    @GetMapping("/user/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            response.setContentType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("用户导入模板", "UTF-8") + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            Resource resource = new ClassPathResource("docs/用户导入模板.xlsx");   //静态文件位置
            InputStream stream = resource.getInputStream();
            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);
            stream.close();
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(404);
        }
    }

    /**
     * 通过 ResultUser 类生成实体类 UserInfo
     * @param user ResultUser类
     * @param b 是否新建用户
     * @return 实体类UserInfo
     */
    private UserInfo createUser(ResultUser user, boolean b){
        UserInfo u = userInfoService.findById(user.getId());
        if(b && u != null) {
            return null;
        }
        UserInfo userInfo = new UserInfo();
        Account account = new Account();
        Department department = departmentService.findByDeptName(user.getDepartment());
        Role role = roleService.findByDescription(user.getRole());

        account.setId(user.getId());
        if(b || (user.getPassword() != null && "".equals(user.getPassword()))) {
            account.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        } else{
            account.setPassword(u.getAccount().getPassword());
        }
        account.setActive(true);
        account.setRole(role);

        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setDepartment(department);
        userInfo.setAccount(account);

        return userInfo;
    }
}
