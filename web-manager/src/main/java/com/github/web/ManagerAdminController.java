package com.github.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.common.Const;
import com.github.common.json.JsonResult;
import com.github.common.page.PageParam;
import com.github.common.page.PageReturn;
import com.github.common.util.FileUtil;
import com.github.common.util.U;
import com.github.config.ManagerConfig;
import com.github.dto.ManagerRoleDto;
import com.github.dto.ManagerUserDto;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.liuanxin.api.annotation.ApiMethod;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.manager.constant.ManagerConst;
import com.github.manager.model.ManagerRole;
import com.github.manager.model.ManagerUser;
import com.github.manager.service.ManagerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiGroup(ManagerConst.MODULE_INFO)
@RestController
@RequestMapping("/admin")
public class ManagerAdminController {

    @Reference(version = Const.DUBBO_VERSION, lazy = true, check = false, timeout = Const.DUBBO_TIMEOUT)
    private ManagerService adminService;

    private final ManagerConfig config;
    public ManagerAdminController(ManagerConfig config) {
        this.config = config;
    }

    @ApiMethod(value = "查询用户", index = 10)
    @GetMapping("/user")
    public JsonResult<PageReturn<ManagerUser>> queryUser(String userName, Boolean status, PageParam page) {
        return JsonResult.success("查询用户信息", adminService.queryUser(userName, status, page));
    }
    @ApiMethod(value = "添加或修改用户", index = 11)
    @PostMapping("/user")
    public JsonResult addOrUpdateUser(ManagerUserDto user, @ApiParam("头像") MultipartFile image) {
        user.basicCheck();

        ManagerUser managerUser = user.operateParam();
        if (U.isNotBlank(managerUser)) {
            managerUser.setAvatar(FileUtil.save(image, config.getFilePath(), config.getFileUrl(), false));
        }
        adminService.addOrUpdateUser(managerUser);
        return JsonResult.success(String.format("用户%s成功", (user.hasUpdate() ? "修改" : "添加")));
    }
    @ApiMethod(value = "删除用户", index = 12)
    @DeleteMapping("/user")
    public JsonResult deleteUser(Long id) {
        adminService.deleteUser(id);
        return JsonResult.success("用户删除成功");
    }

    @ApiMethod(value = "查询角色", index = 20)
    @GetMapping("/role")
    public JsonResult<List<ManagerRole>> queryRole() {
        return JsonResult.success("查询所有角色", adminService.queryBasicRole());
    }
    @ApiMethod(value = "添加或更新角色", index = 21)
    @PostMapping("/role")
    public JsonResult addOrUpdateRole(ManagerRoleDto role) {
        role.basicCheck();

        adminService.addOrUpdateRole(role.operateParam());
        return JsonResult.success(String.format("角色%s成功", (role.hasUpdate() ? "修改" : "添加")));
    }
    @ApiMethod(value = "删除角色", index = 22)
    @DeleteMapping("/role")
    public JsonResult deleteRole(Long id) {
        adminService.deleteRole(id);
        return JsonResult.success("角色删除成功");
    }

    // 菜单 和 权限 的基础数据尽量由前后端配合整理出来, 只操作一次即可, 用接口来操作没必要
}
