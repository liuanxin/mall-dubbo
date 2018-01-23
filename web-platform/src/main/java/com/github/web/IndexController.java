package com.github.web;

import com.github.common.json.JsonResult;
import com.github.common.resource.CollectEnumUtil;
import com.github.common.util.SecurityCodeUtil;
import com.github.common.util.U;
import com.github.liuanxin.api.annotation.ApiIgnore;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.util.WebPlatformSessionUtil;
import com.github.util.WebPlatformDataCollectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.common.json.JsonResult.success;

@ApiIgnore
@Controller
public class IndexController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "web-platform";
    }

    @ApiIgnore(false)
    @GetMapping("/enum")
    @ResponseBody
    public JsonResult enumList(@ApiParam(desc = "枚举类型. 不传则返回列表, type 与 枚举的类名相同, 忽略大小写") String type) {
        return U.isBlank(type) ?
                success("枚举列表", CollectEnumUtil.enumList(WebPlatformDataCollectUtil.ENUMS)) :
                success("枚举信息", CollectEnumUtil.enumInfo(type, WebPlatformDataCollectUtil.ENUMS));
    }

    @GetMapping("/code")
    public void code(HttpServletResponse response, String width, String height,
                     String count, String style) throws IOException {
        SecurityCodeUtil.Code code = SecurityCodeUtil.generateCode(count, style, width, height);

        // 往 session 里面丢值
        WebPlatformSessionUtil.putImageCode(code.getContent());

        // 向页面渲染图像
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        ImageIO.write(code.getImage(), "jpeg", response.getOutputStream());
    }
}
