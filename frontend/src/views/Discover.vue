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

        <!-- 视频推荐功能 -->
        <el-main class="video-main-container">
          <div
            class="video-scroll-container"
            v-infinite-scroll="loadMore"
            :infinite-scroll-disabled="disabled"
            :infinite-scroll-distance="20"
          >
            <div
              v-for="(video, index) in videoList"
              :key="video.videoId + index"
              class="video-card"
              overflow:auto
            >
              <video
                class="video-player"
                :src="video.videoUrl"
                controls
                preload="metadata"
                @play="handleVideoPlay(video, $event)"
              ></video>

              <div class="video-info">
                <h4 class="video-title">{{ video.videoTitle }}</h4>
                <div class="video-actions">
                  <!-- 关注按钮 -->
                  <el-tooltip
                    :content="video.followed ? '已关注' : '关注作者'"
                    placement="top"
                  >
                    <el-button
                      circle
                      size="mini"
                      :icon="
                        video.followed ? 'el-icon-user-solid' : 'el-icon-user'
                      "
                      :type="video.followed ? 'primary' : 'default'"
                      @click="toggleFollow(video)"
                    ></el-button>
                  </el-tooltip>

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
                      <span class="action-count">{{ video.likeCount }}</span>
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
                      <span class="action-count">{{ video.collectCount }}</span>
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

                  <!-- 评论按钮 -->
                  <el-tooltip content="评论" placement="top">
                    <el-button
                      circle
                      size="mini"
                      icon="el-icon-chat-dot-round"
                      @click="toggleComment(video)"
                    >
                      <span class="action-count">{{
                        video.commentCount || 0
                      }}</span>
                    </el-button>
                  </el-tooltip>
                </div>
                <h4 class="video-title">
                  作者：{{ video.authorUsername }} 类别：{{
                    video.category
                  }}
                  上架时间：{{ video.updateTime }}
                </h4>
              </div>
            </div>

            <!-- 加载状态 -->
            <div class="loading-status">
              <div v-if="loading" class="custom-loading"></div>
              <span v-if="noMore" class="no-more">已经到底啦~</span>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
    <video-comments
      :videoId="currentVideoId"
      :show-child-dialog.sync="commentDrawerVisible"
    />
    <el-backtop target=".video-scroll-container" :bottom="100">
      <div
        style="
           {
            height: 100%;
            width: 100%;
            border-radius: 50%;
            background-color: #f2f5f6;
            box-shadow: 0 0 6px rgba(0, 0, 0, 0.12);
            text-align: center;
            line-height: 40px;
            color: #1989fa;
          }
        "
      >
        UP
      </div>
    </el-backtop>
  </div>
</template>

<script>
import axios from "axios";
import UserAside from "@/components/UserAside.vue";
import VideoComments from "@/components/VideoComments.vue";

export default {
  components: { UserAside, VideoComments },
  name: "Discover",
  data() {
    return {
      keyword: "",
      searchText: "", // 添加搜索文本数据
      lastLoadTime: 0, //上次调用时间
      loadMoreLock: false, //状态锁
      videoList: [], // 当前加载的视频数据
      page: 1, // 当前页码
      pageSize: 5, // 每页数据量
      loading: false, // 是否正在加载
      noMore: false, // 是否无更多数据

      activeIndexaside: "2",
      isCollapse: false,
      userInfo: {
        username: "",
      },
      commentDrawerVisible: false,
      currentVideoId: 0,
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
    loadMore() {
      const now = Date.now();
      if (now - this.lastLoadTime < 2000 || this.loadMoreLock) {
        return;
      }

      this.loadMoreLock = true;
      this.lastLoadTime = now;

      if (this.loading || this.noMore) return;
      this.loading = true;
      try {
        axios
          .get("http://localhost:8080/recommend", {
            params: {
              page: this.page,
              pageSize: this.pageSize,
              username: localStorage.getItem("currentUser"),
            },
            headers: {
              token: this.token,
            },
          })
          .then((res) => {
            if (res.data.code == 1) {
              if (res.data.data.rows.length === 0) {
                this.noMore = true;
              } else {
                this.videoList = [...this.videoList, ...res.data.data.rows];
                this.videoList.forEach((element) => {
                  element.updateTime = element.updateTime.replace("T", " ");
                });
                this.page++;
                // 预加载下一批视频的元数据
                this.preloadVideos(res.data.data);
              }
            }
          })
          .catch(function (error) {});
      } finally {
        this.loading = false;
        this.loadMoreLock = false;
      }
    },
    // 预加载视频元数据（不加载视频内容）
    preloadVideos(videos) {
      videos.forEach((video) => {
        const tempVideo = document.createElement("video");
        tempVideo.src = video.videoUrl;
        tempVideo.preload = "metadata";
      });
    },
    //如果已经关注就取消，如果未关注就关注
    toggleFollow(video) {
      const params = new URLSearchParams();
      params.append("authorUsername", video.authorUsername);
      params.append("username", localStorage.getItem("currentUser"));
      const newStatus = !video.followed;

      axios
        .post("http://localhost:8080/follow", params, {
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            // 更新所有该作者的视频
            this.videoList.forEach((item) => {
              if (item.authorUsername === video.authorUsername) {
                item.followed = newStatus;
              }
            });
            this.$message.success(newStatus ? "关注成功" : "已取消关注");
          }
        });
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
    toggleComment(video) {
      if (!video || !video.videoId) {
        this.$message.error("无法获取视频信息");
        return;
      }
      this.currentVideoId = Number(video.videoId);
      this.commentDrawerVisible = true;
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
    handleVideoPlay(video, event) {
      console.log("开始播放视频ID:", video.videoId);
      if (video.hasRecorded) {
        console.log("当前视频已经记录过播放次数");
        return;
      }
      if (!video || !video.videoId) return;

      // 清除之前的定时器
      if (video.playTimer) {
        clearTimeout(video.playTimer);
        video.playTimer = null;
      }

      // 设置3秒定时器
      video.playTimer = setTimeout(() => {
        // 检查视频是否仍在播放
        const videoElement = event.target;
        if (!videoElement.paused) {
          this.recordView(video);
        }
      }, 3000);

      // 监听暂停和结束事件
      const videoElement = event.target;
      const clearPlayTimer = () => {
        if (video.playTimer) {
          clearTimeout(video.playTimer);
          video.playTimer = null;
        }
        videoElement.removeEventListener("pause", clearPlayTimer);
        videoElement.removeEventListener("ended", clearPlayTimer);
      };

      videoElement.addEventListener("pause", clearPlayTimer);
      videoElement.addEventListener("ended", clearPlayTimer);
    },
    recordView(video) {
      const params = new URLSearchParams();
      params.append("videoId", video.videoId);
      params.append("username", localStorage.getItem("currentUser"));
      axios
        .post("http://localhost:8080/view", params, {
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code == 1) {
            console.log("记录当前观看次数成功");
            video.hasRecorded = true;
          } else {
            console.log("记录当前观看次数失败");
          }
        });
    },
  },
  computed: {
    disabled() {
      return this.loading || this.noMore;
    },
  },
  created() {
    this.token = localStorage.getItem("token");
  },
  mounted() {
    this.userInfo.username = localStorage.getItem("currentUser");
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

/* 视频播放接口 */
.video-main-container {
  padding: 0;
  background: #f5f5f5;
}

.video-scroll-container {
  height: calc(100vh - 60px); /*减去顶部导航栏高度 */
  overflow-y: auto;
  padding: 0px;
}

.video-card {
  margin-bottom: 16px;
  min-height: 300px;
  background: #fff;
  border-radius: 0px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.video-player {
  width: 100%;
  max-height: 90vh;
  background: #000;
  cursor: pointer;
}

.video-info {
  padding: 12px;
}

.video-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.loading-status {
  text-align: center;
  padding: 20px 0;
  color: #999;
}

.no-more {
  display: inline-block;
  padding: 8px 16px;
  background: #f0f0f0;
  border-radius: 16px;
}

.is-loading {
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 新增自定义加载动画 */
.custom-loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #409eff;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.video-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-count {
  margin-left: 4px;
  font-size: 12px;
  color: inherit;
}

/* 按钮激活状态动画 */
.el-button.is-active {
  transform: scale(1.1);
  transition: all 0.3s;
}

/* 点赞按钮特殊样式 */
.el-button[icon="el-icon-star-on"] {
  color: #f56c6c;
}

/* 收藏按钮特殊样式 */
.el-button[icon="el-icon-folder-checked"] {
  color: #e6a23c;
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

.el-backtop {
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(5px);
  transition: all 0.3s;
}

.el-backtop:hover {
  background-color: rgba(255, 255, 255, 0.9);
  transform: scale(1.1);
}
</style>