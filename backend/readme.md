# 项目开发

## 后端包结构
[structure.txt](src/main/java/structure.txt)


## 前端包结构
[structure.txt](../../../develop/Vue/vue-project/src/structure.txt)


## 待开发清单
1.公网映射；

2.接入大模型；


## 数据

#### 视频爬取
启动docker的视频爬取容器“douyin_tiktok_api”（localhost:7200）

抖音十个热门标签各20左右的视频
【admin（1.美女 2.游戏 3.音乐 4.美食） 
sports（5.体育） 
fanzhibo（6.旅游 7.动物） 
bdmin（8.汽车） 
zhangchi（9.生活） 
zhaoyiming（10.科技）】



#### 数据库
create table user
(
id          int unsigned auto_increment comment 'ID'
primary key,
username    varchar(20)                       not null comment '用户名',
name        varchar(10)                       not null comment '姓名',
password    varchar(32)      default '123456' null comment '密码',
gender      tinyint unsigned default '1'      not null comment '性别',
image       varchar(300)                      null comment '图像',
create_time datetime                          not null comment '创建时间',
update_time datetime                          not null comment '修改时间',
constraint user_pk_2
unique (username)
)
comment '用户';

create table video
(
id          int unsigned auto_increment comment 'ID'
primary key,
author_id   int unsigned                 not null comment '作者',
video_url   varchar(300)                 not null comment '视频链接',
cover_url   varchar(300)                 not null comment '封面图片',
video_title varchar(300)                 not null comment '视频标题',
status      tinyint unsigned default '1' not null comment '状态:1.审核中,2.公开,3.私密',
category    varchar(20)                  null comment '类别',
like_base   int unsigned     default '0' null comment '基础点赞数',
create_time datetime                     not null comment '创建时间',
update_time datetime                     not null comment '修改时间'
)
comment '视频表';

create table `like`
(
id       int unsigned auto_increment comment 'id'
primary key,
video_id int unsigned not null comment '视频id',
user_id  int unsigned not null comment '用户id'
)
comment '喜欢表';

create table follow
(
id        int unsigned auto_increment comment 'id'
primary key,
follow_id int unsigned not null comment '被关注者id',
fans_id   int unsigned not null comment '粉丝id'
)
comment '关注表';

create table collect
(
id       int unsigned auto_increment comment 'id'
primary key,
video_id int unsigned not null comment '视频ID',
user_id  int unsigned not null comment '用户id'
)
comment '收藏表';

create table message
(
id          int unsigned auto_increment comment 'ID'
primary key,
user_id     int unsigned not null comment '用户id',
contents    varchar(300) not null comment '留言内容',
create_time datetime     not null comment '创建时间',
constraint message_pk_2
unique (user_id)
)
comment '留言';

create table notice
(
id          int unsigned auto_increment comment 'ID'
primary key,
title       varchar(100) not null comment '标题',
content     varchar(300) not null comment '内容',
create_time datetime     not null comment '创建时间'
)
comment '公告';

create table comment
(
id              int unsigned auto_increment comment 'ID'
primary key,
video_id        int unsigned     not null comment '视频ID',
user_id         int unsigned     not null comment '用户ID',
contents        varchar(300)     not null comment '评论内容',
translation     varchar(300)     not null comment '翻译',
sentiment_score tinyint unsigned not null comment '情感分析结果：0:Very negative 1:Negative 2:Neutral 3:Positive 4:Very positive',
sentiment_prob  double           not null comment '情感极性概率',
create_time     datetime         not null comment '创建时间'
)
comment '评论';

create table view
(
id         int unsigned auto_increment comment 'ID'
primary key,
video_id   int unsigned not null comment '视频ID',
user_id    int unsigned not null comment '用户ID',
count      int unsigned not null comment '点击观看次数',
first_time datetime     not null comment '第一次点击时间'
)
comment '观看记录';




## 接口

### 评论分析
CoreNLP
初始化CoreNLP通道
在用户发布评论后对评论进行情感分析
获取结果存储mysql


### 推荐
#### 社交网络


#### 内容
##### 内容标签
##### 内容语义（情感分析）
[评论情感分析计算情感匹配度.docx](../../../2021.9-2025.7%20%E6%AD%A6%E6%B1%89%E7%A7%91%E6%8A%80%E5%A4%A7%E5%AD%A6%E6%9C%AC%E7%A7%91/%E6%AF%95%E4%B8%9A%E8%AE%BE%E8%AE%A1/%E6%AF%95%E4%B8%9A%E8%AE%BA%E6%96%87/%E4%BD%BF%E7%94%A8%E5%85%AC%E5%BC%8F%E5%8F%82%E8%80%83/%E8%AF%84%E8%AE%BA%E6%83%85%E6%84%9F%E5%88%86%E6%9E%90%E8%AE%A1%E7%AE%97%E6%83%85%E6%84%9F%E5%8C%B9%E9%85%8D%E5%BA%A6.docx)

# 项目细节注意事项
1.视频点赞数：查询所有用户点赞数+视频like_base

2.如果用户的视频被下架，但是用户还是想要视频公开，那么需要协调后重新上传

3.系统管理员不能删除用户



# 开发注意事项
1.https://www.douyin.com/search/后面加关键词直接搜索相关视频