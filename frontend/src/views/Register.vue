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
    <div class="login-wrap">
      <el-form class="login-container">
        <h1 class="title">用户注册</h1>
        <el-form-item label="">
          <el-input
            type="text"
            v-model="username"
            placeholder="注册账号"
            autocomplete="username"
          ></el-input>
        </el-form-item>
        <el-form-item label="">
          <el-input
            type="text"
            v-model="name"
            placeholder="注册姓名"
            autocomplete="off"
            name="unimportant-name" 
          ></el-input>
        </el-form-item>
        <el-form-item label="">
          <el-input
            type="password"
            show-password
            v-model="password"
            placeholder="注册密码"
            autocomplete="new-password"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="doSubmit()"
            >提交</el-button
          >
        </el-form-item>
        <el-row style="text-align: center; margin-top: -10px">
          <!-- <el-link type="primary">忘记密码</el-link> -->
          <el-link type="primary" @click="gotoLogin()">用户登录</el-link>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
 
<script>
import axios from "axios";
export default {
  name: "Register",
  data: function () {
    return {
      username: "",
      name: "",
      password: "",
    };
  },
  methods: {
    doSubmit: function () {
      axios
        .post(
          "http://localhost:8080/register",
          {
            username: this.username,
            name: this.name,
            password: this.password,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
          }
        )
        .then((res) => {
          console.log(res);
          if (res.data.code == 1) {
            //注册成功
            this.$message.success(res.data.msg);
            this.$router.push("/login"); // 跳转登录页面
          } else if (res.data.code == 2) {
            //该用户已经注册
            this.$message.error(res.data.msg);
          } else {
            //注册失败
            this.$message.error(res.data.msg);
          }
        })
        .catch(function (error) {});
    },
    gotoLogin: function () {
      this.$router.push("/login");
    },
  },
  mounted() {
    this.username = "";
    this.name = "";
    this.password = "";
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
.login-wrap {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  padding-top: 10%;

  /* background-color: #112346; */
  background-repeat: no-repeat;
  background-position: center right;
  background-size: 100%;
}

.login-container {
  border-radius: 10px;
  margin: 0px auto;
  width: 350px;
  padding: 30px 35px 15px 35px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid #eaeaea;
  text-align: left;
  box-shadow: 0 0 20px 2px rgba(0, 0, 0, 0.1);
}

.title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
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
</style>