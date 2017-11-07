package com.github.common.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetInternalMethods;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.StatementInterceptor;
import com.github.common.util.LogUtil;
import com.github.common.util.U;

import java.sql.SQLException;
import java.util.Properties;

public class ShowSqlInterceptor implements StatementInterceptor {

    @Override
    public void init(Connection connection, Properties properties) throws SQLException {}

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement statement,
                                               Connection connection) throws SQLException {
        if (U.isBlank(sql) && statement != null) {
            sql = statement.toString();
            if (U.isNotBlank(sql) && sql.indexOf(':') > 0) {
                // sql = SqlFormat.format(sql.substring(sql.indexOf(':') + 1).trim());
                sql = SQLUtils.formatMySql(sql.substring(sql.indexOf(':') + 1).trim());
            }
        }
        if (U.isNotBlank(sql)) {
            if (LogUtil.SQL_LOG.isDebugEnabled()) {
                LogUtil.SQL_LOG.debug("{}", sql);
            }
        }
        return null;
    }

    @Override
    public ResultSetInternalMethods postProcess(String s, Statement statement,
                                                ResultSetInternalMethods resultSetInternalMethods,
                                                Connection connection) throws SQLException {
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() { return false; }
    @Override
    public void destroy() {}
}
