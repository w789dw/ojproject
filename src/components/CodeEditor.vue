<template>
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="min-height: 400px; height: 70vh"
  />
  <!--  <a-button @click="fillValue">填充值</a-button>-->
</template>

<script setup lang="ts">
import * as monaco from "monaco-editor";
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from "vue";

const codeEditorRef = ref();
const codeEditor = ref();

interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java",
  handleChange: (v: string) => {
    console.log(v);
  },
});

watch(
  () => props.language,
  () => {
    codeEditor.value = monaco.editor.create(codeEditorRef.value, {
      value: props.value,
      automaticLayout: true,
      language: props.language,
      minimap: {
        enabled: false,
      },
      readOnly: false,
      theme: "vs-dark",
      colorDecorators: true,
      // lineNumbers: "off",
      // roundedSelection: false,
      // scrollBeyondLastLine: false,
    });
  }
);

// const fillValue = () => {
//   if (!codeEditor.value) {
//     return;
//   }
//   //改变值
//   toRaw(codeEditor.value).setValue("新的值");
// };

onMounted(() => {
  if (!codeEditorRef.value) {
    return;
  }
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    automaticLayout: true,
    language: props.language,
    minimap: {
      enabled: false,
    },
    readOnly: false,
    theme: "vs-dark",
    colorDecorators: true,
    // lineNumbers: "off",
    // roundedSelection: false,
    // scrollBeyondLastLine: false,
  });
  //编辑 监听内容变化
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});
</script>
<style scoped></style>
