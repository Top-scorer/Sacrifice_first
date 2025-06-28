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
          <!-- 视频选择器 -->
          <div class="video-selector">
            <el-select
              v-model="selectedVideoId"
              placeholder="请选择视频"
              @change="handleVideoChange"
              style="width: 300px"
            >
              <el-option label="请选择视频" :value="0"></el-option>
              <el-option
                v-for="video in videoList"
                :key="video.id"
                :label="`(${video.id}) ${video.title} (${video.type})`"
                :value="video.id"
              ></el-option>
            </el-select>
          </div>

          <!-- 饼图容器 -->
          <div class="pie-charts-container">
            <!-- 视频评论情感偏向饼图 -->
            <div class="pie-chart-card">
              <div
                id="videoSentimentChart"
                style="width: 100%; height: 400px"
                v-loading="chartLoading"
              ></div>
            </div>

            <!-- 类型视频情感偏向饼图 -->
            <div class="pie-chart-card">
              <div
                id="typeSentimentChart"
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
  name: "VideoAlgorithm",
  data() {
    return {
      username: "",
      selectedVideoId: null,
      videoList: [],
      videoSentimentChart: null,
      typeSentimentChart: null,
      chartLoading: false,
      videoData: {
        total: 0,
        videoSentiment: [], // 当前视频评论情感分布
        typeSentiment: [], // 同类型视频情感分布
      },
    };
  },
  mounted() {
    this.username = localStorage.getItem("currentUser");
    this.fetchVideoList();
    this.initCharts();
    this.showEmptyChart(
      this.videoSentimentChart,
      "视频评论情感偏向",
      "请选择视频"
    );
    this.showEmptyChart(
      this.typeSentimentChart,
      "类型视频情感偏向",
      "请选择视频"
    );
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

    // 初始化图表
    initCharts() {
      this.videoSentimentChart = echarts.init(
        document.getElementById("videoSentimentChart")
      );
      this.typeSentimentChart = echarts.init(
        document.getElementById("typeSentimentChart")
      );
      window.addEventListener("resize", this.resizeCharts);
    },

    // 窗口大小变化时调整图表
    resizeCharts() {
      this.videoSentimentChart && this.videoSentimentChart.resize();
      this.typeSentimentChart && this.typeSentimentChart.resize();
    },

    // 视频选择变化
    handleVideoChange(videoId) {
      this.fetchVideoData(videoId);
    },

    // 获取视频列表
    fetchVideoList() {
      axios
        .get("http://localhost:8080/videolistalgorithm", {
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code === 1) {
            this.videoList = res.data.data.rows;
          }
        })
        .catch((error) => {
          this.$message.error("获取视频列表失败");
          console.error(error);
        });
    },

    // 获取视频数据
    fetchVideoData(videoId) {
      this.chartLoading = true;
      axios
        .get("http://localhost:8080/videodataalgorithm", {
          params: { id: videoId },
          headers: { token: localStorage.getItem("token") },
        })
        .then((res) => {
          if (res.data.code === 1) {
            this.videoData = res.data.data;
            this.updateCharts(videoId);
          }
        })
        .catch((error) => {
          this.$message.error("获取视频算法数据失败:");
          console.error("获取视频算法数据失败:", error);
        })
        .finally(() => {
          this.chartLoading = false;
        });
    },

    // 更新图表
    updateCharts(videoId) {
      // 清空图表
      this.videoSentimentChart.clear();
      this.typeSentimentChart.clear();
      const videoData = this.videoData.videoSentiment;
      const typeData = this.videoData.typeSentiment;

      // 获取当前视频类型
      const currentVideo = this.videoList.find((v) => v.id === videoId);
      const videoType = currentVideo?.type || "未知类型";

      // 视频评论情感偏向饼图（仅修改tooltip）
      this.videoSentimentChart.setOption({
        title: {
          text: "视频评论情感偏向",
          subtext: `视频ID: ${videoId}`,
          left: "center",
        },
        tooltip: {
          trigger: "item",
          formatter: (params) => {
            return `
          ${params.seriesName}<br/>
          ${params.marker}${params.name}: ${params.value} (${
              params.percent
            }%)<br/>
          情感强度: ${(params.data.probability * 100).toFixed(1)}%
        `;
          },
        },
        legend: {
          orient: "vertical",
          left: "left",
          data: videoData.map((item) => item.name),
        },
        series: [
          {
            name: "情感分布",
            type: "pie",
            radius: "50%",
            data: videoData,
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

      // 类型视频情感偏向饼图（仅修改tooltip）
      this.typeSentimentChart.setOption({
        title: {
          text: "同类型视频情感偏向",
          subtext: `视频类型: ${videoType}`,
          left: "center",
        },
        tooltip: {
          trigger: "item",
          formatter: (params) => {
            return `
          ${params.seriesName}<br/>
          ${params.marker}${params.name}: ${params.value} (${
              params.percent
            }%)<br/>
          平均情感强度: ${(params.data.probability * 100).toFixed(1)}%
        `;
          },
        },
        legend: {
          orient: "vertical",
          right: "right",
          data: typeData.map((item) => item.name),
        },
        series: [
          {
            name: "情感分布",
            type: "pie",
            radius: ["40%", "70%"],
            data: typeData,
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
          },
        ],
      });
    },

    // 显示空图表
    showEmptyChart(chart, title, subtext = "暂无数据") {
      if (!chart) return;
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
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeCharts);
    this.videoSentimentChart && this.videoSentimentChart.dispose();
    this.typeSentimentChart && this.typeSentimentChart.dispose();
    // 清空实例引用
    this.videoSentimentChart = null;
    this.typeSentimentChart = null;
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