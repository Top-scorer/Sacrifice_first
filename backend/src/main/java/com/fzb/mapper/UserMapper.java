package com.fzb.mapper;

import com.fzb.pojo.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {


    /**
     * 根据用户名和密码查询用户
     * @param user
     * @return
     */
    @Select("select * from user where username = #{username} and password = #{password}")
    public User getByUsernameAndPassword(User user);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Select("select * from user where username = #{username}")
    public User getByUsername(String username);

    /**
     * 新增用户
     * @param user
     */
    public void insert(User user);

    /**
     *分页查询
     * @param name
     * @param gender
     * @param begin
     * @param end
     * @return
     */
    public List<User> list(String name, Short gender, LocalDateTime begin, LocalDateTime end);

    /**
     * 查询用户图像、关注、粉丝
     * @param username
     * @return
     */
    @Select("SELECT \n" +
            "    u.image,\n" +
            "    u.signature,\n" +
            "    u.gender,\n" +
            "    (SELECT COUNT(*) FROM follow WHERE fans_id = u.id) AS followCount,\n" +
            "    (SELECT COUNT(*) FROM follow WHERE follow_id = u.id) AS fansCount\n" +
            "FROM \n" +
            "    user u\n" +
            "WHERE \n" +
            "    u.username = #{username};")
    public UserInfo getUserInfo(String username);

    /**
     * 查询用户图像、关注、粉丝、是否关注
     * @param username
     * @param myusername
     * @return
     */
    @Select("SELECT u.image," +
            "u.signature," +
            "(SELECT COUNT(*) FROM follow WHERE fans_id = u.id) AS followCount," +
            "(SELECT COUNT(*) FROM follow WHERE follow_id = u.id) AS fansCount, " +
            "EXISTS(SELECT 1 FROM follow f JOIN user u2 ON f.fans_id = u2.id " +
            "WHERE f.follow_id = u.id AND u2.username = #{myusername}) AS followed " +
            "FROM user u  " +
            "WHERE u.username = #{username}")
    public UserInfo getUserInfo2(String username, String myusername);


    /**
     * 更新用户信息
     * @param user
     */
    @Update("update user set name = #{name}, gender = #{gender}, image = #{image}, update_time = #{updateTime} where id = #{id}")
    public void update(User user);

    /**
     * 检查是否关注
     * @return
     */
    @Select("SELECT EXISTS(\n" +
            "    SELECT 1 \n" +
            "    FROM follow f\n" +
            "    JOIN user u_follow ON f.follow_id = u_follow.id  -- 被关注者(作者)\n" +
            "    JOIN user u_fans ON f.fans_id = u_fans.id        -- 粉丝(当前用户)\n" +
            "    WHERE u_follow.username = #{authorUsername}\n" +
            "    AND u_fans.username = #{username}\n" +
            ")")
    public boolean checkFollow(String authorUsername, String username);

    /**
     * 插入关注
     * @param authorUsername
     * @param username
     */
    @Insert("INSERT INTO follow (follow_id, fans_id)\n" +
            "SELECT \n" +
            "    author.id,      -- 被关注者的用户ID\n" +
            "    fan.id          -- 粉丝的用户ID\n" +
            "FROM \n" +
            "    user author,\n" +
            "    user fan\n" +
            "WHERE \n" +
            "    author.username = #{authorUsername}  -- 被关注者的用户名\n" +
            "    AND fan.username = #{username};     -- 粉丝的用户名")
    public void insertfollow(String authorUsername, String username);

    /**
     * 删除关注
     * @param authorUsername
     * @param username
     */
    @Delete("DELETE FROM follow\n" +
            "WHERE follow_id = (SELECT id FROM user WHERE username = #{authorUsername})\n" +
            "AND fans_id = (SELECT id FROM user WHERE username = #{username});")
    public void deletefollow(String authorUsername, String username);

    /**
     * 查询所有的关注
     * @param username
     * @return
     */
    @Select("SELECT " +
            "    u.username, " +
            "    u.image, " +
            "    (SELECT COUNT(*) FROM follow WHERE fans_id = u.id) AS followCount, " +
            "    (SELECT COUNT(*) FROM follow WHERE follow_id = u.id) AS fansCount " +
            "FROM " +
            "    follow f " +
            "JOIN " +
            "    user u ON f.follow_id = u.id " +  // 关注的是用户ID
            "JOIN " +
            "    user me ON f.fans_id = me.id " +  // 当前用户作为粉丝
            "WHERE " +
            "    me.username = #{username}")
    public List<followUser> getFollowList(String username);

    /**
     * 查询所有的粉丝
     * @param username
     * @return
     */
    @Select("SELECT " +
            "    u.username, " +
            "    u.image, " +
            "    (SELECT COUNT(*) FROM follow WHERE fans_id = u.id) AS followCount, " +
            "    (SELECT COUNT(*) FROM follow WHERE follow_id = u.id) AS fansCount " +
            "FROM " +
            "    follow f " +
            "JOIN " +
            "    user u ON f.fans_id = u.id " +  // 粉丝的用户ID
            "JOIN " +
            "    user me ON f.follow_id = me.id " +  // 被关注的当前用户
            "WHERE " +
            "    me.username = #{username}")
    public List<followUser> getFansList(String username);

    /**
     * 根据用户名和关键词搜索用户
     * @param username
     * @param keyword
     * @return
     */
    @Select({"SELECT \n" +
            "        u.id AS userId,\n" +
            "        u.username,\n" +
            "        u.image,\n" +
            "        EXISTS(\n" +
            "            SELECT 1 FROM follow f \n" +
            "            WHERE f.follow_id = u.id \n" +
            "            AND f.fans_id = (SELECT id FROM user WHERE username = #{username})\n" +
            "        ) AS followed,\n" +
            "        (SELECT COUNT(*) FROM follow WHERE fans_id = u.id) AS followCount,\n" +
            "        (SELECT COUNT(*) FROM follow WHERE follow_id = u.id) AS fansCount\n" +
            "    FROM \n" +
            "        user u\n" +
            "    WHERE \n" +
            "        u.username LIKE CONCAT('%', #{keyword}, '%')\n" +
            "        OR u.name LIKE CONCAT('%', #{keyword}, '%')\n" +
            "    ORDER BY \n" +
            "        u.username"})
    public List<SearchUserResult> list2(String username, String keyword);

    /**
     * 更新用户头像
     * @param username
     * @param image
     * @param updateTime
     */
    @Update("UPDATE user SET image = #{image}, update_time = #{updateTime} WHERE username = #{username}")
    public void updateImage(String username, String image, LocalDateTime updateTime);

    /**
     * 更新用户性别
     * @param username
     * @param gender
     * @param updateTime
     */
    @Update("UPDATE user SET gender = #{gender}, update_time = #{updateTime} WHERE username = #{username}")
    public void updateGender(String username, Integer gender, LocalDateTime updateTime);

    /**
     * 更新用户签名
     * @param username
     * @param signature
     * @param updateTime
     */
    @Update("UPDATE user SET signature = #{signature}, update_time = #{updateTime} WHERE username = #{username}")
    public void updateSignature(String username, String signature, LocalDateTime updateTime);

    /**
     * 查询所有用户
     * @return
     */
    @Select("SELECT id, username FROM user ORDER BY username")
    public List<UserIdAndUsername> userList();
}
