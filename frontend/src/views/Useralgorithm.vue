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
          <el-menu style="background-color: rgba(240, 248, 255, 0.3)">
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

        <el-main style="background-color: rgba(255, 255, 255, 0)">
          <!-- 用户选择器 -->
          <div class="user-selector">
            <el-select
              v-model="selectedUserId"
              placeholder="请选择用户"
              @change="handleUserChange"
              style="width: 300px"
            >
              <el-option label="请选择用户" :value="0"></el-option>
              <el-option
                v-for="user in userList"
                :key="user.id"
                :label="user.username"
                :value="user.id"
              ></el-option>
            </el-select>
          </div>

          <!-- 饼图容器 -->
          <div class="pie-charts-container">
            <!-- 整体偏好饼图 -->
            <div class="pie-chart-card">
              <div
                id="categoryPreferenceChart"
                style="width: 100%; height: 400px"
                v-loading="chartLoading"
              ></div>
            </div>

            <!-- 类型偏好饼图 -->
            <div class="pie-chart-card">
              <div
                id="sentimentPreferenceChart"
                style="width: 100%; height: 400px"
                v-loading="chartLoading"
              ></div>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import * as echarts from "echarts";
import axios from "axios";

export default {
  name: "Useralgorithm",
  data: function () {
    return {
      username: "",
      selectedUserId: null,
      userList: [],
      categoryPreferenceChart: null,
      sentimentPreferenceChart: null,
      chartLoading: false,
      userData: { total: 0, category: [], sentiment: [] },
    };
  },
  mounted() {
    this.username = localStorage.getItem("currentUser");
    this.fetchUserList();
    this.initCharts();
    this.showEmptyChart(
      this.categoryPreferenceChart,
      "内容标签偏好",
      "暂无标签数据"
    );
    this.showEmptyChart(
      this.sentimentPreferenceChart,
      "评论情感分析",
      "暂无评论数据"
    );
  },
  beforeDestroy() {
    // 移除事件监听
    window.removeEventListener("resize", this.resizeCharts);

    // 安全销毁图表
    if (
      this.categoryPreferenceChart &&
      !this.categoryPreferenceChart.isDisposed
    ) {
      this.categoryPreferenceChart.dispose();
    }
    if (
      this.sentimentPreferenceChart &&
      !this.sentimentPreferenceChart.isDisposed
    ) {
      this.sentimentPreferenceChart.dispose();
    }

    // 清空实例引用
    this.categoryPreferenceChart = null;
    this.sentimentPreferenceChart = null;
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

    fetchUserList() {
      axios
        .get("http://localhost:8080/userlistalgorithm", {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.userList = res.data.data.rows;
          }
        })
        .catch((error) => {
          console.error("获取用户算法数据失败:", error);
          this.$message.error("获取用户算法数据失败");
        })
        .finally(() => {});
    },

    fetchUserData(userId) {
      this.chartLoading = true;
      axios
        .get("http://localhost:8080/userdataalgorithm", {
          params: {
            id: userId,
          },
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.userData = res.data.data;
            this.updateCharts(userId);
          }
        })
        .catch((error) => {
          console.error("获取用户算法数据失败:", error);
          this.$message.error("获取用户算法数据失败");
        })
        .finally(() => {
          this.chartLoading = false;
        });
    },

    // 初始化图表
    initCharts() {
      this.categoryPreferenceChart = echarts.init(
        document.getElementById("categoryPreferenceChart")
      );
      this.sentimentPreferenceChart = echarts.init(
        document.getElementById("sentimentPreferenceChart")
      );

      // 窗口大小变化时重新调整图表大小
      window.addEventListener("resize", this.resizeCharts);
    },

    resizeCharts() {
      this.categoryPreferenceChart && this.categoryPreferenceChart.resize();
      this.sentimentPreferenceChart && this.sentimentPreferenceChart.resize();
    },

    // 用户选择变化时触发
    handleUserChange(userId) {
      this.fetchUserData(userId);
    },

    // 更新图表数据
    updateCharts(userId) {
      this.categoryPreferenceChart.clear();
      this.sentimentPreferenceChart.clear();
      // 空数据保护
      const categoryData = this.userData.category || [];
      const sentimentData = this.userData.sentiment || [];

      if (!categoryData.length) {
        this.showEmptyChart(
          this.categoryPreferenceChart,
          "内容标签偏好",
          "暂无标签数据"
        );
      }
      if (!sentimentData.length) {
        this.showEmptyChart(
          this.sentimentPreferenceChart,
          "评论情感分析",
          "暂无评论数据"
        );
      }

      if (categoryData.length) {
        // 更新内容标签偏好饼图
        this.categoryPreferenceChart.setOption({
          title: {
            text: "用户内容标签偏好",
            subtext: `用户ID: ${userId}`,
            left: "center",
            top: "0%",
          },
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b}: {c} ({d}%)",
          },
          legend: {
            orient: "vertical",
            left: "left",
            data: categoryData.map((item) => item.name),
          },
          series: [
            {
              name: "内容标签偏好",
              type: "pie",
              radius: "50%",
              data: categoryData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: "rgba(0, 0, 0, 0.5)",
                },
              },
              label: {
                formatter: "{b}: {d}%",
              },
            },
          ],
        });
      }

      if (sentimentData.length) {
        // 更新评论情感分析偏好饼图
        this.sentimentPreferenceChart.setOption({
          title: {
            text: "用户评论情感分析偏好",
            subtext: `用户ID: ${userId}`,
            left: "center",
            top: "0%",
          },
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b}: {c} ({d}%)",
          },
          legend: {
            orient: "vertical",
            right: "right",
            data: sentimentData.map((item) => item.name),
          },
          series: [
            {
              name: "评论情感偏好",
              type: "pie",
              radius: ["40%", "70%"],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: "#fff",
                borderWidth: 2,
              },
              label: {
                show: false,
                position: "center",
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: "18",
                  fontWeight: "bold",
                },
              },
              labelLine: {
                show: false,
              },
              data: sentimentData,
            },
          ],
        });
      }
    },
    showEmptyChart(chart, title, subtext = "暂无数据") {
       chart.setOption({
        title: {
          text: title,
          subtext: subtext,
          left: "center",
          top: "center",
        },
        series: [],
      });
    },
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
/* 新增样式 */
.user-selector {
  margin: 20px 0;
  text-align: center;
}

.pie-charts-container {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.pie-chart-card {
  width: 48%;
  background-color: rgba(255, 255, 255, 0.5); 
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px;
}
</style>