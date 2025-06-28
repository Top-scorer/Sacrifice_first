<template>
  <div class="video-player-container">
    <!-- 视频播放器 -->
    <video
      ref="videoPlayer"
      :src="currentVideo.videoUrl"
      :poster="currentVideo.coverUrl"
      autoplay
      controls
      playsinline
      loop
      class="fullscreen-video"
      @click="togglePlay"
      @timeupdate="updateTime"
    ></video>

    <!-- 右侧悬浮操作按钮 -->
    <div class="action-buttons">
      <!-- 关注按钮 -->
      <div class="action-button" @click="handleFollow">
        <div class="icon-wrapper">
          <i
            class="el-icon-user"
            :class="{ followed: currentVideo.followed }"
          ></i>
        </div>
        <span class="count">{{
          currentVideo.followed ? "已关注" : "关注"
        }}</span>
      </div>

      <!-- 点赞按钮 -->
      <div class="action-button" @click="handleLike">
        <div class="icon-wrapper">
          <i
            class="el-icon-star-off"
            :class="{ liked: currentVideo.liked }"
          ></i>
        </div>
        <span class="count">{{ currentVideo.likeCount }}</span>
      </div>

      <!-- 收藏按钮 -->
      <div class="action-button" @click="handleCollect">
        <div class="icon-wrapper">
          <i
            class="el-icon-folder-add"
            :class="{ collected: currentVideo.collected }"
          ></i>
        </div>
        <span class="count">{{ currentVideo.collectCount }}</span>
      </div>

      <!-- 评论按钮 -->
      <div class="action-button" @click="handleComment">
        <div class="icon-wrapper">
          <i class="el-icon-chat-dot-round"></i>
        </div>
        <span class="count">{{ currentVideo.commentCount }}</span>
      </div>

      <!-- 分享按钮 -->
      <div class="action-button" @click="handleShare">
        <div class="icon-wrapper">
          <i class="el-icon-share"></i>
        </div>
        <span class="count">分享</span>
      </div>
    </div>

    <!-- 底部视频信息 -->
    <div class="video-info">
      <h2 class="video-title">标题： {{ currentVideo.videoTitle }}</h2>
      <div class="author-info" @click="viewUserProfile">
        <el-avatar :size="40" :src="currentVideo.image"></el-avatar>
        <span class="author-name">{{ currentVideo.authorUsername }}</span>
      </div>
      <div class="video-category">分类： {{ currentVideo.category }}</div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-spinner">
      <i class="el-icon-loading"></i> 加载中...
    </div>

    <!-- 返回按钮 -->
    <div class="back-button" @click="goBack">
      <i class="el-icon-arrow-left"></i>
    </div>
    <video-comments
      :videoId="currentVideoId"
      :show-child-dialog.sync="commentDrawerVisible"
    />
  </div>
</template>

<script>
import axios from "axios";
import VideoComments from "@/components/VideoComments.vue";

export default {
  components: { VideoComments },
  name: "VideoPlayer",
  data() {
    return {
      videoId: "",
      currentVideo: {
        videoId: 0,
        videoUrl: "",
        coverUrl: "",
        videoTitle: "",
        authorUsername: "",
        image: "",
        category: "",
        followed: false,
        liked: false,
        collected: false,
        likeCount: 0,
        collectCount: 0,
        commentCount: 0,
      },
      loading: true,
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      isMuted: false,
      commentDrawerVisible: false,
      currentVideoId: 0,
    };
  },
  created() {
    this.videoId = this.$route.query.id;
    this.fetchVideoData();
  },
  methods: {
    async fetchVideoData() {
      try {
        const res = await axios.get("http://localhost:8080/videoplaying", {
          params: {
            videoId: this.videoId,
            username: localStorage.getItem("currentUser"),
          },
          headers: { token: localStorage.getItem("token") },
        });

        if (res.data.code === 1) {
          this.currentVideo = res.data.data;
          this.loading = false;
        }
      } catch (error) {
        console.error("获取视频失败:", error);
        this.loading = false;
      }
    },

    handleFollow() {
      const params = new URLSearchParams();
      params.append("authorUsername", this.currentVideo.authorUsername);
      params.append("username", localStorage.getItem("currentUser"));

      axios
        .post("http://localhost:8080/follow", params, {
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.currentVideo.followed = !this.currentVideo.followed;
            this.$message.success(
              this.currentVideo.followed ? "关注成功" : "已取消关注"
            );
          }
        });
    },

    handleLike() {
      const params = new URLSearchParams();
      params.append("videoId", this.currentVideo.videoId);
      params.append("username", localStorage.getItem("currentUser"));
      axios
        .post("http://localhost:8080/like", params, {
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.currentVideo.liked = !this.currentVideo.liked;
            this.$message.success(
              this.currentVideo.liked ? "点赞成功" : "已取消喜欢"
            );
            this.currentVideo.likeCount += this.currentVideo.liked ? 1 : -1;
          }
        });
    },

    handleCollect() {
      const params = new URLSearchParams();
      params.append("videoId", this.currentVideo.videoId);
      params.append("username", localStorage.getItem("currentUser"));
      axios
        .post("http://localhost:8080/collect", params, {
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.currentVideo.collected = !this.currentVideo.collected;
            this.$message.success(
              this.currentVideo.collected ? "收藏成功" : "已取消收藏"
            );
            this.currentVideo.collectCount += this.currentVideo.collected
              ? 1
              : -1;
          }
        });
    },
    handleComment() {
      if (!this.currentVideo || !this.currentVideo.videoId) {
        this.$message.error("无法获取视频信息");
        return;
      }
      this.currentVideoId = Number(this.currentVideo.videoId);
      this.commentDrawerVisible = true;
    },
    handleShare() {
      const videoUrl = this.currentVideo.videoUrl;
      const videoTitle = this.currentVideo.videoTitle;
      const category = this.currentVideo.category;
      const authorUsername = this.currentVideo.authorUsername;
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
    },

    togglePlay() {
      const video = this.$refs.videoPlayer;
      if (this.isPlaying) {
        video.pause();
        this.isPlaying = false;
      } else {
        video
          .play()
          .then(() => {
            this.isPlaying = true;
          })
          .catch((error) => {
            console.error("播放失败:", error);
            video.controls = true;
          });
      }
    },
    
    // 用户卡片展示
    viewUserProfile() {
      if (
        this.$route.path === "/userplayer" &&
        this.$route.query.username === this.currentVideo.authorUsername
      ) {
        return; // 已在展示该用户，不重复跳转
      }
      console.log("进入用户界面展示");
      this.$router.push({
        path: "/userplayer",
        query: { username: this.currentVideo.authorUsername, from: "search" },
      });
    },

    updateTime() {
      this.currentTime = this.$refs.videoPlayer.currentTime;
      this.duration = this.$refs.videoPlayer.duration;
    },

    goBack() {
      this.$router.go(-1);
    },
  },
};
</script>

<style scoped>
.video-player-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #edd1d1;
  /* background-color:  #F8F8F8; */
  /* background: linear-gradient(45deg, #F7F3F0, #E8E6E2); */
  z-index: 9999;
}

.fullscreen-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 优化默认控制条样式 */
.fullscreen-video::-webkit-media-controls {
  opacity: 0.3;
  display: flex !important;
}

.fullscreen-video::-webkit-media-controls-panel {
  position: absolute;
  bottom: 0;
  height: 20%; /* 现在正确表示底部20%区域 */
  width: 100%;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.8),
    transparent
  ) !important;
  transition: opacity 0.3s;
}

.fullscreen-video::-webkit-media-controls-play-button,
.fullscreen-video::-webkit-media-controls-volume-slider,
.fullscreen-video::-webkit-media-controls-mute-button,
.fullscreen-video::-webkit-media-controls-timeline,
.fullscreen-video::-webkit-media-controls-current-time-display,
.fullscreen-video::-webkit-media-controls-time-remaining-display,
.fullscreen-video::-webkit-media-controls-fullscreen-button {
  color: white;
  filter: invert(1);
}

/* 右侧悬浮操作按钮 */
.action-buttons {
  position: absolute;
  right: 16px;
  bottom: 150px; /* 调整位置避免遮挡控制条 */
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
}

.action-button {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 5px;
  transition: all 0.3s;
}

.icon-wrapper i {
  font-size: 24px;
  color: white;
}

.icon-wrapper:hover {
  background-color: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.count {
  color: white;
  font-size: 12px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
}

/* 状态样式 */
.followed {
  color: #07c160 !important;
}

.liked {
  color: #ff2442 !important;
}

.collected {
  color: #ffcd00 !important;
}

/* 底部视频信息 */
.video-info {
  position: absolute;
  bottom: 60px;
  left: 20px;
  color: white;
  z-index: 10;
  max-width: 70%;
}

.video-title {
  font-size: 16px;
  margin-bottom: 10px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
}

.author-info {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.author-name {
  margin-left: 10px;
  font-size: 14px;
}

.video-category {
  font-size: 12px;
  opacity: 0.8;
}

.loading-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 18px;
}

.back-button {
  position: absolute;
  top: 20px;
  left: 20px;
  color: white;
  font-size: 24px;
  cursor: pointer;
  z-index: 10;
  background: rgba(0, 0, 0, 0.5);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .action-buttons {
    bottom: 120px;
    right: 10px;
  }

  .icon-wrapper {
    width: 40px;
    height: 40px;
  }

  .icon-wrapper i {
    font-size: 20px;
  }

  .video-info {
    bottom: 40px;
    max-width: 60%;
  }
}
</style>