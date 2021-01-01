package com.fishpound.accountservice.controller;

import com.fishpound.accountservice.result.JsonResult;
import com.fishpound.accountservice.result.ResultTool;
import com.fishpound.accountservice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    NoticeService noticeService;

    /**
     * 获取用户未读消息条数
     * @param uid
     * @return
     */
    @GetMapping("/count")
    public JsonResult countUnreadNotice(@RequestParam(value = "uid") String uid){
        return ResultTool.success(noticeService.findNoticeUnread(uid));
    }

    /**
     * 获取消息详细内容
     * @param id
     * @return
     */
    @GetMapping()
    public JsonResult deleteNotice(@RequestParam(value = "id") Integer id){
        return ResultTool.success(noticeService.getOne(id));
    }

    /**
     * 获取消息列表（分页）
     * @param uid
     * @param page
     * @return
     */
    @GetMapping("/notices")
    public JsonResult getAllByUser(@RequestParam(value = "uid") String uid,
                                   @RequestParam(value = "page", defaultValue = "1") Integer page)
    {
        return null;
    }
}