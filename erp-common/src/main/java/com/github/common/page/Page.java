package com.github.common.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * <pre>
 * 此实体类在 controller 和 service 中用到分页时使用.
 *
 * &#064;controller --> 接收请求中带过来的参数时使用 Page 进行接收
 * public JsonResult xx(xxx, Page page) {
 *     PageInfo pageInfo = xxxService.page(xxx, page);
 *     return success("xxx", (page.isWasMobile() ? pageInfo.getList() : pageInfo));
 * }
 *
 * &#064;service --> 调用方法使用 Page 进行传递, 返回时使用 PageInfo
 * public PageInfo page(xxx, Page page) {
 *     PageBounds pageBounds = Pages.param(page);
 *     List&lt;XXX> xxxList = xxxMapper.selectByExample(xxxxx, pageBounds);
 *     return Pages.returnList(xxxList);
 * }
 *
 * 这么做的目的是分页包只需要在服务端引入即可
 * </pre>
 * @see Pages#param(Page)
 */
@Setter
@Getter
@NoArgsConstructor
public class Page {

    /** 分页默认页 */
    public static final int DEFAULT_PAGE_NO = 1;
    /** 分页默认的每页条数 */
    public static final int DEFAULT_LIMIT = 15;
    /** 前台传递过来的分页参数名 */
    public static final String GLOBAL_PAGE = "page";
    /** 前台传递过来的每页条数名 */
    public static final String GLOBAL_LIMIT = "limit";

    /** 当前页数 */
    private int page;
    /** 每页条数 */
    private int limit;
    /** 是否是移动端 */
    private boolean wasMobile = false;

    public Page(String page, String limit) {
        int pageNum = NumberUtils.toInt(page);
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NO;
        }
        this.page = pageNum;

        int limitNum = NumberUtils.toInt(limit);
        if (limitNum <= 0 || limitNum > 1000) {
            limitNum = DEFAULT_LIMIT;
        }
        this.limit = limitNum;
    }
}
