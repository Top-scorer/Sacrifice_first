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
        <el-main>
          <div class="search-results-container">
            <!-- 搜索结果头部信息 -->
            <div class="search-header">
              <h2>搜索结果</h2>
              <div class="search-meta">
                <span
                  ><strong class="highlight-number"
                    >找到 {{ totalResults }} 条结果</strong
                  ></span
                >
                <span class="search-keyword">"{{ lastSearchText }}"</span>
              </div>
            </div>

            <!-- 搜索结果分类标签 -->
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
              <el-tab-pane label="视频" name="videos"></el-tab-pane>
              <el-tab-pane label="用户" name="users"></el-tab-pane>
            </el-tabs>

            <!-- 视频搜索结果 -->
            <div v-show="activeTab === 'videos'" class="video-results-section">
              <el-row :gutter="20">
                <el-col
                  v-for="(video, index) in videoResults"
                  :key="index"
                  :xs="12"
                  :sm="8"
                  :md="6"
                  :lg="4"
                >
                  <div class="video-card" @click="playVideo(video)">
                    <img :src="video.coverUrl" class="cover" />
                    <div class="video-info">
                      <div class="title">{{ video.videoTitle }}</div>
                      <div class="creator">
                        <el-avatar :size="24" :src="video.image"></el-avatar>
                        <span>{{ video.authorUsername }}</span>
                      </div>
                      <div class="stats">
                        <span
                          ><i class="el-icon-star-off"></i>
                          {{ video.likeCount }}</span
                        >
                        <span
                          ><i class="el-icon-folder-add"></i>
                          {{ video.collectCount }}</span
                        >
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <!-- 用户搜索结果 -->
            <div v-show="activeTab === 'users'" class="user-results-section">
              <el-row :gutter="20">
                <el-col
                  v-for="(user, index) in userResults"
                  :key="index"
                  :xs="12"
                  :sm="8"
                  :md="6"
                  :lg="4"
                >
                  <div class="user-card" @click="viewUserProfile(user)">
                    <el-avatar :size="80" :src="user.image"></el-avatar>
                    <div class="user-info">
                      <h4>{{ user.username }}</h4>
                      <p class="user-bio">{{ user.bio || "世界你好" }}</p>
                      <div class="user-stats">
                        <span
                          ><i class="el-icon-user"></i>关注
                          {{ user.followCount }}</span
                        >
                        <span
                          ><i class="el-icon-user"></i>粉丝
                          {{ user.fansCount }}</span
                        >
                      </div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
          <el-backtop
            target=".el-main"
            :bottom="100"
            :right="40"
            style="
              background: rgba(255, 255, 255, 0.8);
              box-shadow: 0 0 6px rgba(0, 0, 0, 0.12);
              color: #1989fa;
              border-radius: 50%;
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
import VideoPlayer from "@/components/VideoPlayer.vue";

export default {
  props: ["keyword"],
  components: { UserAside, VideoPlayer },
  name: "Search",
  data() {
    return {
      searchText: "",
      lastSearchText: "",
      activeIndexaside: "7",
      isCollapse: false,
      userInfo: {
        username: "",
      },
      // 搜索相关数据
      activeTab: "videos", // 默认显示视频
      loading: false,
      videoResults: [],
      userResults: [],
      totalResults: 0,
    };
  },
  watch: {
    "$route.query.keyword": {
      immediate: true,
      handler(newKeyword, oldKeyword) {
        if (newKeyword !== oldKeyword) {
          // 如果父组件没有自动更新props，可以这样处理
          this.searchText = newKeyword || "";
          this.lastSearchText = newKeyword || "";
          this.doSearch(newKeyword);
        }
      },
    },
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
    // 搜索方法
    handleSearch() {
      if (!this.searchText) {
        alert("请输入搜索内容");
        this.searchText = this.lastSearchText;
        console.log("上次搜索内容：", this.lastSearchText);
      } else {
        if (
          (this.searchText.trim() && !this.lastSearchText) ||
          this.searchText.trim() != this.lastSearchText.trim()
        ) {
          // 这里可以添加搜索逻辑
          console.log("搜索内容:", this.searchText);
          this.$router.push({
            path: "/search",
            query: { keyword: this.searchText.trim() },
          });
          this.lastSearchText = this.searchText;
        } else if (this.searchText.trim() == this.lastSearchText.trim()) {
          console.log("搜索内容:", this.searchText);
          alert("已在当前页面");
        } else {
          alert("请输入搜索内容");
          this.searchText = this.lastSearchText;
          console.log("上次搜索内容：", this.lastSearchText);
        }
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name;
      if (this.lastSearchText) {
        this.doSearch(this.lastSearchText);
      }
    },

    // 搜索执行
    doSearch(searchKeyword) {
      console.log("执行搜索内容:", searchKeyword);
      if (!searchKeyword) return;
      this.loading = true;
      if (this.activeTab === "videos") {
        axios
          .get("http://localhost:8080/searchVideoResults", {
            params: {
              username: this.userInfo.username,
              keyword: this.lastSearchText.trim(),
            },
            headers: {
              token: this.token,
            },
          })
          .then((res) => {
            if (res.data.code == 1) {
              this.totalResults = res.data.data.length;
              this.videoResults = res.data.data.map((item) => ({
                videoId: item.videoId,
                videoUrl: item.videoUrl,
                coverUrl: item.coverUrl,
                videoTitle: item.videoTitle,
                category: item.category,
                authorUsername: item.authorUsername,
                image: item.image,
                follewed: item.follewed,
                liked: item.liked,
                collected: item.collected,
                likeCount: item.likeCount,
                collectCount: item.collectCount,
                isPlaying: false,
              }));
            }
          })
          .catch(function (error) {});
      }

      if (this.activeTab === "users") {
        axios
          .get("http://localhost:8080/searchUserResults", {
            params: {
              username: this.userInfo.username,
              keyword: this.lastSearchText.trim(),
            },
            headers: {
              token: this.token,
            },
          })
          .then((res) => {
            if (res.data.code == 1) {
              this.totalResults = res.data.data.length;
              this.userResults = res.data.data.map((item) => ({
                userId: item.userId,
                username: item.username,
                image: item.image,
                follewed: item.follewed,
                followCount: item.followCount,
                fansCount: item.fansCount,
                isPlaying: false,
              }));
            }
          })
          .catch(function (error) {});
      }
    },

    // 视频展示
    playVideo(video) {
      if (
        this.$route.path === "/videoplayer" &&
        this.$route.query.id === video.videoId
      ) {
        return; // 已在播放该视频，不重复跳转
      }
      console.log("进入视频全屏播放");
      this.$router.push({
        path: "/videoplayer",
        query: { id: video.videoId, from: "search" },
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
  },
  created() {
    this.token = localStorage.getItem("token");
  },
  mounted() {
    this.userInfo.username = localStorage.getItem("currentUser");
    this.searchText = this.keyword;
    this.lastSearchText = this.keyword;
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
.el-main {
  color: #333;
  overflow-y: auto; /* 允许垂直滚动 */
  padding-bottom: 20px; /* 添加底部内边距 */
  position: relative;
  height: calc(100vh - 80px);
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
.avatar-hover {
  cursor: pointer;
  transition: all 0.3s;
}
.avatar-hover:hover {
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
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

/* 搜索结果展示 */
.search-results-container {
  background-color: rgba(151, 153, 18, 0);
  border-radius: 8px;
  padding: 20px;
  /* min-height: calc(100vh - 140px); */
  min-height: auto;
}

.search-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.search-header h2 {
  margin: 0;
  color: #eaeff1; /* 明亮的科技蓝 */
  text-shadow: 0 0 8px rgba(79, 195, 247, 0.3); /* 微光效 */
}

.search-meta {
  margin-top: 10px;
  font-size: 14px;
  color: #909399;
}

.search-keyword {
  color: #409eff;
  font-weight: bold;
}
/* 视频结果样式 */
.video-results-section {
  margin-top: 20px;
}

/* dasdasdasdadadadas */
/* 修改视频卡片样式 */
.video-card {
  margin-bottom: 24px;
  border-radius: 8px;
  height: 250px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  position: relative; /* 关键：为绝对定位子元素提供参照 */
  transition: all 0.3s;
}

.video-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.2);
}

/* 封面图片样式 */
.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
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
  color: white; /* 确保文字在深色背景上可见 */
}

.creator {
  display: flex;
  align-items: center;
  margin: 8px 0;
}

.creator span {
  margin-left: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9); /* 浅色文字 */
}

.stats {
  display: flex;
  font-size: 12px;
}

.stats span {
  margin-right: 12px;
  color: rgba(255, 255, 255, 0.8);
}

.stats i {
  margin-right: 4px;
}

/* 用户结果样式 */
.user-results-section {
  margin-top: 20px;
}

.user-card {
  margin-bottom: 20px;
  padding: 16px;
  text-align: center;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.user-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.2);
}

.user-info h4 {
  margin: 12px 0 8px;
  font-size: 16px;
}

.user-bio {
  margin: 8px 0;
  font-size: 13px;
  color: #606266;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.user-stats {
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
}

.user-stats span {
  margin: 0 8px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .video-card {
    margin-bottom: 15px;
  }

  .user-bio {
    height: auto;
  }
}
.highlight-number {
  color: #ece7e7; /* 更深的黑色 */
  font-weight: bold;
}
.el-backtop {
  border-radius: 50% !important;
  width: 40px !important;
  height: 40px !important;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.el-backtop:hover {
  background-color: rgba(255, 255, 255, 0.9) !important;
  transform: scale(1.1);
}
</style>