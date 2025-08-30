<template>
  <Viewer :value="value" :plugins="plugins" />
</template>

<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import hightlight from "@bytemd/plugin-highlight";
import { Editor, Viewer } from "@bytemd/vue-next";
import { ref, withDefaults, defineProps } from "vue";

/**
 * 定义组件属性类型
 */
interface Props {
  value: string;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
});

const plugins = [
  gfm(),
  hightlight(),
  // Add more plugins here
];
</script>
<style>
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display: none !important;
}
</style>
