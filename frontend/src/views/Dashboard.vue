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
      <source src="../assets/background/Wavy Grass.mp4" type="video/mp4" />
    </video>
    <el-container style="height: 800px; border: 1px solid #eee">
      <el-header
        style="
          height: 80px;
          display: flex;
          font-size: 40px;
          background-color: rgb(240, 248, 255, 0.3);
          align-items: center;
        "
      >
        <div class="header-left">
          <span class="system-title">系统管理界面</span>
        </div>
        <div style="margin-left: auto; height: 45px">
          <el-tooltip effect="dark" :content="username" placement="bottom">
            <el-avatar icon="el-icon-user-solid"></el-avatar>
          </el-tooltip>
        </div>
        <div style="margin-left: 10px; height: 70px">
          <el-popconfirm title="确定切换到用户界面吗？" @confirm="gotoHome"
            >//点击确认调用
            <el-button slot="reference">切换界面</el-button>
          </el-popconfirm>
          <el-popconfirm title="确定退出吗？" @confirm="gotoLogin"
            >//点击确认调用
            <el-button slot="reference">退出管理</el-button>
          </el-popconfirm>
        </div>
      </el-header>

      <el-container>
        <el-aside
          width="230px"
          style="border: 1px solid #eee background-color: rgb(240, 248, 255, 0);"
        >
          <el-menu
            :default-openeds="['1', '2', '3', '4']"
            style="background-color: rgba(240, 248, 255, 0.3)"
          >
            <el-submenu index="1">
              <template slot="title"
                ><i class="el-icon-s-platform"></i>系统管理首页</template
              >
              <el-menu-item-group>
                <el-menu-item index="1-1" @click="gotoDashboard"
                  >进入首页</el-menu-item
                >
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="2">
              <template slot="title"
                ><i class="el-icon-menu"></i>系统信息管理</template
              >
              <el-menu-item-group>
                <el-menu-item index="2-1" @click="gotoUserManagement"
                  >用户管理</el-menu-item
                >
                <el-menu-item index="2-2" @click="gotoVideoManagement"
                  >视频管理</el-menu-item
                >
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="3">
              <template slot="title"
                ><i class="el-icon-menu"></i>视频处理</template
              >
              <el-menu-item-group>
                <el-menu-item index="3-1" @click="gotoVideoCheck"
                  >视频审核</el-menu-item
                >
                <el-menu-item index="3-2" @click="gotoVideoRemoval"
                  >私密视频查询</el-menu-item
                >
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="4">
              <template slot="title"
                ><i class="el-icon-menu"></i>算法数据</template
              >
              <el-menu-item-group>
                <el-menu-item index="4-1" @click="gotoUserAlgorithm"
                  >用户数据</el-menu-item
                >
                <el-menu-item index="4-2" @click="gotoVideoAlgorithm"
                  >视频数据</el-menu-item
                >
              </el-menu-item-group>
            </el-submenu>
          </el-menu>
        </el-aside>

        <!-- dashboard首页轮播大屏 -->
        <el-main style="background-color: rgba(255, 255, 255, 0)">
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
                  :src="require(`@/assets/carousel/${item}.png`)"
                  class="carousel-image"
                />
              </el-carousel-item>
            </el-carousel>
          </div>

          <div class="admin-notice-board">
            <div class="board-header">
              <h3 class="board-title">
                <i class="el-icon-s-flag"></i> 系统公告
                <el-button
                  type="primary"
                  size="mini"
                  @click="showNoticeDialog"
                  class="add-notice-btn"
                >
                  发布公告
                </el-button>
              </h3>
            </div>

            <div class="board-content">
              <div v-if="loadingNotices" class="loading-notices">
                <i class="el-icon-loading"></i> 加载中...
              </div>

              <el-table
                v-else
                :data="notices"
                style="width: 100%"
                :row-class-name="tableRowClassName"
              >
                <el-table-column
                  prop="title"
                  label="标题"
                  width="180"
                ></el-table-column>
                <el-table-column prop="content" label="内容"></el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="180">
                  <template slot-scope="scope">
                    {{ formatTime(scope.row.createTime) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template slot-scope="scope">
                    <el-button
                      type="danger"
                      size="mini"
                      @click="deleteNotice(scope.row.id)"
                      icon="el-icon-delete"
                      circle
                    ></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>

          <!-- 发布公告对话框 -->
          <el-dialog
            title="发布新公告"
            :visible.sync="noticeDialogVisible"
            width="50%"
          >
            <el-form :model="newNotice" label-width="80px">
              <el-form-item label="公告标题">
                <el-input
                  v-model="newNotice.title"
                  placeholder="请输入标题"
                ></el-input>
              </el-form-item>
              <el-form-item label="公告内容">
                <el-input
                  type="textarea"
                  :rows="4"
                  v-model="newNotice.content"
                  placeholder="请输入公告内容"
                ></el-input>
              </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
              <el-button @click="noticeDialogVisible = false">取 消</el-button>
              <el-button type="primary" @click="submitNotice">确 定</el-button>
            </span>
          </el-dialog>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Dashboard",
  data: function () {
    return {
      username: "",
      notices: [],
      loadingNotices: false,
      noticeDialogVisible: false,
      newNotice: {
        title: "",
        content: "",
      },
    };
  },
  methods: {
    gotoHome() {
      if (this.$route.path !== "/home") {
        this.$router.push("/home");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoLogin() {
      this.$router.push("/login");
      localStorage.removeItem("currentUser");
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
    },
    gotoDashboard() {
      if (this.$route.path !== "/dashboard") {
        this.$router.push("/dashboard");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoUserManagement() {
      if (this.$route.path !== "/user") {
        this.$router.push("/user");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoVideoManagement() {
      if (this.$route.path !== "/video") {
        this.$router.push("/video");
      } else {
        this.$message.info("已在当前页面");
      }
    },

    gotoVideoCheck() {
      if (this.$route.path !== "/videocheck") {
        this.$router.push("/videocheck");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoVideoRemoval() {
      if (this.$route.path !== "/videoremoval") {
        this.$router.push("/videoremoval");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoUserAlgorithm() {
      if (this.$route.path !== "/useralgorithm") {
        this.$router.push("/useralgorithm");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoVideoAlgorithm() {
      if (this.$route.path !== "/videoalgorithm") {
        this.$router.push("/videoalgorithm");
      } else {
        this.$message.info("已在当前页面");
      }
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
              id: item.id,
              title: item.title,
              content: item.content,
              createTime: item.createTime,
            }));
            console.log("获取公告板数据成功");
          } else {
            console.log("获取公告板数据失败");
          }
        })
        .catch((error) => {
          console.error("获取公告失败:", error);
          this.$message.error("获取公告失败");
        })
        .finally(() => {
          this.loadingNotices = false;
        });
    },

    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return "";
      const date = new Date(timestamp);
      return date
        .toLocaleString("zh-CN", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
          hour12: false,
        })
        .replace(/\//g, "-");
    },

    // 显示发布公告对话框
    showNoticeDialog() {
      this.newNotice = { title: "", content: "" };
      this.noticeDialogVisible = true;
    },

    // 提交新公告
    submitNotice() {
      if (!this.newNotice.title.trim()) {
        this.$message.warning("请输入公告标题");
        return;
      }
      if (!this.newNotice.content.trim()) {
        this.$message.warning("请输入公告内容");
        return;
      }
      const params = new URLSearchParams();
      params.append("title", this.newNotice.title);
      params.append("content", this.newNotice.content);
      axios
        .post("http://localhost:8080/postnotice", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            console.log("发布公告成功");
            this.noticeDialogVisible = false;
            this.fetchNotices();
          } else {
            console.log("发布公告失败");
          }
        })
        .catch((error) => {
          console.error("发布公告失败:", error);
          this.$message.error("发布公告失败");
        });
    },

    // 删除公告
    deleteNotice(id) {
      this.$confirm("确定删除该公告吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          axios
            .get("http://localhost:8080/deletenotice", {
              params: {
                id: id,
              },
              headers: {
                token: localStorage.getItem("token"),
              },
            })
            .then((res) => {
              if (res.data.code == 1) {
                console.log("公告删除成功");
                this.fetchNotices();
              } else {
                console.log("公告删除失败");
              }
            })
            .catch((error) => {
              console.error("删除公告失败:", error);
              this.$message.error("删除公告失败");
            });
        })
        .catch(() => {});
    },

    // 表格行样式
    tableRowClassName({ rowIndex }) {
      return rowIndex % 2 === 0 ? "even-row" : "odd-row";
    },
  },
  mounted() {
    this.username = localStorage.getItem("currentUser");
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
#building {
  background: url("../assets/background/Wavy Grass.mp4");
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
.admin-notice-board {
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  padding: 20px;
  max-width: 1200px;
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

.board-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
}

.board-title i {
  margin-right: 10px;
  color: #409eff;
}

.add-notice-btn {
  margin-left: 15px;
}

/* 表格样式 */
.el-table {
  background-color: transparent;
}

.el-table::before {
  height: 0;
}

.el-table /deep/ th {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

.el-table /deep/ .even-row {
  background-color: rgba(255, 255, 255, 0.7);
}

.el-table /deep/ .odd-row {
  background-color: rgba(240, 248, 255, 0.7);
}

/* 加载状态 */
.loading-notices {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

/* 对话框样式 */
.notice-dialog .el-dialog__header {
  border-bottom: 1px solid #ebeef5;
}

.notice-dialog .el-dialog__body {
  padding: 20px;
}
</style>