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
          <!-- 轮播卡片 -->
          <div class="carousel-wrapper">
            <el-carousel
              :interval="4000"
              indicator-position="outside"
              height="255px"
              type="card"
            >
              <el-carousel-item v-for="item in 4" :key="item">
                <img
                  :src="require(`@/assets/home_carousel/${item}.png`)"
                  class="carousel-image"
                />
              </el-carousel-item>
            </el-carousel>
          </div>
          <div class="board-container" style="margin-top: 20px">
            <!-- 留言板 -->
            <div class="message-board">
              <h3 class="board-title">留言板</h3>
              <div class="board-content">
                <el-input
                  type="textarea"
                  :rows="3"
                  placeholder="留下您的宝贵意见..."
                  v-model="messageContent"
                  class="message-input"
                ></el-input>
                <el-button
                  type="primary"
                  @click="submitMessage"
                  class="submit-btn"
                  :loading="submitting"
                  >提交留言</el-button
                >

                <!-- 留言列表 -->
                <div class="message-list">
                  <div v-if="loadingMessages" class="loading-messages">
                    <i class="el-icon-loading"></i> 加载中...
                  </div>
                  <div v-else-if="messages.length === 0" class="no-messages">
                    暂无留言
                  </div>
                  <div
                    v-else
                    v-for="(message, index) in messages"
                    :key="index"
                    class="message-item"
                  >
                    <el-avatar
                      :size="40"
                      :src="message.avatar"
                      class="message-avatar"
                    ></el-avatar>
                    <div class="message-content">
                      <div class="message-user">{{ message.username }}</div>
                      <div class="message-text">{{ message.content }}</div>
                      <div class="message-time">
                        {{ formatTime(message.createTime) }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 公告板 -->
            <div class="notice-board">
              <h3 class="board-title">公告板</h3>
              <div class="board-content">
                <div v-if="loadingNotices" class="loading-notices">
                  <i class="el-icon-loading"></i> 加载中...
                </div>
                <el-scrollbar v-else class="notice-scroll">
                  <ul class="notice-list">
                    <li
                      v-for="(notice, index) in notices"
                      :key="index"
                      class="notice-item"
                    >
                      <div class="notice-title">{{ notice.title }}</div>
                      <div class="notice-content">{{ notice.content }}</div>
                      <div class="notice-time">
                        {{ formatTime(notice.createTime) }}
                      </div>
                    </li>
                  </ul>
                </el-scrollbar>
              </div>
            </div>
          </div>
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
  name: "Home",
  data() {
    return {
      keyword: "",
      searchText: "", // 添加搜索文本数据
      activeIndexaside: "1",
      isCollapse: false,
      userInfo: {
        username: "",
      },
      messageContent: "",
      messages: [],
      notices: [],
      submitting: false,
      loadingMessages: false,
      loadingNotices: false,
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
    //搜索方法
    handleSearch() {
      if (this.searchText.trim()) {
        // 这里可以添加搜索逻辑
        console.log("搜索内容:", this.searchText);
        this.$router.push({
          path: "/search",
          query: { keyword: this.searchText.trim() },
        });
      } else {
        alert("请输入搜索内容");
      }
    },
    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return "";
      const date = new Date(timestamp);
      return date.toLocaleString();
    },

    // 获取留言列表
    fetchMessages() {
      this.loadingMessages = true;
      axios
        .get("http://localhost:8080/getmessage", {
          params: {
            username: localStorage.getItem("currentUser"),
          },
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.messages = res.data.data.map((item) => ({
              avatar: item.image,
              username: item.username,
              content: item.content,
              createTime: item.createTime,
            }));
            console.log("获取留言板数据成功");
          } else {
            console.log("获取留言板数据失败");
          }
        })
        .catch(function (error) {});
      this.loadingMessages = false;
    },

    // 获取公告列表
    fetchNotices() {
      this.loadingNotices = true;
      axios
        .get("http://localhost:8080/getnotice", {
          params: {
            username: localStorage.getItem("currentUser"),
          },
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.notices = res.data.data.map((item) => ({
              title: item.title,
              content: item.content,
              createTime: item.createTime,
            }));
            console.log("获取公告板数据成功");
          } else {
            console.log("获取公告板数据失败");
          }
        })
        .catch(function (error) {});
      this.loadingNotices = false;
    },

    // 提交留言
    async submitMessage() {
      if (!this.messageContent.trim()) {
        this.$message.warning("请输入留言内容");
        return;
      }

      this.submitting = true;
      const params = new URLSearchParams();
      params.append("content", this.messageContent);
      params.append("username", localStorage.getItem("currentUser"));
      axios
        .post("http://localhost:8080/postmessage", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            console.log("留言成功");
            this.fetchMessages();
            this.messageContent = "";
          } else {
            console.log("提交留言失败");
          }
        });
      this.submitting = false;
      this.fetchMessages();
    },
  },
  mounted() {
    this.userInfo.username = localStorage.getItem("currentUser");
    this.fetchMessages();
    this.fetchNotices();
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
.carousel-wrapper {
  width: 80%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.el-carousel--card {
  padding-bottom: 40px;
}

.el-carousel__container {
  height: 400px !important;
}

.carousel-card {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #f5f7fa;
}

.el-carousel__item.is-active {
  transform: scale(1);
  z-index: 2;
}

.el-carousel__indicators {
  bottom: 0;
}

.el-carousel__indicator {
  padding: 12px 4px;
}

.el-carousel__button {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.3);
}

.el-carousel__indicator.is-active .el-carousel__button {
  background-color: #409eff;
  width: 12px;
  border-radius: 6px;
}

@media (max-width: 768px) {
  .carousel-wrapper {
    width: 95%;
  }

  .el-carousel__container {
    height: 300px !important;
  }
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
/* 留言板和公告板容器 */
.board-container {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
  gap: 20px;
}

/* 通用面板样式 */
.message-board,
.notice-board {
  flex: 1;
  background: rgba(255, 255, 255, 0.85);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 左侧留言板 */
.message-board {
  flex: 2;
}

/* 右侧公告板 */
.notice-board {
  flex: 1;
}

/* 面板标题 */
.board-title {
  padding: 15px 20px;
  margin: 0;
  background: #409eff;
  color: white;
  font-size: 16px;
}

/* 面板内容区域 */
.board-content {
  padding: 20px;
}

/* 留言输入区域 */
.message-input {
  margin-bottom: 15px;
}

.submit-btn {
  margin-bottom: 20px;
}

/* 留言列表 */
.message-list {
  max-height: 300px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.message-avatar {
  margin-right: 15px;
}

.message-content {
  flex: 1;
}

.message-user {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.message-text {
  margin-bottom: 5px;
  line-height: 1.5;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

/* 公告板滚动区域 */
.notice-scroll {
  height: 450px;
}

.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-item {
  padding: 15px 0;
  border-bottom: 1px dashed #ebeef5;
}

.notice-title {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.notice-content {
  margin-bottom: 8px;
  line-height: 1.5;
  color: #606266;
}

.notice-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .board-container {
    flex-direction: column;
  }

  .message-board,
  .notice-board {
    width: 100%;
  }
}
</style>