import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/ExampleView.vue";
import AdminView from "@/views/AdminView.vue";
import NoAuth from "@/views/NoAuth.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import userLayout from "@/layouts/userLayout.vue";
import userLoginView from "@/views/user/userLoginView.vue";
import userRegisterView from "@/views/user/userRegisterView.vue";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import ViewQuestionView from "@/views/question/ViewQuestionView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: userLayout,
    meta: {
      hideInMenu: true,
    },
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: userLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: userRegisterView,
      },
    ],
  },
  {
    path: "/",
    name: "主页",
    component: QuestionsView,
  },
  {
    path: "/questions",
    name: "浏览题目",
    component: QuestionsView,
  },
  {
    path: "/add/question",
    name: "创建题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/view/question/:id",
    name: "在线做题",
    props: true,
    component: ViewQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/manage/question",
    name: "管理题目",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/update/question",
    name: "更新题目",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/noAuth",
    name: "无权限",
    component: NoAuth,
    meta: {
      hideInMenu: true,
    },
  },
  // {
  //   path: "/hide",
  //   name: "隐藏页面",
  //   component: NoAuth,
  //   meta: {
  //     hideInMenu: true,
  //   },
  // },
  // {
  //   path: "/admin",
  //   name: "管理员可见",
  //   component: AdminView,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //   },
  // },
  // {
  //   path: "/about",
  //   name: "关于我的",
  //   component: () =>
  //     import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  // },
];
