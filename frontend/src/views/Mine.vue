<template>
  <div id="building">
    <video
      autoplay
      muted
      loop
      playsinline
      id="video-bg"
      :poster="require('../assets/background/1.jpg')"
    >
      <source src="../assets/background/Black Sea.mp4" type="video/mp4" />
    </video>
    <el-container>
      <!-- width的宽度跟collapse一样动态控制 -->
      <user-aside
        :isCollapse="isCollapse"
        :activeIndexaside="activeIndexaside"
      ></user-aside>
      <el-container>
        <el-header>
          <div
            :title="isCollapse ? '点击展开' : '点击收起'"
            class="box_bgd"
            @click="isC"
          >
            <!-- 点击展开收起导航和切换对应图标 -->
            <img
              src="../assets/icon/1.png"
              style="width: 59px; height: 59px; border-radius: 20px"
              :title="isCollapse ? '展开' : '收起'"
            />
          </div>

          <!-- 搜索框 -->
          <div class="search-container">
            <el-input
              placeholder="请输入搜索内容"
              v-model="searchText"
              class="search-input"
              @keyup.enter.native="handleSearch"
            >
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="handleSearch"
              ></el-button>
            </el-input>
          </div>

          <div style="margin-left: auto; height: 40px">
            <el-tooltip
              effect="dark"
              :content="userInfo.username"
              placement="bottom"
            >
              <el-avatar
                icon="el-icon-user-solid"
                class="avatar-hover"
              ></el-avatar>
            </el-tooltip>
          </div>
          <div style="margin-left: 10px; height: 40px">
            <el-popconfirm
              title="确定切换到管理界面吗？"
              @confirm="gotoDashboard"
            >
              <el-button slot="reference">切换界面</el-button>
            </el-popconfirm>
            <el-popconfirm title="确定退出吗？" @confirm="gotoLogin"
              >//点击确认调用
              <el-button slot="reference">退出</el-button>
            </el-popconfirm>
          </div>
        </el-header>
        <el-main style="height: calc(100vh - 80px); overflow: auto">
          <div class="user-profile">
            <div style="position: relative; display: inline-block">
              <!-- 头像（动态绑定src） -->
              <!-- <el-avatar :size="80" :src="userInfo.image"></el-avatar> -->
              <el-avatar :size="80" :src="userInfo.image"></el-avatar>
              <el-button
                type="text"
                icon="el-icon-edit"
                @click="showAvatarDialog"
                style="position: absolute; top: 50px; left: 50px"
              ></el-button>
            </div>

            <!-- 用户名（插值语法） -->
            <h2>{{ userInfo.username }}</h2>

            <!-- 粉丝/关注（绑定数据） -->
            <div class="stats" style="color: red">
              <span>关注：{{ userInfo.followCount }} </span>
              <span>粉丝：{{ userInfo.fansCount }}</span>
            </div>

            <p v-if="userInfo.signature" style="color: red; margin-right: 10px">
              {{ userInfo.signature }}
              <el-button
                type="text"
                icon="el-icon-edit"
                @click="showEditSignatureDialog"
                style="color: red; padding: 0 5px"
              ></el-button>
            </p>
            <p v-else style="color: red; margin-right: 10px">
              世界你好
              <el-button
                type="text"
                icon="el-icon-edit"
                @click="showEditSignatureDialog"
                style="color: red; padding: 0 5px"
              ></el-button>
            </p>
            <!-- 新增性别显示 -->
            <div class="gender-display" style="margin: 10px 0">
              <el-tag
                :type="userInfo.gender == 1 ? 'primary' : 'danger'"
                class="no-border-tag"
              >
                <i
                  :class="
                    userInfo.gender == 1 ? 'el-icon-male' : 'el-icon-female'
                  "
                ></i>
                {{ userInfo.gender == 1 ? "男" : "女" }}
              </el-tag>
              <el-button
                type="text"
                icon="el-icon-edit"
                @click="showEditGenderDialog"
                style="padding: 0 5px"
              ></el-button>
            </div>
          </div>
          <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="1" @click="myworks">作品</el-menu-item>
            <el-menu-item index="2" @click="mylikes">喜欢</el-menu-item>
            <el-menu-item index="3" @click="mycollects">收藏</el-menu-item>
            <el-menu-item index="4" @click="myfollows">关注</el-menu-item>
            <el-menu-item index="5" @click="myfans">粉丝</el-menu-item>
            <el-menu-item index="6" disabled :style="{ fontSize: '14px' }"
              >Notice:点击即可进入对应栏目，点击视频卡片播放视频，再次点击暂停视频</el-menu-item
            >
          </el-menu>

          <!-- 我的作品视频展示区域 -->
          <div v-if="showWorks" class="video-grid">
            <!-- 当showWorks为true时才会展示我的作品 -->
            <el-row :gutter="20">
              <el-col
                v-for="(video, index) in videoList"
                :key="index"
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
              >
                <div class="video-card">
                  <img
                    v-show="!video.isPlaying"
                    :src="video.coverUrl"
                    class="cover"
                    @click="playVideo(index)"
                  />
                  <video
                    v-show="video.isPlaying"
                    ref="videoplayer"
                    :src="video.videoUrl"
                    loop
                    class="video"
                    @click="pauseVideo(index)"
                  ></video>
                  <!-- 底部信息栏 -->
                  <div class="video-info" v-show="!video.isPlaying">
                    <div class="title">标题：{{ video.videoTitle }}</div>
                    <div class="title">
                      状态：
                      {{
                        video.status == 1
                          ? "审核中"
                          : video.status == 2
                          ? "公开"
                          : "私密"
                      }}
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 我的喜欢视频展示区域 -->
          <div v-if="showLikes" class="video-grid">
            <!-- 当showLikes为true时才会展示我的喜欢 -->
            <el-row :gutter="20">
              <el-col
                v-for="(video, index) in videoList2"
                :key="index"
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
              >
                <div class="video-card">
                  <img
                    v-show="!video.isPlaying"
                    :src="video.coverUrl"
                    class="cover"
                    @click="playVideo2(index)"
                  />
                  <video
                    v-show="video.isPlaying"
                    ref="videoplayer"
                    :src="video.videoUrl"
                    loop
                    class="video"
                    @click="pauseVideo2(index)"
                  ></video>
                  <!-- 底部信息栏 -->
                  <div class="video-info" v-show="!video.isPlaying">
                    <div class="title">作者：{{ video.authorUsername }}</div>
                    <div class="title">标题：{{ video.videoTitle }}</div>
                    <div class="video-actions">
                      <!-- 点赞按钮 -->
                      <el-tooltip
                        :content="video.liked ? '已点赞' : '点赞'"
                        placement="top"
                      >
                        <el-button
                          circle
                          size="mini"
                          :icon="
                            video.liked ? 'el-icon-star-on' : 'el-icon-star-off'
                          "
                          :type="video.liked ? 'danger' : 'default'"
                          @click="toggleLike(video)"
                        >
                          <span class="action-count">{{
                            video.likeCount
                          }}</span>
                        </el-button>
                      </el-tooltip>
                      <!-- 收藏按钮 -->
                      <el-tooltip
                        :content="video.collected ? '已收藏' : '收藏'"
                        placement="top"
                      >
                        <el-button
                          circle
                          size="mini"
                          :icon="
                            video.collected
                              ? 'el-icon-folder-checked'
                              : 'el-icon-folder-add'
                          "
                          :type="video.collected ? 'warning' : 'default'"
                          @click="toggleCollect(video)"
                        >
                          <span class="action-count">{{
                            video.collectCount
                          }}</span>
                        </el-button>
                      </el-tooltip>
                      <!-- 分享按钮 -->
                      <el-tooltip :content="'分享'" placement="top">
                        <el-button
                          circle
                          size="mini"
                          :icon="'el-icon-share'"
                          @click="toggleShare(video)"
                        ></el-button>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 我的收藏视频展示区域 -->
          <div v-if="showCollects" class="video-grid">
            <!-- 当showCollects为true时才会展示我的收藏 -->
            <el-row :gutter="20">
              <el-col
                v-for="(video, index) in videoList3"
                :key="index"
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
              >
                <div class="video-card">
                  <img
                    v-show="!video.isPlaying"
                    :src="video.coverUrl"
                    class="cover"
                    @click="playVideo3(index)"
                  />
                  <video
                    v-show="video.isPlaying"
                    ref="videoplayer"
                    :src="video.videoUrl"
                    loop
                    class="video"
                    @click="pauseVideo3(index)"
                  ></video>
                  <!-- 底部信息栏 -->
                  <div class="video-info" v-show="!video.isPlaying">
                    <div class="title">作者：{{ video.authorUsername }}</div>
                    <div class="title">标题：{{ video.videoTitle }}</div>
                    <div class="video-actions">
                      <!-- 点赞按钮 -->
                      <el-tooltip
                        :content="video.liked ? '已点赞' : '点赞'"
                        placement="top"
                      >
                        <el-button
                          circle
                          size="mini"
                          :icon="
                            video.liked ? 'el-icon-star-on' : 'el-icon-star-off'
                          "
                          :type="video.liked ? 'danger' : 'default'"
                          @click="toggleLike(video)"
                        >
                          <span class="action-count">{{
                            video.likeCount
                          }}</span>
                        </el-button>
                      </el-tooltip>
                      <!-- 收藏按钮 -->
                      <el-tooltip
                        :content="video.collected ? '已收藏' : '收藏'"
                        placement="top"
                      >
                        <el-button
                          circle
                          size="mini"
                          :icon="
                            video.collected
                              ? 'el-icon-folder-checked'
                              : 'el-icon-folder-add'
                          "
                          :type="video.collected ? 'warning' : 'default'"
                          @click="toggleCollect(video)"
                        >
                          <span class="action-count">{{
                            video.collectCount
                          }}</span>
                        </el-button>
                      </el-tooltip>
                      <!-- 分享按钮 -->
                      <el-tooltip :content="'分享'" placement="top">
                        <el-button
                          circle
                          size="mini"
                          :icon="'el-icon-share'"
                          @click="toggleShare(video)"
                        ></el-button>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 我的关注展示区域 -->
          <div v-if="showFollows" class="follow-grid">
            <el-row :gutter="20">
              <el-col
                v-for="(user, index) in followList"
                :key="index"
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
              >
                <div class="user-card" @click="viewUserProfile(user)">
                  <el-avatar
                    :size="80"
                    :src="user.avatarUrl"
                    class="user-avatar"
                  ></el-avatar>
                  <div class="user-info">
                    <div class="username">{{ user.username }}</div>
                    <div class="stats2">
                      <span>关注 {{ user.followCount }}</span>
                      <span>粉丝 {{ user.fansCount }}</span>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 我的粉丝展示区域 -->
          <div v-if="showFans" class="follow-grid">
            <el-row :gutter="20">
              <el-col
                v-for="(user, index) in fansList"
                :key="index"
                :xs="12"
                :sm="8"
                :md="6"
                :lg="4"
              >
                <div class="user-card" @click="viewUserProfile(user)">
                  <el-avatar
                    :size="80"
                    :src="user.avatarUrl"
                    class="user-avatar"
                  ></el-avatar>
                  <div class="user-info">
                    <div class="username">{{ user.username }}</div>
                    <div class="stats2">
                      <span>关注 {{ user.followCount }}</span>
                      <span>粉丝 {{ user.fansCount }}</span>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <el-dialog
            title="修改签名"
            :visible.sync="signatureDialogVisible"
            width="400px"
          >
            <el-input
              type="textarea"
              :rows="3"
              placeholder="说点什么吧..."
              v-model="newSignature"
              maxlength="50"
              show-word-limit
            ></el-input>
            <span slot="footer" class="dialog-footer">
              <el-button @click="signatureDialogVisible = false"
                >取消</el-button
              >
              <el-button type="primary" @click="updateSignature"
                >保存</el-button
              >
            </span>
          </el-dialog>
          <!-- 新增修改性别的对话框 -->
          <el-dialog
            title="修改性别"
            :visible.sync="genderDialogVisible"
            width="400px"
          >
            <el-radio-group v-model="newGender">
              <el-radio label="1">男</el-radio>
              <el-radio label="2">女</el-radio>
            </el-radio-group>
            <span slot="footer" class="dialog-footer">
              <el-button @click="genderDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="updateGender">保存</el-button>
            </span>
          </el-dialog>
          <!-- 头像修改对话框 -->
          <el-dialog
            title="修改头像"
            :visible.sync="avatarDialogVisible"
            width="500px"
          >
            <el-upload
              class="avatar-uploader"
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleImageUpload"
            >
              <img v-if="ImageUrl" :src="ImageUrl" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <span slot="footer" class="dialog-footer">
              <el-button @click="avatarDialogVisible = false">取消</el-button>
              <el-button
                type="primary"
                @click="updateAvatar"
                :disabled="!newAvatarUrl"
                >保存</el-button
              >
            </span>
          </el-dialog>
          <el-backtop
            target=".el-main"
            :bottom="100"
            :right="40"
            style="
              background: rgba(255, 255, 255, 0.8);
              box-shadow: 0 0 6px rgba(0, 0, 0, 0.12);
              color: #1989fa;
            "
          >
            <div
              style="
                height: 100%;
                width: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 14px;
              "
            >
              UP
            </div>
          </el-backtop>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";
import UserAside from "@/components/UserAside.vue";

export default {
  components: { UserAside },
  name: "Mine",
  data() {
    return {
      keyword: "",
      searchText: "", // 添加搜索文本数据
      activeIndexaside: "4",
      activeIndex: "0",
      isCollapse: false,
      showWorks: false,
      showLikes: false,
      showCollects: false,
      showFollows: false,
      showFans: false,
      videoList: [] /* 我的作品 */,
      videoList2: [] /* 我的喜欢 */,
      videoList3: [] /* 我的收藏 */,
      followList: [] /*我的关注 */,
      fansList: [] /*我的粉丝*/,
      userInfo: {
        image: "", // 头像URL
        username: "", // 用户名
        followCount: 0, // 关注数
        fansCount: 0, // 粉丝数
        signature: "", // 签名
        gender: 1, //性别
      },
      signatureDialogVisible: false, // 控制对话框显示
      newSignature: "", // 临时存储新签名
      genderDialogVisible: false,
      newGender: 0,
      avatarDialogVisible: false,
      ImageUrl: "", // 临时预览URL
      newAvatarUrl: "", // 新头像服务器URL
      avatarFile: null, // 上传的文件对象
    };
  },
  methods: {
    isC() {
      this.isCollapse = !this.isCollapse;
    },
    gotoDashboard() {
      if (localStorage.getItem("currentUser") != "admin") {
        alert("你不是管理员，无法使用管理功能");
      } else {
        if (this.$route.path !== "/dashboard") {
          this.$router.push("/dashboard");
        } else {
          this.$message.info("已在当前页面");
        }
      }
    },
    gotoLogin() {
      this.$router.push("/login");
      localStorage.removeItem("currentUser");
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
    },
    // 用户基本信息
    userInformation() {
      axios
        .get("http://localhost:8080/getmine", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.userInfo.image = res.data.data.image;
            this.userInfo.followCount = res.data.data.followCount;
            this.userInfo.fansCount = res.data.data.fansCount;
            this.userInfo.signature = res.data.data.signature;
            this.userInfo.gender = String(res.data.data.gender);
            this.newGender = String(res.data.data.gender);
          } else {
            this.$message.error("获取数据失败！");
          }
        })
        .catch(function (error) {});
    },
    myworks() {
      this.showLikes = false;
      this.showCollects = false;
      this.showFollows = false;
      this.showFans = false;
      this.showWorks = true;
      axios
        .get("http://localhost:8080/getmyworks", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询我的作品成功");
            this.videoList = res.data.data.map((item) => ({
              videoId: item.id,
              coverUrl: item.coverUrl,
              videoUrl: item.videoUrl,
              videoTitle: item.videoTitle,
              status: item.status,
              isPlaying: false,
            }));
          }
        })
        .catch(function (error) {});
    },
    // 原视频卡片播放视频
    /*     playVideo(index) {
      this.videoList[index].isPlaying = true;
      this.$nextTick(() => {
        this.$refs.videoplayer[index].play();
      });
    }, */
    // 原视频卡片暂停视频
    /*     pauseVideo(index) {
      this.videoList[index].isPlaying = false;
      this.$refs.videoplayer[index].pause();
    }, */
    //我的作品全屏播放
    playVideo(index) {
      if (
        this.$route.path === "/videoplayer" &&
        this.$route.query.id === this.videoList[index].videoId
      ) {
        return; // 已在播放该视频，不重复跳转
      }
      console.log("进入视频全屏播放");
      this.$router.push({
        path: "/videoplayer",
        query: { id: this.videoList[index].videoId, from: "search" },
      });
    },

    /*     playVideo2(index) {
      this.videoList2[index].isPlaying = true;
      this.$nextTick(() => {
        this.$refs.videoplayer[index].play();
      });
    },
    pauseVideo2(index) {
      this.videoList2[index].isPlaying = false;
      this.$refs.videoplayer[index].pause();
    }, */
    //我的喜欢全屏播放
    playVideo2(index) {
      if (
        this.$route.path === "/videoplayer" &&
        this.$route.query.id === this.videoList2[index].videoId
      ) {
        return; // 已在播放该视频，不重复跳转
      }
      console.log("进入视频全屏播放");
      this.$router.push({
        path: "/videoplayer",
        query: { id: this.videoList2[index].videoId, from: "search" },
      });
    },

    /*     playVideo3(index) {
      this.videoList3[index].isPlaying = true;
      this.$nextTick(() => {
        this.$refs.videoplayer[index].play();
      });
    },
    pauseVideo3(index) {
      this.videoList3[index].isPlaying = false;
      this.$refs.videoplayer[index].pause();
    }, */
    //我的收藏全屏播放
    playVideo3(index) {
      if (
        this.$route.path === "/videoplayer" &&
        this.$route.query.id === this.videoList3[index].videoId
      ) {
        return; // 已在播放该视频，不重复跳转
      }
      console.log("进入视频全屏播放");
      this.$router.push({
        path: "/videoplayer",
        query: { id: this.videoList3[index].videoId, from: "search" },
      });
    },

    mylikes() {
      this.showWorks = false;
      this.showCollects = false;
      this.showFollows = false;
      this.showFans = false;
      this.showLikes = true;
      axios
        .get("http://localhost:8080/getmylikes", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询我的喜欢成功");
            this.videoList2 = res.data.data.map((item) => ({
              videoId: item.videoId,
              coverUrl: item.coverUrl,
              videoUrl: item.videoUrl,
              videoTitle: item.videoTitle,
              authorUsername: item.authorUsername,
              liked: item.liked,
              likeCount: item.likeCount,
              collected: item.collected,
              collectCount: item.collectCount,
              isPlaying: false,
            }));
          }
        })
        .catch(function (error) {});
    },
    toggleLike(video) {
      const params = new URLSearchParams();
      params.append("videoId", video.videoId);
      params.append("username", localStorage.getItem("currentUser"));
      const newStatus = !video.liked;

      axios
        .post("http://localhost:8080/like", params, {
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            // 更新所有该作者的视频
            video.liked = newStatus;
            this.$message.success(newStatus ? "点赞成功" : "已取消喜欢");
          }
        });
      if (video.liked) {
        video.likeCount--;
      } else {
        video.likeCount++;
      }
    },

    mycollects() {
      this.showWorks = false;
      this.showLikes = false;
      this.showFollows = false;
      this.showFans = false;
      this.showCollects = true;
      axios
        .get("http://localhost:8080/getmycollects", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询我的收藏成功");
            this.videoList3 = res.data.data.map((item) => ({
              videoId: item.videoId,
              coverUrl: item.coverUrl,
              videoUrl: item.videoUrl,
              videoTitle: item.videoTitle,
              authorUsername: item.authorUsername,
              liked: item.liked,
              likeCount: item.likeCount,
              collected: item.collected,
              collectCount: item.collectCount,
              isPlaying: false,
            }));
          }
        })
        .catch(function (error) {});
    },

    toggleCollect(video) {
      const params = new URLSearchParams();
      params.append("videoId", video.videoId);
      params.append("username", localStorage.getItem("currentUser"));
      const newStatus = !video.collected;

      axios
        .post("http://localhost:8080/collect", params, {
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            // 更新所有该作者的视频
            video.collected = newStatus;
            this.$message.success(newStatus ? "收藏成功" : "已取消收藏");
          }
        });
      if (video.collected) {
        video.collectCount--;
      } else {
        video.collectCount++;
      }
    },
    toggleShare(video) {
      if (video) {
        const videoUrl = video.videoUrl;
        const videoTitle = video.videoTitle;
        const category = video.category;
        const authorUsername = video.authorUsername;
        const textToCopy = `【${videoTitle}】\n作者：${authorUsername} 分类：${category}\n视频链接：${videoUrl}`;
        // 复制到剪贴板（假设在浏览器环境中）
        navigator.clipboard
          .writeText(textToCopy)
          .then(() => {
            alert("链接已复制到剪贴板！");
          })
          .catch((err) => {
            console.error("复制失败:", err);
          });
      } else {
        this.$message.error("复制失败");
      }
    },

    myfollows() {
      this.showWorks = false;
      this.showLikes = false;
      this.showCollects = false;
      this.showFans = false;
      this.showFollows = true;
      axios
        .get("http://localhost:8080/getmyfollows", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询我的关注成功");
            this.followList = res.data.data.map((item) => ({
              avatarUrl: item.image,
              followCount: item.followCount,
              fansCount: item.fansCount,
              username: item.username,
            }));
          }
        })
        .catch(function (error) {});
    },

    myfans() {
      this.showWorks = false;
      this.showLikes = false;
      this.showCollects = false;
      this.showFollows = false;
      this.showFans = true;
      axios
        .get("http://localhost:8080/getmyfans", {
          params: {
            username: this.userInfo.username,
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询我的粉丝成功");
            this.fansList = res.data.data.map((item) => ({
              avatarUrl: item.image,
              followCount: item.followCount,
              fansCount: item.fansCount,
              username: item.username,
            }));
          }
        })
        .catch(function (error) {});
    },
    // 用户卡片展示
    viewUserProfile(user) {
      if (
        this.$route.path === "/userplayer" &&
        this.$route.query.username === user.username
      ) {
        return; // 已在展示该用户，不重复跳转
      }
      console.log("进入用户界面展示");
      this.$router.push({
        path: "/userplayer",
        query: { username: user.username, from: "search" },
      });
    },
    //搜索方法
    handleSearch() {
      if (this.searchText.trim()) {
        // 这里可以添加搜索逻辑
        console.log("搜索内容:", this.searchText);
        this.$router.push({
          path: "/search",
          query: { keyword: this.searchText },
        });
      } else {
        alert("请输入搜索内容");
      }
    },
    // 显示签名编辑对话框
    showEditSignatureDialog() {
      this.newSignature = this.userInfo.signature || "1"; // 初始化内容
      this.signatureDialogVisible = true;
    },

    // 更新签名
    updateSignature() {
      if (!this.newSignature.trim()) {
        this.$message.warning("签名不能为空");
        return;
      }
      const params = new URLSearchParams();
      params.append("username", localStorage.getItem("currentUser"));
      params.append("signature", this.newSignature);
      axios
        .post("http://localhost:8080/updatesignature", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("签名修改成功");
            this.userInfo.signature = this.newSignature; // 更新本地数据
            this.signatureDialogVisible = false; // 关闭对话框
          } else {
            this.$message.error(res.data.message || "更新失败");
          }
        });
    },
    showEditGenderDialog() {
      this.newGender = this.userInfo.gender;
      this.genderDialogVisible = true;
    },
    updateGender() {
      if (!this.newGender) {
        this.$message.warning("请选择性别");
        return;
      }
      console.log(this.newGender);
      const params = new URLSearchParams();
      params.append("username", localStorage.getItem("currentUser"));
      params.append("gender", this.newGender);

      axios
        .post("http://localhost:8080/updategender", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("性别修改成功");
            this.userInfo.gender = this.newGender; // 更新本地数据
            this.genderDialogVisible = false; // 关闭对话框
          } else {
            this.$message.error(res.data.message || "更新失败");
          }
        });
    },
    showAvatarDialog() {
      this.avatarDialogVisible = true;
      this.ImageUrl = this.userInfo.image; // 初始化显示当前头像
      this.newAvatarUrl = ""; // 重置新头像URL
    },

    // 处理图片上传
    handleImageUpload(file) {
      // 验证文件类型和大小
      const isImage = ["image/jpeg", "image/png", "image/gif"].includes(
        file.raw.type
      );
      const isLt2M = file.raw.size / 1024 / 1024 < 2;

      if (!isImage) {
        this.$message.error("只能上传图片文件!");
        return false;
      }
      if (!isLt2M) {
        this.$message.error("图片大小不能超过2MB!");
        return false;
      }

      // 创建预览
      this.ImageUrl = URL.createObjectURL(file.raw);
      this.avatarFile = file.raw;

      // 立即上传到服务器
      this.uploadImage(file.raw);

      return false; // 阻止自动上传
    },

    // 上传图片到服务器
    uploadImage(file) {
      const formData = new FormData();
      formData.append("image", file);
      const token = localStorage.getItem("token");

      axios
        .post("http://localhost:8080/uploadimage", formData, {
          headers: {
            token: token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.newAvatarUrl = res.data.data; // 保存服务器返回的URL
            this.$message.success(res.data.message || "图片上传成功");
          } else {
            this.$message.error("图片上传失败");
            this.ImageUrl = this.userInfo.image; // 恢复原头像
          }
        })
        .catch((error) => {
          this.$message.error("图片上传失败");
          this.ImageUrl = this.userInfo.image; // 恢复原头像
          console.error(error);
        });
    },

    // 更新头像
    updateAvatar() {
      if (!this.newAvatarUrl) {
        this.$message.warning("请先上传新头像");
        return;
      }

      const params = new URLSearchParams();
      params.append("username", this.userInfo.username);
      params.append("image", this.newAvatarUrl);

      axios
        .post("http://localhost:8080/updateimage", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("头像更新成功");
            this.userInfo.image = this.newAvatarUrl;
            this.avatarDialogVisible = false;
          } else {
            this.$message.error(res.data.message || "头像更新失败");
          }
        })
        .catch((error) => {
          this.$message.error("头像更新失败: " + error.message);
        });
    },
  },
  created() {
    this.token = localStorage.getItem("token");
  },
  mounted() {
    this.userInfo.username = localStorage.getItem("currentUser");
    this.userInformation();
  },
  beforeDestroy() {
    const video = document.getElementById("video-bg");
    if (video) {
      video.pause();
      video.removeAttribute("src"); // 关键！解除资源引用
      video.load(); // 触发内存回收
    }
    // 移除所有事件监听器
    window.removeEventListener("resize", this.handleResize);
  },
};
</script>

<style scoped>
.el-header {
  background: rgba(18, 9, 9, 0.3);
  color: #fbfbfb;
  padding: 0 !important;
  height: 80px;
  display: flex;
  align-items: center;
}

#building {
  background: url("../assets/background/Black Sea.mp4");
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  background-size: cover;
  background-attachment: fixed;
}
/* 视频背景样式 */
#video-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1; /* 确保视频在内容下方 */
  background: url("../assets/background/1.jpg") no-repeat center center; /* 备用背景 */
  background-size: cover;
}
/* 确保主要内容容器有相对定位 */
.el-container {
  position: relative;
  z-index: 1; /* 确保内容在视频上方 */
  min-height: 100vh;
}
.el-menu {
  border-right-width: 0;
}
.el-main {
  color: #333;
}
.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}
.box_bgd {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.box_bgd:hover {
  background: rgb(203, 215, 230);
}
/* 用户界面 */
.user-profile {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.profile-info {
  margin-left: 20px;
}

.profile-info h2 {
  margin: 0;
  font-size: 24px;
}

.profile-info p {
  margin: 5px 0 0;
  color: #888;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}
.avatar-hover {
  cursor: pointer;
  transition: all 0.3s;
}
.avatar-hover:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
}

.video-grid {
  margin-top: 20px;
  padding: 0 10px;
}

.video-card {
  margin-bottom: 20px;
  border-radius: 8px;
  height: 250px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  position: relative;
  cursor: pointer;
}
/* 视频信息区域 - 覆盖在封面上 */
.video-info {
  position: absolute; /* 绝对定位 */
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: rgba(0, 0, 0, 0.7); /* 半透明黑色背景 */
  color: white;
  transition: all 0.3s;
}
.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.3s;
}

.video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.3s;
}

/* 悬停时信息区域效果 */
.video-card:hover .video-info {
  background: rgba(0, 0, 0, 0.8); /* 悬停时更明显 */
}

/* 调整内部元素样式 */
.video-info .title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: rgba(251, 249, 249, 0.3);
}

/*关注卡片区域 */
.follow-grid {
  padding: 20px;
}

.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.user-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.2);
}

.user-avatar {
  margin-bottom: 10px;
}

.user-info {
  text-align: center;
}

.username {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 16px;
}

.stats2 {
  display: flex;
  justify-content: space-around;
  width: 100%;
  font-size: 14px;
  color: #666;
}

.stats2 span {
  margin: 0 5px;
}
.search-container {
  display: flex;
  align-items: center;
  margin: 0 auto;
  width: 400px; /* 可以根据需要调整宽度 */
}

.search-input {
  width: 100%;
}
/* 使签名和编辑按钮在同一行 */
.user-profile p {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 对话框文本域样式 */
.el-textarea__inner {
  font-family: inherit;
  color: #606266;
}
.el-backtop {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.el-backtop:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.1);
}
.gender-display {
  display: flex;
  align-items: center;
  justify-content: center;
}

.gender-display .el-tag {
  background-color: rgba(255, 255, 255, 0);

  margin-right: 8px;
  font-size: 14px;
}
.no-border-tag {
  border: none !important;
  background: transparent !important;
}
.avatar-uploader {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.avatar-uploader /deep/ .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}

.avatar-uploader /deep/ .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>