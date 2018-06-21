package com.github.common.page;

import com.github.common.util.A;
import com.github.liuanxin.page.model.PageBounds;
import com.github.liuanxin.page.model.PageList;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <span style="color:red">此实体类只在 Service 中用到分页时使用, Controller 中不要使用此类</span>
 *
 * &#064;Controller --> request 请求中带过来的参数使用 Page 进行接收(如果前端不传, 此处接收则程序会使用默认值)
 * public JsonResult xx(xxx, Page page) {
 *     PageInfo pageInfo = xxxService.page(xxx, page);
 *     return success("xxx", (page.isWasMobile() ? pageInfo.getList() : pageInfo));
 * }
 *
 * &#064;Service --> 调用方法使用 Page 进行传递, 返回 PageInfo
 * public PageInfo page(xxx, Page page) {
 *     PageBounds pageBounds = Pages.param(page);
 *     List&lt;XXX> xxxList = xxxMapper.selectByExample(xxxxx, pageBounds);
 *     return Pages.returnPage(xxxList);
 * }
 * </pre>
 */
public final class Pages {

    /** 在 service 的实现类中调用 --> 在 repository 方法上的参数是 PageBounds, service 上的参数是 Page, 使用此方法进行转换 */
    public static PageBounds param(Page page) {
        return page.isWasMobile() ?
                new PageBounds(page.getLimit()) :
                new PageBounds(page.getPage(), page.getLimit());
    }

    /** 在 service 的实现类中调用 --> 在 repository 方法上的返回类型是 List, service 上的返回类型是 PageInfo, 使用此方法进行转换 */
    public static <T> PageInfo<T> returnPage(List<T> list) {
        if (A.isEmpty(list)) {
            return PageInfo.emptyReturn();
        } else if (list instanceof PageList) {
            return PageInfo.returnPage(((PageList) list).getTotal(), new ArrayList<T>(list));
        } else {
            return PageInfo.returnPage(list.size(), list);
        }
    }
}
