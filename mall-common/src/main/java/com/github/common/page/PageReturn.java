package com.github.common.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.common.util.U;
import com.github.liuanxin.api.annotation.ApiReturn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 * 此实体类在 Controller 和 Service 中用到分页时使用.
 *
 * &#064;Controller --> request 请求中带过来的参数使用 Page 进行接收(如果前端不传, 此处接收则程序会使用默认值)
 * public JsonResult&lt;XXXVo&gt; xx(xxxDto xxx, PageParam page) {
 *     PageReturn&lt;XXX&gt; pageInfo = xxxService.page(xxx.checkAndReturnParam(), page);
 *     return success("xxx", XXXVo.assemblyData(pageInfo));
 * }
 *
 * &#064;Service --> 调用方法使用 Page 进行传递, 返回 PageInfo
 * public PageReturn&lt;XXX&gt; page(xxx xx, PageParam page) {
 *     return Pages.returnPage(xxxMapper.selectByExample(xx, Pages.param(page)));
 * }
 *
 * 这么做的目的是 mybatis 包只需要在服务端引入即可
 * </pre>
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PageReturn<T> implements Serializable {
    private static final long serialVersionUID = 0L;

    @ApiReturn("SELECT COUNT(*) FROM ... 的结果")
    private int total;

    @ApiReturn("SELECT ... FROM ... LIMIT 0, 10 的结果")
    private List<T> list;

    public static <T> PageReturn<T> emptyReturn() {
        return new PageReturn<>(0, Collections.emptyList());
    }
    public static <T> PageReturn<T> returnPage(int total, List<T> list) {
        return new PageReturn<>(total, list);
    }

    /** 在 Controller 中调用 --> 组装不同的 vo 时使用此方法 */
    public static <S,T> PageReturn<T> convert(PageReturn<S> pageInfo) {
        if (U.isBlank(pageInfo)) {
            return emptyReturn();
        } else {
            // 只要总条数
            PageReturn<T> info = new PageReturn<>();
            info.setTotal(pageInfo.getTotal());
            return info;
        }
    }
}
