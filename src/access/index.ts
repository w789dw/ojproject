import router from "@/router/index";
import store from "@/store/index";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  console.log("登陆用户信息", store.state.user.loginUser);
  let loginUser = store.state.user.loginUser;
  //如果没有登陆，自动登录
  if (!loginUser || !loginUser.userRole) {
    //加await是为了等用户登陆成功之后再执行后续的代码
    await store.dispatch("user/getLoginUser");
    loginUser = store.state.user.loginUser;
  }
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  //要跳转的页面必须要登录
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    //如果没登陆，跳转到登陆页面
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
    //如果登陆了，权限不足，那么跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  next();
});
