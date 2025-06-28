<template>
  <el-container style="height: 800px; border: 1px solid #eee">
    <el-header
      style="
        height: 80px;
        display: flex;
        font-size: 40px;
        background-color: rgb(240, 248, 255);
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
      <el-aside width="230px" style="border: 1px solid #eee">
        <el-menu :default-openeds="['1', '2']">
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

      <!-- 查询功能和用户分页查询 -->
      <el-main>
        <!-- 表单 -->
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="姓名"></el-input>
          </el-form-item>

          <el-form-item label="性别">
            <el-select v-model="searchForm.gender" placeholder="性别">
              <el-option label="男" value="1"></el-option>
              <el-option label="女" value="2"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="创建时间">
            <el-date-picker
              v-model="searchForm.createtime"
              type="datetimerange"
              :picker-options="pickerOptions"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              align="right"
            >
            </el-date-picker>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
          </el-form-item>
        </el-form>

        <!-- 查询员工表格 -->
        <el-table
          :data="tableData"
          border
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="id" label="ID" width="100"></el-table-column>
          <el-table-column
            prop="name"
            label="姓名"
            width="150"
          ></el-table-column>

          <el-table-column label="图像" width="150">
            <template slot-scope="scope">
              <img :src="scope.row.image" width="100px" height="100px" />
            </template>
          </el-table-column>

          <el-table-column label="性别" width="140">
            <template slot-scope="scope">
              {{ scope.row.gender == 1 ? "男" : "女" }}
            </template>
          </el-table-column>

          <el-table-column
            prop="createTime"
            label="创建时间"
            width="220"
          ></el-table-column>

          <el-table-column
            prop="updateTime"
            label="最后操作时间"
            width="220"
          ></el-table-column>

          <el-table-column label="操作">
            <template slot-scope="scope">
              <el-button
                type="primary"
                size="mini"
                @click="handleEdit(scope.row)"
                >编辑</el-button
              >
              <el-dialog title="编辑用户信息" :visible.sync="dialogFormVisible">
                <el-form :model="form">
                  <el-form-item label="姓名" :label-width="formLabelWidth">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                  </el-form-item>

                  <el-form-item label="用户头像">
                    <el-upload
                      class="upload-area"
                      action="#"
                      drag
                      :http-request="handleImageUpload"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      accept="image/jpeg,image/png"
                    >
                      <i class="el-icon-upload"></i>
                      <div class="el-upload__text">
                        <template v-if="!ImageUrl">
                          将图像拖到此处，或<em>点击上传</em>
                        </template>
                        <template v-else>
                          图像已选择，可<em>点击重新上传</em>
                        </template>
                      </div>
                      <div class="el-upload__tip" slot="tip">
                        只能上传JPG/PNG文件，且不超过500KB
                      </div>
                      <!-- 图像预览容器 -->
                      <div class="image-preview-container" v-if="ImageUrl">
                        <img :src="ImageUrl" class="image-preview" />
                      </div>
                    </el-upload>
                  </el-form-item>

                  <el-form-item label="性别" :label-width="formLabelWidth">
                    <el-radio v-model="form.gender" label="1">男性</el-radio>
                    <el-radio v-model="form.gender" label="2">女性</el-radio>
                  </el-form-item>
                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="dialogFormVisible = false"
                    >取 消</el-button
                  >
                  <el-button type="primary" @click="editsubmit"
                    >确 定</el-button
                  >
                </div>
              </el-dialog>
              <!-- 涉及用户权限问题，未设置删除功能 -->
              <el-button
                type="danger"
                size="mini"
                @click="handleDelete(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>

        <br />

        <!-- 分页条 -->
        <!-- Pagination 分页 -->
        <el-pagination
          layout="total,sizes, prev, pager, next, jumper"
          :current-page="pagination.currentPage"
          :page-size="pagination.pageSize"
          :page-sizes="[3, 5, 10, 20, 50, 100]"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          v-model="pagination"
        ></el-pagination>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import axios from "axios";

export default {
  name: "User",
  data() {
    return {
      dialogFormVisible: false,
      form: {
        id: "",
        name: "",
        gender: "",
        createtime: [],
        image: "",
      },
      ImageUrl: "",
      formLabelWidth: "120px",
      username: "",
      token: "",
      pagination: {
        currentPage: 1,
        pageSize: 5,
        total: 0,
      },
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      searchForm: {
        name: null,
        gender: null,
        createtime: [],
      },
      tableData: [], //接收后端返回的用户列表数据
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
      localStorage.removeItem("token");
      this.$router.push("/login");
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
    beforeImageUpload(file) {
      const isImage = ["image/jpeg", "image/png"].includes(file.type);
      const isLt500K = file.size / 1024 < 500;

      if (!isImage) {
        this.$message.error("只能上传JPG/PNG格式图片!");
        return false;
      }
      if (!isLt500K) {
        this.$message.error("图片不能超过500KB!");
        return false;
      }
      return true;
    },

    handleImageUpload(file) {
      this.ImageUrl = URL.createObjectURL(file.file);
      const formData = new FormData();
      formData.append("image", file.file);
      const token = localStorage.getItem("token");

      axios
        .post("http://localhost:8080/uploadimage", formData, {
          headers: {
            token: token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.form.image = res.data.data;
            // 显示成功消息
            this.$message.success(res.data.message || "图片上传成功");
          } else {
            this.$message.error("图片上传失败");
          }
        })
        .catch((error) => {
          this.$message.error("图片上传失败");
          throw error;
        });
    },

    handleEdit(row) {
      this.form.id = row.id;
      this.dialogFormVisible = true;
    },
    handleDelete(row) {
      alert(
        "此功能所需权限过高，不建议直接删除用户，若需要强制删除，请前往数据库操作"
      );
    },

    editsubmit() {
      if(this.form.name == '' || this.form.gender == '' || this.form.image == ''){
        alert("请填完所有修改信息");
        return;
      }

      const params = new URLSearchParams();
      params.append("id", this.form.id);
      params.append("name", this.form.name);
      params.append("gender", this.form.gender);
      params.append("image", this.form.image);

      axios
        .post("http://localhost:8080/users/update", params, {
          headers: {
            token: this.token,
          },
        })
        .then((response) => {
          if (response.data.code === 1) {
            this.$message.success("修改成功");
            this.onSubmit();
          } else {
            throw new Error(response.data.message || "修改失败");
          }
        })
        .catch((error) => {
          this.$message.error(error.message);
        });
      this.dialogFormVisible = false;
    },

    onSubmit() {
      if (!this.searchForm.createtime) {
        this.searchForm.createtime = [];
      }
      //分页查询
      axios
        .get("http://localhost:8080/users", {
          params: {
            page: this.pagination.currentPage,
            pageSize: this.pagination.pageSize,
            name: this.searchForm.name,
            gender: this.searchForm.gender,
            begin: this.searchForm.createtime[0],
            end: this.searchForm.createtime[1],
          },
          headers: {
            token: this.token,
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.$message.success("查询成功!");
            this.pagination.total = res.data.data.total;
            this.tableData = res.data.data.rows;
            this.tableData.forEach((element) => {
              element.createTime = element.createTime.replace("T", " ");
              element.updateTime = element.updateTime.replace("T", " ");
            });
          } else {
            this.$message.error("获取数据失败！");
          }
        })
        .catch(function (error) {});
    },

    handleSizeChange(pageSize) {
      this.pagination.pageSize = pageSize;
      this.pagination.currentPage = 1;
      this.onSubmit();
    },
    handleCurrentChange(page) {
      this.pagination.currentPage = page;
      this.onSubmit();
    },
  },
  created() {
    this.token = localStorage.getItem("token");
  },
  mounted() {
    this.onSubmit();
    this.username = localStorage.getItem("currentUser");
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
.el-carousel__item h3 {
  color: #475669;
  font-size: 18px;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持比例并填满容器 */
  display: block;
}

/* 上传区域样式 */
.upload-area {
  position: relative;
  width: 100%;
  min-height: 180px;
}

/* 视频预览容器 */
.image-preview-container {
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
.image-preview {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* 上传区域文本样式（有图片时） */
.upload-area .el-upload-dragger.has-image .el-icon-upload,
.upload-area .el-upload-dragger.has-image .el-upload__text {
  opacity: 0.7;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

/* 上传拖拽区域样式 */
.upload-area >>> .el-upload-dragger {
  position: relative;
  z-index: 2;
  transition: all 0.3s;
}

.upload-area >>> .el-upload-dragger:hover {
  border-color: #409eff;
}
</style>