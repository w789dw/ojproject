<template>
  <div id="addQuestionView">
    <h2>创建题目</h2>
    <a-form :model="form" label-align="left">
      <a-form-item field="title" label="标题">
        <a-input v-model="form.title" placeholder="请输入标题" />
      </a-form-item>
      <a-form-item field="tags" label="标签">
        <a-input-tag v-model="form.tags" placeholder="请选择标签" allow-clear />
      </a-form-item>

      <a-form-item field="content" label="题目内容">
        <MdEditor
          v-bind:value="form.content"
          v-bind:handle-change="onContentChange"
        />
      </a-form-item>
      <a-form-item field="answer" label="答案">
        <MdEditor
          v-bind:value="form.answer"
          v-bind:handle-change="onAnswerChange"
        />
      </a-form-item>

      <a-form-item label="判题配置" :content-flex="false" :merge-props="false">
        <a-space direction="vertical" style="min-width: 480px">
          <a-form-item field="judgeConfig.memoryLimit" label="内存限制">
            <a-input-number
              placeholder="请输入内存限制"
              v-model="form.judgeConfig.memoryLimit"
              mode="button"
              min="0"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.stackLimit" label="堆栈限制">
            <a-input-number
              placeholder="请输入堆栈限制"
              v-model="form.judgeConfig.stackLimit"
              mode="button"
              min="0"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.timeLimit" label="时间限制">
            <a-input-number
              placeholder="请输入时间限制"
              v-model="form.judgeConfig.timeLimit"
              mode="button"
              min="0"
              size="large"
            />
          </a-form-item>
        </a-space>
      </a-form-item>

      <a-form-item
        label="测试用例配置"
        :content-flex="false"
        :merge-props="false"
      >
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
          no-style
        >
          <a-space direction="vertical" style="min-width: 680px">
            <a-form-item
              :field="`form.judgeCaseItem[${index}].input`"
              :label="`输入用例-${index}`"
              :key="index"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="请输入测试输入用例"
              />
            </a-form-item>
            <a-form-item
              :field="`form.judgeCaseItem[${index}].output`"
              :label="`输出用例-${index}`"
              :key="index"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="请输入测试输出用例"
              />
            </a-form-item>
            <a-button @click="handleDelete(index)" status="danger"
              >删除
            </a-button>
          </a-space>
        </a-form-item>
        <div style="margin-top: 32px">
          <a-button @click="handleAdd" type="outline" status="success"
            >新增测试用例
          </a-button>
        </div>
      </a-form-item>

      <a-form-item style="margin-top: 16px">
        <a-button type="primary" style="min-width: 200px" @click="doSubmit"
          >提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import ExampleView from "@/views/ExampleView.vue";
import {
  Question,
  QuestionAddRequest,
  QuestionControllerService,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";
import * as stream from "stream";

const route = useRoute();
const updatePage = route.path.includes("update");

const form = ref({
  title: "",
  tags: [],
  answer: "",
  content: "",
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
});

/**
 * 根据题目id获取老的数据
 */
const localData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (res.code === 0) {
    form.value = res.data as any;
    if (!form.value.judgeCase) {
      form.value.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      //json转js对象
      form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
    }
    if (!form.value.judgeConfig) {
      form.value.judgeConfig = {
        memoryLimit: 1000,
        stackLimit: 1000,
        timeLimit: 1000,
      };
    } else {
      form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
    }
    if (!form.value.tags) {
      form.value.tags = [];
    } else {
      form.value.tags = JSON.parse(form.value.tags as any);
    }
  } else {
    message.error("加载失败" + res.message);
  }
};

onMounted(() => {
  localData();
});

const doSubmit = async () => {
  console.log(form.value);
  //区分更新还是创建
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("更新成功");
    } else {
      message.error("更新失败" + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("创建成功");
    } else {
      message.error("创建失败" + res.message);
    }
  }
};

const onContentChange = (value: string) => {
  form.value.content = value;
};

const onAnswerChange = (value: string) => {
  form.value.answer = value;
};

/**
 * 新增测试用例
 */
const handleAdd = () => {
  if (form.value.judgeCase) {
    form.value.judgeCase.push({
      input: "",
      output: "",
    });
  }
};

/**
 * 删除测试用例
 * @param index
 */
const handleDelete = (index: number) => {
  if (form.value.judgeCase) {
    form.value.judgeCase.splice(index, 1);
  }
};
</script>

<style scoped>
#addQuestionView {
}

.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display: none !important;
}
</style>
