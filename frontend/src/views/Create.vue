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
          <div class="upload-container">
            <!-- 视频上传区域 -->
            <div class="upload-section">
              <h3 class="upload-title">上传视频</h3>
              <el-upload
                class="upload-area"
                drag
                :http-request="handleVideoUpload"
                :show-file-list="false"
                :before-upload="beforeVideoUpload"
                accept="video/*"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                  <template v-if="!videoUrl">
                    将视频拖到此处，或<em>点击上传</em>
                  </template>
                  <template v-else>
                    视频已选择，可<em>点击重新上传</em>
                  </template>
                </div>
                <div class="el-upload__tip" slot="tip">
                  支持MP4/WebM格式，大小不超过100MB
                </div>
                <!-- 视频预览容器 -->
                <div class="video-preview-container" v-if="videoUrl">
                  <video
                    :src="videoUrl"
                    class="video-preview"
                    controls
                    autoplay
                    muted
                  ></video>
                </div>
              </el-upload>
            </div>

            <!-- 封面上传区域 -->
            <div class="upload-section">
              <h3 class="upload-title">上传封面</h3>
              <el-upload
                class="upload-area"
                drag
                :http-request="handleCoverUpload"
                :show-file-list="false"
                :before-upload="beforeCoverUpload"
                accept="image/jpeg,image/png"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                  <template v-if="!videoForm.cover">
                    将封面拖到此处，或<em>点击上传</em>
                  </template>
                  <template v-else>
                    封面已选择，可<em>点击重新上传</em>
                  </template>
                </div>
                <div class="el-upload__tip" slot="tip">
                  只能上传JPG/PNG文件，且不超过500KB
                </div>
                <!-- 封面预览容器 -->
                <div class="cover-preview-container" v-if="videoForm.cover">
                  <img :src="videoForm.cover" class="cover-preview" />
                </div>
              </el-upload>
            </div>

            <!-- 视频信息表单 -->
            <el-form label-width="80px" class="video-form">
              <el-form-item label="视频标题" required>
                <el-input
                  v-model="videoForm.title"
                  placeholder="请输入视频标题"
                  maxlength="50"
                  show-word-limit
                ></el-input>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  @click="submit"
                  :loading="submitting"
                  :disabled="!isFormValid"
                >
                  发布视频
                </el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
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
  name: "Create",
  data() {
    return {
      keyword: "",
      searchText: "", // 添加搜索文本数据
      activeIndexaside: "6",
      isCollapse: false,
      userInfo: {
        username: "",
      },
      videoUrl: "", // 视频预览URL
      CoverUrl: "", //封面预览URL
      videoForm: {
        title: "",
        cover: "",
        video: "",
      },
      submitting: false,
    };
  },
  computed: {
    //按钮绑定，如果没有上传对应文件和标题，按钮禁用
    isFormValid() {
      return this.videoForm.title && this.videoUrl && this.videoForm.cover;
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
    beforeVideoUpload(file) {
      const isVideo = ["video/mp4", "video/webm"].includes(file.type);
      const isLt100M = file.size / 1024 / 1024 < 100;

      if (!isVideo) {
        this.$message.error("请上传MP4或WebM格式视频!");
        return false;
      }
      if (!isLt100M) {
        this.$message.error("视频大小不能超过100MB!");
        return false;
      }
      return true;
    },

    beforeCoverUpload(file) {
      const isImage = ["image/jpeg", "image/png"].includes(file.type);
      const isLt500K = file.size / 1024 < 500;

      if (!isImage) {
        this.$message.error("只能上传JPG/PNG格式图片!");
        return false;
      }
      if (!isLt500K) {
        this.$message.error("封面图片不能超过500KB!");
        return false;
      }
      return true;
    },

    handleVideoUpload(file) {
      this.videoUrl = URL.createObjectURL(file.file);
      const formData = new FormData();
      formData.append("video", file.file);
      const token = localStorage.getItem("token");

      axios
        .post("http://localhost:8080/uploadvideo", formData, {
          headers: {
            token: token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            // 存储后端返回的URL
            this.videoForm.video = res.data.data;
            // 显示成功消息
            this.$message.success(res.data.message || "视频上传成功");
          } else {
            this.$message.error("视频上传失败");
          }
        })
        .catch((error) => {
          this.$message.error("视频上传失败");
          throw error;
        });
    },

    handleCoverUpload(file) {
      this.CoverUrl = URL.createObjectURL(file.file);
      const formData = new FormData();
      formData.append("cover", file.file);
      const token = localStorage.getItem("token");

      axios
        .post("http://localhost:8080/uploadcover", formData, {
          headers: {
            token: token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.videoForm.cover = res.data.data;
            // 显示成功消息
            this.$message.success(res.data.message || "封面上传成功");
          } else {
            this.$message.error("封面上传失败");
          }
        })
        .catch((error) => {
          this.$message.error("封面上传失败");
          throw error;
        });
    },

    submit() {
      // 表单验证
      if (!this.videoForm.title) {
        return this.$message.error("请输入视频标题");
      }
      if (!this.videoForm.video) {
        return this.$message.error("请上传视频文件");
      }
      this.submitting = true;

      const token = localStorage.getItem("token");
      const params = new URLSearchParams();
      params.append("videoUrl", this.videoForm.video); // 对应后端 @RequestParam String videoUrl
      params.append("coverUrl", this.videoForm.cover); // 对应后端 @RequestParam String coverUrl
      params.append("videoTitle", this.videoForm.title); // 对应后端 @RequestParam String videoTitle
      params.append("username", this.userInfo.username); // 对应后端 @RequestParam String username
      axios
        .post("http://localhost:8080/createvideo", params, {
          headers: {
            token: token,
          },
        })
        .then((response) => {
          if (response.data.code === 1) {
            this.$message.success("发布成功");
            this.resetForm();
          } else {
            throw new Error(response.data.message || "发布失败");
          }
        })
        .catch((error) => {
          this.$message.error(error.message);
        })
        .finally(() => {
          this.submitting = false;
        });
    },

    resetForm() {
      this.videoUrl = "";
      this.videoForm = {
        title: "",
        cover: "",
        video: "",
      };
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
.upload-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.video-form {
  margin-top: 30px;
}

/* 视频上传区域 */
.upload-area {
  position: relative;
  width: 100%;
  min-height: 200px;
}

/* 视频预览容器 */
.video-preview-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

/* 视频预览元素 */
.video-preview {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  background-color: #000;
}

/* 封面预览容器 */
.cover-preview-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

/* 封面预览元素 */
.cover-preview {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* 上传区域文本（当有视频时降低透明度） */
.upload-area .el-upload-dragger {
  position: relative;
  z-index: 2;
}

.upload-area .el-upload-dragger.has-video {
  background-color: transparent;
}

.upload-area .el-upload-dragger.has-video .el-icon-upload,
.upload-area .el-upload-dragger.has-video .el-upload__text {
  opacity: 0.7;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
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
.search-container {
  display: flex;
  align-items: center;
  margin: 0 auto;
  width: 400px; /* 可以根据需要调整宽度 */
}

.search-input {
  width: 100%;
}
</style>