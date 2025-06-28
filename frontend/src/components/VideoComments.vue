<template>
  <el-drawer
    :title="`评论区 (${comments.length})`"
    :visible.sync="showDialog"
    direction="rtl"
    size="25%"
    :before-close="handleClose"
    :modal="false"
    :lock-scroll="false"
    custom-class="comment-drawer"
  >
    <div class="comments-container">
      <!-- 评论输入框 -->
      <div class="comment-input">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          v-model="newComment"
          resize="none"
        ></el-input>
        <el-button
          type="primary"
          size="small"
          @click="submitComment"
          :loading="submitting"
          style="margin-top: 10px"
        >
          发表评论
        </el-button>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div v-if="loading" class="loading-comments">
          <i class="el-icon-loading"></i> 加载中...
        </div>

        <div v-else-if="comments.length === 0" class="no-comments">
          暂无评论，快来抢沙发~
        </div>

        <div
          v-else
          v-for="comment in comments"
          :key="comment.id"
          class="comment-item"
        >
          <el-avatar
            :size="40"
            :src="comment.avatar"
            class="comment-avatar"
          ></el-avatar>
          <div class="comment-content">
            <div class="comment-header">
              <span class="username">{{ comment.username }}</span>
              <span class="time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import axios from "axios";

export default {
  name: "VideoComments",
  props: {
    showChildDialog: {
      type: Boolean,
      default: false,
    },
    videoId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      comments: [],
      newComment: "",
      loading: false,
      submitting: false,
    };
  },
  created() {
    this.fetchComments();
  },
  computed: {
    showDialog: {
      get() {
        return this.showChildDialog;
      },
      set(newValue) {
        this.$emit("update:showChildDialog", newValue);
      },
    },
  },
  methods: {
    fetchComments() {
      this.loading = true;
      axios
        .get("http://localhost:8080/getcomment", {
          params: {
            username: localStorage.getItem("currentUser"),
            videoId: this.videoId,
          },
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            this.comments = res.data.data.map((item) => ({
              id: item.id,
              avatar: item.image,
              username: item.username,
              createTime: item.createTime,
              content: item.content,
            }));
            console.log("获取评论数据成功");
          } else {
            console.log("获取评论数据失败");
          }
        })
        .catch(function (error) {});
      this.loading = false;
    },

    submitComment() {
      if (!this.newComment.trim()) {
        this.$message.warning("请输入评论内容");
        return;
      }
      this.submitting = true;
      const params = new URLSearchParams();
      params.append("username", localStorage.getItem("currentUser"));
      params.append("videoId", this.videoId);
      params.append("content", this.newComment);
      axios
        .post("http://localhost:8080/postcomment", params, {
          headers: {
            token: localStorage.getItem("token"),
          },
        })
        .then((res) => {
          if (res.data.code == 1) {
            console.log("评论成功");
            this.fetchComments();
            this.newComment = "";
          } else {
            console.log("评论失败");
          }
        });
      this.submitting = false;
    },

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

    handleClose(done) {
      this.showDialog = false;
      done();
    },
  },
  watch: {
    showChildDialog(newVal) {
      if (newVal) {
        this.fetchComments();
      }
    },
  },
};
</script>

<style scoped>
.comments-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: rgba(90, 90, 90, 0); 
}

.comment-input {
  margin-bottom: 20px;
}

.comment-list {
  flex: 1;
  overflow-y: auto;
}

.comment-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.comment-avatar {
  margin-right: 15px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.username {
  font-weight: bold;
  color: #409eff;
}

.time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  line-height: 1.5;
  word-break: break-word;
  color: #fcf6f6;
}

.loading-comments,
.no-comments {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}
:deep(.el-drawer) {
  border-top-left-radius: 12px !important;
  border-bottom-left-radius: 12px !important;
  background-color: rgba(0, 0, 0, 0.7);

}

/* 移除默认的顶部边框线 */
:deep(.el-drawer__header) {
  border-bottom: none !important;
}
:deep(.el-drawer__header) {
    color: #00e1ff !important; 
}
</style>