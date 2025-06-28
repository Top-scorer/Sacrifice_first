<template>
  <div id="building">
    <!-- 返回按钮 -->
    <el-button
      class="back-button"
      type="primary"
      icon="el-icon-arrow-left"
      circle
      @click="goBack"
    ></el-button>
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
    <div class="user-profile-container">
      <!-- 用户信息卡片 -->
      <div class="user-card1">
        <!-- 用户头像 -->
        <el-avatar
          :size="120"
          :src="userInfo.image"
          class="user-avatar1"
        ></el-avatar>

        <!-- 用户基本信息 -->
        <div class="user-info1">
          <h2 class="username">{{ userInfo.username }}</h2>

          <!-- 关注/粉丝数据 -->
          <div class="stats1">
            <div class="stat-item">
              <span class="label">关注</span>
              <span class="count">{{ userInfo.followCount }}</span>
            </div>
            <div class="stat-item">
              <span class="label">粉丝</span>
              <span class="count">{{ userInfo.fansCount }}</span>
            </div>
          </div>

          <!-- 用户签名 -->
          <p class="signature1" v-if="userInfo.signature">
            {{ userInfo.signature }}
          </p>
          <p class="signature1" v-else>{{ userInfo.signature }}</p>

          <!-- 关注按钮 -->
          <el-button
            type="primary"
            size="medium"
            @click="toggleFollow"
            class="follow-btn"
          >
            {{ isFollowing ? "已关注" : "关注" }}
          </el-button>
        </div>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeIndex"
        class="profile-menu"
        mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="1" @click="showTab('works')">作品</el-menu-item>
        <el-menu-item index="2" @click="showTab('likes')">喜欢</el-menu-item>
        <el-menu-item index="3" @click="showTab('collects')">收藏</el-menu-item>
        <el-menu-item index="4" @click="showTab('follows')">关注</el-menu-item>
        <el-menu-item index="5" @click="showTab('fans')">粉丝</el-menu-item>
        <el-menu-item index="6" disabled :style="{ fontSize: '14px' }">
          Notice:点击即可进入对应栏目，点击视频卡片播放视频，再次点击暂停视频
        </el-menu-item>
      </el-menu>

      <!-- 作品展示区域 -->
      <div v-if="activeTab === 'works'" class="content-grid">
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
              <div class="video-info" v-show="!video.isPlaying">
                <div class="title">标题：{{ video.videoTitle }}</div>
                <div class="status">
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

      <!-- 喜欢展示区域 -->
      <div v-if="activeTab === 'likes'" class="content-grid">
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
              <div class="video-info" v-show="!video.isPlaying">
                <div class="title">标题：{{ video.videoTitle }}</div>
                <div class="author">作者：{{ video.authorUsername }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 收藏展示区域 -->
      <div v-if="activeTab === 'collects'" class="content-grid">
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
              <div class="video-info" v-show="!video.isPlaying">
                <div class="title">标题：{{ video.videoTitle }}</div>
                <div class="author">作者：{{ video.authorUsername }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 关注用户展示区域 -->
      <div v-if="activeTab === 'follows'" class="user-grid">
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
              <el-avatar :size="80" :src="user.avatarUrl"></el-avatar>
              <div class="user-info">
                <h4>{{ user.username }}</h4>
                <div class="user-stats">
                  <span
                    ><i class="el-icon-user"></i> 关注
                    {{ user.followCount }}</span
                  >
                  <span
                    ><i class="el-icon-user"></i> 粉丝
                    {{ user.fansCount }}</span
                  >
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 粉丝展示区域 -->
      <div v-if="activeTab === 'fans'" class="user-grid">
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
              <el-avatar :size="80" :src="user.avatarUrl"></el-avatar>
              <div class="user-info">
                <h4>{{ user.username }}</h4>
                <div class="user-stats">
                  <span
                    ><i class="el-icon-user"></i> 关注
                    {{ user.followCount }}</span
                  >
                  <span
                    ><i class="el-icon-user"></i> 粉丝
                    {{ user.fansCount }}</span
                  >
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserPlayer",
  data() {
    return {
      activeIndex: "1",
      activeTab: "works",
      userInfo: {
        image: "", // 头像URL
        username: "", // 用户名
        followCount: 0, // 关注数
        fansCount: 0, // 粉丝数
        signature: "世界你好", // 签名
      },
      isFollowing: false,
      videoList: [],
      videoList2: [],
      videoList3: [],
      followList: [],
      fansList: [],
    };
  },
  created() {
    this.token = localStorage.getItem("token");
    this.userInfo.username = this.$route.query.username;
    this.userInformation();
  },
  watch: {
    // 监听路由参数变化
    "$route.query.username": {
      immediate: true, // 立即执行一次
      handler(newUsername, oldUsername) {
        if (newUsername !== oldUsername && oldUsername) {
          // 用户名变化时刷新数据
          this.userInfo.username = newUsername;
          this.userInformation();
          this.showTab(this.activeTab); // 重新加载当前标签页数据
        }
      },
    },
  },
  methods: {
    userInformation() {
      //获取展示用户信息
      axios
        .get("http://localhost:8080/getuser", {
          params: {
            username: this.userInfo.username,
            myusername: localStorage.getItem("currentUser"),
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
            this.isFollowing = res.data.data.followed;
          } else {
            this.$message.error("获取数据失败！");
          }
        })
        .catch(function (error) {});
    },
    // 切换关注状态
    //如果已经关注就取消，如果未关注就关注
    toggleFollow() {
      const params = new URLSearchParams();
      params.append("authorUsername", this.userInfo.username);
      params.append("username", localStorage.getItem("currentUser"));
      const newStatus = !this.isFollowing;
      axios
        .post("http://localhost:8080/follow", params, {
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.isFollowing = newStatus;
            this.$message.success(newStatus ? "关注成功" : "已取消关注");
          }
        });
    },
    showTab(tabName) {
      this.activeTab = tabName;
      if (this.activeTab == "works") {
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
      } else if (this.activeTab == "likes") {
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
      } else if (this.activeTab == "collects") {
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
      } else if (this.activeTab == "follows") {
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
      } else if (this.activeTab == "fans") {
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
      }
    },
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
    goBack() {
      this.$router.go(-1); // 返回上一页
    },
  },
};
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-profile-header {
  text-align: center;
  margin-bottom: 20px;
}

.user-profile-header h2 {
  margin: 15px 0 10px;
}
.user-card1 {
  width: 90%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  margin-left: auto;
  margin-right: auto;
  margin-top: 0;
  margin-bottom: 20px;
}

.user-avatar1 {
  margin-bottom: 20px;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info1 {
  width: 100%;
}

.username1 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #333;
}

.stats1 {
  display: flex;
  justify-content: center;
  margin: 20px 0;
}

.stat-item {
  margin: 0 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-item:hover {
  transform: scale(1.05);
}

.count {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.label {
  font-size: 14px;
  color: #666;
}

.signature {
  margin: 20px 0;
  color: #666;
  font-size: 16px;
  line-height: 1.5;
}

.follow-btn {
  width: 120px;
  margin-top: 15px;
}

.stats {
  margin: 10px 0;
}

.stats span {
  margin: 0 10px;
}

.profile-menu {
  margin-bottom: 20px;
}

.content-grid {
  margin-top: 20px;
  padding: 0 10px;
}
.user-grid {
  padding: 20px;
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

.video-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.2);
}

.cover,
.video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: opacity 0.3s;
}

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

.video-info .status,
.video-info .author {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.user-card {
  margin-bottom: 20px;
  padding: 15px;
  text-align: center;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.user-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.2);
}

.user-info h4 {
  margin: 10px 0 5px;
}

.user-stats {
  font-size: 12px;
  color: #909399;
}

.user-stats span {
  margin: 0 5px;
}

@media (max-width: 768px) {
  .video-card,
  .user-card {
    margin-bottom: 15px;
  }

  .cover,
  .video {
    height: 150px;
  }
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
.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  background-color: rgba(255, 255, 255, 0.3);
  border: none;
  color: #fff;
  font-size: 18px;
  width: 40px;
  height: 40px;
  transition: all 0.3s;
}

.back-button:hover {
  background-color: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}
</style>