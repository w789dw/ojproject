<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<style></style>
<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

/**
 * 全局初始化函数，有全局单次的调用的代码
 */
const doInit = () => {
  console.log("hello,welcome to my world");
};
onMounted(() => {
  doInit();
});
</script>
