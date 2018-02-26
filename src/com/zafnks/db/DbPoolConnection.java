package com.zafnks.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zafnks.utils.PathContants;

public class DbPoolConnection {
    private static DbPoolConnection databasePool = null;
    private static DruidDataSource dds = null;

    static {
        Properties properties = loadPropertyFile("database.properties");
        try {
            dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DbPoolConnection() {
    }

    public static synchronized DbPoolConnection getInstance() {
        if (null == databasePool) {
            databasePool = new DbPoolConnection();
        }
        return databasePool;
    }

    public DruidPooledConnection getConnection() throws SQLException {
        return dds.getConnection();
    }

    public static Properties loadPropertyFile(String fullFile) {

        Properties p = new Properties();
        if (fullFile == "" || fullFile.equals("")) {
            System.out.println("属性文件为空!~");
        } else {
            // 加载属性文件

            // DbPoolConnection.class.getClassLoader()
            // .getResourceAsStream(fullFile);
            try {
                InputStream inStream = new FileInputStream(new File(PathContants.getBinPath() + fullFile));
                p.load(inStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return p;
    }

    public void updateSQL(String sql) {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);// 准备执行语句
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != pst) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateSQL(String sql, Object[] param) {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);// 准备执行语句
            if (null != param) {
                for (int i = 0; i < param.length; i++) {
                    Object obj = param[i];
                    if (null == obj) {
                        continue;
                    }
                    Class<?> cls = obj.getClass();
                    if (cls.equals(String.class)) {
                        pst.setString(i + 1, (String) obj);
                    } else if (cls.equals(int.class) || cls.equals(Integer.class)) {
                        pst.setInt(i + 1, (int) obj);
                    }
                }
            }
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != pst) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object querySQL(String sql, OperAfterQuery oper) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet set = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);// 准备执行语句
            set = pst.executeQuery(sql);
            Object obj = oper.operation(set);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != pst) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Object querySQL(String sql, OperAfterQuery oper, Object[] param) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet set = null;
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);// 准备执行语句
            if (null != param) {
                for (int i = 0; i < param.length; i++) {
                    Object obj = param[i];
                    if (null == obj) {
                        continue;
                    }
                    Class<?> cls = obj.getClass();
                    if (cls.equals(String.class)) {
                        pst.setString(i + 1, (String) obj);
                    } else if (cls.equals(int.class) || cls.equals(Integer.class)) {
                        pst.setInt(i + 1, (int) obj);
                    }
                }
            }
            set = pst.executeQuery();
            Object obj = oper.operation(set);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != pst) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}