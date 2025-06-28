<template>
  <!-- width的宽度跟collapse一样动态控制 -->
  <el-aside width="collapse">
    <el-menu
      :default-active="activeIndexaside"
      :collapse="isCollapse"
      class="el-menu-vertical"
      background-color="#393e42"
      text-color="#fff"
      active-text-color="rgb(64, 158, 255)"
    >
      <el-menu-item index="1" @click="gotoHome">
        <i class="el-icon-house"></i>
        <span slot="title">首页</span>
      </el-menu-item>
      <el-menu-item index="2" @click="gotoDiscover">
        <i class="el-icon-discover"></i>
        <span slot="title">推荐</span>
      </el-menu-item>
      <el-menu-item index="3" @click="gotoSelect">
        <i class="el-icon-sunny"></i>
        <span slot="title">精选</span>
      </el-menu-item>
      <el-menu-item index="4" @click="gotoMine">
        <i class="el-icon-s-custom"></i>
        <span slot="title">我的</span>
      </el-menu-item>
      <el-menu-item index="5" @click="gotoRandom">
        <i class="el-icon-magic-stick"></i>
        <span slot="title">随心刷</span>
      </el-menu-item>
      <el-menu-item index="6" @click="gotoCreate">
        <i class="el-icon-edit"></i>
        <span slot="title">创作</span>
      </el-menu-item>
      <el-menu-item index="7" @click="gotoSearch">
        <i class="el-icon-search"></i>
        <span slot="title">搜索</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script>
export default {
  name: "UserAside",
  props: {
    isCollapse: {
      type: Boolean,
      default: false,
    },
    activeIndexaside:{
        type: String,
    }
  },
  methods: {
    gotoHome() {
      if (this.$route.path !== "/home") {
        this.$router.push("/home");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoDiscover() {
      if (this.$route.path !== "/discover") {
        this.$router.push("/discover");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoSelect() {
      if (this.$route.path !== "/select") {
        this.$router.push("/select");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoMine() {
      if (this.$route.path !== "/mine") {
        this.$router.push("/mine");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoRandom() {
      if (this.$route.path !== "/random") {
        this.$router.push("/random");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoCreate() {
      if (this.$route.path !== "/create") {
        this.$router.push("/create");
      } else {
        this.$message.info("已在当前页面");
      }
    },
    gotoSearch() {
      if (this.$route.path !== "/search") {
        this.$router.push("/search");
      } else {
        this.$message.info("已在当前页面");
      }
    },
  },
};
</script>

<style scoped>
.el-aside {
  background: rgba(18, 9, 9, 0.3);
  color: #333;
  height: 100vh;
}
.el-menu {
  border-right: none; /* 移除边框 */
  background-color: rgba(57, 62, 66, 0.3) !important; /* 更透明 */
}

.el-menu-item {
  background-color: transparent !important; /* 菜单项透明 */
}

.el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important; /* 悬停效果 */
}

.el-menu--collapse {
  background-color: rgba(57, 62, 66, 0.3) !important; /* 折叠状态也透明 */
}
.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}
</style>