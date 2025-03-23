<script setup>
import { ref, computed } from 'vue';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { message } from 'ant-design-vue';
import fileApi from '../api/file';
import { isImageFile, checkFileSize } from '../utils/file';

// 定义组件属性
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '500px'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  toolbars: {
    type: Array,
    default: () => [
      'bold', 'italic', 'strikethrough', 'title', 'sub', 'sup', 'quote',
      'unorderedList', 'orderedList', 'codeRow', 'code', 'link', 'image',
      'table', 'revoke', 'next', 'save', 'preview'
    ]
  }
});

// 定义事件
const emit = defineEmits(['update:modelValue']);

// 编辑器内容
const content = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// 上传中状态
const uploading = ref(false);

// 自定义图片上传
const onUploadImg = async (files, callback) => {
  if (!files || files.length === 0) {
    return;
  }
  
  // 检查文件类型
  for (const file of files) {
    if (!isImageFile(file)) {
      message.error('只能上传图片文件');
      return;
    }
  }
  
  // 检查文件大小（限制为5MB）
  for (const file of files) {
    if (!checkFileSize(file, 5)) {
      message.error('图片大小不能超过5MB');
      return;
    }
  }
  
  try {
    uploading.value = true;
    
    // 上传所有图片
    const uploadPromises = files.map(file => fileApi.uploadFile(file));
    const results = await Promise.all(uploadPromises);
    
    // 获取上传后的图片URL列表
    const urls = results.map(result => {
      if (result.code === 200 && result.data) {
        return result.data;
      }
      return null;
    }).filter(url => url !== null);
    
    // 回调函数，将图片URL插入编辑器
    callback(urls);
    
    if (urls.length > 0) {
      message.success('图片上传成功');
    } else {
      message.error('图片上传失败');
    }
  } catch (error) {
    console.error('图片上传失败:', error);
    message.error('图片上传失败，请稍后重试');
  } finally {
    uploading.value = false;
  }
};
</script>

<template>
  <div class="md-editor-with-upload">
    <MdEditor
      v-model="content"
      :height="height"
      :toolbars="toolbars"
      :placeholder="placeholder"
      @onUploadImg="onUploadImg"
    />
  </div>
</template>

<style scoped>
.md-editor-with-upload {
  width: 100%;
}

/* 确保Markdown编辑器在表单中正确显示 */
:deep(.md-editor) {
  margin-bottom: 16px;
}

:deep(.md-editor-preview-wrapper) {
  padding: 16px;
}
</style>